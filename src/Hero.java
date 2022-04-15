import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Hero extends GameObject {

    private int hpRegen = 0;

    public static boolean specialActive = false;

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    static Update update;
    private final BufferedImage[] hero_image = new BufferedImage[3];
    private final BufferedImage[] heroMirror_image = new BufferedImage[3];
    private final BufferedImage[] heroSwingR_image = new BufferedImage[3];
    private final BufferedImage[] heroSwingL_image = new BufferedImage[3];
    private final BufferedImage[] special_image = new BufferedImage[5];

    Animation anim;
    Animation animMirror;
    Animation animSwing;
    Animation animSwingMirror;
    Animation animSpecial;

    public Hero(int x, int y, ID id, Update update, SpriteSheet spriteSheet, SpriteSheet spriteSheet2) {
        super(x, y, id, spriteSheet, spriteSheet);
        Hero.update = update;

        hero_image[0] = spriteSheet.grabImage(1.5, 0.5, 28, 48);
        hero_image[1] = spriteSheet.grabImage(5.375, 0.5, 32, 48);
        hero_image[2] = spriteSheet.grabImage(9.3125, 0.5, 36, 48);

        heroMirror_image[0] = spriteSheet.grabImage(17.625, 4.5, 28, 48);
        heroMirror_image[1] = spriteSheet.grabImage(21.5625, 4.5, 32, 48);
        heroMirror_image[2] = spriteSheet.grabImage(25.5, 4.5, 36, 48);

        heroSwingR_image[0] = spriteSheet.grabImage(13.6875, 0.5, 42, 48);
        heroSwingR_image[1] = spriteSheet.grabImage(17.5, 0.5, 44, 48);
        heroSwingR_image[2] = spriteSheet.grabImage(21.4375, 0.5, 32, 48);

        heroSwingL_image[0] = spriteSheet.grabImage(12.9375, 6.625, 50, 48);
        heroSwingL_image[1] = spriteSheet.grabImage(16.9375, 6.625, 60, 48);
        heroSwingL_image[2] = spriteSheet.grabImage(21.4375, 6.625, 36, 48);

        special_image[0] = spriteSheet2.grabImage(4.625, 4.625, 86, 44);
        special_image[1] = spriteSheet2.grabImage(0.8125, 4.625, 74, 44);
        special_image[2] = spriteSheet2.grabImage(5, 2.6875, 64, 44);
        special_image[3] = spriteSheet2.grabImage(0.8125, 4.625, 74, 44);
        special_image[4] = spriteSheet2.grabImage(4.625, 4.625, 86, 44);

        anim = new Animation(2, hero_image);
        animMirror = new Animation(2, heroMirror_image);
        animSwing = new Animation(2, heroSwingR_image);
        animSwingMirror = new Animation(2, heroSwingL_image);
        animSpecial = new Animation(1, special_image);

        Runnable regen = new Runnable() {
            public void run() {
                if ((Game.hero_hp + hpRegen) < 100) {
                    Game.hero_hp += hpRegen;
                } else if ((Game.hero_hp + hpRegen) > 100) {
                    Game.hero_hp = 100;
                }
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(regen, 10, 1, TimeUnit.SECONDS);
    }

    public void tick() {
        x += velX;
        y += velY;

        //movement control
        //vertical
        float acceleration = 1f;
        float deceleration = 0.5f;
        if (update.isUp()) {
            velY -= (acceleration + Game.speed);
        } else if (update.isDown()) {
            velY += (acceleration + Game.speed);
        } else if (!update.isUp() && !update.isDown()) {
            if (velY > 0) {
                velY -= deceleration;
            } else if (velY < 0) {
                velY += deceleration;
            }
        }

        //horizontal
        if (update.isRight()) {
            velX += (acceleration + Game.speed);
        } else if (update.isLeft()) {
            velX -= (acceleration + Game.speed);
        } else if (!update.isRight() && !update.isLeft()) {
            if (velX > 0) {
                velX -= deceleration;
            } else if (velX < 0) {
                velX += deceleration;
            }
        }

        velX = clamp((int)velX, -Game.speed, Game.speed);
        velY = clamp((int)velY, -Game.speed, Game.speed);

        collision();

        if (specialActive) {
            animSpecial.runAnimation();
        } else if (MouseInput.isSwingingRight) {
            animSwing.runAnimation();
        } else if (MouseInput.isSwingingLeft) {
            animSwingMirror.runAnimation();
        } else if (velX >= 0) {
            anim.runAnimation();
        } else if (velX < 0) {
            animMirror.runAnimation();
        }

        if(Game.hero_hp <= 0) {
            update.removeObject(this);
        }
    }

    public static void specialActivate() {
        specialActive = true;
        for (int i = 0; i < update.object.size(); i++) {
            GameObject tempObject = update.object.get(i);

            if (tempObject.getId() == ID.Player) {
                update.addObject(new Special(tempObject.getX()-40, tempObject.getY()-30, ID.Special, update, spriteSheet));
            }
        }
    }

    private void collision() {
        for (int i = 0; i < update.object.size(); i++) {
            GameObject tempObject = update.object.get(i);

            if (tempObject.getId() == ID.Block) {

                if(getBounds().intersects(tempObject.getBounds())) {
                    if (velX > 0) { // right

                        velX = 0;
                        x = tempObject.getX() - 28;

                    } else if (velX < 0) { // left

                        velX = 0;
                        x = (int) (tempObject.getX() + tempObject.getW());
                    }
                }

                if(getBounds2().intersects(tempObject.getBounds())) {
                    if (velY > 0) { // down

                        velY = 0;
                        y = tempObject.getY() - 48;
                    } else if (velY < 0) { //up

                        velY = 0;
                        y = (int) (tempObject.getY() + tempObject.getH());

                    }
                }
            }

            if (tempObject.getId() == ID.Item_Sword) {
                if (getBounds().intersects(tempObject.getBounds())) {
                    Game.attack += 10;
                    Game.specialDamage += 20;
                    update.removeObject(tempObject);
                }
            }

            if (tempObject.getId() == ID.Item_Ring) {
                if (getBounds().intersects(tempObject.getBounds())) {
                    Game.magic += 10;
                    Game.specialDamage += 20;
                    update.removeObject(tempObject);
                }
            }

            if (tempObject.getId() == ID.Item_Potion) {
                if (getBounds().intersects(tempObject.getBounds())) {
                    if (Game.hero_hp < 75) {
                        Game.hero_hp += 25;
                    } else if (Game.hero_hp >= 75) {
                        Game.hero_hp = 100;
                    }
                    update.removeObject(tempObject);
                }
            }

            if (tempObject.getId() == ID.Item_Armor) {
                if (getBounds().intersects(tempObject.getBounds())) {
                    Game.armor += 0.5;
                    update.removeObject(tempObject);
                }
            }

            if (tempObject.getId() == ID.Item_Boots) {
                if (getBounds().intersects(tempObject.getBounds())) {
                    Game.speed += 0.25;
                    update.removeObject(tempObject);
                }
            }

            if (tempObject.getId() == ID.Item_Manapotion) {
                if (getBounds().intersects(tempObject.getBounds())) {
                    if (Game.mana >= 50) {
                        Game.mana = 100;
                    } else if (Game.mana < 50) {
                        Game.mana += 50;
                    }
                    update.removeObject(tempObject);
                }
            }

            if (tempObject.getId() == ID.Item_Chunchunmaru) {
                if (getBounds().intersects(tempObject.getBounds())) {
                    if (Game.magicRegen < 50) {
                        Game.magicRegen += 10;
                    } else if (Game.magicRegen >= 50) {
                        Game.mana = 100;
                    }

                    update.removeObject(tempObject);
                }
            }

            if (tempObject.getId() == ID.Item_Excalisomething) {
                if (getBounds().intersects(tempObject.getBounds())) {
                    if (Game.specialRegen < 50) {
                        Game.specialRegen += 10;
                    } else if (Game.specialRegen >= 50) {
                        Game.special_time = 100;
                    }

                    update.removeObject(tempObject);
                }
            }

            if (tempObject.getId() == ID.Item_Randomcursedsword) {
                if (getBounds().intersects(tempObject.getBounds())) {
                    if (hpRegen < 50) {
                        hpRegen += 10;
                    }
                    update.removeObject(tempObject);
                }
            }

            if (tempObject.getId() == ID.Enemy_Zombie || tempObject.getId() == ID.Enemy_Ghoul || tempObject.getId() == ID.Enemy_Warrior || tempObject.getId() == ID.Enemy_Robed || tempObject.getId() == ID.Enemy_Bat || tempObject.getId() == ID.Boss_Lich || tempObject.getId() == ID.Boss_Pharaoh) {
                if (getBounds().intersects(tempObject.getBounds())) {
                    if (Game.hero_hp <= 100) {
                        if (tempObject.getId() == ID.Enemy_Ghoul) {
                            double ghoulDamage = (1 + (0.2 * Game.stage) - Game.armor);
                            if (ghoulDamage > 0) {
                                Game.hero_hp -= ghoulDamage;
                            } else if (ghoulDamage <= 0) {
                                Game.hero_hp -= 0.15;
                            }
                        } else if (tempObject.getId() == ID.Enemy_Bat) {
                            double batDamage = (0.5 + (0.1 * Game.stage) - Game.armor);
                            if (batDamage > 0) {
                                Game.hero_hp -= batDamage;
                            } else if (batDamage <= 0) {
                                Game.hero_hp -= 0.1;
                            }
                        } else if (tempObject.getId() == ID.Enemy_Warrior) {
                            double warriorDamage = (2 + (0.25 * Game.stage) - Game.armor);
                            if (warriorDamage > 0) {
                                Game.hero_hp -= warriorDamage;
                            } else if (warriorDamage <= 0) {
                                Game.hero_hp -= 0.25;
                            }
                        } else if (tempObject.getId() == ID.Enemy_Zombie) {
                            double zombieDamage = (0.75 + (0.1 * Game.stage) - Game.armor);
                            if (zombieDamage > 0) {
                                Game.hero_hp -= zombieDamage;
                            } else if (zombieDamage <= 0) {
                                Game.hero_hp -= 0.15;
                            }
                        } else if (tempObject.getId() == ID.Enemy_Robed) {
                            double robedDamage = (1.5 + (0.2 * Game.stage) - Game.armor);
                            if (robedDamage > 0) {
                                Game.hero_hp -= robedDamage;
                            } else if (robedDamage <= 0) {
                                Game.hero_hp -= 0.2;
                            }
                        } else if (tempObject.getId() == ID.Boss_Lich) {
                            double lichDamage = (2 + (0.5 * Game.stage) - Game.armor);
                            if (lichDamage > 0) {
                                Game.hero_hp -= lichDamage;
                            } else if (lichDamage <= 0) {
                                Game.hero_hp -= 2;
                            }
                        } else if (tempObject.getId() == ID.Boss_Pharaoh) {
                            double pharaohDamage = (1.5 + (0.2 * Game.stage) - Game.armor);
                            if (pharaohDamage > 0) {
                                Game.hero_hp -= pharaohDamage;
                            } else if (pharaohDamage <= 0) {
                                Game.hero_hp -= 1.5;
                            }
                        }
                    }
                }
            }

            if (tempObject.getId() == ID.Boss_Proj) {
                if (getBounds().intersects(tempObject.getBounds())) {
                    double bossProjDamage = (10 + (Game.stage) - Game.armor);
                    if (bossProjDamage > 0) {
                        Game.hero_hp -= bossProjDamage;
                    } else if (bossProjDamage <= 0) {
                        Game.hero_hp -= 10;
                    }
                    Boss.heal();
                    update.removeObject(tempObject);
                }
            }

            if (tempObject.getId() == ID.Door) {
                if (getBounds().intersects(tempObject.getBounds())) {
                    update.clearLevel();
                    Game.stage++;
                    Game.levelSelect();
                }
            }
        }
    }

    public Rectangle getBounds() {

        float bx = x + velX;
        float by = y;
        float bw = 28 + velX/2;
        float bh = 48;

        return new Rectangle((int)bx, (int)by, (int)bw, (int)bh);
    }

    public Rectangle getBounds2() {

        float bx = x;
        float by = y + velY;
        float bw = 28;
        float bh = 48 + velY/2;

        return new Rectangle((int)bx, (int)by, (int)bw, (int)bh);
    }

    public void render(Graphics g) {
        if (specialActive) {
            animSpecial.drawAnimation(g, x-30, y, 0);
        } else if (MouseInput.isSwingingRight) {
            animSwing.drawAnimation(g, x, y, 0);
        } else if (MouseInput.isSwingingLeft) {
            animSwingMirror.drawAnimation(g, x, y, 0);
        } else if (velX == 0 && velY == 0) {
            g.drawImage(hero_image[0], x, y, null);
        } else if (velX >= 0) {
            anim.drawAnimation(g, x, y, 0);
        } else if (velX < 0) {
            animMirror.drawAnimation(g, x, y, 0);
        }
    }
}