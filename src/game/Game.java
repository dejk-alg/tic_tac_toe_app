package game;

public class Game {
    int size;
    Field field;
    int turn;
    int victory;

    public Game() {
        size = 3;
        field = new Field(size, 2);
        turn = 0;
        victory = 3;
    }

    public int getValue(int row, int column) {
        return  field.getValue(row, column);
    }

    public int getPlayer() {
        return turn % 2;
    }

    public boolean turnAllowed(int row, int column) {
        return field.emptySpace(row, column);
    }

    public boolean makeTurn(int player, int row, int column) {
        if (!turnAllowed(row, column)) return false;
        turn++;
        return field.setValue(row, column, player);
    }

    public String outputField() {
        System.out.println(field.display());
        return field.display() + "Ходит игрок " + (getPlayer() + 1);
    }

}
