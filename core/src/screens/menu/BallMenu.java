package screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import Tweens.Value;
import Tweens.ValueAccessor;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;
import helpers.AssetLoader;

import static configuration.Configuration.tail;

/**
 * Created by ManuGil on 15/01/15.
 */
public class BallMenu {
    private Vector2 position;
    private Vector2 velocity;
    private Vector2 acceleration;
    private Circle colCircle;
    private MenuWorld world;
    private float radius = 14;

    private boolean collided = false;
    private Value radiusValue = new Value();
    private TweenManager manager;
    private Tween padTween = Tween.to(radiusValue,0,0);

    private ParticleEffect particleEffect, effect;

    private Sprite sprite;

    public BallMenu(MenuWorld gameWorld) {
        this.world = gameWorld;
        this.colCircle = new Circle(MathUtils.random(20,world.gameWidth-20), MathUtils.random(20,world.gameHeight-20), radius);
        position = new Vector2(colCircle.x, colCircle.y);
        velocity = new Vector2((float)Math.random()*20, (float) (Math.random()*20));
        acceleration = new Vector2();

        Tween.registerAccessor(Value.class, new ValueAccessor());
        manager = new TweenManager();

        radiusValue.setValue(radius);
        radiusValue.setValue(0);
        particleEffect = new ParticleEffect();
        particleEffect.load(Gdx.files.internal("emitter.p"),Gdx.files.internal(""));
        particleEffect.setPosition(-100, -100);
        particleEffect.start();

        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("emitter1.p"), Gdx.files.internal(""));
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
        sprite.setSize(colCircle.radius * 2, colCircle.radius * 2);
        sprite.setPosition(colCircle.x - colCircle.radius, colCircle.y - colCircle.radius);
        collisions();
    }

    private void collisions() {
        if(this.position.x<0 + colCircle.radius){
            this.velocity.x = this.velocity.x *-1;
            this.acceleration.x = this.acceleration.x *-1;
        }else if(this.position.x>world.gameWidth-colCircle.radius){
            this.velocity.x = this.velocity.x *-1;

            this.acceleration.x = this.acceleration.x *-1;
        }else if(this.position.y<0 + colCircle.radius){
            this.velocity.y = this.velocity.y *-1;

            this.acceleration.y = this.acceleration.y *-1;
        }else if(this.position.y>world.gameHeight-colCircle.radius){
            this.velocity.y = this.velocity.y *-1;
            this.acceleration.y = this.acceleration.y *-1;
        }
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

                acceleration = new Vector2(MathUtils.random(-120,120),MathUtils.random(-120,120));
                if(tail){
                    effect.start();
                }
            }
        };
        Tween.to(radiusValue, -1, .5f).target(radius).repeatYoyo(0, 0).setCallback(cb).setCallbackTriggers(TweenCallback.COMPLETE)
                .ease(TweenEquations.easeInOutSine).start(manager);
    }

    public void finish(){
        Gdx.app.log("Radius",radiusValue.getValue()+"");
        radiusValue.setValue(colCircle.radius);

        Tween.to(radiusValue, -1, .5f).target(0).repeatYoyo(0, 0).ease(TweenEquations.easeInOutSine).start(manager);
    }
}
