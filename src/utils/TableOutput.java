package utils;

public class TableOutput {
    public TableOutput(Column[] tableColumns) {

    }
    class Column {
        int maxLength;
        public enum alignment {
            left,
            right
        }

        public Column(int maxLength, alignment textDirection) {
            this.maxLength = maxLength;
        }
    }
}


