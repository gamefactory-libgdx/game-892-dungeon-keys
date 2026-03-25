package com.dungeonkeys000892.app9711.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
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

public class MainMenuScreen implements Screen {

    private static final String BG = "ui/main_menu.png";

    private final MainGame game;
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;

    public MainMenuScreen(MainGame game) {
        this.game = game;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }

        game.playMusic("sounds/music/music_menu.ogg");

        TextButton.TextButtonStyle btnStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);
        buildUI(btnStyle);
        registerInput();
    }

    private void buildUI(TextButton.TextButtonStyle btnStyle) {
        // PLAY (top-Y=420 → libgdxY=378)
        TextButton playBtn = UiFactory.makeButton("PLAY", btnStyle,
                Constants.BTN_MAIN_W, Constants.BTN_MAIN_H);
        playBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_MAIN_W) / 2f, 378f);
        playBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playClick();
                game.setScreen(new DungeonSelectScreen(game));
            }
        });

        // LEADERBOARD (top-Y=496 → libgdxY=302)
        TextButton lbBtn = UiFactory.makeButton("LEADERBOARD", btnStyle,
                Constants.BTN_MAIN_W, Constants.BTN_MAIN_H);
        lbBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_MAIN_W) / 2f, 302f);
        lbBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playClick();
                game.setScreen(new LeaderboardScreen(game));
            }
        });

        // SETTINGS (top-Y=572 → libgdxY=226)
        TextButton settingsBtn = UiFactory.makeButton("SETTINGS", btnStyle,
                Constants.BTN_MAIN_W, Constants.BTN_MAIN_H);
        settingsBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_MAIN_W) / 2f, 226f);
        settingsBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playClick();
                game.setScreen(new SettingsScreen(game));
            }
        });

        // HOW TO PLAY (top-Y=648 → libgdxY=150)
        TextButton howBtn = UiFactory.makeButton("HOW TO PLAY", btnStyle,
                Constants.BTN_MAIN_W, Constants.BTN_MAIN_H);
        howBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_MAIN_W) / 2f, 150f);
        howBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playClick();
                game.setScreen(new HowToPlayScreen(game));
            }
        });

        stage.addActor(playBtn);
        stage.addActor(lbBtn);
        stage.addActor(settingsBtn);
        stage.addActor(howBtn);
    }

    private void registerInput() {
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, new InputAdapter() {
            @Override public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.BACK) {
                    Gdx.app.exit();
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
