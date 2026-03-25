package com.dungeonkeys000892.app9711.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dungeonkeys000892.app9711.Constants;
import com.dungeonkeys000892.app9711.MainGame;
import com.dungeonkeys000892.app9711.UiFactory;

import java.util.Arrays;

public class LeaderboardScreen implements Screen {

    private static final String BG = "ui/leaderboard.png";

    private final MainGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;

    // ── Static helper — call from GameOverScreen after each run ──────────
    public static void addScore(int score) {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        // Read existing scores
        int[] scores = new int[Constants.LEADERBOARD_SIZE];
        for (int i = 0; i < Constants.LEADERBOARD_SIZE; i++) {
            scores[i] = prefs.getInteger(Constants.PREF_LEADER_SCORE_PREFIX + i, 0);
        }
        // Insert and sort descending
        scores[Constants.LEADERBOARD_SIZE - 1] = score;
        Arrays.sort(scores);
        // Reverse to descending
        for (int l = 0, r = Constants.LEADERBOARD_SIZE - 1; l < r; l++, r--) {
            int tmp = scores[l]; scores[l] = scores[r]; scores[r] = tmp;
        }
        // Persist
        for (int i = 0; i < Constants.LEADERBOARD_SIZE; i++) {
            prefs.putInteger(Constants.PREF_LEADER_SCORE_PREFIX + i, scores[i]);
        }
        prefs.flush();
    }

    // ── Constructor ───────────────────────────────────────────────────────
    public LeaderboardScreen(MainGame game) {
        this.game = game;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }

        game.playMusic("sounds/music/music_menu.ogg");

        buildUI();
        registerInput();
    }

    private void buildUI() {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        TextButton.TextButtonStyle smallStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);

        // BACK button (top-Y=20, left@20, size=120x44 → libgdxY=790)
        TextButton backBtn = UiFactory.makeButton("BACK", smallStyle,
                Constants.BTN_SMALL_W, Constants.BTN_SMALL_H);
        backBtn.setPosition(20f, 790f);
        backBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playBack();
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(backBtn);

        // Title label
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, new Color(0xFFCA28FF));
        Label titleLabel = new Label("LEADERBOARD", titleStyle);
        titleLabel.setAlignment(Align.center);
        titleLabel.setWidth(Constants.WORLD_WIDTH);
        titleLabel.setPosition(0, 730f);
        stage.addActor(titleLabel);

        // Rank rows
        // FIGMA: RANK 1 top-Y=240 → libgdxY=554, rows spaced 76px apart
        // Rows 6-10 continue below row 5
        Label.LabelStyle rowStyle  = new Label.LabelStyle(game.fontBody, Color.WHITE);
        Label.LabelStyle zeroStyle = new Label.LabelStyle(game.fontBody, new Color(0.5f, 0.5f, 0.5f, 1f));

        float[] rowY = {
            554f, 478f, 402f, 326f, 250f,   // ranks 1-5 (from FIGMA)
            174f, 98f                         // ranks 6-7 (fit remaining space)
        };

        for (int i = 0; i < Constants.LEADERBOARD_SIZE; i++) {
            int s = prefs.getInteger(Constants.PREF_LEADER_SCORE_PREFIX + i, 0);
            String text = String.format("#%d  %s", i + 1, s > 0 ? String.valueOf(s) : "---");
            Label.LabelStyle style = (s > 0) ? rowStyle : zeroStyle;
            Label rowLabel = new Label(text, style);
            rowLabel.setAlignment(Align.center);
            rowLabel.setWidth(360f);
            float yPos = (i < rowY.length) ? rowY[i] : rowY[rowY.length - 1] - (i - rowY.length + 1) * 40f;
            rowLabel.setPosition((Constants.WORLD_WIDTH - 360f) / 2f, yPos);
            stage.addActor(rowLabel);
        }
    }

    private void registerInput() {
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    playBack();
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        }));
    }

    private void playBack() {
        if (game.sfxEnabled)
            game.manager.get("sounds/sfx/sfx_button_back.ogg", Sound.class).play(1.0f);
    }

    @Override
    public void show() {
        game.playMusic("sounds/music/music_menu.ogg");
        registerInput();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();
        game.batch.draw(game.manager.get(BG, Texture.class),
                0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}
