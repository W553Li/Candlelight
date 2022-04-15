import java.awt.*;

public abstract class GameObject {

    protected int x, y;
    protected float velX = 0, velY = 0;
    protected ID id;
    protected float w, h;
    protected static SpriteSheet spriteSheet;

    public GameObject(int x, int y, ID id, SpriteSheet spriteSheet, SpriteSheet sheet) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.spriteSheet = spriteSheet;
    }

    public abstract void tick();
    public abstract void render(Graphics g);
    public abstract Rectangle getBounds();

    public float getW() {
        return w;
    }

    public float getH() {
        return h;
    }

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public float getVelX() {
        return velX;
    }

    public void setVelX(float velX) {
        this.velX = velX;
    }

    public float getVelY() {
        return velY;
    }

    public void setVelY(float velY) {
        this.velY = velY;
    }
}
