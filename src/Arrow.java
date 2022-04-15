import java.awt.*;

public class Arrow extends GameObject {

    private final Update update;

    public Arrow(int x, int y, ID id, Update update, int mx, int my, SpriteSheet spriteSheet) {
        super(x, y, id, spriteSheet, spriteSheet);
        this.update = update;

        float dx = mx - x;
        float dy = my - y;
        double distance = Math.pow(((dx*dx)+(dy*dy)),0.5);

        velX = (dx/((float)distance)) * 7;
        velY = (dy/((float)distance)) * 7;
    }

    public void tick() {
        x += velX;
        y += velY;

        for (int i = 0; i < update.object.size(); i++) {
            GameObject tempObject = update.object.get(i);

            if (tempObject.getId() == ID.Block) {
                if (getBounds().intersects(tempObject.getBounds())) {
                    update.removeObject(this);
                }
            }
        }
    }

    public void render(Graphics g) {
        g.setColor(Color.red);
        g.fillOval(x, y, 8, 8);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 8, 8);
    }
}
