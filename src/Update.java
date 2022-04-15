import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Update {

    // LinkedList<GameObject> object = new LinkedList<GameObject>();
    ArrayList<GameObject> object = new ArrayList<GameObject>();

    private boolean up = false, down = false, left = false, right = false;

    public void tick() {
        for (int i = 0; i < object.size(); i++) {
            GameObject tempObject = object.get(i);

            tempObject.tick();
        }
    }

    public void render(Graphics g) {
        for (int i = 0; i < object.size(); i++) {
            GameObject tempObject = object.get(i);

            tempObject.render(g);
        }
    }

    public void clearLevel() {
        object.clear();
    }

    public void addObject(GameObject tempObject) {
        object.add(tempObject);
    }

    public void removeObject(GameObject tempObject) {
        object.remove(tempObject);
    }

    public boolean isUp() {
        return up;
    }

    public Action setUp(boolean up) {
        this.up = up;
        return null;
    }

    public boolean isDown() {
        return down;
    }

    public Action setDown(boolean down) {
        this.down = down;
        return null;
    }

    public boolean isLeft() {
        return left;
    }

    public Action setLeft(boolean left) {
        this.left = left;
        return null;
    }

    public boolean isRight() {
        return right;
    }

    public Action setRight(boolean right) {
        this.right = right;
        return null;
    }
}
