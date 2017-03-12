package screens.menu;

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
import helpers.AssetLoader;


public class MenuWorld {

    public float gameWidth;
    public float gameHeight;
    private MenuObject menuObject;
    private CirclePongGame cpgame;
    private ActionResolver actionResolver;
    public Value r1 = new Value();
    private TweenManager manager;

    public MenuWorld(CirclePongGame cpgame, final ActionResolver actionResolver, float gameWidth,
                     float gameHeight) {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        this.cpgame = cpgame;
        this.actionResolver = actionResolver;
        menuObject = new MenuObject(this, gameWidth, gameHeight);
        Tween.registerAccessor(Value.class, new ValueAccessor());
        manager = new TweenManager();
        r1.setValue(0);
        if (!AssetLoader.getAds()) {
            if (Math.random() < Configuration.AD_FREQUENCY) {
                TweenCallback cb = new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        actionResolver.showOrLoadInterstital();
                    }
                };
                Tween.to(r1, -1, 0.6f).target(1).repeatYoyo(0, 0).setCallback(cb)
                     .setCallbackTriggers(TweenCallback.COMPLETE)
                     .ease(TweenEquations.easeOutSine).start(manager);
            }
        }
        actionResolver.rateDialog();
    }

    public void update(float delta) {
        menuObject.update(delta);
        manager.update(delta);

    }

    public MenuObject getMenuObject() {
        return menuObject;
    }

    public CirclePongGame getGame() {
        return cpgame;
    }

    public ActionResolver getActionResolver() {
        return actionResolver;
    }
}
