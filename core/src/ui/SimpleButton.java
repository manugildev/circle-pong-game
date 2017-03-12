package ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import Tweens.Value;
import Tweens.ValueAccessor;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;
import screens.GameScreen;
import screens.menu.MenuWorld;

/**
 * Created by ManuGil on 15/01/15.
 */
public class SimpleButton {

    private final MenuWorld world;
    private float x, y, width, height;

    private TextureRegion buttonUp;
    private TextureRegion buttonDown;

    private Rectangle bounds;

    public boolean isPressed = false;

    private Value size = new Value();
    private TweenManager manager;
    private Value second = new Value();

    private Color color;
    public Sprite sprite;

    public SimpleButton(MenuWorld world, float x, float y, float width, float height,
                        TextureRegion buttonUp, TextureRegion buttonDown, Color color) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.buttonUp = buttonUp;
        this.buttonDown = buttonDown;
        this.color = color;

        bounds = new Rectangle(x - (width / 2), y - (height / 2), width, height);
        Tween.registerAccessor(Value.class, new ValueAccessor());
        manager = new TweenManager();
        sprite = new Sprite(buttonUp);
        sprite.setSize(width, height);
        sprite.setPosition(bounds.x, bounds.y);


    }

    public boolean isClicked(int screenX, int screenY) {
        return bounds.contains(screenX, screenY);
    }

    public void draw(SpriteBatch batcher) {

        if (isPressed) {
            sprite.setAlpha(0.8f);
            sprite.draw(batcher);
        } else {
            sprite.setAlpha(1f);
            sprite.draw(batcher);
        }
    }

    public boolean isTouchDown(int screenX, int screenY) {

        if (bounds.contains(screenX, screenY)) {
            isPressed = true;
            return true;
        }

        return false;
    }

    public boolean isTouchUp(int screenX, int screenY) {

        // It only counts as a touchUp if the button is in a pressed state.
        if (bounds.contains(screenX, screenY) && isPressed) {
            isPressed = false;
            return true;
        }

        // Whenever a finger is released, we will cancel any presses.
        isPressed = false;
        return false;
    }

    public void start() {
        size.setValue(0);
        Tween.to(size, -1, .5f).target(1).repeatYoyo(0, 0)
             .ease(TweenEquations.easeInOutSine).start(manager);

    }

    public void end() {
        size.setValue(1);
        Tween.to(size, -1, .5f).target(0).repeatYoyo(0, 0)
             .ease(TweenEquations.easeInOutSine).start(manager);

    }

    public void update(float delta) {
        manager.update(delta);
        bounds = new Rectangle(x - (bounds.width / 2), y - (bounds.height / 2),
                               width * size.getValue(), height * size.getValue());
        sprite.setScale(size.getValue(), size.getValue());
        sprite.setOriginCenter();
        //Gdx.app.log("Width",width+"");
    }

    public void tranToGameScreen() {
        second.setValue(0);

        TweenCallback cb = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                world.getGame()
                     .setScreen(new GameScreen(world.getGame(), world.getActionResolver()));
            }
        };
        Tween.to(second, -1, .5f).target(1).repeatYoyo(0, 0).setCallback(cb)
             .setCallbackTriggers(TweenCallback.COMPLETE)
             .ease(TweenEquations.easeInOutSine).start(manager);
        world.getMenuObject().end();
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setIsPressed(boolean bol) {
        isPressed = bol;
    }

    public void setX(float x) {
        bounds.setX(x);
        sprite.setX(x);
    }
}