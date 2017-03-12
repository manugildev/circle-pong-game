package circlepong;

import com.badlogic.gdx.Game;

import helpers.AssetLoader;
import screens.GameScreen;
import screens.SplashScreen;

public class CirclePongGame extends Game{

    private ActionResolver actionresolver;


    public CirclePongGame(ActionResolver actionresolver) {
    this.actionresolver = actionresolver;
    }

    @Override
    public void create() {
        AssetLoader.load();

        setScreen(new SplashScreen(this,actionresolver));
    }

    @Override
    public void dispose() {
        super.dispose();
        AssetLoader.dispose();
    }
}
