import java.awt.*;

public class Slash extends GameObject{

    private Update update;

    public Slash(int x, int y, ID id, Update update, SpriteSheet spriteSheet) {
        super(x, y, id, spriteSheet, spriteSheet);
        this.update = update;
    }

    public void tick() {

    }

    public void render(Graphics g) {

    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 20, 48);
    }
}
