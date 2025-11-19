package snakegame;
import java.util.LinkedList;
import java.util.Random;

public class Snake {
    private LinkedList<Point> body;
    private Direction currentDirection;
    private Direction nextDirection;
    private boolean grow;

    public Snake(Point initialPosition, Direction initialDirection) {
        body = new LinkedList<>();
        body.add(initialPosition);
        currentDirection = initialDirection;
        nextDirection = initialDirection;
        grow = false;
    }

    public LinkedList<Point> getBody() {
        return body;
    }

    public Direction getCurrentDirection() {
        return currentDirection;
    }

    public void setNextDirection(Direction direction) {
        this.nextDirection = direction;
    }

    public Point getHead() {
        return body.getFirst();
    }

    public void move() {
        currentDirection = nextDirection;
        Point head = getHead();
        Point newHead = null;
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
