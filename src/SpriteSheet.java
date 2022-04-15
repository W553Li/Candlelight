import java.awt.image.BufferedImage;

public class SpriteSheet {

    private BufferedImage image;

    public SpriteSheet(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage grabImage(double col, double row, int width, int height) {
        return image.getSubimage((int)(col*32), (int)(row*32), width, height);
    }

}
