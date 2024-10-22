package ca.codepet.drop.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;

import ca.codepet.drop.Drop;

public class MainMenuScreen implements Screen {

  final Drop game;

  OrthographicCamera camera;

  public MainMenuScreen(final Drop game) {
    this.game = game;

    camera = new OrthographicCamera();
    camera.setToOrtho(false, 800, 480);
  }

  @Override
  public void show() {
    // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method 'show'");
  }

  @Override
  public void render(float delta) {
    ScreenUtils.clear(0, 0, 0.2f, 1);

    camera.update();
    game.batch.setProjectionMatrix(camera.combined);

    game.batch.begin();
    game.font.draw(game.batch, "Welcome to Drop!!! ", 100, 150);
    game.font.draw(game.batch, "Tap anywhere to begin!", 100, 100);
    game.batch.end();

    if (Gdx.input.isTouched()) {
      game.setScreen(new GameScreen(game));
      dispose();
    }
  }

  @Override
  public void resize(int width, int height) {
    // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method 'resize'");
  }

  @Override
  public void pause() {
    // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method 'pause'");
  }

  @Override
  public void resume() {
    // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method 'resume'");
  }

  @Override
  public void hide() {
    // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method 'hide'");
  }

  @Override
  public void dispose() {
    // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented method 'dispose'");
  }

}
