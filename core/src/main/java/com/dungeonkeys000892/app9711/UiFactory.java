package com.dungeonkeys000892.app9711;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class UiFactory {

    /**
     * Rectangle button style — standard "action" button used on menu screens.
     */
    public static TextButton.TextButtonStyle makeRectStyle(AssetManager mgr, BitmapFont font) {
        TextButton.TextButtonStyle s = new TextButton.TextButtonStyle();
        s.font  = font;
        s.up    = new TextureRegionDrawable(new TextureRegion(
                mgr.get("ui/buttons/button_rectangle_depth_gradient.png", Texture.class)));
        s.down  = new TextureRegionDrawable(new TextureRegion(
                mgr.get("ui/buttons/button_rectangle_depth_flat.png", Texture.class)));
        return s;
    }

    /**
     * Round button style — used for icon-sized buttons (pause, back arrow, etc.).
     */
    public static TextButton.TextButtonStyle makeRoundStyle(AssetManager mgr, BitmapFont font) {
        TextButton.TextButtonStyle s = new TextButton.TextButtonStyle();
        s.font  = font;
        s.up    = new TextureRegionDrawable(new TextureRegion(
                mgr.get("ui/buttons/button_round_depth_gradient.png", Texture.class)));
        s.down  = new TextureRegionDrawable(new TextureRegion(
                mgr.get("ui/buttons/button_round_depth_flat.png", Texture.class)));
        return s;
    }

    /**
     * Convenience factory — creates a sized rectangle TextButton in one call.
     */
    public static TextButton makeButton(String label,
                                        TextButton.TextButtonStyle style,
                                        float w, float h) {
        TextButton btn = new TextButton(label, style);
        btn.setSize(w, h);
        return btn;
    }

    /**
     * Convenience factory — creates a sized round TextButton in one call.
     */
    public static TextButton makeRoundButton(String label,
                                             TextButton.TextButtonStyle style,
                                             float size) {
        TextButton btn = new TextButton(label, style);
        btn.setSize(size, size);
        return btn;
    }
}
