package entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

import utils.GameConstants;


import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Platform {
    private final Body body;
    private final float width;
    private final float height;

    public Platform(Body body, float width, float height) {
        this.body = body;
        this.width = width;
        this.height = height;
    }

    public Body getBody() {
        return body;
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.rect(body.getPosition().x - width / 2, body.getPosition().y - height / 2, width, height);
    }
}