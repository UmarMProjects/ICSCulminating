package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import utils.AssetManager;
import utils.GameConstants;

public class Player implements ContactListener {
    private static final float PLAYER_SIZE = 0.25f;
    private final Body body;
    private Animation<TextureRegion> idleAnimation;
    private Animation<TextureRegion> runAnimation;
    private Animation<TextureRegion> jumpAnimation;
    private Animation<TextureRegion> fallAnimation;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> dodgeAnimation;
    private Animation<TextureRegion> airAttackAnimation;
    private Animation<TextureRegion> lightAttackFirstAnimation;
    private Animation<TextureRegion> lightAttackSecondAnimation;
    private Animation<TextureRegion> hurtAnimation;
    private Animation<TextureRegion> deathAnimation;

    private boolean isAttacking;
    private float attackTimer;
    private static final float ATTACK_DURATION = 0.25f;
    private static final float ATTACK_RANGE = 1f;
    private static final int ATTACK_DAMAGE = 25;

    private static final float MAX_HEALTH = 100f;
    private static final float HURT_ANIMATION_DURATION = 0.5f;
    private static final float INVINCIBILITY_DURATION = 1f; // Time of invincibility after getting hurt

    private boolean isHurt;
    private float hurtTimer;
    private boolean isInvincible;
    private float invincibilityTimer;
    private boolean isDead;

    private static final float LIGHT_ATTACK_RANGE = 0.8f;
    private static final int LIGHT_ATTACK_DAMAGE = 15;

    private boolean isLightAttacking;
    private float lightAttackTimer;
    private static final float LIGHT_ATTACK_DURATION = 0.3f;
    private int lightAttackCombo; // 0 = no attack, 1 = first attack, 2 = second attack
    private static final float COMBO_WINDOW = 0.5f;
    private float comboTimer;

    private boolean canSecondAttack;
    private int currentAttackPhase;

    private State currentState;
    private float stateTime;
    private boolean facingRight;
    private static final float SPRITE_WIDTH = 64f;
    private static final float SPRITE_HEIGHT = 32f;
    private static final float ATTACK_SCALE = 0.02f;
    private static final float REGULAR_SCALE_X = 0.01f;
    private static final float REGULAR_SCALE_Y = 0.015f;

    private static final float IDLE_OFFSET = 0.01f;
    private static final float RUN_OFFSET = 0.075f;
    private static final float ATTACK_OFFSET = 0.02f;
    private static final float JUMP_OFFSET = 0.02f;
    private World world;

    private boolean canJump;
    private boolean isInAir;

    private static final float DODGE_DURATION = 0.6f;
    private static final float DODGE_COOLDOWN = 1.2f;
    private static final float DODGE_SPEED = 3f;
    private boolean isDodging;
    private float dodgeTimer;
    private float dodgeCooldownTimer;
    private boolean canDodge;

    private float maxHealth = 100;
    private float currentHealth = 100;

    private HealthBar healthBar;

    public enum State {
        IDLE, RUNNING, JUMPING, FALLING, ATTACKING, DODGING, AIR_ATTACKING, LIGHT_ATTACKING, DYING, HURT
    }

    public Player(World world, float startX, float startY, HealthBar healthBar) {
        this.healthBar = healthBar;
        this.currentHealth = 100; // Initialize health to 100
        this.world = world;

        // Create the body and fixture
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startX, startY);
        bodyDef.fixedRotation = true;
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(PLAYER_SIZE, PLAYER_SIZE);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1f;
        fixtureDef.friction = 0.5f;
        body.createFixture(fixtureDef).setUserData("player");
        shape.dispose();

        world.setContactListener(this);

        currentHealth = MAX_HEALTH;
        isHurt = false;
        hurtTimer = 0;
        isInvincible = false;
        invincibilityTimer = 0;
        isDead = false;

        currentState = State.IDLE;
        stateTime = 0;
        facingRight = true;
        canJump = false;

        isLightAttacking = false;
        lightAttackTimer = 0;
        canSecondAttack = false;
        comboTimer = 0;
        currentAttackPhase = 0;

        facingRight = true;

        isAttacking = false;
        attackTimer = 0;

        isDodging = false;
        dodgeTimer = 0;
        dodgeCooldownTimer = 0;
        canDodge = true;

        loadAnimations();
    }

    private void loadAnimations() {
        idleAnimation = AssetManager.getPlayerIdleAnimation();
        runAnimation = AssetManager.getPlayerRunAnimation();
        jumpAnimation = AssetManager.getPlayerJumpAnimation();
        fallAnimation = AssetManager.getPlayerFallAnimation();
        attackAnimation = AssetManager.getPlayerAttackAnimation();
        dodgeAnimation = AssetManager.getPlayerDodgeAnimation();
        airAttackAnimation = AssetManager.getPlayerAirAttackAnimation();
        lightAttackFirstAnimation = AssetManager.getPlayerLightAttackFirstAnimation();
        lightAttackSecondAnimation = AssetManager.getPlayerLightAttackSecondAnimation();
        hurtAnimation = AssetManager.getPlayerHurtAnimation();
        deathAnimation = AssetManager.getPlayerDeathAnimation();

        if (dodgeAnimation == null) {
            System.out.println("Warning: Dodge animation is null!");
        }
    }

    private void die() {
        isDead = true;
        currentState = State.DYING;
        // Disable physics interactions
        body.setActive(false);
    }

    private void startDodge() {
        if (!isDodging && canDodge && !isAttacking) {
            System.out.println("Starting dodge!");
            isDodging = true;
            canDodge = false;
            dodgeTimer = 0;
            currentState = State.DODGING;

            // Apply stronger dodge force
            float dodgeVelocityX = facingRight ? DODGE_SPEED : -DODGE_SPEED;
            body.setLinearVelocity(dodgeVelocityX, body.getLinearVelocity().y * 0.5f); // Reduce vertical velocity
        }
    }

    private void handleInput() {
        if (isDodging) return;

        Vector2 vel = body.getLinearVelocity();

        // Handle light attack (R key)
        if (Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            if (!isLightAttacking && !isAttacking) {
                performLightAttack();
            } else if (isLightAttacking && lightAttackCombo == 1 && comboTimer < COMBO_WINDOW) {
                performLightAttack();
            }
        }

        // Handle heavy attack (E key)
        if (Gdx.input.isKeyJustPressed(Input.Keys.E) && !isLightAttacking) {
            startAttack();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            takeDamage(10); // Take 10 damage when H key is pressed
        }

        // Handle dodge
        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) && canDodge) {
            startDodge();
        }

        // Movement is always allowed unless dodging
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            body.setLinearVelocity(-GameConstants.PLAYER_SPEED, vel.y);
            facingRight = false;
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            body.setLinearVelocity(GameConstants.PLAYER_SPEED, vel.y);
            facingRight = true;
        } else {
            body.setLinearVelocity(0, vel.y);
        }

        // Handle jumping
        if ((Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) && canJump) {
            body.setLinearVelocity(vel.x, 0);
            body.applyLinearImpulse(new Vector2(0, GameConstants.JUMP_FORCE), body.getWorldCenter(), true);
            canJump = false;
            isInAir = true;
        }
    }

    private void performLightAttack() {
        if (isInAir) {
            performAirAttack();
            return;
        }

        isLightAttacking = true;
        lightAttackTimer = 0;

        if (lightAttackCombo == 0) {
            lightAttackCombo = 1;
            comboTimer = 0;
        } else if (lightAttackCombo == 1) {
            lightAttackCombo = 2;
        }

        currentState = State.LIGHT_ATTACKING;

        float attackX = facingRight ? body.getPosition().x + LIGHT_ATTACK_RANGE
                : body.getPosition().x - LIGHT_ATTACK_RANGE;

        world.QueryAABB(fixture -> {
            if (fixture.getUserData() != null && fixture.getUserData().equals("enemy")) {
                Body enemyBody = fixture.getBody();
                // Handle enemy collision
            }
            return true;
        }, attackX - LIGHT_ATTACK_RANGE / 2, body.getPosition().y - PLAYER_SIZE / 2, attackX + LIGHT_ATTACK_RANGE / 2,
                body.getPosition().y + PLAYER_SIZE / 2);
    }

    private void performAirAttack() {
        if (!isAttacking) {
            isAttacking = true;
            attackTimer = 0;
            currentState = State.AIR_ATTACKING;

            float attackX = facingRight ? body.getPosition().x + ATTACK_RANGE : body.getPosition().x - ATTACK_RANGE;

            world.QueryAABB(fixture -> {
                if (fixture.getUserData() != null && fixture.getUserData().equals("enemy")) {
                    Body enemyBody = fixture.getBody();
                    // Handle enemy collision
                }
                return true;
            }, attackX - ATTACK_RANGE / 2, body.getPosition().y - PLAYER_SIZE / 2, attackX + ATTACK_RANGE / 2,
                    body.getPosition().y + PLAYER_SIZE / 2);
        }
    }

    private void startAttack() {
        if (!isAttacking && !isDodging) {
            System.out.println("Starting attack!");
            isAttacking = true;
            attackTimer = 0;

            currentState = isInAir ? State.AIR_ATTACKING : State.ATTACKING;

            float attackX = facingRight ? body.getPosition().x + ATTACK_RANGE : body.getPosition().x - ATTACK_RANGE;

            world.QueryAABB(fixture -> {
                if (fixture.getUserData() != null && fixture.getUserData().equals("enemy")) {
                    Body enemyBody = fixture.getBody();
                    // Handle enemy collision
                }
                return true;
            }, attackX - ATTACK_RANGE / 2, body.getPosition().y - PLAYER_SIZE / 2, attackX + ATTACK_RANGE / 2,
                    body.getPosition().y + PLAYER_SIZE / 2);
        }
    }

    public void update() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        Vector2 vel = body.getLinearVelocity();

        handleInput();

        if (isDead) {
            stateTime += deltaTime;
            return;
        }

        if (isHurt) {
            hurtTimer += deltaTime;
            if (hurtTimer >= HURT_ANIMATION_DURATION) {
                isHurt = false;
                if (currentHealth > 0) {
                    currentState = State.IDLE;
                }
            }
        }

        if (isInvincible) {
            invincibilityTimer += deltaTime;
            if (invincibilityTimer >= INVINCIBILITY_DURATION) {
                isInvincible = false;
            }
        }

        if (isLightAttacking) {
            lightAttackTimer += deltaTime;
            if (lightAttackTimer >= LIGHT_ATTACK_DURATION) {
                isLightAttacking = false;

                if (lightAttackCombo == 1) {
                    comboTimer = 0;
                } else if (lightAttackCombo == 2) {
                    lightAttackCombo = 0;
                }

                if (isInAir) {
                    currentState = vel.y < 0 ? State.FALLING : State.JUMPING;
                } else {
                    currentState = State.IDLE;
                }
            }
        }

        if (lightAttackCombo == 1 && !isLightAttacking) {
            comboTimer += deltaTime;
            if (comboTimer >= COMBO_WINDOW) {
                lightAttackCombo = 0;
            }
        }

        if (isDodging) {
            dodgeTimer += deltaTime;
            if (dodgeTimer >= DODGE_DURATION) {
                isDodging = false;
                currentState = isInAir ? State.FALLING : State.IDLE;
                float endSpeed = vel.x * 0.5f;
                body.setLinearVelocity(endSpeed, vel.y);
            } else {
                float progress = dodgeTimer / DODGE_DURATION;
                float currentDodgeSpeed = DODGE_SPEED * (1 - progress);
                float direction = facingRight ? 1 : -1;
                body.setLinearVelocity(currentDodgeSpeed * direction, vel.y);
            }
        }

        if (!canDodge) {
            dodgeCooldownTimer += deltaTime;
            if (dodgeCooldownTimer >= DODGE_COOLDOWN) {
                canDodge = true;
                dodgeCooldownTimer = 0;
            }
        }

        if (isAttacking) {
            attackTimer += deltaTime;
            if (attackTimer >= ATTACK_DURATION) {
                isAttacking = false;
                currentState = isInAir ? State.FALLING : State.IDLE;
            }
        }

        if (!isLightAttacking && !isAttacking && !isDodging) {
            if (Math.abs(vel.y) > 0.1f) {
                currentState = vel.y < 0 ? State.FALLING : State.JUMPING;
                isInAir = true;
            } else {
                isInAir = false;
                if (Math.abs(vel.x) > 0.1f) {
                    currentState = State.RUNNING;
                } else {
                    currentState = State.IDLE;
                }
            }
        }
    }

    public void takeDamage(float damage) {
        if (!isInvincible && !isDead) {
            currentHealth -= damage;
            if (currentHealth < 0) currentHealth = 0;

            healthBar.setHealth(currentHealth);

            if (currentHealth == 0) {
                die();
            } else {
                currentState = State.HURT;
                isHurt = true;
                hurtTimer = 0;
                isInvincible = true;
                invincibilityTimer = 0;
            }
        }
    }

    public void render(SpriteBatch batch) {
        stateTime += Gdx.graphics.getDeltaTime();

        TextureRegion frame = null;
        float currentScaleX = REGULAR_SCALE_X;
        float currentScaleY = REGULAR_SCALE_Y;
        float currentOffset = IDLE_OFFSET;

        switch (currentState) {
            case DYING:
                frame = deathAnimation.getKeyFrame(stateTime, false);
                break;
            case HURT:
                frame = hurtAnimation.getKeyFrame(stateTime, false);
                break;
            case LIGHT_ATTACKING:
                if (lightAttackCombo == 1 && lightAttackFirstAnimation != null) {
                    frame = lightAttackFirstAnimation.getKeyFrame(lightAttackTimer, false);
                } else if (lightAttackCombo == 2 && lightAttackSecondAnimation != null) {
                    frame = lightAttackSecondAnimation.getKeyFrame(lightAttackTimer, false);
                }
                currentScaleX = ATTACK_SCALE;
                currentScaleY = ATTACK_SCALE;
                currentOffset = ATTACK_OFFSET;
                break;
            case AIR_ATTACKING:
                if (airAttackAnimation != null) {
                    frame = airAttackAnimation.getKeyFrame(attackTimer, false);
                    currentScaleX = ATTACK_SCALE;
                    currentScaleY = ATTACK_SCALE;
                    currentOffset = ATTACK_OFFSET;
                }
                break;
            case DODGING:
                if (dodgeAnimation != null) {
                    frame = dodgeAnimation.getKeyFrame(dodgeTimer, false);
                    currentOffset = JUMP_OFFSET;
                } else {
                    System.out.println("Dodge animation is null in render!");
                }
                break;
            case ATTACKING:
                if (attackAnimation != null) {
                    frame = attackAnimation.getKeyFrame(attackTimer, false);
                    currentScaleX = ATTACK_SCALE;
                    currentScaleY = ATTACK_SCALE;
                    currentOffset = ATTACK_OFFSET;
                }
                break;
            case FALLING:
                if (fallAnimation != null) {
                    frame = fallAnimation.getKeyFrame(stateTime, true);
                    currentOffset = JUMP_OFFSET;
                }
                break;
            case JUMPING:
                if (jumpAnimation != null) {
                    frame = jumpAnimation.getKeyFrame(stateTime, true);
                    currentOffset = JUMP_OFFSET;
                }
                break;
            case RUNNING:
                if (runAnimation != null && !isInAir) {
                    frame = runAnimation.getKeyFrame(stateTime, true);
                    currentOffset = RUN_OFFSET;
                }
                break;
            default:
                if (idleAnimation != null) {
                    frame = idleAnimation.getKeyFrame(stateTime, true);
                    currentOffset = IDLE_OFFSET;
                }
                if (isHurt && hurtAnimation != null) {
                    frame = hurtAnimation.getKeyFrame(hurtTimer, false);
                } else if (idleAnimation != null) {
                    frame = idleAnimation.getKeyFrame(stateTime, true);
                }
                break;
        }

        if (frame != null) {
            drawFrame(batch, frame, currentScaleX, currentScaleY, currentOffset);
        }
    }



	private void drawFrame(SpriteBatch batch, TextureRegion frame, float scaleX, float scaleY, float offset) {
		TextureRegion currentFrame = new TextureRegion(frame);

		if (!facingRight && !currentFrame.isFlipX()) {
			currentFrame.flip(true, false);
		} else if (facingRight && currentFrame.isFlipX()) {
			currentFrame.flip(true, false);
		}

		Vector2 position = body.getPosition();
		float scaledWidth = SPRITE_WIDTH * scaleX;
		float scaledHeight = SPRITE_HEIGHT * scaleY;

		batch.draw(currentFrame, position.x - scaledWidth / 2, position.y - scaledHeight / 2 - offset, scaledWidth,
				scaledHeight);
	}
	
	 public Body getBody() {
	        return body;
	    }

	@Override
	public void beginContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		if (fixtureA.getUserData() != null && fixtureA.getUserData().equals("player")
				|| fixtureB.getUserData() != null && fixtureB.getUserData().equals("player")) {
			canJump = true;
			isInAir = false;
		}
	}

	@Override
	public void endContact(Contact contact) {
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();

		if (fixtureA.getUserData() != null && fixtureA.getUserData().equals("player")
				|| fixtureB.getUserData() != null && fixtureB.getUserData().equals("player")) {
			isInAir = true; // Player has left the ground
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
	}

	public void dispose() {
		// Cleanup resources if needed
	}

	public float getMaxHealth() {
		return maxHealth;
	}

	public float getHealth() {
		return currentHealth;
	}

	public float getCurrentHealth() {
		// TODO Auto-generated method stub
		return currentHealth;
	}
}