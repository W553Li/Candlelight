import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

public class Special extends GameObject{

    private Update update;

    public Special(int x, int y, ID id, Update update, SpriteSheet spriteSheet) {
        super(x, y, id, spriteSheet, spriteSheet);
        this.update = update;
    }


    public void tick() {
        TimerTask task = new TimerTask() {
            public void run() {
                Hero.specialActive = false;
                update.removeObject(Special.this);
            }
        };
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(task, 200, 2500);
    }


    public void render(Graphics g) {

    }


    public Rectangle getBounds() {
        return new Rectangle(x, y, 128, 128);
    }
}
