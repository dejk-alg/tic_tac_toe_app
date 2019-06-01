package game;

public class Field {
    int[][] field;
    int size;
    int cell_size;

    public Field(int _size, int _cell_size) {
        size = _size;
        cell_size = _cell_size;
        field = new int[size][size];
    }

    public Field() {
        size = 3;
        cell_size = 4;
        field = new int[size][size];
    }

    private String usualLine() {
        String line = "";
        for (int cnt = 0; cnt < size; cnt++) {
            line += new String(new char[cell_size * 2 * 2]).replace("\0", " ");
            if (cnt != size-1) {
                line += "|";
            }
        }
        line += "\n";
        return line;
    }

    private String playableLine(int row) {
        String line = "";
        for (int cnt = 0; cnt < size; cnt++) {
            line += new String(new char[(cell_size - 1) * 2]).replace("\0", " ");
            switch (field[row][cnt]) {
                case 0: line += "_";
                break;
                case -1: line += "O";
                break;
                case 1: line += "X";
            }

            line += new String(new char[cell_size * 2]).replace("\0", " ");

            if (cnt != size-1) {
                line += "|";
            }
        }
        line += "\n";
        return line;
    }

    private String boundaryLine() {
        String line = "";
        for (int cnt = 0; cnt < size; cnt++) {
            line += new String(new char[cell_size * 2]).replace("\0", "_");
            if (cnt != size-1) {
                line += "|";
            }
        }
        line += "\n";
        return line;
    }

    public String display() {
        String field = "";
        for (int cellCnt = 0; cellCnt < size; cellCnt++) {

            for (int lineCnt = 0; lineCnt < cell_size / 2; lineCnt++) {
                field += usualLine();
            }

            field += playableLine(cellCnt);

            for (int lineCnt = 0; lineCnt < cell_size / 2; lineCnt++) {
                field += usualLine();
            }

            field += boundaryLine();
        }
        return field;
    }

    public boolean correctIndex(int row, int column) {
        return !(row < 0 | row >= size | column < 0 | column >= size);
    }

    public boolean emptySpace(int row, int column) {
        if (!correctIndex(row, column))
            throw new RuntimeException("Wrong index");
        return getValue(row, column) == 0;
    }

    public int getValue(int row, int column) {
        if (!correctIndex(row, column)) return -2;
        return field[row][column];
    }

    public boolean checkVictory(int row, int column, int victory) {
        int length = 1;

        int currentValue = getValue(row, column);


        int curCol = column;

        int curRow = row + 1;
        while (getValue(curRow, curCol) == currentValue){
            length++;
            curRow++;
        }

        curRow = row - 1;

        while (getValue(curRow, curCol) == currentValue){
            length++;
            curRow--;
        }

        if (length >= victory) {
            return true;
        }

        length = 1;
        curRow = row;
        curCol = column + 1;

        while (getValue(curRow, curCol) == currentValue){
            length++;
            curCol++;
        }

        curCol = column - 1;

        while (getValue(curRow, curCol) == currentValue){
            length++;
            curCol--;
        }

        if (length >= victory) {
            return true;
        }

        length = 1;
        curRow = row + 1;
        curCol = column + 1;

        while (getValue(curRow, curCol) == currentValue){
            length++;
            curCol++;
            curRow++;
        }

        curRow = row - 1;
        curCol = column - 1;

        while (getValue(curRow, curCol) == currentValue){
            length++;
            curCol--;
            curRow--;
        }

        if (length >= victory) {
            return true;
        }

        length = 1;
        curRow = row + 1;
        curCol = column - 1;

        while (getValue(curRow, curCol) == currentValue){
            length++;
            curCol--;
            curRow++;
        }

        curRow = row - 1;
        curCol = column + 1;

        while (getValue(curRow, curCol) == currentValue){
            length++;
            curCol++;
            curRow--;
        }

        if (length >= victory) {
            return true;
        }

        return false;
    }

    public boolean setValue(int row, int column, int player) {
        if (emptySpace(row, column)) {
            if (player == 0)
                field[row][column] = -1;
            else field[row][column] = 1;
        }
        return checkVictory(row, column, 3);
    }
}