package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.pfa.DefaultGraphPath;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.Heuristic;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import utils.AssetManager;
import utils.GridGraph;
import utils.GridNode;

import com.badlogic.gdx.physics.box2d.Body;

import utils.MyHeuristic;


public class Enemy {
    private static final float ENEMY_SIZE = 0.25f;
    private final Body body;
    private Animation<TextureRegion> idleAnimation, attackAnimation, hurtAnimation, deathAnimation, walkAnimation;
    private boolean isAttacking, isHurt, isDead;
    private float attackTimer, hurtTimer, stateTime, currentHealth;
    private static final float ATTACK_DURATION = 0.6f, ATTACK_RANGE = 1f, ATTACK_DAMAGE = 10f;
    private final Player player;
    private final World world;

    private GridGraph graph;
    private IndexedAStarPathFinder<GridNode> pathFinder;
    private GraphPath<GridNode> path;
    private Heuristic<GridNode> heuristic;

    private State currentState;
    private boolean facingLeft; // Track the direction the enemy is facing

    public enum State {
        IDLE, WALKING, ATTACKING, HURT, DEAD
    }

    public Enemy(World world, float startX, float startY, Player player, GridGraph graph) {
        this.world = world;
        this.player = player;
        this.graph = graph;
        this.currentHealth = 50; // Initial health of the enemy

        // Create the body and fixture
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startX, startY);
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(ENEMY_SIZE, ENEMY_SIZE);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;
        body.createFixture(fixtureDef).setUserData("enemy");
        shape.dispose();

        // Initialize state variables
        currentState = State.IDLE;
        stateTime = 0;
        isAttacking = false;
        attackTimer = 0;
        isHurt = false;
        hurtTimer = 0;
        isDead = false;
        facingLeft = false; // Initially facing right

        // Initialize pathfinding
        pathFinder = new IndexedAStarPathFinder<>(graph);
        path = new DefaultGraphPath<>();
        heuristic = new MyHeuristic();

        loadAnimations();
    }

    private void loadAnimations() {
        idleAnimation = AssetManager.getEnemyIdleAnimation();
        attackAnimation = AssetManager.getEnemyAttackAnimation();
        hurtAnimation = AssetManager.getEnemyHurtAnimation();
        deathAnimation = AssetManager.getEnemyDeathAnimation();
        walkAnimation = AssetManager.getEnemyWalkAnimation();
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;

        if (isDead) {
            return;
        }

        if (isHurt) {
            hurtTimer += deltaTime;
            if (hurtTimer >= 0.5f) { // Duration of hurt animation
                isHurt = false;
                currentState = State.IDLE;
            }
        }

        if (isAttacking) {
            attackTimer += deltaTime;
            if (attackTimer >= ATTACK_DURATION) {
                isAttacking = false;
                currentState = State.IDLE;
                System.out.println("Attack finished, switching to IDLE state.");
            }
        }

        if (!isHurt && !isAttacking) {
            followPlayer();
        }
    }

    private void followPlayer() {
        int startX = (int) body.getPosition().x;
        int startY = (int) body.getPosition().y;
        int endX = (int) player.getBody().getPosition().x;
        int endY = (int) player.getBody().getPosition().y;

        GridNode startNode = graph.getNode(startX, startY);
        GridNode endNode = graph.getNode(endX, endY);

        // Debugging: Print start and end nodes
        System.out.println("Enemy start node: (" + startX + ", " + startY + ")");
        System.out.println("Player end node: (" + endX + ", " + endY + ")");

        path.clear();
        pathFinder.searchNodePath(startNode, endNode, heuristic, path);

        // Debugging: Print path node positions
        if (path.getCount() > 0) {
            for (GridNode node : path) {
                System.out.println("Path node: (" + node.getX() + ", " + node.getY() + ")");
            }
        } else {
            System.out.println("No path found.");
        }

        if (path.getCount() > 1) {
            GridNode nextNode = path.get(1);
            Vector2 direction = new Vector2(nextNode.getX() - startX, nextNode.getY() - startY).nor();
            body.setLinearVelocity(direction.scl(0.4f)); // Adjust speed as needed
            currentState = State.WALKING;
            System.out.println("Moving to next node: (" + nextNode.getX() + ", " + nextNode.getY() + ")");

            // Determine direction
            facingLeft = direction.x < 0;

            // Jump if necessary
            if (nextNode.getY() > startY) {
                body.applyLinearImpulse(new Vector2(0, 5f), body.getWorldCenter(), true); // Adjust jump force as needed
                System.out.println("Jumping to next node.");
            }
        } else {
            body.setLinearVelocity(0, 0);
            startAttack();
        }
    }

    private void startAttack() {
        if (!isAttacking && !isHurt && !isDead) {
            float distanceToPlayer = body.getPosition().dst(player.getBody().getPosition());
            if (distanceToPlayer <= ATTACK_RANGE) {  // Only attack if within range
                isAttacking = true;
                attackTimer = 0;
                currentState = State.ATTACKING;
                System.out.println("Started attacking the player.");

                // Deal damage to the player
                player.takeDamage(ATTACK_DAMAGE);
            }
        }
    }

    public void takeDamage(float damage) {
        if (!isDead) {
            currentHealth -= damage;
            if (currentHealth <= 0) {
                die();
            } else {
                isHurt = true;
                hurtTimer = 0;
                currentState = State.HURT;
            }
        }
    }

    private void die() {
        isDead = true;
        currentState = State.DEAD;
        body.setActive(false); // Disable physics interactions
    }

    public void render(SpriteBatch batch) {
        TextureRegion frame = null;

        switch (currentState) {
            case DEAD:
                frame = deathAnimation.getKeyFrame(stateTime, false);
                break;
            case HURT:
                frame = hurtAnimation.getKeyFrame(stateTime, false);
                break;
            case ATTACKING:
                frame = attackAnimation.getKeyFrame(attackTimer, false);
                break;
            case WALKING:
                frame = walkAnimation.getKeyFrame(stateTime, true);
                break;
            default:
                frame = idleAnimation.getKeyFrame(stateTime, true);
                break;
        }

        if (frame != null) {
            Vector2 position = body.getPosition();
            float spriteWidth = ENEMY_SIZE * 2; // Adjust the width multiplier as needed
            float spriteHeight = ENEMY_SIZE * 2; // Adjust the height multiplier as needed

            // Flip the sprite if the enemy is facing left
            float originX = spriteWidth / 2;
            float scaleX = facingLeft ? -1 : 1;

            batch.draw(frame, position.x - originX, position.y - spriteHeight / 2, originX, spriteHeight / 2, spriteWidth, spriteHeight, scaleX, 1, 0);
        }
    }

    public void dispose() {
        // Dispose of resources if needed
    }
}