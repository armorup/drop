package ca.codepet.drop.screens;

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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import ca.codepet.drop.Drop;

import com.badlogic.gdx.assets.AssetManager;

public class GameScreen implements Screen {
  final Drop game; // Reference to the Game

  AssetManager assetManager;
  OrthographicCamera camera;
  Array<Sprite> raindrops;
  long lastDropTime;
  int dropsGathered;

  Sprite bucketSprite;

  public GameScreen(final Drop game) {
    this.game = game;

    // Create an AssetManager
    assetManager = new AssetManager();

    // Queue assets for loading
    assetManager.load("images/drop.png", Texture.class);
    assetManager.load("images/bucket.png", Texture.class);
    assetManager.load("images/background.png", Texture.class);
    assetManager.load("audio/drop.mp3", Sound.class);
    assetManager.load("audio/music.mp3", Music.class);

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
    Texture bucketImage = assetManager.get("images/bucket.png", Texture.class);

    // Set up the bucket sprite
    bucketSprite = new Sprite(bucketImage);
    bucketSprite.setSize(100, 100); // Set size
    bucketSprite.setPosition(800 / 2 - 64 / 2, 20); // Initial position

    // Start the background music
    Music rainMusic = assetManager.get("audio/music.mp3", Music.class);
    rainMusic.setLooping(true);
    rainMusic.play();

    // Spawn the first raindrop
    spawnRaindrop();
  }

  private void spawnRaindrop() {
    Texture dropImage = assetManager.get("images/drop.png", Texture.class);
    Sprite raindropSprite = new Sprite(dropImage); // Create a new Sprite for the raindrop
    raindropSprite.setSize(64, 64); // Set size
    raindropSprite.setPosition(MathUtils.random(0, 800 - 64), 480); // Set initial position
    raindrops.add(raindropSprite); // Add the Sprite to the array
    lastDropTime = TimeUtils.nanoTime();
  }

  @Override
  public void render(float delta) {
    ScreenUtils.clear(Color.BLACK);
    camera.update();
    game.batch.setProjectionMatrix(camera.combined);

    // Begin a new batch and draw the background, text, and raindrops
    game.batch.begin();
    Texture backgroundImage = assetManager.get("images/background.png", Texture.class);
    game.batch.draw(backgroundImage, 0, 0, camera.viewportWidth, camera.viewportHeight);
    game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 480);

    // Draw the bucket sprite
    bucketSprite.draw(game.batch);

    // Draw all raindrop sprites
    for (Sprite raindrop : raindrops) {
      raindrop.draw(game.batch);
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

    Iterator<Sprite> iter = raindrops.iterator();
    Sound dropSound = assetManager.get("audio/drop.mp3", Sound.class);
    while (iter.hasNext()) {
      Sprite raindrop = iter.next();
      raindrop.translateY(-200 * Gdx.graphics.getDeltaTime()); // Move the raindrop down
      if (raindrop.getY() + raindrop.getHeight() < 0)
        iter.remove();
      if (raindrop.getBoundingRectangle().overlaps(bucketSprite.getBoundingRectangle())) {
        dropsGathered++;
        dropSound.play();
        iter.remove();
      }
    }
  }

  @Override
  public void resize(int width, int height) {
    // Handle resizing if needed
  }

  @Override
  public void hide() {
    // Handle hide if needed
  }

  @Override
  public void pause() {
    // Handle pause if needed
  }

  @Override
  public void resume() {
    // Handle resume if needed
  }

  @Override
  public void dispose() {
    assetManager.dispose();
  }
}
