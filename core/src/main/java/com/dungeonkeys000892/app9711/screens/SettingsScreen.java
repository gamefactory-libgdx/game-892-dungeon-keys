package com.dungeonkeys000892.app9711.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dungeonkeys000892.app9711.Constants;
import com.dungeonkeys000892.app9711.MainGame;
import com.dungeonkeys000892.app9711.UiFactory;

public class SettingsScreen implements Screen {

    private static final String BG = "ui/settings.png";

    private final MainGame game;
    private final Preferences prefs;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;

    // Toggle button references so we can update their labels
    private TextButton musicBtn;
    private TextButton sfxBtn;
    private TextButton vibBtn;

    public SettingsScreen(MainGame game) {
        this.game  = game;
        this.prefs = Gdx.app.getPreferences(Constants.PREFS_NAME);

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }

        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);
        TextButton.TextButtonStyle smallStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);
        buildUI(rectStyle, smallStyle);
        registerInput();
    }

    private void buildUI(TextButton.TextButtonStyle rectStyle, TextButton.TextButtonStyle smallStyle) {
        // BACK (top-Y=20, left@20, size=120x44 → libgdxY=790)
        TextButton backBtn = UiFactory.makeButton("BACK", smallStyle,
                Constants.BTN_SMALL_W, Constants.BTN_SMALL_H);
        backBtn.setPosition(20f, 790f);
        backBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playBack();
                game.setScreen(new MainMenuScreen(game));
            }
        });

        // MUSIC toggle (top-Y=280, centered, size=320x52 → libgdxY=522)
        musicBtn = UiFactory.makeButton(musicLabel(), rectStyle, 320f, 52f);
        musicBtn.setPosition((Constants.WORLD_WIDTH - 320f) / 2f, 522f);
        musicBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.musicEnabled = !game.musicEnabled;
                prefs.putBoolean(Constants.PREF_MUSIC, game.musicEnabled);
                prefs.flush();
                if (game.currentMusic != null) {
                    if (game.musicEnabled) game.currentMusic.play();
                    else game.currentMusic.pause();
                }
                musicBtn.setText(musicLabel());
                playToggle();
            }
        });

        // SFX toggle (top-Y=348, centered, size=320x52 → libgdxY=454)
        sfxBtn = UiFactory.makeButton(sfxLabel(), rectStyle, 320f, 52f);
        sfxBtn.setPosition((Constants.WORLD_WIDTH - 320f) / 2f, 454f);
        sfxBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.sfxEnabled = !game.sfxEnabled;
                prefs.putBoolean(Constants.PREF_SFX, game.sfxEnabled);
                prefs.flush();
                sfxBtn.setText(sfxLabel());
                playToggle();
            }
        });

        // VIBRATION toggle (top-Y=416, centered, size=320x52 → libgdxY=386)
        vibBtn = UiFactory.makeButton(vibLabel(), rectStyle, 320f, 52f);
        vibBtn.setPosition((Constants.WORLD_WIDTH - 320f) / 2f, 386f);
        vibBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                game.vibrationEnabled = !game.vibrationEnabled;
                prefs.putBoolean(Constants.PREF_VIBRATION, game.vibrationEnabled);
                prefs.flush();
                vibBtn.setText(vibLabel());
                playToggle();
            }
        });

        stage.addActor(backBtn);
        stage.addActor(musicBtn);
        stage.addActor(sfxBtn);
        stage.addActor(vibBtn);
    }

    private String musicLabel() { return "MUSIC: " + (game.musicEnabled ? "ON" : "OFF"); }
    private String sfxLabel()   { return "SFX: "   + (game.sfxEnabled   ? "ON" : "OFF"); }
    private String vibLabel()   { return "VIBRATION: " + (game.vibrationEnabled ? "ON" : "OFF"); }

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

    private void playToggle() {
        if (game.sfxEnabled)
            game.manager.get("sounds/sfx/sfx_toggle.ogg", Sound.class).play(0.5f);
    }

    private void playBack() {
        if (game.sfxEnabled)
            game.manager.get("sounds/sfx/sfx_button_back.ogg", Sound.class).play(1.0f);
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
