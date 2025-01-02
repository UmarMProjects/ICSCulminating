package entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class HealthBar {
    private float maxHealth;
    private float currentHealth;
    private float width;
    private float height;
    private float x;
    private float y;

    public HealthBar(float maxHealth, double e, double d, float width, double f) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.width = width;
        this.height = (float) f;
        this.x = (float) e;
        this.y = (float) d;
    }

    public void update(float health) {
        this.currentHealth = health;
    }

    public void render(ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Render background (gray bar)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GRAY);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();

        // Render current health (green bar)
        float healthPercentage = currentHealth / maxHealth;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(x, y, width * healthPercentage, height);
        shapeRenderer.end();

        // Render border (black line)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.BLACK);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();
    }
}