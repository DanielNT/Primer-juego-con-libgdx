package screen;

/**
 * Realizado por Daniel el 12/01/2017.
 */
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import juego.MiJuego;

public class SplashScreen extends AbstractScreen
{
    private Image splashImage1;
    private Image splashImage2;
    private Stage stage;
    private MiJuego game; //el juego principal

    public SplashScreen(MiJuego game) {
        super();
        this.game= game;
    }

    @Override
    public void buildStage() {

        stage = new Stage();
        splashImage1 = new Image(new Texture(
                Gdx.files.internal("logo_sangalda.png")));

        splashImage2 = new Image(new Texture(
                Gdx.files.internal("gf.png")));

        stage.addActor(splashImage1);
        stage.addActor(splashImage2);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1,1,1,1); //color de fondo (la última cifra es el alpha)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void show() {

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();

        splashImage1.setPosition((width - splashImage1.getWidth()) / 2, (height - splashImage1.getHeight()) / 2); //movemos al centro de la pantalla

		/* Fade in the image and then swing it down */
        splashImage1.getColor().a = 0f;
        splashImage1.addAction(Actions.sequence(Actions.fadeIn(0.5f), Actions
                .delay(1, Actions.fadeOut(0.5f))));

        splashImage2.setPosition((width - splashImage2.getWidth()) / 2,
                (height - splashImage2.getHeight()) / 2);

		/* Fade in the image and then swing it down */
        splashImage2.getColor().a = 0f;
        splashImage2.addAction(Actions.delay(2, Actions.sequence(Actions.fadeIn(0.5f), Actions
                .delay(2, Actions.run(new Runnable() {
            @Override
            public void run() {
				/* Ir a la siguiente screen */
                game.setScreen(new GameScreen(game));
            }
        })))));
    }

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}