/** 
 * @author [Phuong Hua] 
 * @version 1.0
 * CS151 Fall 2025 - Project 2
 */

 package gamestate.snakegame;
import java.util.LinkedList;

public class Snake {
    private LinkedList<Integer> body;
    private int currentDirection;
    private int nextDirection;
    private boolean grow;

    public Snake(int initialPosition, int initialDirection) {
        body = new LinkedList<Integer>();
        body.add(initialPosition);
        currentDirection = initialDirection;
        nextDirection = initialDirection;
        grow = false;
    }

    public LinkedList<Integer> getBody() {
        return body;
    }

    public int getCurrentDirection() {
        return currentDirection;
    }

    public void setNextDirection(int direction) {
        this.nextDirection = direction;
    }

    public Integer getHead() {
        return body.getFirst();
    }

    public void move() {
        currentDirection = nextDirection;
        Integer head = getHead();
        Integer newHead = null;
        switch (currentDirection) {
            case UP:
                newHead = new Point(head.getX(), head.getY() - 1);
                break;
            case DOWN:
                newHead = new Point(head.getX(), head.getY() + 1);
                break;
            case LEFT:
                newHead = new Point(head.getX() - 1, head.getY());
                break;
            case RIGHT:
                newHead = new Point(head.getX() + 1, head.getY());
                break;
        }
        body.addFirst(newHead);
        if (!grow) {
            body.removeLast();
        } else {
            grow = false;
        }
    }

    public void grow() {
        this.grow = true;
    }


    
}
