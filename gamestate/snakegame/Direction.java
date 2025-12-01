/** 
 * @author [Phuong Hua] 
 * @version 1.0
 * CS151 Fall 2025 - Project 2
 */

 package gamestate.snakegame;

public enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }
    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public boolean isOpposite(Direction other) {
        return (this == UP && other == DOWN) ||
               (this == DOWN && other == UP) ||
               (this == LEFT && other == RIGHT) ||
               (this == RIGHT && other == LEFT);
    }
}
