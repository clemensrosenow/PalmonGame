package utilities;

import entities.Move;
import entities.Palmon;

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

    private void printEntry(int rowIndex) {
        printRow(columns.stream().map(column -> column.entries[rowIndex]).toArray(), false);
    }

    private void printRow(Object[] values, boolean printHead) {
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
                line.append("S" + " "); //Column names are always displayed as uppercase strings
            } else {
                line.append(column.formatting.flag + " ");
            }

            line.append(columnDivider);
        }
        line.append("%n"); //Newline character to print on own line
        System.out.printf(String.valueOf(line), values);
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

    public static void printPalmonTable(ArrayList<Palmon> palmons) {
        ArrayList<TableOutput.Column> columns = new ArrayList<>();

        columns.add(new TableOutput.Column("id", 5, TableOutput.Column.Formatting.digit, palmons.stream().map(palmon -> palmon.id).toArray()));
        columns.add(new TableOutput.Column("name", 26, TableOutput.Column.Formatting.string, palmons.stream().map(palmon -> palmon.name).toArray()));
        columns.add(new TableOutput.Column("types", 18, TableOutput.Column.Formatting.string, palmons.stream().map(palmon -> palmon.types[0] + (palmon.types[1].isEmpty() ? "" : ", " + palmon.types[1])).toArray()));
        columns.add(new TableOutput.Column("hp", 3, TableOutput.Column.Formatting.digit, palmons.stream().map(palmon -> palmon.hp).toArray()));
        columns.add(new TableOutput.Column("attack", 6, TableOutput.Column.Formatting.digit, palmons.stream().map(palmon -> palmon.attack).toArray()));
        columns.add(new TableOutput.Column("defense", 7, TableOutput.Column.Formatting.digit, palmons.stream().map(palmon -> palmon.defense).toArray()));
        columns.add(new TableOutput.Column("speed", 5, TableOutput.Column.Formatting.digit, palmons.stream().map(palmon -> palmon.speed).toArray()));

        new TableOutput(new ArrayList<>(columns)).print();
    }

    public static void printMoveTable(ArrayList<Move> moves) {
        ArrayList<TableOutput.Column> columns = new ArrayList<>();

        columns.add(new TableOutput.Column("id", 5, TableOutput.Column.Formatting.digit, moves.stream().map(move -> move.id).toArray()));
        columns.add(new TableOutput.Column("name", 20, TableOutput.Column.Formatting.string, moves.stream().map(move -> move.name).toArray()));
        columns.add(new TableOutput.Column("damage", 6, TableOutput.Column.Formatting.digit, moves.stream().map(move -> move.damage).toArray()));
        columns.add(new TableOutput.Column("accuracy", 8, TableOutput.Column.Formatting.digit, moves.stream().map(move -> move.accuracy).toArray()));
        columns.add(new TableOutput.Column("type", 10, TableOutput.Column.Formatting.string, moves.stream().map(move -> move.type).toArray()));

        new TableOutput(columns).print();
    }
}