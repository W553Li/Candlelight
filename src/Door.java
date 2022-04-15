import java.awt.*;
import java.awt.image.BufferedImage;

public class Door extends GameObject {

    private BufferedImage sprite_image;

    public Door(int x, int y, ID id, SpriteSheet spriteSheet) {
        super(x, y, id, spriteSheet, spriteSheet);

        sprite_image = spriteSheet.grabImage(1, 8, 32, 32);
    }

    public void tick() {

    }

    public void render(Graphics g) {
        g.drawImage(sprite_image, x, y, null);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }
}
