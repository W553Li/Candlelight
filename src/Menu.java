import javax.accessibility.Accessible;
import javax.swing.*;
import java.awt.*;

public class Menu extends AbstractButton implements Accessible {

    public void render(Graphics g) {

        Font font1 = new Font("arial", Font.BOLD, 70);
        g.setFont(font1);
        g.setColor(Color.white);
        g.drawString("Candle Light", 420, 100);
    }
}
