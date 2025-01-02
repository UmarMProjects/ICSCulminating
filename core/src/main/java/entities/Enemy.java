package entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import utils.AssetManager;

public class Enemy {
    private static final float ENEMY_SIZE = 0.5f;
    private Body body;
    private World world;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> deathAnimation;
    private float stateTime;
    private boolean isDead;

    public Enemy(World world, float x, float y) {
        this.world = world;
        createBody(x, y);
        loadAnimations();
        stateTime = 0;
        isDead = false;
    }
    
    private void createBody(float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        bodyDef.fixedRotation = true;
        
        body = world.createBody(bodyDef);
        body.setUserData(this);
        
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(ENEMY_SIZE/2, ENEMY_SIZE/2);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.4f;
        
        body.createFixture(fixtureDef).setUserData("enemy");
        shape.dispose();
    }

    private void loadAnimations() {
        idleAnimation = AssetManager.getEnemyIdleAnimation();
        deathAnimation = AssetManager.getEnemyDeathAnimation();
    }

    public void update(float deltaTime) {
        if (isDead) return;
        stateTime += deltaTime;
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        if (body == null || (isDead && deathAnimation.isAnimationFinished(stateTime))) {
            return;
        }

        TextureRegion currentFrame = isDead ? 
            deathAnimation.getKeyFrame(stateTime, false) : 
            idleAnimation.getKeyFrame(stateTime, true);

        if (currentFrame != null) {
            Vector2 position = body.getPosition();
            batch.draw(
                currentFrame,
                position.x - ENEMY_SIZE/2,
                position.y - ENEMY_SIZE/2,
                ENEMY_SIZE,
                ENEMY_SIZE
            );
        }
    }
    
    public boolean isDead() {
        return isDead;
    }

    public void dispose() {
        if (body != null && world != null) {
            world.destroyBody(body);
            body = null;
        }
    }
}