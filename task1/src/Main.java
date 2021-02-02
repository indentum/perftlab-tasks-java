package src;

public class Main {
    static void usage() {
        System.out.println("Usage: java Main NUMBER BASE");
        System.out.println("NUMBER - неотрицательное число (0 <= NUMBER <= 2^31 - 1) для перевода.");
        System.out.println("BASE - алфавит новой системы счисления.");
        System.out.println("OR");
        System.out.println("java Main NUMBER BASE_SRC BASE_DST");
        System.out.println("NUMBER - число для перевода в системе счисления BASE_SRC. В десятичном виде не должно превосходить 2^31 - 1.");
        System.out.println("BASE_SRC - алфавит системы счисления числа NUMBER.");
        System.out.println("BASE_DST - алфавит новой системы счисления.");
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            try {
                int d = Integer.parseInt(args[0]);
                System.out.println(itoBase(d, args[1]));
            } catch (NumberFormatException e) {
                usage();
            }
        } else if (args.length == 3) {
            System.out.println(itoBase(args[0], args[1], args[2]));
        } else {
            usage();
        }
    }

    static String itoBase(int nb, String base) {
        if (!checkBase(base)) {
            System.out.println("Неправильный алфавит!");
            throw new Error();
        }

        if (nb < 0) {
            usage();
            throw new Error("NUMBER отрицательно");
        }
        if (nb == 0) {
            return base.charAt(0) + "";
        }
        int nBase = base.length();
        StringBuilder sb = new StringBuilder();
        
        // переводим в новую СС
        while (nb > 0) {
            sb.insert(0, base.charAt(nb % nBase));
            nb /= nBase;
        }
        return sb.toString();
    }

    static String itoBase(String nb, String baseSrc, String baseDst) {
        if (!checkBase(baseSrc) || !checkBase(baseDst)) {
            System.out.println("Неправильный алфавит!");
            throw new Error();
        }
        
        // Проверяем соотвествие числа его алфавиту
        // путём удаления из числа всех символов алфавита.
        // Если останется что-то, то число не соответствует этому алфавиту.
        String t = nb;
        for (int i = 0; i < baseSrc.length(); i++) {
            t = t.replace(baseSrc.charAt(i), ' ');
        }
        if (!t.trim().isEmpty()) {
            System.out.println("Число не соответствует алфавиту");
            usage();
            throw new Error();
        }
        // Переводим в десятичную СС
        int n = 0;
        int nBase = baseSrc.length();
        for (int i = 0; i < nb.length(); i++) {
            n *= nBase;
            n += baseSrc.indexOf(nb.charAt(i));
        }
        // А потом из десятичной в новую.
        return itoBase(n, baseDst);
    }

    static boolean checkBase(String base) {
        final int MAX_CHARS = Character.MAX_VALUE - Character.MIN_VALUE;
        
        // По принципу Дирехле, если длина строки больше кол-ва вариантов значения char, то есть повторяющийся символ.
        // Пустой алфавит и унарные системы счисления также обрасываем
        if (base.length() > MAX_CHARS && base.length() > 1) {
            return false;
        }

        boolean[] chars = new boolean[MAX_CHARS];
        
        // Ищем повторяющийся символ 
        for (int i = 0; i < base.length(); i++) {
            char c = base.charAt(i);
            if (chars[c]) {
                return false;
            }
            chars[c] = true;
        }
        return true;
    }
}