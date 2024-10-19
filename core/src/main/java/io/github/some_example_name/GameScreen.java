package io.github.some_example_name;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.assets.AssetManager;

public class GameScreen implements Screen {
  final Drop game;

  AssetManager assetManager;
  OrthographicCamera camera;
  Array<Rectangle> raindrops;
  long lastDropTime;
  int dropsGathered;

  Sprite bucketSprite;

  public GameScreen(final Drop game) {
    this.game = game;

    // Create an AssetManager
    assetManager = new AssetManager();

    // Queue assets for loading
    assetManager.load("drop.png", Texture.class);
    assetManager.load("bucket.png", Texture.class);
    assetManager.load("background.png", Texture.class);
    assetManager.load("drop.mp3", Sound.class);
    assetManager.load("music.mp3", Music.class);

    // Create the camera
    camera = new OrthographicCamera();
    camera.setToOrtho(false, 800, 480);

    // Create the raindrops array
    raindrops = new Array<>();
  }

  @Override
  public void show() {
    // Load assets and wait until they are fully loaded
    assetManager.finishLoading();

    // Retrieve the loaded assets
    Texture dropImage = assetManager.get("drop.png", Texture.class);
    Texture bucketImage = assetManager.get("bucket.png", Texture.class);
    Texture backgroundImage = assetManager.get("background.png", Texture.class);
    Sound dropSound = assetManager.get("drop.mp3", Sound.class);
    Music rainMusic = assetManager.get("music.mp3", Music.class);

    // Set up the bucket sprite
    bucketSprite = new Sprite(bucketImage);
    bucketSprite.setSize(100, 100); // Set size
    bucketSprite.setPosition(800 / 2 - 64 / 2, 20); // Initial position

    // Start the background music
    rainMusic.setLooping(true);
    rainMusic.play();

    // Spawn the first raindrop
    spawnRaindrop();
  }

  private void spawnRaindrop() {
    Rectangle raindrop = new Rectangle();
    raindrop.x = MathUtils.random(0, 800 - 64);
    raindrop.y = 480;
    raindrop.width = 64;
    raindrop.height = 64;
    raindrops.add(raindrop);
    lastDropTime = TimeUtils.nanoTime();
  }

  @Override
  public void render(float delta) {
    ScreenUtils.clear(Color.BLACK);
    camera.update();
    game.batch.setProjectionMatrix(camera.combined);

    // Begin a new batch and draw the background, text, and raindrops
    game.batch.begin();
    Texture backgroundImage = assetManager.get("background.png", Texture.class);
    game.batch.draw(backgroundImage, 0, 0, camera.viewportWidth, camera.viewportHeight);
    game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 480);

    // Draw the bucket sprite
    bucketSprite.draw(game.batch);

    // Draw all raindrops
    Texture dropImage = assetManager.get("drop.png", Texture.class);
    for (Rectangle raindrop : raindrops) {
      game.batch.draw(dropImage, raindrop.x, raindrop.y);
    }
    game.batch.end();

    // Process user input
    handleInput();

    // Ensure the bucket stays within the screen bounds
    constrainBucketPosition();

    // Spawn new raindrops and check for collisions
    updateRaindrops();
  }

  private void handleInput() {
    if (Gdx.input.isTouched()) {
      Vector3 touchPos = new Vector3();
      touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
      camera.unproject(touchPos);
      bucketSprite.setX(touchPos.x - bucketSprite.getWidth() / 2);
    }
    if (Gdx.input.isKeyPressed(Keys.LEFT))
      bucketSprite.translateX(-200 * Gdx.graphics.getDeltaTime());
    if (Gdx.input.isKeyPressed(Keys.RIGHT))
      bucketSprite.translateX(200 * Gdx.graphics.getDeltaTime());
  }

  private void constrainBucketPosition() {
    if (bucketSprite.getX() < 0)
      bucketSprite.setX(0);
    if (bucketSprite.getX() > 800 - bucketSprite.getWidth())
      bucketSprite.setX(800 - bucketSprite.getWidth());
  }

  private void updateRaindrops() {
    if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
      spawnRaindrop();

    Iterator<Rectangle> iter = raindrops.iterator();
    Sound dropSound = assetManager.get("drop.mp3", Sound.class);
    while (iter.hasNext()) {
      Rectangle raindrop = iter.next();
      raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
      if (raindrop.y + 64 < 0)
        iter.remove();
      if (raindrop.overlaps(bucketSprite.getBoundingRectangle())) {
        dropsGathered++;
        dropSound.play();
        iter.remove();
      }
    }
  }

  @Override
  public void resize(int width, int height) {
  }

  @Override
  public void hide() {
  }

  @Override
  public void pause() {
  }

  @Override
  public void resume() {
  }

  @Override
  public void dispose() {
    // Dispose the asset manager which will dispose all loaded assets
    assetManager.dispose();
  }
}
