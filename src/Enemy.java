import java.awt.*;
import java.awt.image.BufferedImage;

public class Enemy extends GameObject {

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private final Update update;
    int hp = 0;
    float speed = 0;

    private BufferedImage enemy_image;
    private BufferedImage enemy_mirror_image;

    public Enemy(int x, int y, ID id, Update update, SpriteSheet spriteSheet, SpriteSheet spriteSheet2) {
        super(x, y, id, spriteSheet, spriteSheet);
        this.update = update;

        if (this.getId() == ID.Enemy_Ghoul) {
            enemy_image = spriteSheet.grabImage(1.4, 0, 20, 36);
            enemy_mirror_image = spriteSheet2.grabImage(1.4, 0, 20, 36);
            hp = 100 + (40*Game.stage);
            speed = (float) (3.5f + (0.1*Game.stage));
        } else if (this.getId() == ID.Enemy_Bat) {
            enemy_image = spriteSheet.grabImage(0.125, 1.5, 28, 20);
            enemy_mirror_image = spriteSheet2.grabImage(0.125, 1.5, 28, 20);
            hp = 50 + (20*Game.stage);
            speed = (float) (4.5f + (0.135*Game.stage));
        } else if (this.getId() == ID.Enemy_Robed) {
            enemy_image = spriteSheet.grabImage(2.5, 1.25, 20, 30);
            enemy_mirror_image = spriteSheet2.grabImage(2.5, 1.25, 20, 30);
            hp = 125 + (50*Game.stage);
            speed = (float) (3f + (0.075*Game.stage));
        } else if (this.getId() == ID.Enemy_Warrior) {
            enemy_image = spriteSheet.grabImage(4.6, 1.25, 28, 32);
            enemy_mirror_image = spriteSheet2.grabImage(4.6, 1.25, 28, 32);
            hp = 150 + (60*Game.stage);
            speed = (float) (2.5f + (0.06*Game.stage));
        } else if (this.getId() == ID.Enemy_Zombie) {
            enemy_image = spriteSheet.grabImage(5.875, 2.375, 20, 32);
            enemy_mirror_image = spriteSheet2.grabImage(5.875, 2.375, 20, 32);
            hp = 100 + (40*Game.stage);
            speed = (float) (4f + (0.12*Game.stage));
        }
    }

    public void tick() {
        x += velX;
        y += velY;

        for (int i=0; i<update.object.size(); i++) {
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

            if (tempObject.getId() == ID.Arrow) {
                if (getBounds().intersects(tempObject.getBounds())) {
                    hp -= Game.magic;
                    update.removeObject(tempObject);
                }
            }

            if (tempObject.getId() == ID.Slash) {
                if (getBounds().intersects(tempObject.getBounds())) {
                    hp -= Game.attack;
                    update.removeObject(tempObject);
                }
            }

            if (tempObject.getId() == ID.Special) {
                if (getBounds().intersects(tempObject.getBounds())) {
                    hp -= Game.attack;
                }
            }
        }

        collision();

        if (hp <= 0) {
            if (Game.mana > 70) {
                Game.mana = 90;
            } else if (Game.mana <= 70) {
                Game.mana += 20;
            }
            update.removeObject(this);
        }
    }

    private void collision() {
        for (int i = 0; i < update.object.size(); i++) {
            GameObject tempObject = update.object.get(i);

            if (tempObject.getId() == ID.Block) {

                if(getBounds().intersects(tempObject.getBounds())) {
                    if (velX > 0) { // right

                        velX = 0;

                        if (this.getId() == ID.Enemy_Ghoul || this.getId() == ID.Enemy_Zombie || this.getId() == ID.Enemy_Robed) {
                            x = tempObject.getX() - 20;
                        } else if (this.getId() == ID.Enemy_Bat || this.getId() == ID.Enemy_Warrior) {
                            x = tempObject.getX() - 28;
                        }

                    } else if (velX < 0) { // left

                        velX = 0;
                        x = (int) (tempObject.getX() + tempObject.getW());
                    }
                }

                if(getBounds2().intersects(tempObject.getBounds())) {
                    if (velY > 0) { // down

                        velY = 0;

                        if (this.getId() == ID.Enemy_Ghoul) {
                            y = tempObject.getY() - 36;
                        } else if (this.getId() == ID.Enemy_Bat) {
                            y = tempObject.getY() - 20;
                        } else if (this.getId() == ID.Enemy_Zombie) {
                            y = tempObject.getY() - 32;
                        } else if (this.getId() == ID.Enemy_Warrior) {
                            y = tempObject.getY() - 32;
                        } else if (this.getId() == ID.Enemy_Robed) {
                            y = tempObject.getY() - 30;
                        }

                    } else if (velY < 0) { //up

                        velY = 0;
                        y = (int) (tempObject.getY() + tempObject.getH());

                    }
                }
            }
        }
    }

    public void render(Graphics g) {
        if (velX >= 0) {
            g.drawImage(enemy_image, x, y, null);
        } else if (velX < 0) {
            g.drawImage(enemy_mirror_image, x, y, null);
        }
    }

    public Rectangle getBounds() {
        float bx = 0;
        float by = 0;
        float bw = 0;
        float bh = 0;

        if (this.getId() == ID.Enemy_Ghoul) {
            bx = x + velX;
            by = y;
            bw = 20 + velX / 2;
            bh = 36;
        } else if (this.getId() == ID.Enemy_Bat) {
            bx = x + velX;
            by = y;
            bw = 28 + velX / 2;
            bh = 20;
        } else if (this.getId() == ID.Enemy_Zombie) {
            bx = x + velX;
            by = y;
            bw = 20 + velX / 2;
            bh = 32;
        } else if (this.getId() == ID.Enemy_Warrior) {
            bx = x + velX;
            by = y;
            bw = 28 + velX / 2;
            bh = 32;
        } else if (this.getId() == ID.Enemy_Robed) {
            bx = x + velX;
            by = y;
            bw = 20 + velX / 2;
            bh = 30;
        }

        return new Rectangle((int)bx, (int)by, (int)bw, (int)bh);
    }

    public Rectangle getBounds2() {
        float bx = 0;
        float by = 0;
        float bw = 0;
        float bh = 0;

        if (this.getId() == ID.Enemy_Ghoul) {
            bx = x;
            by = y + velY;
            bw = 20;
            bh = 36 + velY / 2;
        } else if (this.getId() == ID.Enemy_Bat) {
            bx = x;
            by = y + velY;
            bw = 28;
            bh = 20 + velY / 2;
        } else if (this.getId() == ID.Enemy_Zombie) {
            bx = x;
            by = y + velY;
            bw = 20;
            bh = 32 + velY / 2;
        } else if (this.getId() == ID.Enemy_Warrior) {
            bx = x;
            by = y + velY;
            bw = 28;
            bh = 32 + velY / 2;
        } else if (this.getId() == ID.Enemy_Robed) {
            bx = x;
            by = y + velY;
            bw = 20;
            bh = 30 + velY / 2;
        }

        return new Rectangle((int)bx, (int)by, (int)bw, (int)bh);
    }
}
