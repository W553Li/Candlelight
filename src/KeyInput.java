import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {

    Update update;

    public KeyInput(Update update) {
        this.update = update;
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        for (int i=0; i<update.object.size(); i++) {
            GameObject tempObject = update.object.get(i);

            if (tempObject.getId() == ID.Player) {
                if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) update.setUp(true);
                if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) update.setDown(true);
                if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) update.setLeft(true);
                if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) update.setRight(true);
                if (key == KeyEvent.VK_Z) {
                    if (Game.special_time == 100) {
                        Hero.specialActivate();
                        Game.special_time = 0;
                    }
                }
            }
        }
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        for (int i=0; i<update.object.size(); i++) {
            GameObject tempObject = update.object.get(i);

            if (tempObject.getId() == ID.Player) {
                if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) update.setUp(false);
                if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) update.setDown(false);
                if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) update.setLeft(false);
                if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) update.setRight(false);
            }
        }
    }
}
