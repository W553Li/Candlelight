import java.awt.*;
import java.awt.image.BufferedImage;

public class Block extends GameObject {

    private BufferedImage wall_image;

    public Block(int x, int y, ID id, SpriteSheet spriteSheet) {
        super(x, y, id, spriteSheet, spriteSheet);
        w = 32;
        h = 32;

        wall_image = spriteSheet.grabImage(1, 0, 32, 32);
    }

    public void tick() {

    }

    public void render(Graphics g) {
        g.drawImage(wall_image, x, y, null);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }
}
