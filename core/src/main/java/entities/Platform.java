package entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import utils.GameConstants;


public class Platform {
    private final Body body;
    private final float width;
    private final float height;
    private static final Color PLATFORM_COLOR = new Color(0.5f, 0.5f, 0.5f, 1);

    public Platform(World world, float x, float y, float width, float height) {
        this.width = width;
        this.height = height;
        
        // Create body definition
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        
        // Create body
        body = world.createBody(bodyDef);
        
        // Create shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width/2, height/2);
        
        // Create fixture
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0;
        
        body.createFixture(fixtureDef).setUserData("platform");
        shape.dispose();
    }

    public void render(ShapeRenderer shapeRenderer) {  // Changed from renderShape to render
        Vector2 position = body.getPosition();
        shapeRenderer.setColor(PLATFORM_COLOR);
        shapeRenderer.rect(
            position.x - width/2, 
            position.y - height/2,
            width, 
            height
        );
    }

    public void dispose() {
        // Nothing to dispose here anymore
    }
}