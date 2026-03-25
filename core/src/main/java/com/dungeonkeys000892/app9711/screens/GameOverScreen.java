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

public class GameOverScreen implements Screen {

    private static final String BG = "ui/game_over.png";

    private final MainGame game;
    private final int score;
    private final int extra;          // dungeon type: 0=Crypt, 1=Lava, 2=Ice

    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;

    public GameOverScreen(MainGame game, int score, int extra) {
        this.game  = game;
        this.score = score;
        this.extra = extra;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }

        // Save score to leaderboard and update high score
        LeaderboardScreen.addScore(score);
        saveHighScore();

        game.playMusicOnce("sounds/music/music_game_over.ogg");
        if (game.sfxEnabled)
            game.manager.get("sounds/sfx/sfx_game_over.ogg", Sound.class).play(1.0f);

        buildUI();
        registerInput();
    }

    private void saveHighScore() {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int best = prefs.getInteger(Constants.PREF_HIGH_SCORE, 0);
        if (score > best) {
            prefs.putInteger(Constants.PREF_HIGH_SCORE, score);
            prefs.flush();
        }
    }

    private void buildUI() {
        Preferences prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);
        int best = prefs.getInteger(Constants.PREF_HIGH_SCORE, 0);

        TextButton.TextButtonStyle btnStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);

        // "GAME OVER" title — centered around y≈560 (above the cracked banner at y≈310)
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.WHITE);
        Label titleLabel = new Label("GAME OVER", titleStyle);
        titleLabel.setAlignment(Align.center);
        titleLabel.setWidth(Constants.WORLD_WIDTH);
        titleLabel.setPosition(0, 560f);

        // Score label — centered at y≈490
        Label.LabelStyle scoreStyle = new Label.LabelStyle(game.fontScore, new Color(0xFFCA28FF));
        Label scoreLabel = new Label("SCORE: " + score, scoreStyle);
        scoreLabel.setAlignment(Align.center);
        scoreLabel.setWidth(Constants.WORLD_WIDTH);
        scoreLabel.setPosition(0, 480f);

        // Best label — centered at y≈420
        Label.LabelStyle bodyStyle = new Label.LabelStyle(game.fontBody, Color.WHITE);
        Label bestLabel = new Label("BEST: " + best, bodyStyle);
        bestLabel.setAlignment(Align.center);
        bestLabel.setWidth(Constants.WORLD_WIDTH);
        bestLabel.setPosition(0, 420f);

        // RETRY (top-Y=580 → libgdxY=218)
        TextButton retryBtn = UiFactory.makeButton("RETRY", btnStyle,
                Constants.BTN_MAIN_W, Constants.BTN_MAIN_H);
        retryBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_MAIN_W) / 2f, 218f);
        retryBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playClick();
                // Restart with same dungeon type (extra)
                switch (extra) {
                    case 1:  game.setScreen(new LavaDungeonScreen(game));  break;
                    case 2:  game.setScreen(new IceCavernScreen(game));    break;
                    default: game.setScreen(new CryptScreen(game));        break;
                }
            }
        });

        // MENU (top-Y=654 → libgdxY=144)
        TextButton menuBtn = UiFactory.makeButton("MAIN MENU", btnStyle,
                Constants.BTN_MAIN_W, Constants.BTN_MAIN_H);
        menuBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_MAIN_W) / 2f, 144f);
        menuBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playClick();
                game.setScreen(new MainMenuScreen(game));
            }
        });

        stage.addActor(titleLabel);
        stage.addActor(scoreLabel);
        stage.addActor(bestLabel);
        stage.addActor(retryBtn);
        stage.addActor(menuBtn);
    }

    private void registerInput() {
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    playClick();
                    game.setScreen(new MainMenuScreen(game));
                    return true;
                }
                return false;
            }
        }));
    }

    private void playClick() {
        if (game.sfxEnabled)
            game.manager.get("sounds/sfx/sfx_button_click.ogg", Sound.class).play(1.0f);
    }

    @Override
    public void show() {
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
