package com.dungeonkeys000892.app9711;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.dungeonkeys000892.app9711.screens.MainMenuScreen;

public class MainGame extends Game {

    // ── Shared rendering resources ─────────────────────────────────────────
    public SpriteBatch  batch;
    public AssetManager manager;

    // ── Fonts (generated once, shared across all screens) ─────────────────
    public BitmapFont fontTitle;  // Iomanoid.otf — large headings / scores
    public BitmapFont fontBody;   // Skeleboom.ttf — buttons / labels
    public BitmapFont fontSmall;  // Skeleboom.ttf small — secondary labels
    public BitmapFont fontScore;  // Iomanoid.otf large — score display

    // ── Audio state ────────────────────────────────────────────────────────
    public boolean musicEnabled     = true;
    public boolean sfxEnabled       = true;
    public boolean vibrationEnabled = true;
    public Music   currentMusic     = null;

    // ── Skin selection ─────────────────────────────────────────────────────
    public int selectedSkin = 0; // 0 = Knight (default)

    // ──────────────────────────────────────────────────────────────────────
    @Override
    public void create() {
        batch   = new SpriteBatch();
        manager = new AssetManager();

        generateFonts();
        loadCoreAssets();
        manager.finishLoading();

        // Restore persisted settings
        com.badlogic.gdx.Preferences prefs =
                Gdx.app.getPreferences(Constants.PREFS_NAME);
        musicEnabled     = prefs.getBoolean(Constants.PREF_MUSIC,     true);
        sfxEnabled       = prefs.getBoolean(Constants.PREF_SFX,       true);
        vibrationEnabled = prefs.getBoolean(Constants.PREF_VIBRATION, true);
        selectedSkin     = prefs.getInteger(Constants.PREF_SKIN,      0);

        setScreen(new MainMenuScreen(this));
    }

    // ── Font generation ────────────────────────────────────────────────────
    private void generateFonts() {
        FreeTypeFontGenerator titleGen = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/Iomanoid.otf"));
        FreeTypeFontGenerator bodyGen  = new FreeTypeFontGenerator(
                Gdx.files.internal("fonts/Skeleboom.ttf"));

        FreeTypeFontGenerator.FreeTypeFontParameter p =
                new FreeTypeFontGenerator.FreeTypeFontParameter();
        p.borderColor = new Color(0f, 0f, 0f, 0.85f);

        // Title font — 52px
        p.size        = 52;
        p.borderWidth = 3;
        fontTitle = titleGen.generateFont(p);

        // Score font — 64px
        p.size        = 64;
        p.borderWidth = 3;
        fontScore = titleGen.generateFont(p);

        // Body font — 28px
        p.size        = 28;
        p.borderWidth = 2;
        fontBody = bodyGen.generateFont(p);

        // Small font — 18px
        p.size        = 18;
        p.borderWidth = 1;
        fontSmall = bodyGen.generateFont(p);

        titleGen.dispose();
        bodyGen.dispose();
    }

    // ── Core asset loading (music + SFX + UI buttons) ─────────────────────
    private void loadCoreAssets() {
        // Music
        manager.load("sounds/music/music_menu.ogg",      Music.class);
        manager.load("sounds/music/music_gameplay.ogg",  Music.class);
        manager.load("sounds/music/music_game_over.ogg", Music.class);

        // SFX
        manager.load("sounds/sfx/sfx_button_click.ogg",   Sound.class);
        manager.load("sounds/sfx/sfx_button_back.ogg",    Sound.class);
        manager.load("sounds/sfx/sfx_toggle.ogg",         Sound.class);
        manager.load("sounds/sfx/sfx_coin.ogg",           Sound.class);
        manager.load("sounds/sfx/sfx_hit.ogg",            Sound.class);
        manager.load("sounds/sfx/sfx_game_over.ogg",      Sound.class);
        manager.load("sounds/sfx/sfx_level_complete.ogg", Sound.class);
        manager.load("sounds/sfx/sfx_power_up.ogg",       Sound.class);

        // UI buttons
        manager.load("ui/buttons/button_rectangle_depth_gradient.png", Texture.class);
        manager.load("ui/buttons/button_rectangle_depth_flat.png",     Texture.class);
        manager.load("ui/buttons/button_round_depth_gradient.png",     Texture.class);
        manager.load("ui/buttons/button_round_depth_flat.png",         Texture.class);
        manager.load("ui/buttons/star.png",         Texture.class);
        manager.load("ui/buttons/star_outline.png", Texture.class);
    }

    // ── Music helpers ──────────────────────────────────────────────────────
    /**
     * Play a looping music track. If the same track is already playing, does
     * nothing (prevents restarting when navigating between sibling screens).
     */
    public void playMusic(String path) {
        Music requested = manager.get(path, Music.class);
        if (requested == currentMusic && currentMusic.isPlaying()) return;
        if (currentMusic != null) currentMusic.stop();
        currentMusic = requested;
        currentMusic.setLooping(true);
        currentMusic.setVolume(0.7f);
        if (musicEnabled) currentMusic.play();
    }

    /**
     * Play a one-shot music track (game-over jingle — never loops).
     */
    public void playMusicOnce(String path) {
        if (currentMusic != null) currentMusic.stop();
        currentMusic = manager.get(path, Music.class);
        currentMusic.setLooping(false);
        currentMusic.setVolume(0.7f);
        if (musicEnabled) currentMusic.play();
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────
    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        manager.dispose();
        fontTitle.dispose();
        fontBody.dispose();
        fontSmall.dispose();
        fontScore.dispose();
    }
}
