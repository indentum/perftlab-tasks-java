package src;

import java.util.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.io.*;

class Point {
    public double x, y, z;
    
    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Point(String s) {
        String[] coords = s.trim().split(",");
        x = Double.parseDouble(coords[0]);
        y = Double.parseDouble(coords[1]);
        z = Double.parseDouble(coords[2]);
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public String toString() { 
        return String.format("Point(%f, %f, %f)", x, y, z);
    }
}

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Введите имя файла (и только его)!");
        }
        String dataStr = readData(args[0]).trim();
        Map<String, String> data = parseData(dataStr);
    
        double radius = Double.parseDouble(data.get("radius"));
        Point c = new Point(data.get("center"));

        String line = data.get("line");
        int divider = line.lastIndexOf("[");
        Point a = new Point(line.substring(line.indexOf("[") + 1, line.indexOf("]")));
        Point b = new Point(line.substring(line.indexOf("[", divider) + 1, line.indexOf("]", divider)));
        
        /*
            Решить систему из уравнения сферы и канонического уравнения прямой: 
            {
                (x - center.x)^2 + (y - center.y)^2 + (z - center.z)^2 = radius^2,
                (x - a.x) / (a.x - b.x) = (x - a.y) / (a.y - b.y) = (x - a.z) / (a.z - b.z)
            }
            Переведём в параметрический вид:
            {
                (x - center.x)^2 + (y - center.y)^2 + (z - center.z)^2 = radius^2,
                x = (a.x - b.x) t + a.x,
                y = (a.y - b.y) t + a.y,
                z = (a.z - b.z) t + a.z
            }
        */

        Point l = new Point(a.x - b.x, a.y - b.y, a.z - b.z); // направляющий вектор прямой ab
        Point v = new Point(a.x - c.x, a.y - c.y, a.z - c.z); // вектор от точки на прямой до центрa сферы
        
        double ta = l.x * l.x + l.y * l.y + l.z * l.z;
        double tb = 2 * l.x * a.x - 2 * l.x * c.x + 2 * l.y * a.y - 2 * l.y * c.y + 2 * l.z * a.z - 2 * l.z * c.z;
        double tc = a.x * a.x + c.x * c.x + a.y * a.y + c.y * c.y + a.z * a.z + c.z * c.z - 2 * a.x * c.x - 2 * a.y * c.y - 2 * a.z * c.z - radius * radius;

        double D = tb * tb - 4 * ta * tc;
        
        if (Math.abs(D) < 1e-9) {
            double t = -tb / (2 * ta);
            Point p = new Point((a.x - b.x) * t + a.x, (a.y - b.y) * t + a.y, (a.z - b.z) * t + a.z);
            System.out.println("Коллизия: " + p.toString());
        } else if (D > 0) {
            double t1 = (-tb + Math.sqrt(D)) / (2 * ta);
            double t2 = (-tb - Math.sqrt(D)) / (2 * ta);
            Point p1 = new Point((a.x - b.x) * t1 + a.x, (a.y - b.y) * t1 + a.y, (a.z - b.z) * t1 + a.z);
            Point p2 = new Point((a.x - b.x) * t2 + a.x, (a.y - b.y) * t2 + a.y, (a.z - b.z) * t2 + a.z);
            System.out.println("Коллизии: " + p1.toString() + ", " + p2.toString());
        } else {
            System.out.println("Коллизий не найдено");
        }

        // Point lxv = new Point(l.y * v.z - l.z * v.y, -(l.x * v.z - l.z * v.x), l.x * v.y - l.y * v.x); // Векторное произведение [l x v]

        // double distance = lxv.length() / l.length(); // расстояние от центра сферы до прямой.
    }

    static String readData(String filename) {
        try {
            return new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new Error(e);
        }
    }   

    static TreeMap<String, String> parseData(String data) {
        TreeMap<String, String> result = new TreeMap<>();
        
        int center = data.indexOf("center");
        result.put("center", data.substring(data.indexOf("[", center) + 1, data.indexOf("]", center)));

        int radius = data.indexOf("radius");
        result.put("radius", data.substring(data.indexOf(":", radius) + 1,
             Math.min(data.indexOf(",", radius) == -1 ? data.length() : data.indexOf(",", radius), data.indexOf("}", radius))).trim());
    
        int line = data.indexOf("line");
        result.put("line", data.substring(data.indexOf("{", line) + 1, data.indexOf("}", line)));

        return result;
    }
}