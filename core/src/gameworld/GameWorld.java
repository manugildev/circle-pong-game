package gameworld;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import Tweens.Value;
import Tweens.ValueAccessor;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;
import circlepong.ActionResolver;
import circlepong.CirclePongGame;
import configuration.Configuration;
import gameobjects.Ball;
import gameobjects.CenterCircle;
import gameobjects.Pad;
import helpers.AssetLoader;
import helpers.ColorManager;
import helpers.FlatColors;
import screens.Menu;
import ui.Arrow;
import ui.MuteButton;


public class GameWorld {

    private final TweenManager manager;
    private final CirclePongGame cpgame;
    private final MuteButton volumeButton;
    private final float buttonSize = 75;
    public float gameWidth;
    public float gameHeight;
    private int velocity = 0;

    private Pad pad;
    private Ball ball;
    private CenterCircle centerCircle;
    private int score;
    public boolean finish;

    private Value secondValue = new Value();
    private Value fiveValue = new Value();
    private TweenCallback cb;
    public ColorManager colorManager;
    private Value distance = new Value();
    private ActionResolver actionResolver;

    private Arrow arrowL, arrowR;

    public GameWorld(CirclePongGame cpgame, ActionResolver actionresolver, float gameWidth, float gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.cpgame = cpgame;
        this.actionResolver = actionresolver;
        pad = new Pad(this);
        ball = new Ball(this);
        centerCircle = new CenterCircle(this);
        finish = false;

        colorManager = new ColorManager();
        ball.start();

        Tween.registerAccessor(Value.class, new ValueAccessor());
        manager = new TweenManager();
        secondValue.setValue(0);
        distance.setValue(0);

        arrowL = new Arrow(this, -100, gameHeight / 2 + 300, AssetLoader.arrowLT.getWidth()/2, AssetLoader.arrowLT.getHeight()/2, AssetLoader.arrowL, AssetLoader.arrowL);
        arrowR = new Arrow(this, gameWidth +100, gameHeight / 2 + 300, AssetLoader.arrowLT.getWidth()/2, AssetLoader.arrowLT.getHeight()/2, AssetLoader.arrowR, AssetLoader.arrowR);

        TweenCallback cb1 = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                colorManager.start();

            }
        };
        Tween.to(fiveValue, -1, 3f).target(1).repeatYoyo(500, 0).setCallback(cb1).setCallbackTriggers(TweenCallback.END)
                .ease(TweenEquations.easeInSine).start(manager);

        arrowL.goToRight(gameWidth / 2 - 220-AssetLoader.arrowLT.getWidth()/2);
        arrowR.goToRight(gameWidth / 2 +220);
        colorManager.start();


        this.volumeButton = new MuteButton(20 + this.buttonSize,20+ this.buttonSize, this.buttonSize, this.buttonSize, AssetLoader.soundI, AssetLoader.muteI,
                FlatColors.WHITE);
        volumeButton.start();

        if (AssetLoader.music.isPlaying()) {
            getVolumeButton().isPressed = false;
        }else{

            getVolumeButton().isPressed =true;
        }


    }

    public void update(float delta) {
        manager.update(delta);
        colorManager.update(delta);
        volumeButton.update(delta);
        arrowR.update(delta);
        arrowL.update(delta);
        pad.update(delta);
        ball.update(delta);
        centerCircle.update(delta);
        collisions();
    }

    private void collisions() {
        if (!ball.hasCollided()) {
            for (int i = 0; i < pad.getcolCircles().size(); i++) {
                if (Intersector.overlaps(pad.getcolCircles().get(i), ball.getColCircle())) {
                    ball.collide();
                    ball.setCollided(true);
                    //Gdx.app.log("Angle", ball.getVelocity().toString());
                    //double perp = 2.0 * ball.getVelocity().cpy().dot(pad.returnNormal(i));
                    //Vector2 reflectDir = ball.getVelocity().cpy().sub((pad.returnNormal(i).scl((float) perp))).scl(1);
                    float newAngle = getAngle2Vecs(ball.getVelocity(), pad.returnNormal(i));

                    //Gdx.app.log("Angle", newAngle + "");
                    ball.setVelocity(new Vector2(gameWidth / 2 - ball.getColCircle().x, gameHeight / 2 - ball.getColCircle().y));

                    int rand = (int) Math.random() * 90 + 5;
                    if (pad.getAngularVelocity() < 0) {
                        ball.setVelocity(ball.getVelocity().cpy().rotate((float) (rand + Math.random() * 50)));
                    } else if (pad.getAngularVelocity() > 0) {
                        ball.setVelocity(ball.getVelocity().cpy().rotate((float) (-rand - Math.random() * 50)));
                    } else {

                        ball.setVelocity(ball.getVelocity().cpy().rotate(Math.random() < 0.5 ? -rand : rand));
                    }

                        if (score < 20) {
                            ball.setVelocity(ball.getVelocity().cpy().scl(Configuration.VELOCITY_OVER_0));
                        } else if(score>=20 && score<50){
                            ball.setVelocity(ball.getVelocity().cpy().scl(Configuration.VELOCITY_OVER_20));
                        }else if(score>=50 && score<75){
                            ball.setVelocity(ball.getVelocity().cpy().scl(Configuration.VELOCITY_OVER_50));
                        }else if(score>=75 && score<100){
                            ball.setVelocity(ball.getVelocity().cpy().scl(Configuration.VELOCITY_OVER_75));
                        }else{
                            ball.setVelocity(ball.getVelocity().cpy().scl(Configuration.VELOCITY_OVER_100));
                        }

                    //Gdx.app.log("VEL",ball.getVelocity().len() + "");

                    //EFFECTS
                    ball.paddleCollide();
                    centerCircle.paddleCollide();

                    if (secondValue.getValue() == 1) {
                        score++;
                        AssetLoader.bounce.play();
                    }


                    secondValue.setValue(0);
                    Tween.to(secondValue, -1, 0.1f).target(1).repeatYoyo(0, 0)
                            .ease(TweenEquations.easeInSine).start(manager);
                }

            }
        } else {
            for (int i = 0; i < pad.getcolCircles().size(); i++) {
                if (!Intersector.overlaps(pad.getcolCircles().get(i), ball.getColCircle())) {
                    ball.setCollided(false);
                } else {
                    //ball.setCollided(true);
                }
            }
        }

        if (!Intersector.overlaps(ball.getColCircle(), new Rectangle(-50, 50, gameWidth + 50, gameHeight + 50)) && !finish) {
            finishGame();
        }

    }

    private void finishGame() {
        finish = true;
        centerCircle.end();
        pad.end();
        secondValue.setValue(0);
        arrowR.goToRight(gameWidth + 100);
        arrowL.goToLeft(-100);
        pad.noClick();
        cb = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                cpgame.setScreen(new Menu(cpgame, actionResolver));
            }
        };
        Tween.to(secondValue, -1, 0.55f).target(1).repeatYoyo(0, 0).setCallback(cb).setCallbackTriggers(TweenCallback.COMPLETE)
                .ease(TweenEquations.easeInSine).start(manager);
        colorManager.end();
        volumeButton.end();
        AssetLoader.addGamesPlayed();
        int gamesPlayed = AssetLoader.getGamesPlayed();
        // GAMES PLAYED ACHIEVEMENTS!
        actionResolver.submitScore(score);
        actionResolver.submitGamesPlayed(gamesPlayed);
        AssetLoader.setScore(score);
        Tween.to(secondValue, -1, 0.6f).target(1).repeatYoyo(0, 0).setCallback(cb).setCallbackTriggers(TweenCallback.COMPLETE)
                .ease(TweenEquations.easeInSine).start(manager);
        if (score > AssetLoader.getHighScore()) {
            AssetLoader.setHighScore(score);
        } else {
            // ADS
            if (Math.random()<0.1f) {


            }
        }

        checkAchievements();
    }

    private void checkAchievements() {
        if (actionResolver.isSignedIn()) {
            if (score >= 10)
                actionResolver.unlockAchievementGPGS(Configuration.SCORE_10);
            if (score >= 25)
                actionResolver.unlockAchievementGPGS(Configuration.SCORE_25);
            if (score >= 50)
                actionResolver.unlockAchievementGPGS(Configuration.SCORE_50);
            if (score >= 75)
                actionResolver.unlockAchievementGPGS(Configuration.SCORE_75);
            if (score >= 100)
                actionResolver.unlockAchievementGPGS(Configuration.SCORE_100);


            int gamesPlayed = AssetLoader.getGamesPlayed();
            // GAMES PLAYED
            if (gamesPlayed >= 10)
                actionResolver.unlockAchievementGPGS(Configuration.GAMESPLAYED_10);
            if (gamesPlayed >= 25)
                actionResolver.unlockAchievementGPGS(Configuration.GAMESPLAYED_25);
            if (gamesPlayed >= 50)
                actionResolver.unlockAchievementGPGS(Configuration.GAMESPLAYED_50);
            if (gamesPlayed >= 75)
                actionResolver.unlockAchievementGPGS(Configuration.GAMESPLAYED_75);
            if (gamesPlayed >= 100)
                actionResolver.unlockAchievementGPGS(Configuration.GAMESPLAYED_100);
        }
    }


    public Pad getPad() {
        return pad;
    }

    public Ball getBall() {
        return ball;
    }

    public CenterCircle getCenterCircle() {
        return centerCircle;
    }

    public float getAngle2Vecs(Vector2 vec, Vector2 vec1) {

        float angle = (float) Math.toDegrees(Math.acos(((vec.x * vec1.x) + (vec.y * vec1.y)) /
                ((Math.sqrt((Math.pow(vec.x, 2)) + (Math.pow(vec.y, 2)))) *
                        (Math.sqrt((Math.pow(vec1.x, 2)) + (Math.pow(vec1.y, 2)))))));


        return angle;
    }

    private Vector2 calculatePosition(float angle, float dtc) {
        float distanceToCenter = dtc - 2;
        float cx = gameWidth / 2;
        float cy = gameHeight / 2;
        return new Vector2((float) (cx + distanceToCenter
                * Math.sin(Math.toRadians(-angle))),
                (float) (cy + distanceToCenter
                        * Math.cos(Math.toRadians(-angle))));
    }

    public int getScore() {
        return score;
    }

    public static Color parseColor(String hex, float alpha) {
        String hex1 = hex;
        if (hex1.indexOf("#") != -1) {
            hex1 = hex1.substring(1);
            // Gdx.app.log("Hex", hex1);
        }
        Color color = Color.valueOf(hex1);
        color.a = alpha;
        return color;
    }

    public CirclePongGame getGame() {
        return cpgame;
    }

    public Arrow getArrowR() {
        return arrowR;
    }

    public Arrow getArrowL() {
        return arrowL;
    }

    public MuteButton getVolumeButton(){return volumeButton;}
}

