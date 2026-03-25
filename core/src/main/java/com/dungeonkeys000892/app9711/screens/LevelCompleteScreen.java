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

public class LevelCompleteScreen extends ScreenAdapter {

    private static final String BG = "ui/level_complete.png";

    private final MainGame game;
    private final int dungeonIndex;
    private final int completedLevel;
    private final int score;

    private OrthographicCamera camera;
    private StretchViewport viewport;
    private Stage stage;
    private Texture bgTex;

    public LevelCompleteScreen(MainGame game, int dungeonIndex, int completedLevel, int score) {
        this.game           = game;
        this.dungeonIndex   = dungeonIndex;
        this.completedLevel = completedLevel;
        this.score          = score;

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
        TextButton.TextButtonStyle rectStyle = UiFactory.makeRectStyle(game.manager, game.fontBody);

        // Title label — centered around y=580 (on the ornate scroll at figma y≈340)
        Label.LabelStyle titleStyle = new Label.LabelStyle(game.fontTitle, Color.WHITE);
        Label titleLbl = new Label("LEVEL COMPLETE!", titleStyle);
        titleLbl.setPosition((Constants.WORLD_WIDTH - titleLbl.getPrefWidth()) / 2f, 540f);
        stage.addActor(titleLbl);

        // Level info
        Label.LabelStyle bodyStyle = new Label.LabelStyle(game.fontBody, Color.WHITE);
        String dungeonName = dungeonIndex == 0 ? "Stone Crypt" :
                             dungeonIndex == 1 ? "Lava Dungeon" : "Ice Cavern";
        Label levelLbl = new Label(dungeonName + "  Level " + (completedLevel + 1), bodyStyle);
        levelLbl.setPosition((Constants.WORLD_WIDTH - levelLbl.getPrefWidth()) / 2f, 490f);
        stage.addActor(levelLbl);

        // Score label
        Label.LabelStyle scoreStyle = new Label.LabelStyle(game.fontScore, new Color(0.78f, 0.66f, 0.29f, 1f));
        Label scoreLbl = new Label(String.valueOf(score), scoreStyle);
        scoreLbl.setPosition((Constants.WORLD_WIDTH - scoreLbl.getPrefWidth()) / 2f, 420f);
        stage.addActor(scoreLbl);

        Label scoreCaption = new Label("SCORE", bodyStyle);
        scoreCaption.setPosition((Constants.WORLD_WIDTH - scoreCaption.getPrefWidth()) / 2f, 390f);
        stage.addActor(scoreCaption);

        boolean isLastLevel = (completedLevel >= Constants.LEVELS_PER_DUNGEON - 1);

        if (!isLastLevel) {
            // NEXT LEVEL — top-Y=600 → libgdxY = 854-600-56 = 198
            TextButton nextBtn = UiFactory.makeButton("NEXT LEVEL", rectStyle,
                    Constants.BTN_MAIN_W, Constants.BTN_MAIN_H);
            nextBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_MAIN_W) / 2f, 198f);
            nextBtn.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) {
                    playSound("sounds/sfx/sfx_button_click.ogg");
                    int nextLevel = completedLevel + 1;
                    if      (dungeonIndex == 0) game.setScreen(new CryptScreen(game, nextLevel));
                    else if (dungeonIndex == 1) game.setScreen(new LavaDungeonScreen(game, nextLevel));
                    else                        game.setScreen(new IceCavernScreen(game, nextLevel));
                }
            });
            stage.addActor(nextBtn);
        } else {
            // Dungeon complete label
            Label completeLbl = new Label("DUNGEON COMPLETE!", bodyStyle);
            completeLbl.setColor(new Color(0.78f, 0.66f, 0.29f, 1f));
            completeLbl.setPosition((Constants.WORLD_WIDTH - completeLbl.getPrefWidth()) / 2f, 210f);
            stage.addActor(completeLbl);
        }

        // MENU — top-Y=674 → libgdxY = 854-674-56 = 124
        TextButton menuBtn = UiFactory.makeButton("MAIN MENU", rectStyle,
                Constants.BTN_MAIN_W, Constants.BTN_MAIN_H);
        menuBtn.setPosition((Constants.WORLD_WIDTH - Constants.BTN_MAIN_W) / 2f, 124f);
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
