public class Camera {

    private float x, y;

    public Camera(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void tick(GameObject object) {

        x += ((object.getX() - x) - 1280/2) * 0.05f;
        y += ((object.getY() - y) - 720/2) * 0.05f;

        if (x <= 0) {
            x = 0;
        }

        // 784 if remove scale
        if (x >= 784) {
            x = 784;
        }

        if (y <= 0) {
            y = 0;
        }

        // 343 if remove scale
        if (y >= 343) {
            y = 343;
        }

    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
