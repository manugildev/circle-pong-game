package screens.menu;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import helpers.ColorManager;

/**
 * Created by ManuGil on 18/01/15.
 */
public class MenuRenderer {

    private MenuWorld world;
    private OrthographicCamera cam;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private ShaderProgram fontShader;
    private MenuObject menuObject;
    Color color;

    public MenuRenderer(MenuWorld world, float gameWidth, float gameHeight) {
        this.world = world;
        cam = new OrthographicCamera();
        cam.setToOrtho(true, gameWidth, gameHeight);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(cam.combined);
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(cam.combined);
        initObjects();
        initFont();
        color = ColorManager.parseColor("#F8F9FB",1f);
    }

    private void initObjects() {
        menuObject = world.getMenuObject();
       
    }
    
    private void initFont(){
        fontShader = new ShaderProgram(Gdx.files.internal("font.vert"),
                Gdx.files.internal("font.frag"));
        if (!fontShader.isCompiled()) {
            Gdx.app.error("fontShader",
                    "compilation failed:\n" + fontShader.getLog());
        }
    }

    public void render(float delta, float runTime) {

        Gdx.gl.glClearColor(color.r,color.g,color.b, color.a);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        //Gdx.app.log("D ",debugRenderer.toString());

        batch.begin();
        menuObject.render(batch, shapeRenderer,fontShader);
        batch.end();


    }
}
