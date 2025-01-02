package GameMain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
//import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import utils.GameConstants;
import entities.Player;
import entities.AnimatedBackground;
import entities.Enemy;
import entities.HealthBar;
import entities.Platform;
import java.util.ArrayList;
import utils.AssetManager; 



public class GameScreen implements Screen {
    private final World world;
    private final Player player;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final SpriteBatch batch;
    private final Box2DDebugRenderer debugRenderer;
    private final ShapeRenderer shapeRenderer;
    private final ArrayList<Platform> platforms;
    private ArrayList<Enemy> enemies;
    private Enemy enemy;
    private final HealthBar healthBar; // Initialize this properly
    private final AnimatedBackground animatedBackground;
    

    public GameScreen() {
        // Load assets
        AssetManager.loadAssets();

        world = new World(new Vector2(0, -1f), true);
        
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConstants.VIEWPORT_WIDTH, GameConstants.VIEWPORT_HEIGHT, camera);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        
        batch = new SpriteBatch();
        debugRenderer = new Box2DDebugRenderer();
        shapeRenderer = new ShapeRenderer(); 
        
        platforms = new ArrayList<>();
        enemies = new ArrayList<>();
        
        createPlatforms();
        
        enemies.add(new Enemy(world, 5, 1));
        enemies.add(new Enemy(world, 7, 3));

        // Initialize the health bar
        healthBar = new HealthBar(0.4, 4.3, 1, 0.4, 100); 

        // Pass the health bar to the player
        player = new Player(world, viewport.getWorldWidth() / 2, 3, healthBar);
        
        // Initialize the animated background with textures, speeds, and desired size
        Texture[] layers = {
            AssetManager.backgroundLayer1,
            AssetManager.backgroundLayer2,
            AssetManager.backgroundLayer3,
            AssetManager.backgroundLayer4,
            AssetManager.backgroundLayer5
        };
        float[] speeds = {1, 1, 1, 1, 1}; 
        float scaleWidth = 8;  
        float scaleHeight = 5; 
        animatedBackground = new AnimatedBackground(layers, speeds, scaleWidth, scaleHeight);
        
        camera.update();
    }
    
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(1/60f, 6, 2);

        player.update();
        for (Enemy e : enemies) {
            e.update(delta);
        }
        animatedBackground.update(delta);

        camera.update();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        animatedBackground.render(batch);
        batch.end();

        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Platform platform : platforms) {
            platform.render(shapeRenderer);
        }
        shapeRenderer.end();

        batch.begin();
        for (Enemy e : enemies) {
            e.render(batch, camera);
        }
        player.render(batch);
        batch.end();

        // Render the health bar last to ensure it is on top
        healthBar.render(shapeRenderer, camera);

        if (GameConstants.DEBUG_MODE) {
            debugRenderer.render(world, camera.combined);
        }
    }

    private void createPlatforms() {
        platforms.add(createPlatform(viewport.getWorldWidth() / 2, 0.5f, viewport.getWorldWidth(), 1f));
        platforms.add(createPlatform(2, 2, 2, 0.3f));
        platforms.add(createPlatform(6, 2, 2, 0.3f));
        platforms.add(createPlatform(4, 3, 2, 0.3f));
    }

    private Platform createPlatform(float x, float y, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0f;
        body.createFixture(fixtureDef);

        shape.dispose();
        
        return new Platform(body, width, height);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
    }

    @Override
    public void dispose() {
        world.dispose();
        batch.dispose();
        debugRenderer.dispose();
        shapeRenderer.dispose();
        player.dispose(); // Dispose player resources
        for (Enemy e : enemies) {
            e.dispose();
        }
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}