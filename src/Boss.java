import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Boss extends GameObject{

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private final Update update;
    static float hp = 0;
    static float max_hp = 0;
    float speed = 0;
    float maxSpeed = 0;
    boolean alive = true;
    static boolean bossAlive = false;

    private BufferedImage enemy_image;
    private BufferedImage enemy_mirror_image;
    private BufferedImage bossItem_Sheet = null;
    BufferedImageLoader loader = new BufferedImageLoader();

    public Boss(int x, int y, ID id, Update update, SpriteSheet spriteSheet, SpriteSheet sheet) {
        super(x, y, id, spriteSheet, sheet);
        this.update = update;

        if (this.getId() == ID.Boss_Lich) {
            enemy_image = spriteSheet.grabImage(0.6875, 3.625, 64, 64);
            enemy_mirror_image = sheet.grabImage(0.6875, 3.625, 64, 64);
            hp = 1000 + (1000*Game.stage);
            speed = (float) (2.5f + (0.06*Game.stage));
            max_hp = 1000 + (1000*Game.stage);
            bossAlive = true;
        } else if (this.getId() == ID.Boss_Pharaoh) {
            enemy_image = spriteSheet.grabImage(4.25, 3.625, 52, 64);
            enemy_mirror_image = sheet.grabImage(4.25, 3.625, 52, 64);
            hp = 1000 + (1500*Game.stage);
            speed = (float) (3.5f + (0.1*Game.stage));
            max_hp = 1000 + (1500*Game.stage);
            maxSpeed = (speed + 4);
            bossAlive = true;
        }

        if (this.getId() == ID.Boss_Lich) {
            Runnable summonProj = new Runnable() {
                public void run() {
                    if (alive) {
                        update.addObject(new BossProj(getX(), getY(), ID.Boss_Proj, update, spriteSheet));
                    }
                }
            };

            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(summonProj, 0, 5, TimeUnit.SECONDS);
        }

        if (this.getId() == ID.Boss_Pharaoh) {
            Runnable speedUp = new Runnable() {
                public void run() {
                    if ((speed + 2) < maxSpeed) {
                        speed += 2;
                    } else if ((speed + 2) > maxSpeed) {
                        speed = (maxSpeed - 0.1f);
                    }
                }
            };

            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(speedUp, 3000, 1000, TimeUnit.MILLISECONDS);
        }

        if (this.getId() == ID.Boss_Pharaoh) {
            Runnable speedDown = new Runnable() {
                public void run() {
                    if (speed > 2) {
                        speed -= 2;
                    } else if (speed <= 2) {
                        speed = 0.5f;
                    }
                }
            };

            ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(speedDown, 3000, 1000, TimeUnit.MILLISECONDS);
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
            Random random = new Random();
            int randomItem;

            randomItem = random.nextInt(3);
            if (randomItem == 0) {
                bossItem_Sheet = loader.loadImage("/chunchunmaru.png");
            } else if (randomItem == 1) {
                bossItem_Sheet = loader.loadImage("/randomcursedsword.png");
            } else if (randomItem == 2) {
                bossItem_Sheet = loader.loadImage("/excalisomething.png");
            }
            SpriteSheet bossItem = new SpriteSheet(bossItem_Sheet);

            if (randomItem == 0) {
                update.addObject(new Item(x, y, ID.Item_Chunchunmaru, bossItem));
            } else if (randomItem == 1) {
                update.addObject(new Item(x, y, ID.Item_Randomcursedsword, bossItem));
            } else if (randomItem == 2) {
                update.addObject(new Item(x, y, ID.Item_Excalisomething, bossItem));
            }

            alive = false;
            bossAlive = false;
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
                        if (this.getId() == ID.Boss_Lich) {
                            x = tempObject.getX() - 64;
                        } else if (this.getId() == ID.Boss_Pharaoh) {
                            x = tempObject.getX() - 52;
                        }

                    } else if (velX < 0) { // left

                        velX = 0;
                        x = (int) (tempObject.getX() + tempObject.getW());
                    }
                }

                if(getBounds2().intersects(tempObject.getBounds())) {
                    if (velY > 0) { // down

                        velY = 0;
                        y = tempObject.getY() - 64;

                    } else if (velY < 0) { //up

                        velY = 0;
                        y = (int) (tempObject.getY() + tempObject.getH());

                    }
                }
            }
        }
    }

    public static void heal() {
        hp += 500;
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

        if (this.getId() == ID.Boss_Lich) {
            bx = x + velX;
            by = y;
            bw = 64 + velX / 2;
            bh = 64;
        } else if (this.getId() == ID.Boss_Pharaoh) {
            bx = x + velX;
            by = y;
            bw = 52 + velX / 2;
            bh = 64;
        }

        return new Rectangle((int)bx, (int)by, (int)bw, (int)bh);
    }

    public Rectangle getBounds2() {
        float bx = 0;
        float by = 0;
        float bw = 0;
        float bh = 0;

        if (this.getId() == ID.Boss_Lich) {
            bx = x;
            by = y + velY;
            bw = 64;
            bh = 64 + velY / 2;
        } else if (this.getId() == ID.Boss_Pharaoh) {
            bx = x;
            by = y + velY;
            bw = 52;
            bh = 64 + velY / 2;
        }

        return new Rectangle((int)bx, (int)by, (int)bw, (int)bh);
    }
}
