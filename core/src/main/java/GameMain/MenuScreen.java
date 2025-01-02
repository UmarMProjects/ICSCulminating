package GameMain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class MenuScreen implements Screen {
    private Main game;
    private OrthographicCamera camera;

    public MenuScreen(Main game) {
        this.game = game;
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Main.VIRTUAL_WIDTH, Main.VIRTUAL_HEIGHT);
    }

    @Override
    public void render(float delta) {
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        game.batch.begin();
        game.font.draw(game.batch, "Welcome to Infinite Platformer!", 100, Main.VIRTUAL_HEIGHT - 100);
        game.font.draw(game.batch, "Press SPACE to begin!", 100, Main.VIRTUAL_HEIGHT - 150);
        game.batch.end();

        if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            game.setScreen(game.gameScreen);
            dispose();
        }
    }

    @Override
    public void show() {}

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}
}