package util;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class Input {
    private static final Scanner SC = new Scanner(System.in, "UTF-8");

    public static String readLine(String prompt) {
        System.out.print(prompt);
        if (!SC.hasNextLine()) {
            throw new NoSuchElementException("Потік введення закрито");
        }
        return SC.nextLine().trim();
    }

    public static String readNonEmpty(String prompt) {
        while (true) {
            String s = readLine(prompt);
            if (!s.isEmpty()) {
                return s;
            }
            System.out.println("Помилка: рядок не може бути порожнім.");
        }
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            try {
                int value = Integer.parseInt(readLine(prompt));
                if (value < min || value > max) {
                    throw new IllegalArgumentException("Число має бути від " + min + " до " + max);
                }
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Помилка: введіть ціле число.");
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt));
            } catch (NumberFormatException e) {
                System.out.println("Помилка: введіть ціле число.");
            }
        }
    }
}
