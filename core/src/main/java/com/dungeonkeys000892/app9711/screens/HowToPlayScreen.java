package com.dungeonkeys000892.app9711.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
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
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.dungeonkeys000892.app9711.Constants;
import com.dungeonkeys000892.app9711.MainGame;
import com.dungeonkeys000892.app9711.UiFactory;

public class HowToPlayScreen extends ScreenAdapter {

    private static final String BG = "ui/how_to_play.png";

    private final MainGame game;
    private OrthographicCamera camera;
    private StretchViewport viewport;
    private Stage stage;
    private Texture bgTex;

    public HowToPlayScreen(MainGame game) {
        this.game = game;

        camera   = new OrthographicCamera();
        viewport = new StretchViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        stage    = new Stage(viewport, game.batch);

        if (!game.manager.isLoaded(BG)) {
            game.manager.load(BG, Texture.class);
            game.manager.finishLoading();
        }
        bgTex = game.manager.get(BG, Texture.class);

        buildUi();
        registerInput();
    }

    private void buildUi() {
        TextButton.TextButtonStyle rectStyle  = UiFactory.makeRectStyle(game.manager, game.fontBody);
        TextButton.TextButtonStyle smallStyle = UiFactory.makeRectStyle(game.manager, game.fontSmall);

        // BACK — top-Y=20 → libgdxY = 854-20-44 = 790
        TextButton backBtn = UiFactory.makeButton("BACK", smallStyle,
                Constants.BTN_SMALL_W, Constants.BTN_SMALL_H);
        backBtn.setPosition(20f, 790f);
        backBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playSound("sounds/sfx/sfx_button_back.ogg");
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(backBtn);

        // Title
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.WHITE);
        Label titleLbl = new Label("HOW TO PLAY", titleStyle);
        titleLbl.setPosition((Constants.WORLD_WIDTH - titleLbl.getPrefWidth()) / 2f, 720f);
        stage.addActor(titleLbl);

        // Instruction text — placed over the blank scroll area
        Label.LabelStyle bodyStyle = new Label.LabelStyle(game.fontBody, Color.WHITE);
        Label.LabelStyle smallLs   = new Label.LabelStyle(game.fontSmall, new Color(0.9f, 0.85f, 0.7f, 1f));

        String[] lines = {
            "TAP to move in a direction",
            "",
            "Collect all 5 KEYS",
            "to open the EXIT DOOR",
            "",
            "Reach the EXIT to",
            "complete the level",
            "",
            "Avoid RED ENEMIES",
            "or it's GAME OVER",
            "",
            "Faster completion =",
            "higher TIME BONUS"
        };

        float startY = 640f;
        float lineH  = 34f;
        for (String line : lines) {
            if (line.isEmpty()) { startY -= lineH * 0.5f; continue; }
            Label lbl = new Label(line, smallLs);
            lbl.setPosition((Constants.WORLD_WIDTH - lbl.getPrefWidth()) / 2f, startY);
            stage.addActor(lbl);
            startY -= lineH;
        }

        // GOT IT — top-Y=760 → libgdxY = 854-760-56 = 38
        TextButton gotItBtn = UiFactory.makeButton("GOT IT!", rectStyle,
                Constants.BTN_MAIN_W, Constants.BTN_MAIN_H);
        gotItBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_MAIN_W) / 2f, 38f);
        gotItBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                playSound("sounds/sfx/sfx_button_click.ogg");
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(gotItBtn);
    }

    private void registerInput() {
        Gdx.input.setInputProcessor(new InputMultiplexer(
            stage,
            new InputAdapter() {
                @Override public boolean keyDown(int keycode) {
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
        game.playMusic("sounds/music/music_menu.ogg");
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(Constants.COLOR_BG_R, Constants.COLOR_BG_G, Constants.COLOR_BG_B, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(bgTex, 0, 0, Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        game.batch.end();

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { viewport.update(width, height, true); }
    @Override public void dispose() { stage.dispose(); }

    private void playSound(String path) {
        if (game.sfxEnabled) game.manager.get(path, Sound.class).play(1f);
    }
}
