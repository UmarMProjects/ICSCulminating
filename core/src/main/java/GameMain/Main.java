package GameMain;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.btree.decorator.Random;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import utils.AssetManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
//Core Game Class
public class Main extends Game {
    // Game constants
    public static final String TITLE = "Infinite Platformer";
    public static final float VIRTUAL_WIDTH = 800;
    public static final float VIRTUAL_HEIGHT = 480;
    public static final float SCALE = 1f;
    
    // Game resources that will be used across different screens
    public SpriteBatch batch;
    public BitmapFont font;
    public ShapeRenderer shapeRenderer;
    
    // Game states
    public GameScreen gameScreen;
    public MenuScreen menuScreen;
    
    @Override
    public void create() {
    	AssetManager.loadAssets();
        // Initialize resources
        batch = new SpriteBatch();
        font = new BitmapFont();
        shapeRenderer = new ShapeRenderer();
        
        // Initialize game states
        gameScreen = new GameScreen();
        menuScreen = new MenuScreen(this);
        
        // Set initial screen
        setScreen(menuScreen);
    }

    @Override
    public void render() {
        // Clear the screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        // Update and render current screen
        super.render();
    }

    @Override
    public void resize(int width, int height) {
        // Delegate resize to current screen
        super.resize(width, height);
    }

    @Override
    public void pause() {
        // Delegate pause to current screen
        super.pause();
    }

    @Override
    public void resume() {
        // Delegate resume to current screen
        super.resume();
    }

    @Override
    public void dispose() {
        // Dispose of all resources
    	AssetManager.dispose();
        batch.dispose();
        font.dispose();
        shapeRenderer.dispose();
        
        // Dispose screens
        if (gameScreen != null) gameScreen.dispose();
        if (menuScreen != null) menuScreen.dispose();
    }
}