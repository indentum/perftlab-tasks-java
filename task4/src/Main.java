package src;

import java.util.*;

public class Main {
    static String normalize(String s) {
        StringBuilder sb = new StringBuilder();
        char[] str = s.toCharArray();
        for (int i = 0; i < str.length; i++) {
            sb.append(str[i]);
            if (str[i] == '*') {
                while (str[i] == '*' && i < str.length) i++;
                i--;
            }
        }
        return sb.toString();
    }
    
    static boolean compare(String a, String b) {
        if (a.equals(b)) {
            return true;
        } else if (a.isEmpty()) {
            return b.equals("*");
        } else if (b.isEmpty()) {
            return false;
        } else if (b.charAt(0) == '*') {
            return compare(a, b.substring(1)) || compare(a.substring(1), b);
        } else if (b.charAt(b.length() - 1) == '*') {
            return compare(a, b.substring(0, b.length() - 1)) || compare(a.substring(0, a.length() - 1), b);
        } else if (a.charAt(0) == b.charAt(0)) {
            return compare(a.substring(1), b.substring(1));
        } else {
            return false;
        }
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String a = in.nextLine();
        String b = in.nextLine();

        System.out.println("a == b: " + compare(a, b));
    }
}