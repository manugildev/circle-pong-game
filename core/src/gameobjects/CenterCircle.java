package gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;

import Tweens.Value;
import Tweens.ValueAccessor;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;
import gameworld.GameWorld;
import helpers.AssetLoader;
import helpers.FlatColors;

/**
 * Created by ManuGil on 18/01/15.
 */
public class CenterCircle {

    public Circle colCircle;
    public Value radiusValue = new Value();
    public Value scale = new Value();
    public float radius = 250;
    public TweenManager manager;
    private GameWorld world;
    private Sprite sprite;
    private Tween padTween;

    public CenterCircle(GameWorld world) {
        this.world = world;
        this.colCircle = new Circle(world.gameWidth / 2, world.gameHeight / 2, 0);
        Tween.registerAccessor(Value.class, new ValueAccessor());
        manager = new TweenManager();
        radiusValue.setValue(250 - world.getPad().getCircle().radius);
        radiusValue.setValue(0);
        scale.setValue(3f);
        scale.setValue(0.1f);
        sprite = new Sprite(AssetLoader.dot);
        padTween = Tween.to(radiusValue, 0, 0);

        sprite.setOriginCenter();
        sprite.setColor(FlatColors.WHITE);
        sprite.setAlpha(0.2f);
    }

    public void update(float delta) {
        manager.update(delta);
        AssetLoader.font.setScale(scale.getValue(), -scale.getValue());

        sprite.setPosition(colCircle.x - radiusValue.getValue(), colCircle.y - radiusValue.getValue());
        sprite.setSize(radiusValue.getValue() * 2, radiusValue.getValue() * 2);
        colCircle.setRadius(radiusValue.getValue());
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        //shapeRenderer.setColor(world.parseColor("#FFFFFF",0.3f));
        //shapeRenderer.circle(colCircle.x,colCircle.y,colCircle.radius);
        shapeRenderer.end();

        batch.begin();
        sprite.draw(batch);
    }

    public void end() {
        Tween.to(radiusValue, -1, 0.5f).target(0).repeatYoyo(0, 0)
                .ease(TweenEquations.easeInOutSine).start(manager);
        Tween.to(scale, -1, 0.5f).target(0.1f).repeatYoyo(0, 0)
                .ease(TweenEquations.easeInOutSine).start(manager);


    }

    public void start() {
        radiusValue.setValue(0);
        Tween.to(radiusValue, -1, 0.7f).target(radius).repeatYoyo(0, 0)
                .ease(TweenEquations.easeInOutSine).start(manager);

        scale.setValue(0.1f);
        Tween.to(scale, -1, 0.5f).target(3f).repeatYoyo(0, 0)
                .ease(TweenEquations.easeInOutSine).start(manager);
    }

    public void paddleCollide() {
        if(radiusValue.getValue() ==250){
        radiusValue.setValue(250);
        padTween.kill();
        padTween = Tween.to(radiusValue, -1, .1f).target(radiusValue.getValue() + 5).repeatYoyo(1, 0)
                .ease(TweenEquations.easeInOutBounce).start(manager);}
    }
}
