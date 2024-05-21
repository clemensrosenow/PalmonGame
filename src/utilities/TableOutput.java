package utilities;

import entities.Move;
import entities.Palmon;

import java.util.ArrayList;

/**
 * Utility class for printing tabular data.
 */
public class TableOutput {
    ArrayList<Column> columns; //ArrayList instead of Array for easier mapping

    /**
     * Constructs a TableOutput with the specified columns.
     *
     * @param columns the columns to be displayed in the table
     */
    private TableOutput(ArrayList<Column> columns) {
        this.columns = columns;
    }

    /**
     * Represents a column in the table.
     */
    private static class Column {
        String name;
        int characterLength;
        Formatting formatting;
        Object[] entries;

        /**
         * Constructs a Column with the specified attributes.
         *
         * @param name           the name of the column
         * @param characterLength the character length reserved for the column, based on the maximum entry or column name length
         * @param formattingFlag the formatting type for the column
         * @param entries        the entries in the column, the length should be the same for all columns
         */
        private Column(String name, int characterLength, Formatting formattingFlag, Object[] entries) {
            this.name = name;
            this.characterLength = characterLength;
            this.formatting = formattingFlag;
            this.entries = entries;
        }

        /**
         * Enum representing the formatting type for column data.
         * The flag is used in the printf() method to format the data.
         */
        private enum Formatting {
            digit("d"), string("s"), uppercaseString("S");
            final String flag;

            Formatting(String flag) {
                this.flag = flag;
            }
        }
    }

    /**
     * Enum representing the position of the table border.
     */
    private enum BorderPosition {
        top, bottom, inner
    }

    /**
     * Prints the entire table, including head, entries and cell borders.
     */
    private void print() {
        printBorder(BorderPosition.top);
        printHead();

        for (int i = 0; i < columns.getFirst().entries.length; i++) {
            printBorder(BorderPosition.inner);
            printEntry(i);
        }

        printBorder(BorderPosition.bottom);
    }

    /**
     * Prints the border for the table at the specified position.
     *
     * @param borderPosition the position of the border (top, bottom, inner)
     */
    private void printBorder(BorderPosition borderPosition) {
        // Set the border characters based on the desired position
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

        // Create the borderline based on the column lengths
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

    /**
     * Prints the header row of the table.
     */
    private void printHead() {
        printRow(columns.stream().map(column -> column.name).toArray(), true);
    }

    /**
     * Prints an entry row at the specified index.
     *
     * @param rowIndex the index of the row to print
     */
    private void printEntry(int rowIndex) {
        printRow(columns.stream().map(column -> column.entries[rowIndex]).toArray(), false);
    }

    /**
     * Prints a row of the table.
     *
     * @param values    the values to print
     * @param printHead true if the row is a header row; false otherwise
     */
    private void printRow(Object[] values, boolean printHead) {
        char columnDivider = '│';
        StringBuilder line = new StringBuilder(); //Used for efficient string concatenation
        line.append(columnDivider);

        for (Column column : columns) {
            line.append(" "); // Padding for cell content
            line.append("%"); // Placeholder for value insertion
            if (column.formatting == Column.Formatting.string) { // Left-align strings
                line.append("-"); // Required formatting flag for printf()
            }
            line.append(column.characterLength); // Set the character length for the column
            line.append(printHead ? Column.Formatting.uppercaseString.flag : column.formatting.flag); // Column names are always displayed as uppercase strings
            line.append(" "); // Padding for cell content
            line.append(columnDivider);
        }
        line.append("%n"); //Newline character to print on own line

        System.out.printf(String.valueOf(line), values); //Print the formatted string with the values
    }
    
    /**
     * Prints a table of Palmon data.
     *
     * @param palmons the list of Palmon entities
     */
    public static void printPalmonTable(ArrayList<Palmon> palmons) {
        ArrayList<Column> columns = new ArrayList<>();

        // Add columns for the Palmon data by mapping the Palmon attributes
        columns.add(new Column(Localization.getMessage("tableoutput.column.id"), 5, Column.Formatting.digit, palmons.stream().map(palmon -> palmon.id).toArray()));
        columns.add(new Column(Localization.getMessage("tableoutput.column.name"), 26, Column.Formatting.string, palmons.stream().map(palmon -> palmon.name).toArray()));
        columns.add(new Column(Localization.getMessage("tableoutput.column.height"), 6, Column.Formatting.digit, palmons.stream().map(palmon -> palmon.height).toArray()));
        columns.add(new Column(Localization.getMessage("tableoutput.column.weight"), 7, Column.Formatting.digit, palmons.stream().map(palmon -> palmon.weight).toArray()));
        columns.add(new Column(Localization.getMessage("tableoutput.column.types"), 18, Column.Formatting.string, palmons.stream().map(Palmon::getTypes).toArray()));
        columns.add(new Column(Localization.getMessage("tableoutput.column.hp"), 3, Column.Formatting.digit, palmons.stream().map(palmon -> palmon.hp).toArray()));
        columns.add(new Column(Localization.getMessage("tableoutput.column.attack"), 7, Column.Formatting.digit, palmons.stream().map(palmon -> palmon.attack).toArray()));
        columns.add(new Column(Localization.getMessage("tableoutput.column.defense"), 12, Column.Formatting.digit, palmons.stream().map(palmon -> palmon.defense).toArray()));
        columns.add(new Column(Localization.getMessage("tableoutput.column.speed"), 15, Column.Formatting.digit, palmons.stream().map(palmon -> palmon.speed).toArray()));

        new TableOutput(new ArrayList<>(columns)).print(); //Create a new TableOutput instance and print the table
    }

    /**
     * Prints a table of Move data.
     *
     * @param moves the list of Move entities
     */
    public static void printMoveTable(ArrayList<Move> moves) {
        ArrayList<Column> columns = new ArrayList<>();

        // Add columns for the Move data by mapping the Move attributes
        columns.add(new Column(Localization.getMessage("tableoutput.column.id"), 5, Column.Formatting.digit, moves.stream().map(move -> move.id).toArray()));
        columns.add(new Column(Localization.getMessage("tableoutput.column.name"), 20, Column.Formatting.string, moves.stream().map(move -> move.name).toArray()));
        columns.add(new Column(Localization.getMessage("tableoutput.column.damage"), 7, Column.Formatting.digit, moves.stream().map(move -> move.damage).toArray()));
        columns.add(new Column(Localization.getMessage("tableoutput.column.accuracy"), 16, Column.Formatting.digit, moves.stream().map(move -> (int) move.accuracy * 100).toArray()));
        columns.add(new Column(Localization.getMessage("tableoutput.column.type"), 10, Column.Formatting.string, moves.stream().map(move -> move.type).toArray()));

        new TableOutput(columns).print(); //Create a new TableOutput instance and print the table
    }
}