package com.dungeonkeys000892.app9711.screens;

import com.badlogic.gdx.Screen;
import com.dungeonkeys000892.app9711.MainGame;

public class IceCavernScreen extends BaseGameScreen {

    public IceCavernScreen(MainGame game, int level) {
        super(game, 2, level);
    }

    public IceCavernScreen(MainGame game) {
        this(game, 0);
    }

    @Override protected String getBgPath() { return "ui/ice_cavern_screen.png"; }

    @Override protected Screen createFreshScreen() { return new IceCavernScreen(game, currentLevel); }
}
