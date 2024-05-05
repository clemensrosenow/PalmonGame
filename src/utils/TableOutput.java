package utils;

public class TableOutput {

}
class TableColumn {
    int maxLength;
    public enum alignment {
        left,
        right
    }

    public TableColumn(int maxLength, alignment textDirection) {
        this.maxLength = maxLength;
    }
}

