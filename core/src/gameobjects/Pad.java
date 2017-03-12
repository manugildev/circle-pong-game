package gameobjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import Tweens.Value;
import Tweens.ValueAccessor;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;
import gameworld.GameWorld;
import helpers.AssetLoader;

/**
 * Created by ManuGil on 15/01/15.
 */
public class Pad {

    private GameWorld world;
    Circle colCircle, colCircle1, colCircle2, colCircle3, colCircle4, colCircle5, colCircle6, colCircle7, colCircle8;

    private Vector2 position;
    private Vector2 velocity;
    private Vector2 acceleration;

    private int radius = 8;
    float distanceToCenter;
    private float rotation;

    private float angle;
    private float angVel;
    private float angAcc;

    private Value dtcVal = new Value();
    private Value spriteSize = new Value();

    private TweenManager manager;

    private ArrayList<Circle> colCircles = new ArrayList<Circle>();
    private Tween padTween = Tween.to(dtcVal,0,0);
    private Sprite sprite;

    public Pad(GameWorld gameWorld) {
        this.world = gameWorld;
        this.position = new Vector2(0, 0);

        for (int i = 0; i < 7; i++) {
            colCircle = new Circle(0, 0, radius);
            colCircles.add(colCircle);
        }
        distanceToCenter = 0;

        Tween.registerAccessor(Value.class, new ValueAccessor());
        manager = new TweenManager();
        dtcVal.setValue(distanceToCenter);
        spriteSize.setValue(0);

        sprite = new Sprite(AssetLoader.pad);
        //sprite.setPosition(world.gameWidth/2,world.gameHeight/2);
        sprite.setOriginCenter();
        sprite.setScale(0);
        sprite.rotate(180);
    }

    public void update(float delta) {
        manager.update(delta);
        sprite.setScale(spriteSize.getValue());
        angVel += angAcc * delta;
        angle += angVel * delta;
        distanceToCenter = world.getCenterCircle().colCircle.radius;
        for (int i = 0; i < colCircles.size(); i++) {
            position = new Vector2(calculatePosition(i));
            colCircles.get(i).set(position, radius);
        }

        sprite.setPosition(colCircles.get(3).x-(sprite.getWidth()/2),colCircles.get(3).y-(sprite.getHeight()/2));
        sprite.setRotation(angle+180);

    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer) {
        batch.end();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.BLUE);


        for (int i = 0; i < colCircles.size(); i++) {
            //shapeRenderer.circle(colCircles.get(i).x, colCircles.get(i).y, colCircles.get(i).radius);
            //shapeRenderer.setColor(Color.GRAY);
            //shapeRenderer.line(colCircles.get(i).x, colCircles.get(i).y, world.gameWidth / 2, world.gameHeight / 2);
        }
        shapeRenderer.end();
        batch.begin();
        sprite.draw(batch);

    }

    public void paddleCollide(){
        dtcVal.setValue(250);
        padTween.kill();
        padTween = Tween.to(dtcVal, -1, .1f).target(dtcVal.getValue() + 5).repeatYoyo(1, 0)
                .ease(TweenEquations.easeInOutBounce).start(manager);
    }

    private Vector2 calculatePosition(int i) {
        float cx = world.gameWidth / 2;
        float cy = world.gameHeight / 2;
        return new Vector2((float) (cx + distanceToCenter
                * Math.sin(Math.toRadians(-angle - (4 * i)+12))),
                (float) (cy + distanceToCenter
                        * Math.cos(Math.toRadians(-angle - (4 * i)+12))));
    }


    public Vector2 getPosition() {
        return new Vector2(position.x - radius, position.y - radius);
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public float getAngularVelocity() {
        return angVel;
    }

    public void setAngularVelocity(float angularVelocity) {
        this.angVel = angularVelocity;
    }

    public float getAngularAcceleration() {
        return angAcc;
    }

    public void setAngularAcceleration(float angularAcceleration) {
        this.angAcc = angularAcceleration;
    }

    public Circle getCircle() {
        return colCircle;
    }

    public void setCircle(Circle circle) {
        this.colCircle = circle;
    }


    public ArrayList<Circle> getcolCircles() {
        return colCircles;
    }

    public void noClick() {
        setAngularVelocity(0);
    }

    public void clickLeft() {
        setAngularVelocity(-200);
    }

    public void clickRight() {
        setAngularVelocity(200);
    }

    public Vector2 returnNormal(int i) {
        Vector2 vector = new Vector2(world.gameWidth / 2 - colCircles.get(i).x, world.gameHeight / 2 - colCircles.get(i).y);

        return vector;
    }

    public void end(){
        Tween.to(dtcVal, -1, .4f).target(0).repeatYoyo(0, 0)
                .ease(TweenEquations.easeInOutSine).start(manager);
        Tween.to(spriteSize, -1, .5f).target(0).repeatYoyo(0, 0)
                .ease(TweenEquations.easeOutSine).start(manager);
        Tween.to(spriteSize, -1, .5f).target(0).repeatYoyo(0, 0)
                .ease(TweenEquations.easeOutSine).start(manager);
    }
    public void start(){
        dtcVal.setValue(0.1f);
        Tween.to(dtcVal, -1, 1f).target(250).repeatYoyo(0, 0)
                .ease(TweenEquations.easeOutSine).start(manager);

        Tween.to(spriteSize, -1, 1f).target(1).repeatYoyo(0, 0)
                .ease(TweenEquations.easeOutSine).start(manager);
    }

}
