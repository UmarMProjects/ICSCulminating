package utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AssetManager {
    // Textures
    private static Texture catIdleTexture;
    private static Texture catRunTexture;
    private static Texture catJumpTexture;
    private static Texture catFallTexture;
    private static Texture catAttackTexture;
    private static Texture catAirAttackTexture;
    private static Texture catDodgeTexture;
    private static Texture catLightAttackTexture;
    private static Texture playerHurtTexture;
    private static Texture playerDeathTexture;

    // Animations
    private static Animation<TextureRegion> catIdleAnimation;
    private static Animation<TextureRegion> catRunAnimation;
    private static Animation<TextureRegion> catJumpAnimation;
    private static Animation<TextureRegion> catFallAnimation;
    private static Animation<TextureRegion> catAttackAnimation;
    private static Animation<TextureRegion> catAirAttackAnimation;
    private static Animation<TextureRegion> catLightAttackFirstAnimation;
    private static Animation<TextureRegion> catLightAttackSecondAnimation;
    private static Animation<TextureRegion> catDodgeAnimation;
    private static Animation<TextureRegion> enemyIdleAnimation;
    private static Animation<TextureRegion> enemyDeathAnimation;
    private static Animation<TextureRegion> playerHurtAnimation;
    private static Animation<TextureRegion> playerDeathAnimation;

    public static void loadAssets() {
        try {
            // Load textures
            catIdleTexture = new Texture(Gdx.files.internal("sprites/player/Cat_Idle.png"));
            catRunTexture = new Texture(Gdx.files.internal("sprites/player/Cat_Run.png"));
            catJumpTexture = new Texture(Gdx.files.internal("sprites/player/Cat_Jump.png"));
            catFallTexture = new Texture(Gdx.files.internal("sprites/player/Cat_Fall.png"));
            catAttackTexture = new Texture(Gdx.files.internal("sprites/player/Cat_Attack.png"));
            catDodgeTexture = new Texture(Gdx.files.internal("sprites/player/Cat_Dodge.png"));
            catAirAttackTexture = new Texture(Gdx.files.internal("sprites/player/Cat_AirAttack.png"));
            catLightAttackTexture = new Texture(Gdx.files.internal("sprites/player/Cat_LightAttack.png"));
            
            Texture enemyIdleTexture = new Texture(Gdx.files.internal("sprites/enemy/Enemy3-Idle.png"));
            Texture enemyDeathTexture = new Texture(Gdx.files.internal("sprites/enemy/Enemy3_Die.png"));
            
            playerHurtTexture = new Texture(Gdx.files.internal("sprites/player/Cat_Hurt.png"));
            playerDeathTexture = new Texture(Gdx.files.internal("sprites/player/Cat_Death.png"));

            // Create animations
            TextureRegion[][] tmp = TextureRegion.split(
                catLightAttackTexture,
                catLightAttackTexture.getWidth() / 10,
                catLightAttackTexture.getHeight()
            );

            // Create first attack animation (6 frames)
            TextureRegion[] firstAttackFrames = new TextureRegion[6];
            System.arraycopy(tmp[0], 0, firstAttackFrames, 0, 6);
            catLightAttackFirstAnimation = new Animation<>(0.08f, firstAttackFrames);

            // Create second attack animation (4 frames)
            TextureRegion[] secondAttackFrames = new TextureRegion[4];
            System.arraycopy(tmp[0], 6, secondAttackFrames, 0, 4);
            catLightAttackSecondAnimation = new Animation<>(0.08f, secondAttackFrames);

            catIdleAnimation = createAnimation(catIdleTexture, 8, 1, GameConstants.FRAME_DURATION);
            catRunAnimation = createAnimation(catRunTexture, 10, 1, GameConstants.FRAME_DURATION);
            catJumpAnimation = createAnimation(catJumpTexture, 4, 1, GameConstants.FRAME_DURATION);
            catFallAnimation = createAnimation(catFallTexture, 4, 1, GameConstants.FRAME_DURATION);
            catAttackAnimation = createAnimation(catAttackTexture, 6, 1, 0.13f);
            catDodgeAnimation = createAnimation(catDodgeTexture, 8, 1, 0.075f);
            catAirAttackAnimation = createAnimation(catAirAttackTexture, 6, 1, 0.13f);
            
            enemyIdleAnimation = createAnimation(enemyIdleTexture, 8, 1, 0.1f);
            enemyDeathAnimation = createAnimation(enemyDeathTexture, 16, 1, 0.1f);
            
            
            playerHurtAnimation = createAnimation(playerHurtTexture, 4, 1, 0.1f);
            playerDeathAnimation = createAnimation(playerDeathTexture, 9, 1, 0.2f);

        } catch (Exception e) {
            System.err.println("Error loading assets: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Animation<TextureRegion> createAnimation(Texture texture, int cols, int rows, float frameDuration) {
        TextureRegion[][] tmp = TextureRegion.split(
            texture, 
            texture.getWidth() / cols, 
            texture.getHeight() / rows
        );
        
        TextureRegion[] frames = new TextureRegion[cols * rows];
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                frames[index++] = tmp[i][j];
            }
        }
        
        return new Animation<>(frameDuration, frames);
    }

    // Getters
    public static Animation<TextureRegion> getPlayerIdleAnimation() { return catIdleAnimation; }
    public static Animation<TextureRegion> getPlayerRunAnimation() { return catRunAnimation; }
    public static Animation<TextureRegion> getPlayerJumpAnimation() { return catJumpAnimation; }
    public static Animation<TextureRegion> getPlayerFallAnimation() { return catFallAnimation; }
    public static Animation<TextureRegion> getPlayerAttackAnimation() { return catAttackAnimation; }
    public static Animation<TextureRegion> getPlayerDodgeAnimation() { return catDodgeAnimation; }
    public static Animation<TextureRegion> getPlayerAirAttackAnimation() { return catAirAttackAnimation; }
    public static Animation<TextureRegion> getPlayerLightAttackFirstAnimation() { return catLightAttackFirstAnimation; }
    public static Animation<TextureRegion> getPlayerLightAttackSecondAnimation() { return catLightAttackSecondAnimation; }
    public static Animation<TextureRegion> getEnemyIdleAnimation() { return enemyIdleAnimation; }
    public static Animation<TextureRegion> getEnemyDeathAnimation() { return enemyDeathAnimation; }
    
    public static Animation<TextureRegion> getPlayerHurtAnimation() {
        return playerHurtAnimation;
    }

    public static Animation<TextureRegion> getPlayerDeathAnimation() {
        return playerDeathAnimation;
    }

    public static void dispose() {
        catIdleTexture.dispose();
        catRunTexture.dispose();
        catJumpTexture.dispose();
        catFallTexture.dispose();
        catAttackTexture.dispose();
        catDodgeTexture.dispose();
        catAirAttackTexture.dispose();
        catLightAttackTexture.dispose();
    }
}