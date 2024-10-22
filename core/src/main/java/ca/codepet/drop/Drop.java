package ca.codepet.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Drop extends Game {

  public SpriteBatch batch;
  public BitmapFont font;

  public void create() {
    batch = new SpriteBatch();
    font = new BitmapFont(); // default Arial font

    // Generate a font from TTF
    FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Play-Regular.ttf"));
    FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
    parameter.size = 24; // font size
    font = generator.generateFont(parameter); // Generate BitmapFont from TTF
    generator.dispose();

    this.setScreen(new MainMenuScreen(this));
  }

  public void render() {
    super.render(); // important!
  }

  public void dispose() {
    batch.dispose();
    font.dispose();
  }

}
