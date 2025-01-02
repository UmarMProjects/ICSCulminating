package GameMain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import utils.GameConstants;
import entities.Player;
import entities.Enemy;
import entities.HealthBar;
import entities.Platform;
import java.util.ArrayList;


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
    private HealthBar healthBar;

    public GameScreen() {
        world = new World(new Vector2(0, -1f), true);
        
        camera = new OrthographicCamera();
        viewport = new FitViewport(GameConstants.VIEWPORT_WIDTH, GameConstants.VIEWPORT_HEIGHT, camera);
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        
        batch = new SpriteBatch();
        debugRenderer = new Box2DDebugRenderer();
        shapeRenderer = new ShapeRenderer();
        debugRenderer.setDrawBodies(false);
        
        platforms = new ArrayList<>();
        enemies = new ArrayList<>();
        
        createPlatforms();
        
        enemies.add(new Enemy(world, 5, 1));
        enemies.add(new Enemy(world, 7, 3));
        enemy = new Enemy(world, 5f, 5f);
        
        player = new Player(world, viewport.getWorldWidth() / 2, 3);
        
        healthBar = new HealthBar(player.getMaxHealth(), 0.1 , 4.4, 2, 0.3);
        
        camera.update();
    }
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        world.step(1/60f, 6, 2);

        player.update();
        enemy.update(delta);
        for (Enemy e : enemies) {
            e.update(delta);
        }
        
        camera.update();

        // Render platforms
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Platform platform : platforms) {
            platform.render(shapeRenderer);
        }
        shapeRenderer.end();

        // Render sprites
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        for (Enemy e : enemies) {
            e.render(batch, camera);
        }
        enemy.render(batch, camera);
        player.render(batch);
        
        batch.end();

        // Debugging health bar update and render
        System.out.println("Updating health bar with player health: " + player.getCurrentHealth());
        healthBar.update(player.getCurrentHealth());
        System.out.println("Rendering health bar");
        healthBar.render(shapeRenderer, camera);

        if (GameConstants.DEBUG_MODE) {
            debugRenderer.render(world, camera.combined);
        }
    }

    private void createPlatforms() {
        platforms.add(new Platform(world, 
            viewport.getWorldWidth() / 2,
            0.5f,
            viewport.getWorldWidth(),
            1f));

        platforms.add(new Platform(world, 2, 2, 2, 0.3f));
        platforms.add(new Platform(world, 6, 2, 2, 0.3f));
        platforms.add(new Platform(world, 4, 3, 2, 0.3f));
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
        player.dispose();
        enemy.dispose();
        for (Enemy e : enemies) {
            e.dispose();
        }
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}