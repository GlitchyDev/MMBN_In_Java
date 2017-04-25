package GlitchyDev;

import GlitchyDev.ChipMenu.Chip;
import GlitchyDev.ChipMenu.ChipMenuState;
import GlitchyDev.States.CanodumbStates;
import GlitchyDev.States.MegamanStates;
import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import GlitchyDev.Entities.Canodumb;
import GlitchyDev.Entities.Enemy;
import GlitchyDev.Entities.Megaman;
import GlitchyDev.Entities.Tile;
import GlitchyDev.States.GameState;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class Main extends Application {
    private Stage window;
    private long stateStartTime;
    private MediaPlayer backgroundMusic;
    private MediaPlayer soundEffects;

    private GameState gameState;
    // sprites
    private HashMap<String, Image> sprites = new HashMap<>();
    private ArrayList<String> currentKeyPresses = new ArrayList<String>();

    private ArrayList<Tile> tiles;
    private Megaman megaman;
    private ArrayList<Enemy> enemies;
    private ChipMenuState chipMenuState;
    private boolean customGaugeFilled = false;

    private long backGroundRenderTime = 0;

    private boolean suprise = false;


    // Sounds
    private final String Battle_Theme = "GameData/Sounds/Battle_Theme.mp3";
    private final String Main_Menu_Theme = "GameData/Sounds/Main_Menu_Music.wav";
    private final String New_Game_Effect = "GameData/Sounds/New_Game_Confirm.wav";
    private final String Main_Menu_Select = "GameData/Sounds/Main_Menu_Select.wav";
    private final String Start_Encounter_Effect = "GameData/Sounds/Start_Encounter.wav";
    private final String Custom_Screen_Open = "GameData/Sounds/Custom_Screen_Open.wav";
    private final String Gauge_Filled = "GameData/Sounds/Gauge_Filled.wav";

    private final String Chip_Select = "GameData/Sounds/Chip_Select.wav";
    private final String Enemy_Appears_Effect = "GameData/Sounds/Enemy_Appears_Effect.wav";




    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        primaryStage.setTitle("[Megaman Battle Network]");
        primaryStage.setResizable(false);
        primaryStage.setWidth(486);
        primaryStage.setHeight(349);
        Group root = new Group();
        Scene theScene = new Scene(root);
        primaryStage.setScene(theScene);
        Canvas canvas = new Canvas(480, 320);
        root.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        primaryStage.show();

        loadSprites();


        // Starting Game
        stateStartTime = System.nanoTime();
        gameState = GameState.TITLE_SCREEN_START;

        theScene.setOnKeyPressed(
                new EventHandler<KeyEvent>() {
                    public void handle(KeyEvent e) {
                        String key = e.getCode().toString();
                        if (!currentKeyPresses.contains(key)) {
                            currentKeyPresses.add(key);
                            proccessKeyInputs(key);
                        }
                    }
                });
        theScene.setOnKeyReleased(
                new EventHandler<KeyEvent>() {
                    public void handle(KeyEvent e) {
                        String key = e.getCode().toString();
                        currentKeyPresses.remove(key);
                        proccessKeyOutputs(key);
                    }
                });

        new AnimationTimer() {
            // This happens 60fps, but still check time with StateStartTime
            public void handle(long currentNanoTime) {
                // Clear Field
                gc.setFill(Color.WHITE);
                gc.setGlobalAlpha(1.0);

                switch (gameState) {
                    case TITLE_SCREEN_START:
                        doTitleScreenStart(gc, canvas);
                        break;
                    case TITLE_SCREEN_IDLE:
                        doTitleScreenIdle(gc, canvas);
                        break;
                    case TILE_SCREEN_MENU:
                        doTitleScreenMenu(gc, canvas);
                        break;
                    case TITLE_SCREEN_TRANSITION:
                        doTitleScreenMenuTransition(gc, canvas);
                        break;
                    case TILE_SCREEN_TRANSITION_2:
                        doTitleScreenMenuTransition2(gc, canvas);
                        break;
                    case BATTLE_START:
                        doBattleStart(gc, canvas);
                        break;
                    case ENTER_CHIP_MENU:
                        doEnterChipMenu(gc, canvas);
                        break;
                    case CHIP_MENU:
                        doChipMenu(gc,canvas);
                        break;
                    case CHIP_MENU_CONFIRM:
                        doChipMenuConfirm(gc,canvas);
                        break;
                    case BATTLE_RESUME:
                        doBattleResume(gc,canvas);
                        break;
                    case BATTLE:
                        doBattle(gc,canvas);
                        break;
                    case BATTLE_LOSE:
                        doBattleLose(gc,canvas);
                        break;
                    case BATTLE_GAME_OVER:
                        doGameOver(gc,canvas);
                        break;
                    case BATTLE_WIN:
                        doBattleWin(gc,canvas);
                        break;
                    case BETA_SCREEN:
                        doBetaScreen(gc,canvas);
                        break;

                }
            }
        }.start();
    }

    public void loadSprites() {

        System.out.println("Loading all Registered Folders");
        System.out.println("Processing Sprites");
        ArrayList<String> registeredFolders = new ArrayList<>(Arrays.asList(
                "GameData/Sprites/BattleAssets/",
                "GameData/Sprites/Canodumb/",
                "GameData/Sprites/ChipMenuAssets/",
                "GameData/Sprites/Megaman/",
                "GameData/Sprites/Menu/",
                "GameData/Sprites/Tiles/",
                "GameData/Sprites/Other/"
        ));

        for (String currentFolder : registeredFolders) {
            File startingFolder = new File(currentFolder);
            for (File file : startingFolder.listFiles()) {
                if (file.isFile()) {

                    String temp = file.getName().substring(0, file.getName().length() - 4);
                    sprites.put(temp, new Image("file:" + currentFolder + file.getName()));
                    System.out.println("  - " + temp);
                }
            }
        }
        System.out.println("Processing Complete!");
    }




    public void doTitleScreenStart(GraphicsContext gc, Canvas canvas) {
        double progressedSeconds = (System.nanoTime() - stateStartTime) / 1000000000.0;

        gc.setGlobalAlpha(1.0);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        // Draw Capcom logo at increasing and decreasing opacity

        double imageWidth = sprites.get("Capcom_Logo").getWidth() * 2;
        double imageHeight = sprites.get("Capcom_Logo").getHeight() * 2;

        if (progressedSeconds <= 1.0) {
            gc.setGlobalAlpha(progressedSeconds / 1.0);
            gc.drawImage(sprites.get("Capcom_Logo"), canvas.getWidth() / 2 - imageWidth / 2, canvas.getHeight() / 2 - imageHeight / 2, imageWidth, imageHeight);
        } else {
            gc.setGlobalAlpha(1 - (progressedSeconds - 1 / 1.0));
            gc.drawImage(sprites.get("Capcom_Logo"), canvas.getWidth() / 2 - imageWidth / 2, canvas.getHeight() / 2 - imageHeight / 2, imageWidth, imageHeight);
        }


        if (progressedSeconds >= 2.0) {
            gc.setGlobalAlpha(1.0);
            gameState = GameState.TITLE_SCREEN_IDLE;
            Media sound = new Media(new File(Main_Menu_Theme).toURI().toString());
            backgroundMusic = new MediaPlayer(sound);
            backgroundMusic.setOnEndOfMedia(new Runnable() {
                public void run() {
                    backgroundMusic.seek(Duration.ZERO);
                }
            });
            backgroundMusic.play();
            stateStartTime = System.nanoTime();

        }
    }

    public void doTitleScreenIdle(GraphicsContext gc, Canvas canvas) {
        double progressedSeconds = (System.nanoTime() - stateStartTime) / 1000000000.0;
        double backgroundModRate = 1.6;
        double progressModBackground = (System.nanoTime()/ 1000000000.0) % backgroundModRate;
        double flashModRate = 0.5;
        double progressModNewGameFlash = progressedSeconds % flashModRate;

        gc.setGlobalAlpha(1.0);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (progressedSeconds <= 0.1) {
            gc.setGlobalAlpha(progressedSeconds * 10);
        } else {
            gc.setGlobalAlpha(1.0);
        }

        double backgroundWidth = sprites.get("Megaman_Title_Background").getWidth() * 2;
        double backgroundHeight = sprites.get("Megaman_Title_Background").getHeight() * 2;

        // Background
        gc.drawImage(sprites.get("Megaman_Title_Background"), progressModBackground * canvas.getWidth() / backgroundModRate, 0, backgroundWidth, backgroundHeight);
        gc.drawImage(sprites.get("Megaman_Title_Background"), progressModBackground * canvas.getWidth() / backgroundModRate - canvas.getWidth() + 1, 0, backgroundWidth, backgroundHeight);

        // Megaman Logo
        double megamanLogoWidth = sprites.get("Megaman_Logo").getWidth() * 2;
        double megamanLogoHeight = sprites.get("Megaman_Logo").getHeight() * 2;

        gc.drawImage(sprites.get("Megaman_Logo"), (canvas.getWidth() / 2) - megamanLogoWidth / 2, 2.0 * canvas.getHeight() / 16, megamanLogoWidth, megamanLogoHeight);


        double copyrightWidth = sprites.get("Copyright").getWidth() * 2;
        double copyrightHeight = sprites.get("Copyright").getHeight() * 2;

        gc.drawImage(sprites.get("Copyright"), 10, 12.5 * canvas.getHeight() / 16, copyrightWidth, copyrightHeight);


        double PressStartWidth = sprites.get("Press_Start").getWidth() * 2;
        double PressStartHeight = sprites.get("Press_Start").getHeight() * 2;


        if (progressModNewGameFlash <= 0.05) {
            gc.setGlobalAlpha(0);
        } else {
            if (progressModNewGameFlash <= 0.1) {
                gc.setGlobalAlpha(0.5);
            } else {

                if (progressModNewGameFlash >= 0.45) {
                    gc.setGlobalAlpha(0);
                } else {
                    gc.setGlobalAlpha(1.0);
                }
            }

        }
        gc.drawImage(sprites.get("Press_Start"), (canvas.getWidth() / 2) - PressStartWidth / 2, (canvas.getHeight() / 2) - PressStartHeight + 6.5 * (canvas.getHeight() / 16) / 2, PressStartWidth, PressStartHeight);
    }




    public void doTitleScreenMenu(GraphicsContext gc, Canvas canvas) {
        double progressedSeconds = (System.nanoTime() - stateStartTime) / 1000000000.0;
        double backgroundModRate = 1.6;
        double progressModBackground = (System.nanoTime()/ 1000000000.0) % backgroundModRate;

        gc.setGlobalAlpha(1.0);
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());


        double backgroundWidth = sprites.get("Megaman_Title_Background").getWidth() * 2;
        double backgroundHeight = sprites.get("Megaman_Title_Background").getHeight() * 2;

        // Background
        gc.drawImage(sprites.get("Megaman_Title_Background"), progressModBackground * canvas.getWidth() / backgroundModRate, 0, backgroundWidth, backgroundHeight);
        gc.drawImage(sprites.get("Megaman_Title_Background"), progressModBackground * canvas.getWidth() / backgroundModRate - canvas.getWidth() + 1, 0, backgroundWidth, backgroundHeight);

        // Megaman Logo
        double megamanLogoWidth = sprites.get("Megaman_Logo").getWidth() * 2;
        double megamanLogoHeight = sprites.get("Megaman_Logo").getHeight() * 2;

        gc.drawImage(sprites.get("Megaman_Logo"), (canvas.getWidth() / 2) - megamanLogoWidth / 2, 2.0 * canvas.getHeight() / 16, megamanLogoWidth, megamanLogoHeight);


        double copyrightWidth = sprites.get("Copyright").getWidth() * 2;
        double copyrightHeight = sprites.get("Copyright").getHeight() * 2;

        gc.drawImage(sprites.get("Copyright"), 10, 12.5 * canvas.getHeight() / 16, copyrightWidth, copyrightHeight);


        double NewGameWidth = sprites.get("New_Game").getWidth() * 2;
        double NewGameHeight = sprites.get("New_Game").getHeight() * 2;
        double cursorWidth = sprites.get("Main_Menu_Cursor").getWidth() * 2;
        double cursorHeight = sprites.get("Main_Menu_Cursor").getHeight() * 2;


        gc.drawImage(sprites.get("New_Game"), (canvas.getWidth() / 2) - NewGameWidth / 2, (canvas.getHeight() / 2) - NewGameHeight - 35 + 8.5 * (canvas.getHeight() / 16) / 2, NewGameWidth, NewGameHeight);
        gc.drawImage(sprites.get("Main_Menu_Cursor"), (canvas.getWidth() / 2) - NewGameWidth / 2 - 35 - 2 * Math.sin(10 * progressedSeconds * Math.PI), (canvas.getHeight() / 2) - NewGameHeight - 35 + 8.5 * (canvas.getHeight() / 16) / 2, cursorWidth, cursorHeight);

    }


    public void doTitleScreenMenuTransition(GraphicsContext gc, Canvas canvas) {
        double progressedSeconds = (System.nanoTime() - stateStartTime) / 1000000000.0;
        double backgroundModRate = 1.6;
        double progressModBackground = (System.nanoTime()/ 1000000000.0) % backgroundModRate;

        double backgroundWidth = sprites.get("Megaman_Title_Background").getWidth() * 2;
        double backgroundHeight = sprites.get("Megaman_Title_Background").getHeight() * 2;

        // Background
        gc.drawImage(sprites.get("Megaman_Title_Background"), progressModBackground * canvas.getWidth() / backgroundModRate, 0, backgroundWidth, backgroundHeight);
        gc.drawImage(sprites.get("Megaman_Title_Background"), progressModBackground * canvas.getWidth() / backgroundModRate - canvas.getWidth() + 1, 0, backgroundWidth, backgroundHeight);

        // Megaman Logo
        double megamanLogoWidth = sprites.get("Megaman_Logo").getWidth() * 2;
        double megamanLogoHeight = sprites.get("Megaman_Logo").getHeight() * 2;

        gc.drawImage(sprites.get("Megaman_Logo"), (canvas.getWidth() / 2) - megamanLogoWidth / 2, 2.0 * canvas.getHeight() / 16, megamanLogoWidth, megamanLogoHeight);


        double copyrightWidth = sprites.get("Copyright").getWidth() * 2;
        double copyrightHeight = sprites.get("Copyright").getHeight() * 2;

        gc.drawImage(sprites.get("Copyright"), 10, 12.5 * canvas.getHeight() / 16, copyrightWidth, copyrightHeight);


        if (progressedSeconds <= 1.5) {
            gc.setGlobalAlpha(progressedSeconds / 1.5);
            gc.setFill(Color.BLACK);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());


        }
        else
        {
            gc.setFill(Color.BLACK);
            gc.setGlobalAlpha(1.0);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }

        if (progressedSeconds > 5.0) {
            Media sound = new Media(new File(Start_Encounter_Effect).toURI().toString());
            soundEffects = new MediaPlayer(sound);
            soundEffects.play();
            gameState = GameState.TILE_SCREEN_TRANSITION_2;
            stateStartTime = System.nanoTime();

        }


    }

    public void doTitleScreenMenuTransition2(GraphicsContext gc, Canvas canvas) {
        double progressedSeconds = (System.nanoTime() - stateStartTime) / 1000000000.0;

        if (progressedSeconds <= 2.0) {
            gc.setGlobalAlpha(progressedSeconds / 2.0);
            gc.setFill(Color.WHITE);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        } else {
            gameState = GameState.BATTLE_START;
            stateStartTime = System.nanoTime();

            Media sound = new Media(new File(Battle_Theme).toURI().toString());
            backgroundMusic = new MediaPlayer(sound);
            backgroundMusic.setOnEndOfMedia(new Runnable() {
                public void run() {
                    backgroundMusic.seek(Duration.ZERO);
                }
            });
            backgroundMusic.play();
            CreateBattle();
        }
    }

    public void CreateBattle() {
        megaman = new Megaman(2, 2);
        enemies = new ArrayList<>();
        enemies.add(new Canodumb(6, 3, 1));
        enemies.add(new Canodumb(5, 2, 2));
        enemies.add(new Canodumb(6, 1, 3));
        tiles = new ArrayList<>();
        for (int x = 1; x <= 6; x++) {
            for (int y = 1; y <= 3; y++) {
                if (x > 3) {
                    tiles.add(new Tile(x, y, false));
                } else {
                    tiles.add(new Tile(x, y, true));

                }
            }
        }
    }


    public void doBattleStart(GraphicsContext gc, Canvas canvas) {
        double progressedSeconds = (System.nanoTime() - stateStartTime) / 1000000000.0;


        renderBattleBackground(gc,canvas);
        renderTiles(gc, canvas);

        megaman.doMegaman(gc,canvas,sprites,enemies,tiles);
        for(Enemy enemy: enemies)
        {
            enemy.doEnemy(gc,canvas,sprites,enemies,megaman,tiles);
        }
        if(megaman.getY() > 2)
        {
            megaman.doMegaman(gc,canvas,sprites,enemies,tiles);
        }

        renderHealthBar(gc,canvas,0);

        if(progressedSeconds >= enemies.size() * 0.6 )
        {
            gameState = GameState.ENTER_CHIP_MENU;
            stateStartTime = System.nanoTime();
            chipMenuState = new ChipMenuState();
            for(Enemy enemy: enemies) {
                enemy.fixSpawnin();
            }
            megaman.setCurrentState(MegamanStates.Megaman_Idle);

        }
    }

    public void doEnterChipMenu(GraphicsContext gc, Canvas canvas) {
        if(!megaman.paused)
        {
            megaman.paused = true;
            megaman.showChips = false;
            megaman.pauseStateTime = System.nanoTime();


            for(Enemy enemy: enemies)
            {
                enemy.paused = true;
                enemy.pauseStateTime = System.nanoTime();

            }

            for(Tile tile: tiles)
            {
                tile.paused = true;
                tile.pauseStateTime = System.nanoTime();
            }
        }


        double progressedSeconds = (System.nanoTime() - stateStartTime) / 1000000000.0;
        double xOffset = (progressedSeconds / 0.25) * 230;

        renderBattleBackground(gc,canvas);
        renderTiles(gc,canvas);
        renderHealthBar(gc,canvas,xOffset);

        megaman.doMegaman(gc,canvas,sprites,enemies,tiles);
        for(Enemy enemy: enemies)
        {
            enemy.doEnemy(gc,canvas,sprites,enemies,megaman,tiles);
        }
        for(Enemy enemy: enemies)
        {
            if(!enemy.getIsDead())
            {
                enemy.renderHP(gc, canvas, sprites);
            }
        }
        if(megaman.getY() > 2)
        {
            megaman.doMegaman(gc,canvas,sprites,enemies,tiles);
        }


        chipMenuState.doChipMenu(gc,canvas,xOffset,sprites);

        if(progressedSeconds >= 0.25)
        {
            gameState = GameState.CHIP_MENU;
            stateStartTime = System.nanoTime();

            Media sound = new Media(new File(Custom_Screen_Open).toURI().toString());
            soundEffects = new MediaPlayer(sound);
            soundEffects.play();

        }


    }

    public void doChipMenu(GraphicsContext gc, Canvas canvas) {

        double progressedSeconds = (System.nanoTime() - stateStartTime) / 1000000000.0;

        renderBattleBackground(gc,canvas);
        renderTiles(gc,canvas);
        renderHealthBar(gc,canvas,240);

        megaman.doMegaman(gc,canvas,sprites,enemies,tiles);
        for(Enemy enemy: enemies)
        {
            enemy.doEnemy(gc,canvas,sprites,enemies,megaman,tiles);
        }
        for(Enemy enemy: enemies)
        {
            if(!enemy.getIsDead())
            {
                enemy.renderHP(gc, canvas, sprites);
            }
        }
        if(megaman.getY() > 2)
        {
            megaman.doMegaman(gc,canvas,sprites,enemies,tiles);
        }


        chipMenuState.doChipMenu(gc,canvas,240,sprites);

        if(chipMenuState.loadChipsComplete)
        {
            gameState = GameState.CHIP_MENU_CONFIRM;
            stateStartTime = System.nanoTime();
        }
        if(chipMenuState.addChipsComplete)
        {
            // Add ( ADD CHIP ) logic
            gameState = GameState.CHIP_MENU_CONFIRM;
            stateStartTime = System.nanoTime();
        }

    }


    public void doChipMenuConfirm(GraphicsContext gc, Canvas canvas) {
        double progressedSeconds = (System.nanoTime() - stateStartTime) / 1000000000.0;
        renderBattleBackground(gc,canvas);
        renderTiles(gc,canvas);

        megaman.doMegaman(gc,canvas,sprites,enemies,tiles);
        for(Enemy enemy: enemies)
        {
            enemy.doEnemy(gc,canvas,sprites,enemies,megaman,tiles);
        }
        for(Enemy enemy: enemies)
        {
            if(!enemy.getIsDead())
            {
                enemy.renderHP(gc, canvas, sprites);
            }
        }
        if(megaman.getY() > 2)
        {
            megaman.doMegaman(gc,canvas,sprites,enemies,tiles);
        }

        chipMenuState.doChipMenu(gc,canvas,240 - (progressedSeconds/0.5)*240 ,sprites);
        if(progressedSeconds < 0.5) {
            renderHealthBar(gc, canvas, 240 - (progressedSeconds / 0.5) * 240);
        }
        else
        {
            renderHealthBar(gc, canvas, 0);

        }


        if(progressedSeconds >= 1.0)
        {
            stateStartTime = System.nanoTime();
            gameState = GameState.BATTLE_RESUME;



            megaman.showChips = true;

            if(chipMenuState.addChipsComplete)
            {
                if(chipMenuState.addCount != 3)
                {
                    chipMenuState.addCount++;
                    megaman.chips.clear();
                }
            }
            else
            {
                chipMenuState.addCount = 1;
                if(chipMenuState.chipMenu.size() > 5)
                {
                    ArrayList<Chip> temp = new ArrayList<>();
                    for(int i = 0; i < chipMenuState.chipMenu.size(); i++)
                    {
                        if(i < 5)
                        {
                            temp.add(chipMenuState.chipMenu.get(i));
                        }
                    }
                    chipMenuState.chipMenu = temp;
                }
                if(chipMenuState.currentlySelectedChips.size() != 0) {
                    megaman.chips.clear();
                    megaman.chips.addAll(chipMenuState.currentlySelectedChips);
                }

            }





        }

    }

    public void doBattleResume(GraphicsContext gc, Canvas canvas) {

        double progressedSeconds = (System.nanoTime() - stateStartTime) / 1000000000.0;
        renderBattleBackground(gc,canvas);
        renderTiles(gc,canvas);
        renderHealthBar(gc,canvas,0);

        double customGaugeWidth = sprites.get("Custom_Gauge_Idle").getWidth() * 2;
        double customGaugeHeight = sprites.get("Custom_Gauge_Idle").getHeight() * 2;

        gc.drawImage(sprites.get("Custom_Gauge_Idle"),100,0,customGaugeWidth,customGaugeHeight);


        megaman.doMegaman(gc,canvas,sprites,enemies,tiles);
        for(Enemy enemy: enemies)
        {
            enemy.doEnemy(gc,canvas,sprites,enemies,megaman,tiles);
        }
        for(Enemy enemy: enemies)
        {
            if(!enemy.getIsDead())
            {
                enemy.renderHP(gc, canvas, sprites);
            }
        }
        if(megaman.getY() > 2)
        {
            megaman.doMegaman(gc,canvas,sprites,enemies,tiles);
        }


        final double fadeInTime = 0.1;
        final double length = 1.0;
        if(progressedSeconds < length)
        {
            double battleStartWidth = sprites.get("Battle_Start").getWidth() * 2;
            double battleStartHeight = sprites.get("Battle_Start").getHeight() * 2;

            double redrawHeight = 0.0;
            if(progressedSeconds < fadeInTime)
            {
                redrawHeight = battleStartHeight * (progressedSeconds * (1.0/fadeInTime));
            }
            else
            {
                if(progressedSeconds > fadeInTime && progressedSeconds < length - fadeInTime)
                {
                    redrawHeight = battleStartHeight;
                }
                else
                {
                    redrawHeight = battleStartHeight - battleStartHeight * ((progressedSeconds - (length - fadeInTime)) * (1.0/fadeInTime));
                }
            }
            gc.drawImage(sprites.get("Battle_Start"),120,(canvas.getHeight() / 2) - redrawHeight / 2, battleStartWidth,redrawHeight);

        }

        if(progressedSeconds > length) {

            megaman.paused = false;
            megaman.showChips = true;
            megaman.resumeStateTime = System.nanoTime();
            for(Enemy enemy: enemies)
            {
                enemy.paused = false;
                enemy.resumeStateTime = System.nanoTime();

            }
            for(Tile tile: tiles)
            {
                tile.paused = false;
                tile.resumeStateTime = System.nanoTime();

            }
            gameState = GameState.BATTLE;
            stateStartTime = System.nanoTime();
        }


    }

    public void doBattle(GraphicsContext gc, Canvas canvas) {

        double progressedSeconds = (System.nanoTime() - stateStartTime) / 1000000000.0;
        renderBattleBackground(gc, canvas);
        renderTiles(gc, canvas);
        renderHealthBar(gc, canvas, 0);

        double customGaugeWidth = sprites.get("Custom_Gauge_Idle").getWidth() * 2;
        double customGaugeHeight = sprites.get("Custom_Gauge_Idle").getHeight() * 2;

        gc.drawImage(sprites.get("Custom_Gauge_Idle"), 100, 0, customGaugeWidth, customGaugeHeight);


        megaman.doMegaman(gc, canvas, sprites, enemies, tiles);
        for (Enemy enemy : enemies) {
            enemy.doEnemy(gc, canvas, sprites, enemies, megaman, tiles);
        }
        for (Enemy enemy : enemies) {
            if (!enemy.getIsDead()) {
                enemy.renderHP(gc, canvas, sprites);
            }
        }
        if (megaman.getY() > 2) {
            megaman.doMegaman(gc, canvas, sprites, enemies, tiles);
        }
        if (((Canodumb) enemies.get(1)).getState() == CanodumbStates.Canodumb_Pre_Shoot && megaman.getY() == 2)
        {
            megaman.doMegaman(gc, canvas, sprites, enemies, tiles);
        }



        final double fillGaugeTime = 10.0;
        if(progressedSeconds >= fillGaugeTime)
        {
            if(progressedSeconds % 0.30 > 0.15) {
                gc.drawImage(sprites.get("Custom_Gauge_Filled_1"), 100, 0, customGaugeWidth, customGaugeHeight);
            }
            else
            {
                gc.drawImage(sprites.get("Custom_Gauge_Filled_2"), 100, 0, customGaugeWidth, customGaugeHeight);
            }
            if(!customGaugeFilled)
            {
                customGaugeFilled = true;
                Media sound = new Media(new File(Gauge_Filled).toURI().toString());
                soundEffects = new MediaPlayer(sound);
                soundEffects.play();

            }

        }
        else {
            double modi = 1.28;
            int customGaugePercentage = (int)((progressedSeconds / fillGaugeTime) * 100 * modi * 2);
            double customGaugeFillerWidth = sprites.get("Custom_Gauge_Filler").getWidth();
            double customGaugeFillerHeight = sprites.get("Custom_Gauge_Filler").getHeight() * 2;

            for(int i = 1; i < customGaugePercentage; i++)
            {
                gc.drawImage(sprites.get("Custom_Gauge_Filler"), i * customGaugeFillerWidth + 115, 16, customGaugeFillerWidth,customGaugeFillerHeight);
            }
        }

        if(megaman.getCurrentState() == MegamanStates.Megaman_Idle || megaman.getCurrentState() == MegamanStates.Megaman_Firing) {
            if(megaman.getProgressedTime() > 0.1) {
                if (currentKeyPresses.contains("UP")) {
                    megaman.moveUp(tiles, enemies);
                }
                if (currentKeyPresses.contains("DOWN")) {
                    megaman.moveDown(tiles, enemies);
                }
                if (currentKeyPresses.contains("RIGHT")) {
                    megaman.moveRight(tiles, enemies);
                }
                if (currentKeyPresses.contains("LEFT")) {
                    megaman.moveLeft(tiles, enemies);
                }
            }
        }

        if(megaman.isDead)
        {
            gameState = GameState.BATTLE_LOSE;
            stateStartTime = System.nanoTime();
        }

        boolean someAlive = false;
        for(Enemy enemy: enemies)
        {
            if(!enemy.getIsDead())
            {
                someAlive = true;
            }
        }
        if(!someAlive)
        {
            gameState = GameState.BATTLE_WIN;
            stateStartTime = System.nanoTime();
        }

    }




    public void doBattleLose(GraphicsContext gc, Canvas canvas) {

        double progressedSeconds = (System.nanoTime() - stateStartTime) / 1000000000.0;
        renderBattleBackground(gc,canvas);
        renderTiles(gc,canvas);
        //renderHealthBar(gc,canvas,0);

        double customGaugeWidth = sprites.get("Custom_Gauge_Idle").getWidth() * 2;
        double customGaugeHeight = sprites.get("Custom_Gauge_Idle").getHeight() * 2;

        //gc.drawImage(sprites.get("Custom_Gauge_Idle"),100,0,customGaugeWidth,customGaugeHeight);


        for(Enemy enemy: enemies)
        {
            enemy.doEnemy(gc,canvas,sprites,enemies,megaman,tiles);
            if(!enemy.getIsDead())
            {
                enemy.renderHP(gc, canvas, sprites);
            }
        }



        final double fadeInTime = 0.1;
        final double length = 1.0;
        if(progressedSeconds < length)
        {
            double battleStartWidth = sprites.get("Megaman_Deleted").getWidth() * 2;
            double battleStartHeight = sprites.get("Megaman_Deleted").getHeight() * 2;

            double redrawHeight = 0.0;
            if(progressedSeconds < fadeInTime)
            {
                redrawHeight = battleStartHeight * (progressedSeconds * (1.0/fadeInTime));
            }
            else
            {
                if(progressedSeconds > fadeInTime && progressedSeconds < length - fadeInTime)
                {
                    redrawHeight = battleStartHeight;
                }
                else
                {
                    redrawHeight = battleStartHeight - battleStartHeight * ((progressedSeconds - (length - fadeInTime)) * (1.0/fadeInTime));
                }
            }
            gc.drawImage(sprites.get("Megaman_Deleted"),120,(canvas.getHeight() / 2) - redrawHeight / 2, battleStartWidth,redrawHeight);

        }

        if(progressedSeconds > length) {
            if(progressedSeconds > 1.5 && progressedSeconds < 2)
            {
                gc.setGlobalAlpha((1 -(2 - progressedSeconds) * 4));
                gc.setFill(Color.BLACK);
                gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
                gc.setGlobalAlpha(1.0);
            }
            else
            {
                if(progressedSeconds > 2.00) {
                    backgroundMusic.stop();
                    gc.setGlobalAlpha(1.0);
                    gc.setFill(Color.BLACK);
                    gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
                }

                if(progressedSeconds > 2.3)
                {
                    gameState = GameState.BATTLE_GAME_OVER;
                    stateStartTime = System.nanoTime();

                    Media sound = new Media(new File("GameData/Sounds/Game_Over.wav").toURI().toString());
                    backgroundMusic = new MediaPlayer(sound);
                    backgroundMusic.play();
                }


            }
            // gameState = GameState.BATTLE;
            // stateStartTime = System.nanoTime();
        }


    }



    public void doGameOver(GraphicsContext gc, Canvas canvas) {
        double progressedSeconds = (System.nanoTime() - stateStartTime) / 1000000000.0;

        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        gc.setGlobalAlpha(1.0);

        Image backgroundSprite = sprites.get("Game_Over_Background");
        double backgroundSpriteWidth = 2 * sprites.get("Game_Over_Background").getWidth();
        double backgroundSpriteHeight = 2 * sprites.get("Game_Over_Background").getHeight();

        double animationSpeed = 3;
        for(int x = -2; x <= canvas.getWidth() / backgroundSpriteWidth + 3; x++ )
        {
            for(int y = -2; y <= canvas.getHeight() / backgroundSpriteHeight + 3; y++ )
            {
                double percentage = (1 / animationSpeed) * (progressedSeconds % animationSpeed);
                gc.drawImage(backgroundSprite,x * backgroundSpriteWidth + backgroundSpriteWidth * percentage,y * backgroundSpriteHeight + backgroundSpriteHeight * percentage, backgroundSpriteWidth,backgroundSpriteHeight);
            }
        }

        double gameOverWidth = sprites.get("Game_Over").getWidth();
        double gameOverHeight = sprites.get("Game_Over").getHeight();

        gc.drawImage(sprites.get("Game_Over"), (canvas.getWidth() / 2) - (gameOverWidth / 2),(canvas.getHeight() / 2) - (gameOverHeight / 2),gameOverWidth,gameOverHeight);

        if(progressedSeconds < 1.00) {
            gc.setGlobalAlpha(1 - progressedSeconds);
            gc.setFill(Color.BLACK);
            gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        }


    }



    public void doBattleWin(GraphicsContext gc, Canvas canvas) {

        double progressedSeconds = (System.nanoTime() - stateStartTime) / 1000000000.0;

        renderBattleBackground(gc, canvas);
        renderTiles(gc, canvas);
        renderHealthBar(gc, canvas, 0);

        double customGaugeWidth = sprites.get("Custom_Gauge_Idle").getWidth() * 2;
        double customGaugeHeight = sprites.get("Custom_Gauge_Idle").getHeight() * 2;

        gc.drawImage(sprites.get("Custom_Gauge_Idle"), 100, 0, customGaugeWidth, customGaugeHeight);
        megaman.doMegaman(gc, canvas, sprites, enemies, tiles);


        if (progressedSeconds < 1) {
            final double fadeInTime = 0.1;
            final double length = 1.0;
            if (progressedSeconds < length) {
                double battleStartWidth = sprites.get("Enemy_Deleted").getWidth() * 2;
                double battleStartHeight = sprites.get("Enemy_Deleted").getHeight() * 2;

                double redrawHeight = 0.0;
                if (progressedSeconds < fadeInTime) {
                    redrawHeight = battleStartHeight * (progressedSeconds * (1.0 / fadeInTime));
                } else {
                    if (progressedSeconds > fadeInTime && progressedSeconds < length - fadeInTime) {
                        redrawHeight = battleStartHeight;
                    } else {
                        redrawHeight = battleStartHeight - battleStartHeight * ((progressedSeconds - (length - fadeInTime)) * (1.0 / fadeInTime));
                    }
                }
                gc.drawImage(sprites.get("Enemy_Deleted"), 120, (canvas.getHeight() / 2) - redrawHeight / 2, battleStartWidth, redrawHeight);
            }
        }
        else
        {
            if(progressedSeconds < 1.5)
            {
                gc.setGlobalAlpha(1 - (1.5 - progressedSeconds) * 2);
                gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
            }
            else
            {
                if(progressedSeconds > 2)
                {

                    backgroundMusic.stop();
                    Media sound = new Media(new File("GameData/Sounds/Thanks_For_Playing.wav").toURI().toString());
                    backgroundMusic = new MediaPlayer(sound);
                    backgroundMusic.play();
                    // Skip to reward scene
                    gameState = GameState.BETA_SCREEN;
                    stateStartTime = System.nanoTime();


                }
                gc.setGlobalAlpha(1);
                gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            }
        }



    }



    public void doBetaScreen(GraphicsContext gc, Canvas canvas) {
        double progressedSeconds = (System.nanoTime() - stateStartTime) / 1000000000.0;

        gc.setFill(Color.BLACK);
        gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        gc.setGlobalAlpha(1.0);
        gc.drawImage(sprites.get("Beta_Ending"),0,0,sprites.get("Beta_Ending").getWidth() * 2,sprites.get("Beta_Ending").getHeight() * 2);
        if(progressedSeconds < 1.00) {
            gc.setGlobalAlpha(1 - progressedSeconds);
            gc.setFill(Color.BLACK);
            gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        }
        else
        {
            if(suprise)
            {
                gc.setGlobalAlpha(1.0);
                gc.setFill(Color.WHITE);
                gc.fillRect(0,0,canvas.getWidth(),canvas.getHeight());


                gc.setGlobalAlpha(1.0);

                if(progressedSeconds % 1 < 0.3)
                {
                    gc.drawImage(sprites.get("Dad_1"), 0,0,canvas.getWidth(),canvas.getHeight());
                }
                else
                {
                    if(progressedSeconds % 1 < 0.6)
                    {
                        gc.drawImage(sprites.get("Dad_2"), 0,0,canvas.getWidth(),canvas.getHeight());
                    }
                    else
                    {
                        gc.drawImage(sprites.get("Dad_3"), 0,0,canvas.getWidth(),canvas.getHeight());
                    }
                }
                gc.setFill(Color.RED);
                gc.fillText("Late",355,100);
            }
        }




    }




    public void proccessKeyOutputs(String key) {
        switch (gameState) {
            case CHIP_MENU:
                if(key.equals("UP"))
                {
                    chipMenuState.heldButton = "";
                }
                if(key.equals("DOWN"))
                {
                    chipMenuState.heldButton = "";
                }
                if(key.equals("RIGHT"))
                {
                    chipMenuState.heldButton = "";
                }
                if(key.equals("LEFT"))
                {
                    chipMenuState.heldButton = "";
                }
                if(key.equals("S"))
                {
                    chipMenuState.unPressR();
                }

                break;
        }
    }

    public void proccessKeyInputs(String key) {
        switch (gameState) {
            case TITLE_SCREEN_START:
                switch (key) {
                    // DEBUG
                    case "P":
                        gameState = GameState.BATTLE_START;
                        stateStartTime = System.nanoTime();

                        Media sound = new Media(new File(Battle_Theme).toURI().toString());
                        backgroundMusic = new MediaPlayer(sound);
                        backgroundMusic.setOnEndOfMedia(new Runnable() {
                            public void run() {
                                backgroundMusic.seek(Duration.ZERO);
                            }
                        });
                        backgroundMusic.play();
                        chipMenuState = new ChipMenuState();
                        CreateBattle();
                }
                break;
            case TITLE_SCREEN_IDLE:
                switch (key) {
                    case "ENTER":
                        gameState = GameState.TILE_SCREEN_MENU;
                        stateStartTime = System.nanoTime();

                        Media sound = new Media(new File(Main_Menu_Select).toURI().toString());
                        soundEffects = new MediaPlayer(sound);
                        soundEffects.play();

                }
                break;
            case TILE_SCREEN_MENU:
                if(key.equals("Z"))
                {
                    gameState = GameState.TITLE_SCREEN_TRANSITION;
                    stateStartTime = System.nanoTime();
                    backgroundMusic.pause();

                    Media sound = new Media(new File(New_Game_Effect).toURI().toString());
                    soundEffects = new MediaPlayer(sound);
                    soundEffects.play();
                }
                if(key.equals("X"))
                {
                    gameState = GameState.TITLE_SCREEN_IDLE;
                    soundEffects.pause();
                }
                break;
            case CHIP_MENU:
                if(key.equals("Z"))
                {
                    chipMenuState.pressA();
                }
                if(key.equals("X"))
                {
                    chipMenuState.pressB();
                }
                if(key.equals("S"))
                {
                    chipMenuState.pressR();
                }
                if(key.equals("UP"))
                {
                    if(!chipMenuState.buttonSkidEnabled) {
                        chipMenuState.moveUp();
                        chipMenuState.heldButton = "UP";
                        chipMenuState.heldButtonStartTime = System.nanoTime();
                        chipMenuState.buttonSkidEnabled = false;
                    }
                }
                if(key.equals("DOWN"))
                {
                    if(!chipMenuState.buttonSkidEnabled) {
                        chipMenuState.moveDown();
                        chipMenuState.heldButton = "DOWN";
                        chipMenuState.heldButtonStartTime = System.nanoTime();
                        chipMenuState.buttonSkidEnabled = false;
                    }

                }
                if(key.equals("RIGHT"))
                {
                    if(!chipMenuState.buttonSkidEnabled) {
                        chipMenuState.moveRight();
                        chipMenuState.heldButton = "RIGHT";
                        chipMenuState.heldButtonStartTime = System.nanoTime();
                        chipMenuState.buttonSkidEnabled = false;
                    }
                }
                if(key.equals("LEFT"))
                {
                    if(!chipMenuState.buttonSkidEnabled) {
                        chipMenuState.moveLeft();
                        chipMenuState.heldButton = "LEFT";
                        chipMenuState.heldButtonStartTime = System.nanoTime();
                        chipMenuState.buttonSkidEnabled = false;
                    }

                }


                break;

            case BATTLE:
                if(key.equals("Z"))
                {
                    megaman.pressA(tiles,enemies);
                }
                if(key.equals("X"))
                {
                    megaman.pressB(tiles,enemies);
                }
                if(key.equals("R"))
                {
                    megaman.pressB1(tiles,enemies);
                }
                if(key.equals("A"))
                {
                    if(customGaugeFilled)
                    {
                        chipMenuState.reEnter();
                        gameState = GameState.ENTER_CHIP_MENU;
                        stateStartTime = System.nanoTime();
                        customGaugeFilled = false;

                    }
                }
                if(key.equals("S"))
                {
                    if(customGaugeFilled)
                    {
                        chipMenuState.reEnter();
                        gameState = GameState.ENTER_CHIP_MENU;
                        stateStartTime = System.nanoTime();
                        customGaugeFilled = false;
                    }
                }
                if(key.equals("UP"))
                {
                    megaman.moveUp(tiles,enemies);
                }
                if(key.equals("DOWN"))
                {
                    megaman.moveDown(tiles,enemies);
                }
                if(key.equals("RIGHT"))
                {
                    megaman.moveRight(tiles,enemies);
                }
                if(key.equals("LEFT"))
                {
                    megaman.moveLeft(tiles,enemies);
                }
                break;
            case BETA_SCREEN:
                if(key.equals("L"))
                {
                    backgroundMusic.stop();
                    Media sound = new Media(new File("GameData/Sounds/Suprise.mp3").toURI().toString());
                    backgroundMusic = new MediaPlayer(sound);
                    backgroundMusic.setOnEndOfMedia(new Runnable() {
                        public void run() {
                            backgroundMusic.seek(Duration.ZERO);
                        }
                    });
                    backgroundMusic.play();
                    window.setTitle("[Happy Birthday Dad!]");
                    suprise = true;

                }

                break;
        }
    }




    public void renderBattleBackground(GraphicsContext gc, Canvas canvas)
    {
        if(backGroundRenderTime == 0)
        {
            backGroundRenderTime = stateStartTime;
        }
        double progressedSeconds = (System.nanoTime() - backGroundRenderTime) / 1000000000.0;

        double backgroundWidth = sprites.get("Megaman_Title_Background").getWidth() * 2;
        double backgroundHeight = sprites.get("Megaman_Title_Background").getHeight() * 2;
        double backgroundModRate = 3.0;
        double progressModBackground = progressedSeconds % backgroundModRate;

        gc.setGlobalAlpha(1.0);
        gc.setFill(Color.BLACK);

        if (progressedSeconds <= 0.5) {
            gc.setGlobalAlpha(progressedSeconds * 2);
        }
        // Background
        gc.drawImage(sprites.get("Megaman_Title_Background"), progressModBackground * canvas.getWidth() / backgroundModRate, 0, backgroundWidth, backgroundHeight);
        gc.drawImage(sprites.get("Megaman_Title_Background"), progressModBackground * canvas.getWidth() / backgroundModRate - canvas.getWidth(), 0, backgroundWidth, backgroundHeight);

    }

    public void renderTiles(GraphicsContext gc, Canvas canvas) {
        double tileWidth = 80;
        double tileHeight = 48;


        for (Tile tile : tiles) {
            int tileX = 0 + (tile.getX() - 1) * 80;
            int tileY = 145 + (tile.getY() - 1) * 48;
            if (tile.isRed()) {
                gc.drawImage(sprites.get(tile.getSprite(megaman,tiles)), tileX, tileY, tileWidth, tileHeight);
                gc.drawImage(sprites.get("Red_Tile_Border"), tileX, tileY + 48, 80, 16);
            } else {
                gc.drawImage(sprites.get(tile.getSprite(megaman,tiles)), tileX, tileY, tileWidth, tileHeight);
                gc.drawImage(sprites.get("Blue_Tile_Border"), tileX, tileY + 48, 80, 16);

            }
        }
    }

    public void renderHealthBar(GraphicsContext gc, Canvas canvas, double xOffset)
    {
        gc.setGlobalAlpha(1.0);
        String num = String.valueOf(megaman.getCurrentHealth());
        ArrayList<String> list = new ArrayList<>();
        for(int i = 0; i < num.length(); i++)
        {
            list.add(String.valueOf(num.charAt(i)));
        }
        Collections.reverse(list);

        gc.drawImage(sprites.get("Health_Bar"),xOffset,0,96,30);

        int a = 0;
        for(String n: list)
        {
            gc.drawImage(sprites.get("HP_" + n), xOffset + 65 - 15 * a,5,12,20);
            a++;
        }
    }
}

