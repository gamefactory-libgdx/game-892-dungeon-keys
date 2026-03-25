package com.dungeonkeys000892.app9711;

public class Constants {

    // ── World dimensions ────────────────────────────────────────────────────
    public static final float WORLD_WIDTH  = 480f;
    public static final float WORLD_HEIGHT = 854f;

    // ── Tile / maze grid ────────────────────────────────────────────────────
    public static final int   TILE_SIZE      = 48;   // world units per tile
    public static final int   MAZE_COLS      = 9;    // tiles across (480 / ~53)
    public static final int   MAZE_ROWS      = 15;   // tiles down (excluding HUD)
    public static final float HUD_HEIGHT     = 80f;  // pixels reserved for top HUD

    // ── Player ──────────────────────────────────────────────────────────────
    public static final float PLAYER_SIZE        = 40f;   // world units
    public static final float MOVE_DURATION      = 0.15f; // seconds per tile step

    // ── Enemies ─────────────────────────────────────────────────────────────
    public static final float ENEMY_SIZE         = 40f;
    public static final float ENEMY_MOVE_INTERVAL = 0.6f; // seconds between enemy steps

    // ── Keys & exit ─────────────────────────────────────────────────────────
    public static final int   KEYS_PER_LEVEL     = 5;
    public static final float KEY_SIZE           = 28f;
    public static final float EXIT_SIZE          = 48f;

    // ── Score ────────────────────────────────────────────────────────────────
    public static final int   SCORE_KEY          = 100;
    public static final int   SCORE_LEVEL_BONUS  = 500;
    public static final int   SCORE_TIME_BONUS   = 10;  // per second remaining

    // ── Levels per dungeon ───────────────────────────────────────────────────
    public static final int   LEVELS_PER_DUNGEON = 10;

    // ── Shop prices ─────────────────────────────────────────────────────────
    public static final int   SKIN_ROGUE_PRICE   = 100;
    public static final int   SKIN_WIZARD_PRICE  = 100;
    public static final int   SKIN_SKELETON_PRICE = 150;
    public static final int   SKIN_PALADIN_PRICE  = 150;
    public static final int   SKIN_DARKMAGE_PRICE = 200;

    // ── HUD button sizes ─────────────────────────────────────────────────────
    public static final float BTN_MAIN_W    = 280f;
    public static final float BTN_MAIN_H    = 56f;
    public static final float BTN_SMALL_W   = 120f;
    public static final float BTN_SMALL_H   = 44f;
    public static final float BTN_ROUND_SZ  = 56f;

    // ── Colors ───────────────────────────────────────────────────────────────
    public static final float COLOR_BG_R    = 0x1A / 255f;
    public static final float COLOR_BG_G    = 0x0A / 255f;
    public static final float COLOR_BG_B    = 0x00 / 255f;

    // ── SharedPreferences keys ───────────────────────────────────────────────
    public static final String PREFS_NAME      = "DungeonKeysPrefs";
    public static final String PREF_HIGH_SCORE = "highScore";
    public static final String PREF_MUSIC      = "musicEnabled";
    public static final String PREF_SFX        = "sfxEnabled";
    public static final String PREF_VIBRATION  = "vibrationEnabled";
    public static final String PREF_SKIN       = "selectedSkin";
    public static final String PREF_BEST_CRYPT      = "bestScoreCrypt";
    public static final String PREF_BEST_LAVA       = "bestScoreLava";
    public static final String PREF_BEST_ICE        = "bestScoreIce";
    public static final String PREF_COINS      = "totalCoins";

    // Leaderboard keys (top 10 entries — name + score pairs)
    public static final String PREF_LEADER_SCORE_PREFIX = "leader_score_";
    public static final int    LEADERBOARD_SIZE = 10;
}
