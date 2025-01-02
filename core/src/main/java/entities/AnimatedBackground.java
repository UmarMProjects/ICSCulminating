package entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class AnimatedBackground {
    private final Texture[] layers;
    private final float[] speeds;
    private final float[] offsets;
    private final float width;
    private final float height;

    public AnimatedBackground(Texture[] layers, float[] speeds, float width, float height) {
        this.layers = layers;
        this.speeds = speeds;
        this.offsets = new float[layers.length];
        this.width = width;
        this.height = height;
    }

    public void update(float deltaTime) {
        for (int i = 0; i < layers.length; i++) {
            offsets[i] += speeds[i] * deltaTime;
            if (offsets[i] > width) {
                offsets[i] -= width;
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < layers.length; i++) {
            // Draw the first instance of the texture
            batch.draw(layers[i], -offsets[i], 0, width, height);
            // Draw a second instance of the texture, offset by its width, to create the looping effect
            batch.draw(layers[i], width - offsets[i], 0, width, height);
        }
    }
}