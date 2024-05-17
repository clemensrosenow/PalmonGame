package utils;

import java.util.ArrayList;
import java.util.Arrays;

public class TableOutput {
    ArrayList<Column> columns; //ArrayList instead of Array for easier mapping
    public TableOutput(ArrayList<Column> columns) {
        this.columns = columns;
    }

    public static class Column {
        String name;
        int characterLength;
        Formatting formatting;
        Object[] entries;

        public Column(String name, int characterLength, Formatting formattingFlag, Object[] entries) {
            this.name = name;
            this.characterLength = characterLength;
            this.formatting = formattingFlag;
            this.entries = entries; //Implied assumption: all column entries have the same length
        }

        public enum Formatting {
            digit("d"), string("s");
            final String flag;

            Formatting(String flag) {
                this.flag = flag;
            }
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
        printRow(columns.stream().map(column -> column.name).toArray(), true);
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

    private void printEntry(int rowIndex) {
        //Todo: Get column attributes as Object[]
        printRow(columns.stream().map(column -> column.entries[rowIndex]).toArray(), false);
    }

    public void printRow(Object[] values, boolean printHead) {
        char columnDivider = '│';
        StringBuilder line = new StringBuilder();
        line.append(columnDivider);

        for (Column column : columns) {
            line.append(" %");
            if (column.formatting == Column.Formatting.string) { // Left-align strings
                line.append("-"); // Required formatting flag for printf()
            }
            line.append(column.characterLength);
            if (printHead) {
                line.append("S "); //Column names are always displayed as uppercase strings
            } else {
                line.append(column.formatting.flag + " ");
            }

            line.append(columnDivider);
        }
        line.append("%n"); //Newline character to print on own line
        System.out.printf(String.valueOf(line), values);
    }

    public static void main(String[] args) {
        Object[] ids = {1,2,3,4};
        Object[] names = {"OH", "MY", "GOD", "!!!"};
        Column id = new Column("id", 5, Column.Formatting.digit, ids);
        Column name = new Column("name", 20, Column.Formatting.string, names);


        TableOutput table = new TableOutput(new ArrayList<>(Arrays.asList(id, name)));
        table.print();
    }

    public void print() {
        printBorder(BorderPosition.top);
        printHead();

        for (int i = 0; i < columns.getFirst().entries.length; i++) {
            printBorder(BorderPosition.inner);
            printEntry(i);
        }

        printBorder(BorderPosition.bottom);
    }
}


