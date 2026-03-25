package com.dungeonkeys000892.app9711.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.dungeonkeys000892.app9711.Constants;
import com.dungeonkeys000892.app9711.MainGame;
import com.dungeonkeys000892.app9711.UiFactory;

public abstract class BaseGameScreen extends ScreenAdapter {

    protected final MainGame game;
    protected final int dungeonIndex;   // 0=crypt, 1=lava, 2=ice
    protected int currentLevel;         // 0–9

    // Camera / viewport
    private OrthographicCamera camera;
    private StretchViewport viewport;

    // HUD stage
    private Stage hudStage;
    private Label keysLabel;
    private Label scoreLabel;

    // ShapeRenderer for maze elements
    private ShapeRenderer sr;

    // Textures
    private Texture bgTex;
    private TextureRegion[] playerRegion; // [0=down, 1=up, 2=left, 3=right]

    // ── Maze ────────────────────────────────────────────────────────────────
    private int[][] maze; // 0=wall, 1=floor

    // Player state
    private int playerCol, playerRow;
    private float playerX, playerY;     // world coords (bottom-left)
    private float moveStartX, moveStartY;
    private float targetX, targetY;
    private int targetCol, targetRow;
    private int playerFacing;           // 0=down 1=up 2=left 3=right
    private boolean moving;
    private float moveTimer;

    // Keys
    private Array<int[]> keyPositions;
    private boolean[] keyCollected;
    private int keysCollected;

    // Exit
    private int exitCol, exitRow;
    private boolean exitOpen;

    // Enemies  [index][0=col, 1=row]
    private int[][] enemies;
    private int enemyCount;
    private float enemyTimer;

    // Game state
    private int score;
    private float levelTimer;
    private boolean pendingGameOver;
    private boolean pendingLevelComplete;

    // Maze offset (centered in world, below HUD)
    private static final float MAZE_OFF_X =
            (Constants.WORLD_WIDTH  - Constants.MAZE_COLS * Constants.TILE_SIZE) / 2f;
    private static final float MAZE_OFF_Y =
            (Constants.WORLD_HEIGHT - Constants.HUD_HEIGHT - Constants.MAZE_ROWS * Constants.TILE_SIZE) / 2f;

    // Colors
    private static final Color WALL_COLOR    = new Color(0.10f, 0.06f, 0.14f, 0.88f);
    private static final Color KEY_COLOR     = new Color(0.78f, 0.66f, 0.29f, 1.00f);
    private static final Color ENEMY_COLOR   = new Color(0.88f, 0.33f, 0.31f, 1.00f);
    private static final Color EXIT_OPEN_C   = new Color(0.30f, 0.90f, 0.30f, 0.85f);
    private static final Color EXIT_CLOSED_C = new Color(0.55f, 0.28f, 0.08f, 0.85f);

    // ── Abstract API ────────────────────────────────────────────────────────
    protected abstract String getBgPath();
    protected abstract Screen createFreshScreen();

    // ── Constructor ─────────────────────────────────────────────────────────
    public BaseGameScreen(MainGame game, int dungeonIndex, int level) {
        this.game         = game;
        this.dungeonIndex = dungeonIndex;
        this.currentLevel = level;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        hudStage = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        loadAssets();
        initMaze();
        buildHud();
        registerInput();
    }

    // ── Asset loading ────────────────────────────────────────────────────────
    private void loadAssets() {
        if (!game.manager.isLoaded(getBgPath()))
            game.manager.load(getBgPath(), Texture.class);

        String[] sprites = {
            "sprites/character/Front-Idle.png",
            "sprites/character/Back-Idle.png",
            "sprites/character/Left-Idle.png",
            "sprites/character/Right-Idle.png"
        };
        for (String s : sprites)
            if (!game.manager.isLoaded(s))
                game.manager.load(s, Texture.class);

        game.manager.finishLoading();

        bgTex = game.manager.get(getBgPath(), Texture.class);
        playerRegion = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            Texture t = game.manager.get(sprites[i], Texture.class);
            int tw = t.getWidth(), th = t.getHeight();
            int frames = Math.max(1, tw / th);
            playerRegion[i] = new TextureRegion(t, 0, 0, tw / frames, th);
        }
    }

    // ── Maze generation ──────────────────────────────────────────────────────
    private void initMaze() {
        maze = new int[Constants.MAZE_COLS][Constants.MAZE_ROWS];
        boolean[][] visited = new boolean[Constants.MAZE_COLS][Constants.MAZE_ROWS];
        dfsCarve(1, 1, visited);

        exitCol = Constants.MAZE_COLS - 2; // 7
        exitRow = Constants.MAZE_ROWS - 2; // 13
        maze[exitCol][exitRow] = 1;

        // Player start
        playerCol  = 1;
        playerRow  = 1;
        playerX    = tileX(playerCol);
        playerY    = tileY(playerRow);
        playerFacing = 0;
        moving     = false;

        // Collect floor tiles (excluding start + exit)
        Array<int[]> floor = new Array<>();
        for (int c = 1; c < Constants.MAZE_COLS - 1; c++)
            for (int r = 1; r < Constants.MAZE_ROWS - 1; r++)
                if (maze[c][r] == 1 && !(c == 1 && r == 1) && !(c == exitCol && r == exitRow))
                    floor.add(new int[]{c, r});

        // Shuffle floor list
        for (int i = floor.size - 1; i > 0; i--) {
            int j = MathUtils.random(i);
            int[] tmp = floor.get(i); floor.set(i, floor.get(j)); floor.set(j, tmp);
        }

        // Place keys
        int numKeys = Math.min(Constants.KEYS_PER_LEVEL, floor.size);
        keyPositions = new Array<>(numKeys);
        for (int i = 0; i < numKeys; i++) keyPositions.add(floor.get(i));
        keyCollected  = new boolean[numKeys];
        keysCollected = 0;
        exitOpen      = false;

        // Place enemies — from far end of shuffled floor list
        enemyCount = Math.min(2 + currentLevel / 2, floor.size - numKeys);
        enemyCount = Math.max(1, enemyCount);
        enemies    = new int[enemyCount][2];
        for (int i = 0; i < enemyCount; i++) {
            int[] tile = floor.get(floor.size - 1 - i);
            enemies[i][0] = tile[0];
            enemies[i][1] = tile[1];
        }
        enemyTimer = Constants.ENEMY_MOVE_INTERVAL;

        score               = 0;
        levelTimer          = 120f;
        pendingGameOver     = false;
        pendingLevelComplete = false;
    }

    private void dfsCarve(int c, int r, boolean[][] visited) {
        visited[c][r] = true;
        maze[c][r]    = 1;
        int[][] dirs = {{0, 2}, {0, -2}, {2, 0}, {-2, 0}};
        for (int i = dirs.length - 1; i > 0; i--) {
            int j = MathUtils.random(i);
            int[] tmp = dirs[i]; dirs[i] = dirs[j]; dirs[j] = tmp;
        }
        for (int[] d : dirs) {
            int nc = c + d[0], nr = r + d[1];
            if (nc > 0 && nc < Constants.MAZE_COLS - 1
                    && nr > 0 && nr < Constants.MAZE_ROWS - 1
                    && !visited[nc][nr]) {
                maze[c + d[0] / 2][r + d[1] / 2] = 1;
                dfsCarve(nc, nr, visited);
            }
        }
    }

    private float tileX(int col) {
        return MAZE_OFF_X + col * Constants.TILE_SIZE + (Constants.TILE_SIZE - Constants.PLAYER_SIZE) / 2f;
    }

    private float tileY(int row) {
        return MAZE_OFF_Y + row * Constants.TILE_SIZE + (Constants.TILE_SIZE - Constants.PLAYER_SIZE) / 2f;
    }

    // ── HUD ─────────────────────────────────────────────────────────────────
    private void buildHud() {
        TextButton.TextButtonStyle roundStyle = UiFactory.makeRoundStyle(game.manager, game.fontSmall);
        TextButton pauseBtn = UiFactory.makeRoundButton("||", roundStyle, Constants.BTN_ROUND_SZ);
        pauseBtn.setPosition(
            Constants.WORLD_WIDTH  - 16f - Constants.BTN_ROUND_SZ,
            Constants.WORLD_HEIGHT - 16f - Constants.BTN_ROUND_SZ);
        pauseBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(new PauseScreen(game, BaseGameScreen.this, getBgPath(),
                        () -> game.setScreen(createFreshScreen())));
            }
        });
        hudStage.addActor(pauseBtn);

        Label.LabelStyle ls = new Label.LabelStyle(game.fontSmall, Color.WHITE);
        keysLabel  = new Label("KEYS: 0/" + Constants.KEYS_PER_LEVEL, ls);
        scoreLabel = new Label("SCORE: 0", ls);
        keysLabel.setPosition(16f, Constants.WORLD_HEIGHT - 16f - 44f);
        scoreLabel.setPosition((Constants.WORLD_WIDTH - 160f) / 2f, Constants.WORLD_HEIGHT - 16f - 44f);
        hudStage.addActor(keysLabel);
        hudStage.addActor(scoreLabel);
    }

    // ── Input ────────────────────────────────────────────────────────────────
    private void registerInput() {
        Gdx.input.setInputProcessor(new InputMultiplexer(
            hudStage,
            new InputAdapter() {
                @Override
                public boolean touchDown(int sx, int sy, int pointer, int button) {
                    if (pendingGameOver || pendingLevelComplete || moving) return false;
                    Vector3 touch = new Vector3(sx, sy, 0);
                    camera.unproject(touch);
                    float pcx = MAZE_OFF_X + playerCol * Constants.TILE_SIZE + Constants.TILE_SIZE / 2f;
                    float pcy = MAZE_OFF_Y + playerRow * Constants.TILE_SIZE + Constants.TILE_SIZE / 2f;
                    float dx = touch.x - pcx, dy = touch.y - pcy;
                    if (Math.abs(dx) >= Math.abs(dy)) tryMove(dx > 0 ? 1 : -1, 0);
                    else                               tryMove(0, dy > 0 ? 1 : -1);
                    return true;
                }
                @Override
                public boolean keyDown(int keycode) {
                    if (keycode == Input.Keys.BACK) {
                        game.setScreen(new MainMenuScreen(game));
                        return true;
                    }
                    return false;
                }
            }
        ));
    }

    @Override public void show() {
        registerInput();
        game.playMusic("sounds/music/music_gameplay.ogg");
    }

    // ── Movement ─────────────────────────────────────────────────────────────
    private void tryMove(int dc, int dr) {
        int nc = playerCol + dc, nr = playerRow + dr;
        if (nc < 0 || nc >= Constants.MAZE_COLS || nr < 0 || nr >= Constants.MAZE_ROWS) return;
        if (maze[nc][nr] == 0) return;
        moving      = true;
        moveTimer   = 0;
        moveStartX  = playerX;
        moveStartY  = playerY;
        targetCol   = nc;
        targetRow   = nr;
        targetX     = tileX(nc);
        targetY     = tileY(nr);
        if      (dc ==  1) playerFacing = 3; // right
        else if (dc == -1) playerFacing = 2; // left
        else if (dr ==  1) playerFacing = 1; // up
        else               playerFacing = 0; // down
    }

    // ── Update ───────────────────────────────────────────────────────────────
    private void update(float delta) {
        if (levelTimer > 0) levelTimer -= delta;

        // Player movement interpolation
        if (moving) {
            moveTimer += delta;
            float t = Math.min(moveTimer / Constants.MOVE_DURATION, 1f);
            playerX = moveStartX + (targetX - moveStartX) * t;
            playerY = moveStartY + (targetY - moveStartY) * t;
            if (t >= 1f) {
                moving    = false;
                playerCol = targetCol;
                playerRow = targetRow;
                playerX   = targetX;
                playerY   = targetY;
                onPlayerMoved();
            }
        }

        // Enemy movement
        enemyTimer -= delta;
        if (enemyTimer <= 0) {
            enemyTimer = Constants.ENEMY_MOVE_INTERVAL;
            moveEnemies();
        }

        keysLabel.setText("KEYS: " + keysCollected + "/" + Constants.KEYS_PER_LEVEL);
        scoreLabel.setText("SCORE: " + score);
    }

    private void onPlayerMoved() {
        // Key collection
        for (int i = 0; i < keyPositions.size; i++) {
            if (!keyCollected[i]) {
                int[] kp = keyPositions.get(i);
                if (kp[0] == playerCol && kp[1] == playerRow) {
                    keyCollected[i] = true;
                    keysCollected++;
                    score += Constants.SCORE_KEY;
                    playSound("sounds/sfx/sfx_coin.ogg");
                    if (keysCollected >= keyPositions.size) {
                        exitOpen = true;
                        playSound("sounds/sfx/sfx_power_up.ogg");
                    }
                }
            }
        }
        // Exit reached
        if (exitOpen && playerCol == exitCol && playerRow == exitRow) {
            int timeBonus = (int)(Math.max(0, levelTimer) * Constants.SCORE_TIME_BONUS);
            score += Constants.SCORE_LEVEL_BONUS + timeBonus;
            playSound("sounds/sfx/sfx_level_complete.ogg");
            saveBestScore();
            pendingLevelComplete = true;
            return;
        }
        // Enemy collision
        checkEnemyCollision();
    }

    private void moveEnemies() {
        int[][] dirs = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        for (int i = 0; i < enemyCount; i++) {
            int startDir = MathUtils.random(3);
            for (int attempt = 0; attempt < 4; attempt++) {
                int[] d  = dirs[(startDir + attempt) % 4];
                int nc = enemies[i][0] + d[0];
                int nr = enemies[i][1] + d[1];
                if (nc > 0 && nc < Constants.MAZE_COLS - 1
                        && nr > 0 && nr < Constants.MAZE_ROWS - 1
                        && maze[nc][nr] == 1) {
                    boolean occupied = false;
                    for (int j = 0; j < enemyCount; j++) {
                        if (j != i && enemies[j][0] == nc && enemies[j][1] == nr) {
                            occupied = true; break;
                        }
                    }
                    if (!occupied) {
                        enemies[i][0] = nc;
                        enemies[i][1] = nr;
                        break;
                    }
                }
            }
        }
        checkEnemyCollision();
    }

    private void checkEnemyCollision() {
        for (int i = 0; i < enemyCount; i++) {
            if (enemies[i][0] == playerCol && enemies[i][1] == playerRow) {
                playSound("sounds/sfx/sfx_hit.ogg");
                if (game.vibrationEnabled) {
                    try { Gdx.input.vibrate(200); } catch (Exception ignored) {}
                }
                pendingGameOver = true;
            }
        }
    }

    private void saveBestScore() {
        String key = dungeonIndex == 0 ? Constants.PREF_BEST_CRYPT :
                     dungeonIndex == 1 ? Constants.PREF_BEST_LAVA  : Constants.PREF_BEST_ICE;
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        if (score > prefs.getInteger(key, 0)) {
            prefs.putInteger(key, score);
            prefs.flush();
        }
        // Also update global high score
        if (score > prefs.getInteger(Constants.PREF_HIGH_SCORE, 0)) {
            prefs.putInteger(Constants.PREF_HIGH_SCORE, score);
            prefs.flush();
        }
    }

    private void playSound(String path) {
        if (game.sfxEnabled) game.manager.get(path, Sound.class).play(1f);
    }

    // ── Render ───────────────────────────────────────────────────────────────
    @Override
    public void render(float delta) {
        // Screen transitions before rendering
        if (pendingLevelComplete) {
            game.setScreen(new LevelCompleteScreen(game, dungeonIndex, currentLevel, score));
            return;
        }
        if (pendingGameOver) {
            game.setScreen(new GameOverScreen(game, score, dungeonIndex));
            return;
        }

        update(delta);

        Gdx.gl.glClearColor(Constants.COLOR_BG_R, Constants.COLOR_BG_G, Constants.COLOR_BG_B, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 1. Background
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(bgTex, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // 2. Maze shapes (walls, exit, keys, enemies)
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        drawMazeShapes();
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // 3. Player sprite
        game.batch.begin();
        game.batch.draw(playerRegion[playerFacing], playerX, playerY,
                Constants.PLAYER_SIZE, Constants.PLAYER_SIZE);
        game.batch.end();

        // 4. HUD (always — even when transitioning)
        hudStage.act(delta);
        hudStage.draw();
    }

    private void drawMazeShapes() {
        float ts = Constants.TILE_SIZE;
        float ox = MAZE_OFF_X, oy = MAZE_OFF_Y;

        // Walls
        sr.setColor(WALL_COLOR);
        for (int c = 0; c < Constants.MAZE_COLS; c++)
            for (int r = 0; r < Constants.MAZE_ROWS; r++)
                if (maze[c][r] == 0)
                    sr.rect(ox + c * ts, oy + r * ts, ts, ts);

        // Exit door
        sr.setColor(exitOpen ? EXIT_OPEN_C : EXIT_CLOSED_C);
        sr.rect(ox + exitCol * ts + 4, oy + exitRow * ts + 4, ts - 8, ts - 8);

        // Keys
        sr.setColor(KEY_COLOR);
        float kr = Constants.KEY_SIZE / 2f;
        for (int i = 0; i < keyPositions.size; i++) {
            if (!keyCollected[i]) {
                int[] kp = keyPositions.get(i);
                sr.circle(ox + kp[0] * ts + ts / 2f, oy + kp[1] * ts + ts / 2f, kr, 12);
            }
        }

        // Enemies
        sr.setColor(ENEMY_COLOR);
        float er = Constants.ENEMY_SIZE / 2f - 2;
        for (int i = 0; i < enemyCount; i++) {
            sr.circle(ox + enemies[i][0] * ts + ts / 2f,
                      oy + enemies[i][1] * ts + ts / 2f, er, 16);
        }
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, true); }

    @Override
    public void dispose() {
        hudStage.dispose();
        sr.dispose();
    }
}
