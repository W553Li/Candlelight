import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MouseInput extends MouseAdapter {

    private Update update;
    private Camera camera;
    private Game game;
    private SpriteSheet spriteSheet;
    public static boolean isSwingingRight;
    public static boolean isSwingingLeft;

    public MouseInput(Update update, Camera camera, Game game, SpriteSheet spriteSheet) {
        this.update = update;
        this.camera = camera;
        this.game = game;
        this.spriteSheet = spriteSheet;
    }

    public void mousePressed(MouseEvent e) {
        if (Game.state == Game.State.GAME) {
            if (SwingUtilities.isRightMouseButton(e)) {
                if (Game.mana > 10) {
                    int mx = (int) (e.getX() + camera.getX());
                    int my = (int) (e.getY() + camera.getY());

                    for (int i = 0; i < update.object.size(); i++) {
                        GameObject tempObject = update.object.get(i);

                        if (tempObject.getId() == ID.Player) {
                            update.addObject(new Arrow(tempObject.getX() + 10, tempObject.getY() + 10, ID.Arrow, update, mx, my, spriteSheet));
                        }
                    }
                    Game.mana -= 10;
                }
            }
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (Game.slash_time == 10) {
                    for (int i = 0; i < update.object.size(); i++) {
                        GameObject tempObject = update.object.get(i);

                        if (tempObject.getId() == ID.Player) {
                            int mx = e.getX();
                            int my = e.getY();
                            if (mx <= 423 && my <= 240) { // top left
                                update.addObject(new Slash(tempObject.getX() - 15, tempObject.getY() - 20, ID.Slash, update, spriteSheet));
                                isSwingingLeft = true;
                            } else if (mx > 423 && mx <= 846 && my <= 240) { // top middle
                                update.addObject(new Slash(tempObject.getX() + 5, tempObject.getY() - 20, ID.Slash, update, spriteSheet));
                                isSwingingRight = true;
                            } else if (mx > 846 && mx <= 1280 && my <= 240) { // top right
                                update.addObject(new Slash(tempObject.getX() + 25, tempObject.getY() - 20, ID.Slash, update, spriteSheet));
                                isSwingingRight = true;
                            } else if (mx <= 640 && my > 240 && my <= 480) { // middle left
                                update.addObject(new Slash(tempObject.getX() - 15, tempObject.getY(), ID.Slash, update, spriteSheet));
                                isSwingingLeft = true;
                            } else if (mx > 640 && my > 240 && my <= 480) { // middle right
                                update.addObject(new Slash(tempObject.getX() + 25, tempObject.getY(), ID.Slash, update, spriteSheet));
                                isSwingingRight = true;
                            } else if (mx <= 423 && my >= 480) { // bottom left
                                update.addObject(new Slash(tempObject.getX() - 15, tempObject.getY() + 20, ID.Slash, update, spriteSheet));
                                isSwingingLeft = true;
                            } else if (mx > 423 && mx <= 846 && my >= 480) { // bottom middle
                                update.addObject(new Slash(tempObject.getX() + 5, tempObject.getY() + 20, ID.Slash, update, spriteSheet));
                                isSwingingRight = true;
                            } else if (mx > 846 && mx <= 1280 && my >= 480) { // bottom right
                                update.addObject(new Slash(tempObject.getX() + 25, tempObject.getY() + 20, ID.Slash, update, spriteSheet));
                                isSwingingRight = true;
                            }
                        }
                    }
                    Game.slash_time -= 10;
                    Runnable slash = new Runnable() {
                        public void run() {
                            MouseInput.isSwingingRight = false;
                            MouseInput.isSwingingLeft = false;
                            for (int i = 0; i < update.object.size(); i++) {
                                GameObject tempObject = update.object.get(i);

                                if (tempObject.getId() == ID.Slash) {
                                    update.removeObject(tempObject);
                                }
                            }
                        }
                    };

                    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
                    executor.scheduleAtFixedRate(slash, 100, 1000, TimeUnit.MILLISECONDS);
                }
            }
        }
    }
}
