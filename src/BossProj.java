import java.awt.*;

public class BossProj extends GameObject{

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private final Update update;
    float speed = 6;

    public BossProj(int x, int y, ID id, Update update, SpriteSheet sheet) {
        super(x, y, id, spriteSheet, sheet);
        this.update = update;

    }

    public void tick() {
        x += velX;
        y += velY;

        for (int i = 0; i < update.object.size(); i++) {
            GameObject tempObject = update.object.get(i);

            if (tempObject.getId() == ID.Player) {
                float dx = tempObject.x - x;
                float dy = tempObject.y - y;
                double distance = Math.pow(((dx*dx)+(dy*dy)),0.5);

                velX = (dx/((float)distance)) * speed;
                velY = (dy/((float)distance)) * speed;

                velX = clamp((int)velX, -speed, speed);
                velY = clamp((int)velY, -speed, speed);
            }

            if (tempObject.getId() == ID.Block) {
                if (getBounds().intersects(tempObject.getBounds())) {
                    update.removeObject(this);
                }
            }
        }
    }

    public void render(Graphics g) {
        g.setColor(Color.green);
        g.fillOval(x, y, 16, 16);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 16, 16);
    }
}
