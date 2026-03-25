package com.dungeonkeys000892.app9711.screens;

import com.badlogic.gdx.Screen;
import com.dungeonkeys000892.app9711.MainGame;

public class CryptScreen extends BaseGameScreen {

    public CryptScreen(MainGame game, int level) {
        super(game, 0, level);
    }

    public CryptScreen(MainGame game) {
        this(game, 0);
    }

    @Override protected String getBgPath() { return "ui/crypt_screen.png"; }

    @Override protected Screen createFreshScreen() { return new CryptScreen(game, currentLevel); }
}
