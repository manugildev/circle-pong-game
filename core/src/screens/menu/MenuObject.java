package screens.menu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;

import Tweens.Value;
import Tweens.ValueAccessor;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;
import helpers.AssetLoader;
import helpers.FlatColors;
import ui.MuteButton;
import ui.SimpleButton;

/**
 * Created by ManuGil on 18/01/15.
 */
public class MenuObject {


    private final Rectangle rectangle;
    private final int buttonSize = 100;
    private MenuWorld world;

    private ArrayList<SimpleButton> menuButtons = new ArrayList<SimpleButton>();
    private SimpleButton playButton, rankButton, shareButton, achieveButton, rateButton;
    private MuteButton volumeButton;
    private Sprite title;
    private Pad pad;
    public Value r1 = new Value();
    private TweenManager manager;
    private BallMenu ball;

    public MenuObject(MenuWorld world, float gameWidth, float gameHeight) {
        this.world = world;
        this.rectangle = new Rectangle(0, 0, gameWidth, gameHeight);

        pad = new Pad(world);
        pad.start();
        pad.clickLeft();

        this.playButton = new SimpleButton(world, this.world.gameWidth / 2,
                                           this.rectangle.height / 2 + 160f - 20, buttonSize + 50,
                                           buttonSize + 50, AssetLoader.playButtonUp,
                                           AssetLoader.playButtonUp, Color.WHITE);
        this.rankButton = new SimpleButton(world, this.rectangle.width / 2f - 10 - (buttonSize / 2),
                                           this.rectangle.height / 2 + 220f + 80, this.buttonSize,
                                           this.buttonSize, AssetLoader.rankButtonUp,
                                           AssetLoader.rankButtonDown, Color.WHITE);
        this.shareButton = new SimpleButton(world,
                                            this.rectangle.width / 2f - 30 - (buttonSize * 1.5f),
                                            this.rectangle.height / 2 + 220f + 80, this.buttonSize,
                                            this.buttonSize, AssetLoader.shareButtonUp,
                                            AssetLoader.shareButtonDown, Color.WHITE);
        this.achieveButton = new SimpleButton(world,
                                              this.rectangle.width / 2f + 10 + (buttonSize / 2),
                                              this.rectangle.height / 2 + 220f + 80,
                                              this.buttonSize, this.buttonSize,
                                              AssetLoader.achieveButtonUp,
                                              AssetLoader.achieveButtonDown, Color.WHITE);
        this.rateButton = new SimpleButton(world,
                                           this.rectangle.width / 2f + 30 + (buttonSize * 1.5f),
                                           this.rectangle.height / 2 + 220f + 80, this.buttonSize,
                                           this.buttonSize, AssetLoader.rateButtonUp,
                                           AssetLoader.rateButtonDown, Color.WHITE);

        this.menuButtons.add(this.playButton);
        this.menuButtons.add(this.shareButton);
        this.menuButtons.add(this.rankButton);
        this.menuButtons.add(this.achieveButton);

        if (!AssetLoader.getAds()) this.menuButtons.add(this.rateButton);
        if (AssetLoader.getAds()) {
            this.shareButton.setX(this.shareButton.sprite.getX() + 10 + buttonSize / 2);
            this.achieveButton.setX(this.achieveButton.sprite.getX() + 10 + buttonSize / 2);
            this.rankButton.setX(this.rankButton.sprite.getX() + 10 + buttonSize / 2);
        }

        this.volumeButton = new MuteButton(20 + this.buttonSize - 25, 20 + this.buttonSize - 25,
                                           this.buttonSize - 25, this.buttonSize - 25,
                                           AssetLoader.soundI, AssetLoader.muteI,
                                           FlatColors.BLACK);
        title = new Sprite(AssetLoader.title);
        title.setSize(world.gameWidth,
                      world.gameWidth / AssetLoader.title.getRegionWidth() * AssetLoader.title
                              .getRegionHeight());

        Tween.registerAccessor(Value.class, new ValueAccessor());
        manager = new TweenManager();
        r1.setValue(-500);

        for (int i = 0; i < menuButtons.size(); i++) {
            menuButtons.get(i).start();
        }
        volumeButton.start();

        Tween.to(r1, -1, 0.5f).target(world.gameHeight / 2 - 310).repeatYoyo(0, 0)
             .ease(TweenEquations.easeOutSine).start(manager);
        ball = new BallMenu(this.world);
        ball.start();
    }


    public void update(float delta) {
        manager.update(delta);
        ball.update(delta);
        pad.update(delta);
        for (int i = 0; i < menuButtons.size(); i++) {
            menuButtons.get(i).update(delta);
        }
        title.setPosition(0, r1.getValue());
        volumeButton.update(delta);
        if (Math.random() < 0.01f) {
            pad.clickLeft();
        }
        if (Math.random() < 0.01f) {
            pad.clickRight();
        }

        //Gdx.app.log("Pad", pad.getPosition().toString());

    }

    public void render(SpriteBatch batch, ShapeRenderer shapeRenderer, ShaderProgram fontShader) {
        //ball.render(batch, shapeRenderer);
        for (int i = 0; i < menuButtons.size(); i++) {
            menuButtons.get(i).draw(batch);
        }

        title.draw(batch);
        volumeButton.draw(batch);
        batch.setShader(fontShader);
        AssetLoader.font.setScale(1.5f, -1.5f);
        AssetLoader.font.setColor(FlatColors.DARK_BLACK);
        /*AssetLoader.font.drawWrapped(batch, Configuration.gameName, 0, r1.getValue(),
                world.gameWidth,
                BitmapFont.HAlignment.CENTER);*/

        batch.setShader(fontShader);
        AssetLoader.font2.setColor(FlatColors.DARK_BLACK);
        AssetLoader.font2.drawWrapped(
                batch,
                "Score: " + AssetLoader.getScore(),
                0, r1.getValue() + 140 - 5, world.gameWidth, BitmapFont.HAlignment.CENTER);
        AssetLoader.font2.drawWrapped(
                batch,
                "Highscore: " + AssetLoader.getHighScore(),
                0,
                r1.getValue() + 200 - 5, world.gameWidth, BitmapFont.HAlignment.CENTER);
        AssetLoader.font2
                .drawWrapped(batch,
                             "Games Played: " + AssetLoader.getGamesPlayed(), 0,
                             r1.getValue() + 260 - 5, world.gameWidth,
                             BitmapFont.HAlignment.CENTER);
        batch.setShader(null);
        batch.setColor(Color.WHITE);
        pad.render(batch, shapeRenderer);
    }

    public ArrayList<SimpleButton> getMenuButtons() {
        return menuButtons;
    }

    public Pad getPad() {
        return pad;
    }

    public void end() {
        r1.setValue(world.gameHeight / 2 - 350);
        Tween.to(r1, -1, 0.5f).target(-500).repeatYoyo(0, 0)
             .ease(TweenEquations.easeOutSine).start(manager);
    }

    public MuteButton getVolumeButton() {
        return volumeButton;
    }

    public BallMenu getBall() {
        return ball;
    }
}
