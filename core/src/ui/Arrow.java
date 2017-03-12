package ui;

import com.badlogic.gdx.Gdx;
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
import gameworld.GameWorld;
import screens.GameScreen;
import screens.menu.MenuWorld;

/**
 * Created by ManuGil on 15/01/15.
 */
public class Arrow {

    private final GameWorld world;
    private float x, y, width, height;

    private TextureRegion buttonUp;
    private TextureRegion buttonDown;

    private Rectangle bounds;

    private boolean isPressed = false;

    private Value size = new Value();
    private TweenManager manager;
    private Value second = new Value();

    private Sprite sprite;
    private Value r = new Value();

    public Arrow(GameWorld world, float x, float y, float width, float height,
                 TextureRegion buttonUp, TextureRegion buttonDown) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.buttonUp = buttonUp;
        this.buttonDown = buttonDown;

        bounds = new Rectangle(x, y , width, height);
        Tween.registerAccessor(Value.class, new ValueAccessor());
        manager = new TweenManager();
        sprite = new Sprite(buttonUp);
        r.setValue(bounds.x);
        sprite.setPosition(bounds.x, bounds.y);
        sprite.setSize(width, height);


    }

    public boolean isClicked(int screenX, int screenY) {
        return bounds.contains(screenX, screenY);
    }

    public void draw(SpriteBatch batcher) {
        //Gdx.app.log("Button", isPressed +"");
        if (isPressed) {
            sprite.setColor(world.parseColor("#bdc3c7",1f));
            sprite.draw(batcher);
            sprite.setColor(Color.WHITE);
            //batcher.draw(buttonDown, x-(bounds.width/2), y-(bounds.height/2), bounds.width, bounds.height);
        } else {
            sprite.setColor(Color.WHITE);
            sprite.draw(batcher);
            //batcher.draw(buttonUp, x-(bounds.width/2), y-(bounds.height/2), bounds.width, bounds.height);
        }
    }

    public void isTouchDown() {
        isPressed = true;
    }

    public void isTouchUp() {

        // It only counts as a touchUp if the button is in a pressed state.
        isPressed = false;
    }

    public void goToLeft(float target){
        Tween.to(r, -1, .5f).target(target).repeatYoyo(0, 0)
                .ease(TweenEquations.easeInOutSine).start(manager);
    }

    public void goToRight(float target){
        Tween.to(r, -1, .5f).target(target).repeatYoyo(0, 0)
                .ease(TweenEquations.easeInOutSine).start(manager);
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
        sprite.setX(r.getValue());
        bounds = new Rectangle(x , y, width * size.getValue(), height * size.getValue());

        //Gdx.app.log("Width",width+"");
    }

    public void tranToGameScreen() {

    }

    public Rectangle getBounds() {
        return bounds;
    }
}