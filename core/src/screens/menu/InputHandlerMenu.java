package screens.menu;

import com.badlogic.gdx.InputProcessor;

import java.util.ArrayList;

import configuration.Configuration;
import helpers.AssetLoader;
import ui.SimpleButton;

public class InputHandlerMenu implements InputProcessor {

    private MenuWorld world;
    private ArrayList<SimpleButton> menuButtons;
    private float scaleFactorX;
    private float scaleFactorY;

    public InputHandlerMenu(MenuWorld world, float scaleFactorX,
                            float scaleFactorY) {
        this.world = world;
        this.scaleFactorX = scaleFactorX;
        this.scaleFactorY = scaleFactorY;

        menuButtons = world.getMenuObject().getMenuButtons();
    }

    @Override
    public boolean keyDown(int keycode) {

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {

        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        screenX = scaleX(screenX);
        screenY = scaleY(screenY);
        menuButtons.get(0).isTouchDown(screenX, screenY);
        menuButtons.get(1).isTouchDown(screenX, screenY);
        menuButtons.get(2).isTouchDown(screenX, screenY);
        menuButtons.get(3).isTouchDown(screenX, screenY);
        if(!AssetLoader.getAds()) menuButtons.get(4).isTouchDown(screenX, screenY);

        world.getMenuObject().getVolumeButton().isTouchDown(screenX, screenY);


        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        screenX = scaleX(screenX);
        screenY = scaleY(screenY);

        if (menuButtons.get(0).isTouchUp(screenX, screenY)) {
            world.getMenuObject().getPad().end();
            for (int i = 0; i < menuButtons.size(); i++) {
                menuButtons.get(i).end();
            }
            world.getMenuObject().getVolumeButton().end();
            menuButtons.get(0).tranToGameScreen();
            world.getActionResolver().startButtonClicked();
        } else if (menuButtons.get(1).isTouchUp(screenX, screenY)) {
            world.getActionResolver().shareGame("Try " + Configuration.gameName + "!! ");
        } else if (menuButtons.get(2).isTouchUp(screenX, screenY)) {
            world.getActionResolver().showScores();
        } else if (menuButtons.get(3).isTouchUp(screenX, screenY)) {
            world.getActionResolver().showAchievement();
        } else if (!AssetLoader.getAds()) {
            if (menuButtons.get(4).isTouchUp(screenX, screenY)) {
                world.getActionResolver().iapClick();
            }
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    private int scaleX(int screenX) {
        return (int) (screenX / scaleFactorX);
    }

    private int scaleY(int screenY) {
        return (int) (screenY / scaleFactorY);
    }

}
