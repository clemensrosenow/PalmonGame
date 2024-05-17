package utils;

import java.util.ArrayList;

public class TableOutput<T> {
    ArrayList<Column> columns;
    ArrayList<T> rows;
    public TableOutput(ArrayList<Column> head, ArrayList<T> entries) {
        this.columns = head;
        this.rows = entries;
    }
    public static class Column {
        String name;
        int characterLength;
        boolean displayNumber;

        public Column(String name, int characterLength, boolean displayNumber) {
            this.name = name;
            this.characterLength = characterLength;
            this.displayNumber = displayNumber;
        }

        enum TextAlignment {
            justifyLeft, justifyRight
        }
    }

    enum BorderPosition {
        top, bottom, inner
    }

    private void printBorder(BorderPosition borderPosition) {
        char columnStart;
        char columnInner;
        char columnEnd;

        switch (borderPosition) {
            case top:
                columnStart = '┌';
                columnInner = '┬';
                columnEnd = '┐';
                break;
            case bottom:
                columnStart = '└';
                columnInner = '┴';
                columnEnd = '┘';
                break;
            case inner: default:
                columnStart = '├';
                columnInner = '┼';
                columnEnd = '┤';
        }

        StringBuilder line = new StringBuilder();
        line.append(columnStart);
        for (Column column : columns) {
            line.append("─".repeat(column.characterLength + 2)); //Adding 2 extra characters for alignment with cell padding
            line.append(columnInner);
        }
       line.deleteCharAt(line.length() - 1); //Remove one undesired columnInner character at the string end
        line.append(columnEnd);
        System.out.println(line);
    }

    /* Approach using System.out.printf() */
    private void printHead() {
        //Example of desired format: "│ %5S │ %-20S │%n, 'ID, 'Name'"
        char columnDivider = '│';
        StringBuilder line = new StringBuilder();
        line.append(columnDivider);

        for (Column column : columns) {
            line.append(" %");
            if (!column.displayNumber) { // Left-align strings
                line.append("-"); // Required formatting flag for printf()
            }
            line.append(column.characterLength);
            line.append("S "); //Column names are always displayed as uppercase strings
            line.append(columnDivider);
        }
        line.append("%n"); //Newline character to print on own line
        Object[] columnNames = columns.stream().map(column -> column.name).toArray();
        System.out.printf(String.valueOf(line), columnNames);
    }
    /*private void printEntry(T row) {
        //Example of desired format: "│ %5d │ %-20s │%n", entry.id, entry.name"
        char columnDivider = '│';
        StringBuilder line = new StringBuilder();
        line.append(columnDivider);


        for (Column column: columns) {
            line.append(" %");
            if (column.leftTextAlignment) { // right by default
                line.append("-"); // Leads to left-aligned text in combination with printf()
            }
            line.append(column.characterLength);
            line.append("s "); //Column names are always displayed as strings
            line.append(columnDivider);
        }
        line.append("%n"); //Newline character to print on own line
        Object[] columnNames = columns.stream().map(column -> column.name).toArray(); //Todo: Possible correction varargs of printf: toArray(new String[0]);
        System.out.printf(String.valueOf(line), columnNames);

        //System.out.printf(""); //Todo
    }*/

    private void printEntry(T row) {
        //Todo: copy printHead and abstract for printRow method
        return;
    }

    //Todo: shared method printRow for printHead and printEntry -> Parameters: flag, values

    public static void main(String[] args) {
        Column id = new Column("id", 5, true);
        Column name = new Column("name", 20, false);

        ArrayList<Column> columns = new ArrayList<>();
        columns.add(id);
        columns.add(name);

        TableOutput<String> table = new TableOutput<String>(columns, new ArrayList<String>());

        table.printBorder(BorderPosition.top);
        table.printHead();
        table.printBorder(BorderPosition.bottom);
    }

    public void print() {
        printBorder(BorderPosition.top);
        printHead();
        for (T entry : rows) {
            printBorder(BorderPosition.inner);
            printEntry(entry);
        }
        printBorder(BorderPosition.bottom);
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


