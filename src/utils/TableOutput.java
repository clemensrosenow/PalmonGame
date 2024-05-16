package utils;

import java.util.ArrayList;

public class TableOutput<T> {
    ArrayList<Column> columns;
    ArrayList<T> rows;
    public TableOutput(ArrayList<Column> header, ArrayList<T> entries) {
        this.columns = header;
        this.rows = entries;
    }
    public class Column {
        int characterLength;
        TextAlignment alignment;
        public enum TextAlignment {
            left,
            right
        }

        public Column(int characterLength, TextAlignment alignment) {
            this.characterLength = characterLength;
            this.alignment = alignment;
        }

    }

    public void print() {
        //Todo: Complete Method
        System.out.printf("┌" + "┐%n");
        columns.forEach(column -> "─".repeat(column.characterLength));
    }

    /*private static void printTable(ArrayList<Palmon> rows) {
        //TODO: Adjust string length to 26 if space is available
        //TODO: Make more adaptive to column width and content
        System.out.printf("┌───────┬──────────────────────┐%n");
        System.out.printf("│ %5s │ %-20s │%n", "ID", "Name");

        for (Palmon entry: rows) {
            System.out.printf("├───────┼──────────────────────┤%n");
            System.out.printf("│ %5d │ %-20s │%n", entry.id, entry.name);
        }
        System.out.printf("└───────┴──────────────────────┘%n");
    }
    public static void main(String[] args) {

        System.out.printf("┌───────┬──────────────────────┐%n");
        System.out.printf("│ %5s │ %-20s │%n", "ID", "Name");


        System.out.printf("├───────┼──────────────────────┤%n");
        System.out.printf("│ %5d │ %-20s │%n", 4, "Palmon");

        System.out.printf("├───────┼──────────────────────┤%n");
        System.out.printf("│ %5d │ %-20s │%n", 103, "Crazy Attacker");

        System.out.printf("├───────┼──────────────────────┤%n");
        System.out.printf("│ %5d │ %-20s │%n", 10823, "Long Sick Palmon Name");

        System.out.printf("└───────┴──────────────────────┘%n");
    }*/
}


