package entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class HealthBar {
    private float x, y, width, height;
    private float health;

    public HealthBar(double f, double e, float width, double d, float health) {
        this.x = (float) f;
        this.y = (float) e;
        this.width = width;
        this.height = (float) d;
        this.health = health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public void render(ShapeRenderer shapeRenderer, OrthographicCamera camera) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Draw the background of the health bar
        shapeRenderer.setColor(1, 0, 0, 1);
        shapeRenderer.rect(x, y, width, height);

        // Draw the foreground of the health bar
        shapeRenderer.setColor(0, 1, 0, 1);
        shapeRenderer.rect(x, y, width * (health / 100), height);

        shapeRenderer.end();
    }
}