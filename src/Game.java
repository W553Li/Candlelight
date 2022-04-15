import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Game extends Canvas implements Runnable {

    private boolean isRunning = false;
    private Thread thread;
    private static Update update;
    private final Camera camera;
    private static SpriteSheet spriteSheet;
    private static SpriteSheet heroSheet;
    private static SpriteSheet enemySheet;
    private static SpriteSheet enemyMirrorSheet;
    private static SpriteSheet swordSheet;
    private static SpriteSheet ringSheet;
    private static SpriteSheet potionSheet;
    private static SpriteSheet armorSheet;
    private static SpriteSheet manapotionSheet;
    private static SpriteSheet bootsSheet;
    private static SpriteSheet specialSheet;
    public static int attack = 10;
    public static int magic = 10;
    public static int magicRegen = 5;
    public static int specialRegen = 5;
    public static float armor = 0;
    public static int specialDamage = 100;
    public static float speed = 4;
    public static boolean armorStack = false;
    public static int hero_hp = 100;
    public static int mana = 100;
    public static int slash_time = 10;
    public static int special_time = 100;
    public static int stage = 1;

    private static BufferedImage level = null;
    private final BufferedImage floor;
    private final BufferedImage background;

    public enum State {
        MENU,
        GAME,
    }

    public static State state = State.MENU;

    Menu menu = new Menu();

    public Game() {
        new Window(1280, 720, "Candlelight", this);

        update = new Update();
        start();

        camera = new Camera(0,0);
        this.addKeyListener(new KeyInput(update));

        BufferedImageLoader loader = new BufferedImageLoader();

        // level = loader.loadImage("/game_level.png");
        BufferedImage sprite_sheet = loader.loadImage("/sprite_sheet.png");
        BufferedImage enemy_sheet = loader.loadImage("/enemy_sheet.png");
        BufferedImage hero_sheet = loader.loadImage("/hero_sheet.png");
        BufferedImage sword_sheet = loader.loadImage("/sword.png");
        BufferedImage ring_sheet = loader.loadImage("/ring.png");
        BufferedImage potion_sheet = loader.loadImage("/potion.png");
        BufferedImage armor_sheet = loader.loadImage("/armor.png");
        BufferedImage manapotion_sheet = loader.loadImage("/manapotion.png");
        BufferedImage boots_sheet = loader.loadImage("/boots.png");
        BufferedImage special_sheet = loader.loadImage("/special.png");
        BufferedImage background_sheet = loader.loadImage("/background_sheet.jpeg");
        BufferedImage enemy_mirror_sheet = loader.loadImage("/enemy_reverse_sheet.png");

        spriteSheet = new SpriteSheet(sprite_sheet);
        heroSheet = new SpriteSheet(hero_sheet);
        enemySheet = new SpriteSheet(enemy_sheet);
        enemyMirrorSheet = new SpriteSheet(enemy_mirror_sheet);
        swordSheet = new SpriteSheet(sword_sheet);
        ringSheet = new SpriteSheet(ring_sheet);
        potionSheet = new SpriteSheet(potion_sheet);
        armorSheet = new SpriteSheet(armor_sheet);
        manapotionSheet = new SpriteSheet(manapotion_sheet);
        bootsSheet = new SpriteSheet(boots_sheet);
        specialSheet = new SpriteSheet(special_sheet);
        SpriteSheet backgroundSheet = new SpriteSheet(background_sheet);

        floor = spriteSheet.grabImage(1, 1, 32, 32);
        background = backgroundSheet.grabImage(0,0, 1280,720);

        this.addMouseListener(new MouseInput(update, camera, this, spriteSheet));

        levelSelect();

        Runnable manaRecharge = new Runnable() {
            public void run() {
                if (mana < 100) {
                    mana += magicRegen;
                }
            }
        };

        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(manaRecharge, 0, 2, TimeUnit.SECONDS);

        Runnable slashRecharge = new Runnable() {
            public void run() {
                if (slash_time < 10) {
                    slash_time += 5;
                }
            }
        };

        executor.scheduleAtFixedRate(slashRecharge, 0, 1, TimeUnit.SECONDS);


        Runnable specialRecharge = new Runnable() {
            public void run() {
                if (special_time < 100) {
                    special_time += specialRegen;
                }
            }
        };

        executor.scheduleAtFixedRate(specialRecharge, 0, 3, TimeUnit.SECONDS);
    }

    private void start() {
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    private void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        // this.requestFocus();
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while(delta >= 1) {
                tick();
                delta--;
            }
            // if (isRunning) {
                render();
                frames++;

                if (System.currentTimeMillis() - timer > 1000) {
                    timer += 1000;
                    frames = 0;
                }
            // }
        }
        stop();
    }

    public void tick() {
        if (state == State.GAME) {
            update.tick();

            for (int i = 0; i < update.object.size(); i++) {
                if (update.object.get(i).getId() == ID.Player) {
                    camera.tick(update.object.get(i));
                }
            }
        }
    }

    public void render() {
        BufferStrategy bufferStrategy = this.getBufferStrategy();
        if (bufferStrategy == null) {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bufferStrategy.getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g;

        // Background and Camera
        if (state == State.GAME) {
            g2d.translate(-camera.getX(), -camera.getY());

            for (int xx = 0; xx < 30 * 72; xx += 32) {
                for (int yy = 0; yy < 30 * 72; yy += 32) {
                    g.drawImage(floor, xx, yy, null);
                }
            }

            update.render(g);

            g2d.translate(camera.getX(), camera.getY());

            g.setColor((Color.gray));
            g.fillRect(5, 5, 200, 32);
            g.setColor((Color.green));
            g.fillRect(5, 5, hero_hp * 2, 32);
            g.setColor((Color.black));
            g.drawRect(5, 5, 200, 32);

            g.setColor((Color.gray));
            g.fillRect(5, 40, 200, 32);
            g.setColor((Color.blue));
            g.fillRect(5, 40, mana * 2, 32);
            g.setColor((Color.black));
            g.drawRect(5, 40, 200, 32);

            g.setColor((Color.gray));
            g.fillRect(5, 75, 100, 32);
            g.setColor((Color.white));
            g.fillRect(5, 75, special_time, 32);

            if (Boss.bossAlive) {
                g.setColor((Color.gray));
                g.fillRect(390, 600, 500, 32);
                g.setColor((Color.red));
                g.fillRect(390, 600,  (int) ((Boss.hp/Boss.max_hp) * 500), 32);
                g.setColor((Color.black));
                g.drawRect(390, 600, 500, 32);
            }


            if (hero_hp <= 0) {
                g.setColor(Color.white);
                g.drawString("You have died! Please restart the game", 550, 360);
            }

        } else if (state == State.MENU) {
            g.drawImage(background, 0, 0, null);
            menu.render(g);
        }

        ///////////////////////

        g.dispose();
        bufferStrategy.show();
    }

    public static void levelSelect() {
        Random r = new Random();
        int choose;
        BufferedImageLoader loader = new BufferedImageLoader();

        armorStack = false;
        update.clearLevel();

        if ((stage % 5) == 0) {
            level = loader.loadImage("/boss_level.png");
        } else {
            choose = r.nextInt(9);
            if (choose == 0) {
                level = loader.loadImage("/game_level1.png");
            } else if (choose == 1) {
                level = loader.loadImage("/game_level2.png");
            } else if (choose == 2) {
                level = loader.loadImage("/game_level3.png");
            } else if (choose == 3) {
                level = loader.loadImage("/game_level4.png");
            } else if (choose == 4) {
                level = loader.loadImage("/game_level5.png");
            } else if (choose == 5) {
                level = loader.loadImage("/game_level6.png");
            } else if (choose == 6) {
                level = loader.loadImage("/game_level7.png");
            } else if (choose == 7) {
                level = loader.loadImage("/game_level8.png");
            } else if (choose == 8) {
                level = loader.loadImage("/game_level9.png");
            }
        }

        System.out.println("Stage: " + Game.stage);
        loadLevel(level);
    }

    private static void loadLevel(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();

        Random random = new Random();
        int randomNum;

        for (int xx = 0; xx < w; xx++) {
            for (int yy = 0; yy < h; yy++) {
                int pixel = image.getRGB(xx, yy);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                if ((red == 255) && (green == 0) && (blue == 0)) {
                    update.addObject(new Block(xx * 32, yy * 32, ID.Block, spriteSheet));
                }

                if ((blue == 255) && (green == 0) && (red == 0)) {
                    update.addObject(new Hero(xx * 32, yy * 32, ID.Player, update, heroSheet, specialSheet));
                }

                if ((green == 255) && (blue == 0) && (red == 0)) {
                    randomNum = random.nextInt(5);
                    if (randomNum == 0) {
                        update.addObject(new Enemy(xx * 32, yy * 32, ID.Enemy_Ghoul, update, enemySheet, enemyMirrorSheet));
                    } else if (randomNum == 1) {
                        update.addObject(new Enemy(xx * 32, yy * 32, ID.Enemy_Bat, update, enemySheet, enemyMirrorSheet));
                    } else if (randomNum == 2) {
                        update.addObject(new Enemy(xx * 32, yy * 32, ID.Enemy_Robed, update, enemySheet, enemyMirrorSheet));
                    } else if (randomNum == 3) {
                        update.addObject(new Enemy(xx * 32, yy * 32, ID.Enemy_Warrior, update, enemySheet, enemyMirrorSheet));
                    } else if (randomNum == 4) {
                        update.addObject(new Enemy(xx * 32, yy * 32, ID.Enemy_Zombie, update, enemySheet, enemyMirrorSheet));
                    }
                }

                if ((green == 200) && (blue == 200) && (red == 0)) {
                    randomNum = random.nextInt(6);
                    if (randomNum == 0) {
                        update.addObject(new Item(xx * 32, yy * 32, ID.Item_Sword, swordSheet));
                    } else if (randomNum == 1) {
                        update.addObject(new Item(xx * 32, yy * 32, ID.Item_Ring, ringSheet));
                    } else if (randomNum == 2) {
                        update.addObject(new Item(xx * 32, yy * 32, ID.Item_Potion, potionSheet));
                    } else if (randomNum == 3) {
                        update.addObject(new Item(xx * 32, yy * 32, ID.Item_Boots, bootsSheet));
                    } else if (randomNum == 4) {
                        update.addObject(new Item(xx * 32, yy * 32, ID.Item_Manapotion, manapotionSheet));
                    } else if (randomNum == 5 && !armorStack) {
                        update.addObject(new Item(xx * 32, yy * 32, ID.Item_Armor, armorSheet));
                        armorStack = true;
                    } else if (randomNum == 5 && armorStack) {
                        update.addObject(new Item(xx * 32, yy * 32, ID.Item_Potion, potionSheet));
                    }
                }

                if ((green == 50) && (blue == 50) && (red == 50)) {
                    randomNum = random.nextInt(2);
                    if (randomNum == 0) {
                        update.addObject(new Item(xx * 32, yy * 32, ID.Item_Potion, potionSheet));
                    } else if (randomNum == 1) {
                        update.addObject(new Item(xx * 32, yy * 32, ID.Item_Manapotion, manapotionSheet));
                    }
                }

                if ((green == 100) && (blue == 100) && (red == 100)) {
                    randomNum = random.nextInt(2);
                    if (randomNum == 0) {
                        update.addObject(new Boss(xx * 32, yy * 32, ID.Boss_Lich, update, enemySheet, enemyMirrorSheet));
                    } else if (randomNum == 1) {
                        update.addObject(new Boss(xx * 32, yy * 32, ID.Boss_Pharaoh, update, enemySheet, enemyMirrorSheet));
                    }
                }

                if ((red == 255) && (green == 255) && (blue == 0)) {
                    update.addObject(new Door(xx * 32, yy * 32, ID.Door, spriteSheet));
                }
            }
        }
    }

    public static void main(String[] args) {
        new Game();
    }

}
