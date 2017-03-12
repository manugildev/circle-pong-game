package gameobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

import Tweens.Value;
import Tweens.ValueAccessor;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;
import gameworld.GameWorld;
import helpers.AssetLoader;

import static configuration.Configuration.tail;

/**
 * Created by ManuGil on 15/01/15.
 */
public class Ball {

    private Vector2 position;
    private Vector2 velocity;

    private Vector2 acceleration;

    private Circle colCircle;
    private GameWorld world;

    private float radius = 14;

    private boolean collided = false;

    private Value radiusValue = new Value();
    private TweenManager manager;
    private Tween padTween = Tween.to(radiusValue,0,0);

    private ParticleEffect particleEffect, effect;

    private Sprite sprite;

    public Ball(GameWorld gameWorld) {
        this.world = gameWorld;
        this.colCircle = new Circle(world.gameWidth / 2, world.gameHeight / 2, radius);
        position = new Vector2(colCircle.x, colCircle.y);
        velocity = new Vector2(0, 0);
        acceleration = new Vector2();

        Tween.registerAccessor(Value.class, new ValueAccessor());
        manager = new TweenManager();

        radiusValue.setValue(radius);
        radiusValue.setValue(0);
        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("emitter.p"),Gdx.files.internal(""));
        particleEffect.setPosition(-100,-100);
        particleEffect.start();

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("emitter1.p"),Gdx.files.internal(""));
        effect.setPosition(-100,-100);


        sprite = new Sprite(AssetLoader.ball);


    }

    public void update(float delta) {
        manager.update(delta);
        particleEffect.update(delta);

        if(tail){
        effect.update(delta);
        effect.setPosition(colCircle.x,colCircle.y);
        }

        if(velocity.len()<350){
            velocity.add(acceleration.cpy().scl(delta));
        }

        position.add(velocity.cpy().scl(delta));
        colCircle.setPosition(position);
        colCircle.setRadius(radiusValue.getValue());
        sprite.setSize(colCircle.radius*2,colCircle.radius*2);
        sprite.setPosition(colCircle.x-colCircle.radius,colCircle.y-colCircle.radius);
    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        particleEffect.draw(batch);
        effect.draw(batch);
        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
       // shapeRenderer.setColor(Color.GREEN);
        //shapeRenderer.circle(colCircle.x, colCircle.y, colCircle.radius);

        shapeRenderer.end();
        batch.begin();
        sprite.setColor(Color.WHITE);
        sprite.draw(batch);
        sprite.setColor(Color.WHITE);

    }

    public Circle getColCircle() {
        return colCircle;
    }

    public void collide() {
        //velocity.y = velocity.cpy().y * -1;
    }

    public void paddleCollide(){
        if(particleEffect.isComplete()){
            particleEffect.setPosition(colCircle.x,colCircle.y);
            particleEffect.start();
        }


        radiusValue.setValue(radius);
        padTween.kill();
        padTween = Tween.to(radiusValue, -1, .05f).target(radius + 5).repeatYoyo(1, 0)
                .ease(TweenEquations.easeOutSine).start(manager);
    }

    public boolean outOfScreen() {
        if (position.x >= world.gameWidth || position.x < 0 || position.y < 0 || position.y > world.gameHeight) {
            return true;
        }
        return false;
    }

    public void setPosition(Vector2 pos) {
        position = new Vector2(pos.x, pos.y);
    }

    public boolean hasCollided() {
        return collided;
    }

    public void setCollided(boolean col){
        collided = col;
    }

    public Vector2 getVelocity(){
        return velocity;
    }

    public void setVelocity(Vector2 vec){
        velocity = vec;
    }

    public Vector2 getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Vector2 acceleration) {
        this.acceleration = acceleration;
    }

    public void start(){
        Gdx.app.log("Radius",radiusValue.getValue()+"");
        radiusValue.setValue(0);
        TweenCallback cb = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                world.getPad().start();
                world.getCenterCircle().start();
                acceleration = new Vector2(0,120);
                if(tail){
                    effect.start();
                }
            }
        };
        Tween.to(radiusValue, -1, .5f).target(radius).repeatYoyo(0, 0).setCallback(cb).setCallbackTriggers(TweenCallback.COMPLETE)
                .ease(TweenEquations.easeInOutSine).start(manager);
    }
}
