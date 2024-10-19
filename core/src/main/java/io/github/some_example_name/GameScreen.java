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

public class GameScreen implements Screen {
  final Drop game;

  Texture dropImage;
  Texture bucketImage;
  Texture backgroundImage;
  Sound dropSound;
  Music rainMusic;
  OrthographicCamera camera;
  Array<Rectangle> raindrops;
  long lastDropTime;
  int dropsGathered;

  Sprite bucketSprite;

  public GameScreen(final Drop game) {
    this.game = game;

    // Load the images for the droplet and the bucket, 64x64 pixels each
    dropImage = new Texture(Gdx.files.internal("drop.png"));
    bucketImage = new Texture(Gdx.files.internal("bucket.png"));
    backgroundImage = new Texture(Gdx.files.internal("background.png"));

    // Load the drop sound effect and the rain background "music"
    dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.mp3"));
    rainMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
    rainMusic.setLooping(true);

    // Create the camera
    camera = new OrthographicCamera();
    camera.setToOrtho(false, 800, 480);

    // Create the bucket sprite
    bucketSprite = new Sprite(bucketImage);
    bucketSprite.setSize(100, 100); // Set size to match the original Rectangle size
    bucketSprite.setPosition(800 / 2 - 64 / 2, 20); // Set initial position

    // Create the raindrops array and spawn the first raindrop
    raindrops = new Array<>();
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

    // Update the camera
    camera.update();

    // Set the SpriteBatch to use the camera's coordinate system
    game.batch.setProjectionMatrix(camera.combined);

    // Begin a new batch and draw the background, text, and raindrops
    game.batch.begin();
    game.batch.draw(backgroundImage, 0, 0, camera.viewportWidth, camera.viewportHeight);
    game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 0, 480);

    // Draw the bucket sprite
    bucketSprite.draw(game.batch);

    // Draw all raindrops
    for (Rectangle raindrop : raindrops) {
      game.batch.draw(dropImage, raindrop.x, raindrop.y);
    }
    game.batch.end();

    // Process user input
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

    // Ensure the bucket stays within the screen bounds
    if (bucketSprite.getX() < 0)
      bucketSprite.setX(0);
    if (bucketSprite.getX() > 800 - bucketSprite.getWidth())
      bucketSprite.setX(800 - bucketSprite.getWidth());

    // Check if we need to spawn a new raindrop
    if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
      spawnRaindrop();

    // Move the raindrops and handle collisions
    Iterator<Rectangle> iter = raindrops.iterator();
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
  public void show() {
    // Start the playback of the background music
    rainMusic.play();
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
    dropImage.dispose();
    bucketImage.dispose();
    dropSound.dispose();
    rainMusic.dispose();
  }
}
