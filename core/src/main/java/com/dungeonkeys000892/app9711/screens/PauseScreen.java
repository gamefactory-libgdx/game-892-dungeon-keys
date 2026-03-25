package com.dungeonkeys000892.app9711.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.dungeonkeys000892.app9711.Constants;
import com.dungeonkeys000892.app9711.MainGame;
import com.dungeonkeys000892.app9711.UiFactory;

public class PauseScreen extends ScreenAdapter {

    private final MainGame game;
    private final Screen previousScreen;
    private final String bgPath;
    private final Runnable onRestart;

    private OrthographicCamera camera;
    private StretchViewport viewport;
    private Stage stage;
    private ShapeRenderer sr;
    private Texture bgTex;

    public PauseScreen(MainGame game, Screen previousScreen, String bgPath, Runnable onRestart) {
        this.game           = game;
        this.previousScreen = previousScreen;
        this.bgPath         = bgPath;
        this.onRestart      = onRestart;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);
        sr       = new ShapeRenderer();

        if (!game.manager.isLoaded(bgPath)) {
            game.manager.load(bgPath, Texture.class);
            game.manager.finishLoading();
        }
        bgTex = game.manager.get(bgPath, Texture.class);

        buildUi();
        registerInput();
    }

    private void buildUi() {
        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);

        // PAUSED title
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.WHITE);
        Label titleLabel = new Label("PAUSED", titleStyle);
        titleLabel.setPosition(
            (Constants.WORLD_WIDTH - titleLabel.getPrefWidth()) / 2f, 580f);
        stage.addActor(titleLabel);

        // Resume button   libgdxY = 854 - 340 - 56 = 458
        TextButton resumeBtn = UiFactory.makeButton("RESUME", rectStyle,
                Constants.BTN_MAIN_W, Constants.BTN_MAIN_H);
        resumeBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_MAIN_W) / 2f, 458f);
        resumeBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(previousScreen);
            }
        });
        stage.addActor(resumeBtn);

        // Restart button  libgdxY = 458 - 76 = 382
        TextButton restartBtn = UiFactory.makeButton("RESTART", rectStyle,
                Constants.BTN_MAIN_W, Constants.BTN_MAIN_H);
        restartBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_MAIN_W) / 2f, 382f);
        restartBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playSound("sounds/sfx/sfx_button_click.ogg");
                onRestart.run();
            }
        });
        stage.addActor(restartBtn);

        // Main Menu button
        TextButton menuBtn = UiFactory.makeButton("MAIN MENU", rectStyle,
                Constants.BTN_MAIN_W, Constants.BTN_MAIN_H);
        menuBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_MAIN_W) / 2f, 306f);
        menuBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playSound("sounds/sfx/sfx_button_back.ogg");
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(menuBtn);
    }

    private void registerInput() {
        Gdx.input.setInputProcessor(new InputMultiplexer(
            stage,
            new InputAdapter() {
                @Override public boolean keyDown(int keycode) {
                    if (keycode == Input.Keys.BACK) {
                        game.setScreen(previousScreen);
                        return true;
                    }
                    return false;
                }
            }
        ));
    }

    @Override public void show() { registerInput(); }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Constants.COLOR_BG_R, Constants.COLOR_BG_G, Constants.COLOR_BG_B, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Background
        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(bgTex, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        // Dark overlay
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        sr.setProjectionMatrix(camera.combined);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0f, 0f, 0f, 0.65f);
        sr.rect(0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        sr.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, true); }

    @Override public void dispose() { stage.dispose(); sr.dispose(); }

    private void playSound(String path) {
        if (game.sfxEnabled) game.manager.get(path, Sound.class).play(1f);
    }
}
