package screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import circlepong.ActionResolver;
import circlepong.CirclePongGame;
import helpers.AssetLoader;
import screens.menu.InputHandlerMenu;
import screens.menu.MenuRenderer;
import screens.menu.MenuWorld;

public class Menu implements Screen {

    private MenuWorld world;
    private MenuRenderer renderer;
    private float runTime;
    public float sW = Gdx.graphics.getWidth();
    public float sH = Gdx.graphics.getHeight();
    public float gameWidth = 720;
    public float gameHeight = sH / (sW / gameWidth);


    public Menu(CirclePongGame cpgame, ActionResolver actionResolver) {
        Gdx.app.log("GameScreen", "Attached");
        Gdx.app.log("GameWidth " + gameWidth, "GameHeight " + gameHeight);
        world = new MenuWorld(cpgame, actionResolver, gameWidth, gameHeight);
        Gdx.input.setInputProcessor(new InputHandlerMenu(world, sW / gameWidth, sH
                / gameHeight));
        renderer = new MenuRenderer(world, (int) gameWidth, (int) gameHeight);

        if (AssetLoader.getVolume()) {
            AssetLoader.music.setLooping(true);
            AssetLoader.music.play();
            AssetLoader.setVolume(true);
            world.getMenuObject().getVolumeButton().isPressed = false;
        }

        if (AssetLoader.music.isPlaying()) {
            world.getMenuObject().getVolumeButton().isPressed = false;
        }else{
            world.getMenuObject().getVolumeButton().isPressed =true;
        }

        if (AssetLoader.getAds()) {
            actionResolver.viewAd(false);
        } else {
            actionResolver.viewAd(true);
        }
    }

    @Override
    public void render(float delta) {
        runTime += delta;
        world.update(delta);
        renderer.render(delta, runTime);
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("GameScreen", "resize");
    }

    @Override
    public void show() {
        Gdx.app.log("GameScreen", "show called");
    }

    @Override
    public void hide() {
        Gdx.app.log("GameScreen", "hide called");
    }

    @Override
    public void pause() {
        Gdx.app.log("GameScreen", "pause called");
    }

    @Override
    public void resume() {
        Gdx.app.log("GameScreen", "resume called");
    }

    @Override
    public void dispose() {

    }
}
