package circlepong.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import circlepong.CirclePongGame;


public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Circle Pong";
        config.width = (int) (320 * 1.2f);
        config.height = (int) (480 * 1.2f);
        new LwjglApplication(new CirclePongGame(new ActionResolverDesktop()), config);
    }
}
