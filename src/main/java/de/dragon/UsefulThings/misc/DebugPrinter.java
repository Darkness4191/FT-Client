package de.dragon.UsefulThings.misc;

/**
 * Part of UsefulThings project
 *
 * @author Dragon777/Darkness4191
 **/

public class DebugPrinter {

    private static boolean print_events = false;

    public static void setPrint(boolean print) {
        print_events = print;
    }

    public static void print(String s) {
        if(print_events) {
            System.out.print(s);
        }
    }

    public static void println(String s) {
        if(print_events) {
            System.out.println(s);
        }
    }

}
