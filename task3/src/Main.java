package src;

import java.util.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.io.*;

public class Main {
    static void usage() {
        System.out.println("Usage: java Main FILE TS_START TS_END");
        System.out.println("FILE - файл с логами");
        System.out.println("TS_START, TS_END - дать отчёт по диапазону [TS_START; TS_END]");
        System.out.println("Формат timestamp: YYYY-MM-DDTHH:MM:SS");
    }
    
    static boolean checkTS(String ts) {
        String[] tsArray = ts.split("T");
        if (tsArray.length != 2) return false;
        String[] date = tsArray[0].split("-");
        if (date.length != 3) return false;
        try {
            int year = Integer.parseInt(date[0]);
            int month = Integer.parseInt(date[1]);
            int day = Integer.parseInt(date[2]);
            if (!(year > 0 && month > 0 && month <= 12 && day > 0 && day <= 31)) return false;
        } catch (NumberFormatException nfe) {
            return false;
        }
        String[] time = tsArray[1].split(":");
        try {
            int hour = Integer.parseInt(time[0]);
            int minute = Integer.parseInt(time[1]);
            int second = Integer.parseInt(time[2]);
            if (!(hour >= 0 && hour < 24 && minute >= 0 && minute < 60 && second >= 0 && second < 60)) return false;
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    static List<String> readLog(String filename) {
        try {
            return Files.readAllLines(Paths.get(filename));
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        if (args.length != 3 || !checkTS(args[1]) || !checkTS(args[2])) {
            usage();
            System.exit(0);
        }

        List<String> lines = readLog(args[0]);

        int maxVolume = Integer.parseInt(lines.get(0));
        int baseVolume = Integer.parseInt(lines.get(1));
        
        ArrayList<State> states = new ArrayList<>();
        states.add(new State(baseVolume, new Timestamp(), 0, true));
        for (int i = 2; i < lines.size(); i++) {
            String[] split = lines.get(i).trim().split(" ");
            Timestamp ts = new Timestamp(split[0]);
            int diff = Integer.parseInt(split[split.length - 1].substring(0, split[split.length - 1].length() - 1));
            boolean success;
            int volume = states.get(states.size() - 1).volume;
            if (split[split.length - 2].equals("up")) {
                success = volume + diff <= maxVolume;
            } else {
                diff = -diff;
                success = volume + diff >= 0;
            }
            if (success) {
                volume += diff;
            }
            states.add(new State(volume, ts, diff, success));
        }

        states.sort((a, b) -> a.ts.compareTo(b.ts));

        Timestamp start = new Timestamp(args[1]);
        Timestamp end = new Timestamp(args[2]);
        int idxStart = -1;
        int idxEnd = -1;
        for (int i = 1; i < states.size(); i++) {
            if (idxStart == -1) {
                if (states.get(i).ts.compareTo(start) <= 0) {
                    idxStart = i;
                }
            }
            if (idxEnd == -1) {
                if (states.get(i).ts.compareTo(end) > 0) {
                    idxEnd = i;
                }
            }
        }
        if (idxStart == -1) {
            System.out.println("В данном временном диапазоне ничего не происходило");
            System.exit(0);
        }
        if (idxEnd == -1) {
            idxEnd = states.size();
        }

        int attemptsAdd = 0;
        int attemptsSub = 0;
        int mistakesAdd = 0;
        int mistakesSub = 0;
        int waterAdded = 0;
        int waterSubbed = 0;
        int waterNotAdded = 0;
        int waterNotSubbed = 0;

        for (int i = idxStart; i < idxEnd; i++) {
            State state = states.get(i);
            if (state.diff > 0) {
                attemptsAdd++;
                if (state.lastOperationSuccess) {
                    waterAdded += state.diff;
                } else {
                    waterNotAdded += state.diff;
                    mistakesAdd++;
                }
            } else {
                attemptsSub++;
                if (state.lastOperationSuccess) {
                    waterSubbed += state.diff;
                } else {
                    waterNotSubbed += state.diff;
                    mistakesSub++;
                }
            }
        }
        System.out.println("Попыток налить воду: " + attemptsAdd);
        System.out.println("Процент ошибок при этом: " + 100 * (0.0 + mistakesAdd) / attemptsAdd);
        System.out.println("Налито: " + waterAdded);
        System.out.println("Не налито: " + waterNotAdded);
        System.out.println("Попыток слить воду: " + attemptsSub);
        System.out.println("Процент ошибок при этом: " + 100 * (0.0 + mistakesSub) / attemptsSub);
        System.out.println("Слито: " + waterSubbed);
        System.out.println("Не слито: " + waterNotSubbed);
        System.out.println("Воды в начале отрезка: " + states.get(idxStart - 1).volume);
        System.out.println("Воды в конце отрезка: " + states.get(idxEnd - 1).volume);
    }
}

class Timestamp implements Comparable<Timestamp> {
    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;
    public int second;

    public Timestamp() {
        year = month = day = hour = minute = second = 0;
    }

    public Timestamp(String s) {
        int dot = s.indexOf('.');
        if (dot != -1) {
            s = s.substring(0, dot);
        }
        String[] ts = s.split("T");
        String[] date = ts[0].split("-");
        year = Integer.parseInt(date[0]);
        month = Integer.parseInt(date[1]);
        day = Integer.parseInt(date[2]);
        String[] time = ts[1].split(":");
        hour = Integer.parseInt(time[0]);
        minute = Integer.parseInt(time[1]);
        second = Integer.parseInt(time[2]);
    }

    public int compareTo(Timestamp ts) {
        if (year == ts.year) {
            if (month == ts.month) {
                if (day == ts.day) {
                    if (hour == ts.hour) {
                        if (minute == ts.minute) {
                            return second - ts.second;
                        }
                        return minute - ts.minute;
                    }
                    return hour - ts.hour;
                }
                return day - ts.day;
            }
            return month - ts.month;
        }
        return year - ts.year;
    }
}

class State {
    public int volume;
    public Timestamp ts;
    public int diff;
    public boolean lastOperationSuccess;

    public State() {
        this(0, new Timestamp(), 0, false);
    }

    public State(int volume, Timestamp ts, int diff, boolean lastOperationSuccess) {
        this.volume = volume;
        this.ts = ts;
        this.diff = diff;
        this.lastOperationSuccess = lastOperationSuccess;
    }
}