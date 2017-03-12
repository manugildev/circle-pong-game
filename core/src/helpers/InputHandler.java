package helpers;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

import java.util.ArrayList;

import gameworld.GameWorld;
import ui.SimpleButton;

public class InputHandler implements InputProcessor {

    private GameWorld world;
    private ArrayList<SimpleButton> menuButtons;
    private float scaleFactorX;
    private float scaleFactorY;

    int activeTouch = 0;

    public InputHandler(GameWorld world, float scaleFactorX,
                        float scaleFactorY) {
        this.world = world;
        this.scaleFactorX = scaleFactorX;
        this.scaleFactorY = scaleFactorY;
    }

    @Override
    public boolean keyDown(int keycode) {

        if (keycode == Keys.LEFT) {
            world.getPad().clickRight();
            world.getArrowL().isTouchDown();

        }

        if (keycode == Keys.RIGHT) {
            world.getPad().clickLeft();
            world.getArrowR().isTouchDown();
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {

        if (keycode == Keys.LEFT) {
            world.getPad().noClick();
            world.getArrowL().isTouchUp();
        }

        if (keycode == Keys.RIGHT) {
            world.getPad().noClick();
            world.getArrowR().isTouchUp();
        }

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
        activeTouch++;
        world.getVolumeButton().isTouchDown(screenX,screenY);
        if(!world.finish){
            if (screenX >= world.gameWidth / 2) {
                world.getPad().noClick();
                world.getPad().clickLeft();
                world.getArrowR().isTouchDown();
            } else {
                world.getPad().noClick();
                world.getPad().clickRight();
                world.getArrowL().isTouchDown();
            }
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        screenX = scaleX(screenX);
        screenY = scaleY(screenY);
        activeTouch--;
        if (activeTouch == 0) {
            world.getPad().noClick();
        } else {
            if (screenX >= world.gameWidth / 2) {
                world.getPad().noClick();
                world.getPad().clickRight();
                world.getArrowR().isTouchUp();
            } else {
                world.getPad().noClick();
                world.getPad().clickLeft();
                world.getArrowL().isTouchUp();
            }
        }

        if (screenX >= world.gameWidth / 2) {
            world.getArrowR().isTouchUp();
        } else {
            world.getArrowL().isTouchUp();
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
