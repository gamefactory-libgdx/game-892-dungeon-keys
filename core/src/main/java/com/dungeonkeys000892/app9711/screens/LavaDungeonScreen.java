package com.dungeonkeys000892.app9711.screens;

import com.badlogic.gdx.Screen;
import com.dungeonkeys000892.app9711.MainGame;

public class LavaDungeonScreen extends BaseGameScreen {

    public LavaDungeonScreen(MainGame game, int level) {
        super(game, 1, level);
    }

    public LavaDungeonScreen(MainGame game) {
        this(game, 0);
    }

    @Override protected String getBgPath() { return "ui/lava_dungeon_screen.png"; }

    @Override protected Screen createFreshScreen() { return new LavaDungeonScreen(game, currentLevel); }
}
