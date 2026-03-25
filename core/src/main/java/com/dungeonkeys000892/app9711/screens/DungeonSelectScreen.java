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

public class DungeonSelectScreen extends ScreenAdapter {

    private static final String BG = "ui/dungeon_select.png";

    private final MainGame game;
    private OrthographicCamera camera;
    private StretchViewport viewport;
    private Stage stage;
    private Texture bgTex;

    public DungeonSelectScreen(MainGame game) {
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

        // BACK button — top-Y=20 → libgdxY = 854-20-44 = 790
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
        Label titleLbl = new Label("SELECT DUNGEON", titleStyle);
        titleLbl.setPosition((Constants.WORLD_WIDTH - titleLbl.getPrefWidth()) / 2f, 700f);
        stage.addActor(titleLbl);

        // Three dungeon cards — top-Y=290 → libgdxY = 854-290-200 = 364
        // Card width 130, height 200
        // Left card x=20, center x=175, right x=330
        float cardY = 364f, cardW = 130f, cardH = 200f;
        float[] cardX = {20f, 175f, 330f};
        String[] names  = {"STONE\nCRYPT", "LAVA\nDUNGEON", "ICE\nCAVERN"};
        Color[]  colors = {
            new Color(0.78f, 0.66f, 0.29f, 1f),   // gold
            new Color(0.88f, 0.33f, 0.31f, 1f),   // ember red
            new Color(0.31f, 0.81f, 1.00f, 1f)    // glacial cyan
        };

        int[] bestScores = {
            Gdx.app.getPreferences(Constants.PREFS_NAME).getInteger(Constants.PREF_BEST_CRYPT, 0),
            Gdx.app.getPreferences(Constants.PREFS_NAME).getInteger(Constants.PREF_BEST_LAVA,  0),
            Gdx.app.getPreferences(Constants.PREFS_NAME).getInteger(Constants.PREF_BEST_ICE,   0)
        };

        Label.LabelStyle bodyStyle  = new Label.LabelStyle(game.fontBody,  Color.WHITE);
        Label.LabelStyle smallStyle2 = new Label.LabelStyle(game.fontSmall, Color.WHITE);

        for (int i = 0; i < 3; i++) {
            final int dungeonIdx = i;

            TextButton card = UiFactory.makeButton(names[i], rectStyle, cardW, cardH);
            card.setPosition(cardX[i], cardY);
            card.getLabel().setColor(colors[i]);
            card.getLabel().setWrap(false);
            card.addListener(new ChangeListener() {
                @Override public void changed(ChangeEvent event, Actor actor) {
                    playSound("sounds/sfx/sfx_button_click.ogg");
                    if      (dungeonIdx == 0) game.setScreen(new CryptScreen(game, 0));
                    else if (dungeonIdx == 1) game.setScreen(new LavaDungeonScreen(game, 0));
                    else                      game.setScreen(new IceCavernScreen(game, 0));
                }
            });
            stage.addActor(card);

            // Best score label below card
            Label bestLbl = new Label("BEST: " + bestScores[i], smallStyle2);
            bestLbl.setPosition(cardX[i], cardY - 28f);
            stage.addActor(bestLbl);
        }
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
