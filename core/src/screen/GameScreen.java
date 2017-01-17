package screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

import juego.MiJuego;

/**
 * Realizado por Daniel el 13/01/2017.
 */

public class GameScreen extends AbstractScreen {

    //constantes
    private static final float PLANE_JUMP_IMPULSE = 350;
    private static final float GRAVITY = -20;
    private static final float PLANE_VELOCITY_X = 200;
    private static final float PLANE_START_Y = 100;
    private static final float PLANE_START_X = 50;

    private int level; //nivel actual;
    ShapeRenderer shapeRenderer;
    SpriteBatch batch;
    OrthographicCamera camera, uiCamera;
    Texture background;
    TextureRegion ground;
    float groundOffsetX = 0;
    TextureRegion nubes, rock, rockDown;
    Animation<TextureRegion> plane;
    TextureRegion ready, gameOver;
    BitmapFont font;
    Vector2 planePosition = new Vector2();
    Vector2 planeVelocity = new Vector2();
    float planeStateTime = 0;
    Vector2 gravity = new Vector2();
    Array<Rock> rocks = new Array<Rock>();
    private MiJuego game;
    GameState gameState = GameState.Start;
    int score = 0;
    Rectangle rect1 = new Rectangle(), rect2 = new Rectangle();
    Music music;
    Stage stage;
    Sound explode;
    public static int widthPantalla = 800, heightPantalla = 480;
    private int highscore;
    private Preferences preferences;


    public GameScreen(MiJuego game) {
        super();
        this.game = game;

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("yorkwhiteletter.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 35;
        font = generator.generateFont(parameter); //        font = new BitmapFont(Gdx.files.internal("arial.fnt"));
        generator.dispose();
        background = new Texture("background2.png");
        ground = new TextureRegion(new Texture("grass.png"));
        nubes = new TextureRegion(new Texture("nubes.png"));
    }

    @Override
    public void show() {

        preferences = Gdx.app.getPreferences("highscore");
        highscore = preferences.getInteger("max", 0);
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, widthPantalla, heightPantalla);
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, widthPantalla, heightPantalla);
        uiCamera.update();

//        ceiling = new TextureRegion(ground);
//        ceiling.flip(true, true);

        rockDown = new TextureRegion(modificarSize("obama.png", 135, 128));
//        rockDown = new TextureRegion(rock);
//        rockDown.flip(false, true);
        rock = new TextureRegion(modificarSize("hillary.png", 170, 122));

        Texture frame1 = modificarSize("trumpanimado1.png", 105, 220);
        frame1.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        Texture frame2 = modificarSize("trumpanimado2.png", 105, 220);
        Texture frame3 = modificarSize("trumpanimado3.png", 105, 220);

        ready = new TextureRegion(new Texture("ready.png"));
        gameOver = new TextureRegion(new Texture("gameover.png"));
        plane = new Animation<TextureRegion>(0.2f, new TextureRegion(frame1), new TextureRegion(frame2), new TextureRegion(frame3));
        plane.setPlayMode(Animation.PlayMode.LOOP);

        music = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        music.setLooping(true);
        music.play();

        explode = Gdx.audio.newSound(Gdx.files.internal("explode.wav"));

        resetWorld();
    }

    private void resetWorld() {
        score = 0;
        groundOffsetX = 0;
        planePosition.set(PLANE_START_X, PLANE_START_Y);
        planeVelocity.set(0, 0);
        gravity.set(0, GRAVITY);
        camera.position.x = 400;

        rocks.clear();
        for (int i = 0; i < 5; i++) {
            boolean isDown = MathUtils.randomBoolean();
            rocks.add(new Rock(700 + i * 200, isDown ? heightPantalla - rock.getRegionHeight() : 0, isDown ? rockDown : rock));
        }
    }

    private void updateWorld() {
        float deltaTime = Gdx.graphics.getDeltaTime();
        planeStateTime += deltaTime;

        if (Gdx.input.justTouched()) {
            if (gameState == GameState.Start) {
                gameState = GameState.Running;
            }
            if (gameState == GameState.Running) {
                planeVelocity.set(PLANE_VELOCITY_X, PLANE_JUMP_IMPULSE);
            }
            if (gameState == GameState.GameOver) {
                gameState = GameState.Start;
                resetWorld();
            }
        }

        if (gameState != GameState.Start) planeVelocity.add(gravity);

        planePosition.mulAdd(planeVelocity, deltaTime);

        camera.position.x = planePosition.x + 350;
        if (camera.position.x - groundOffsetX > ground.getRegionWidth() + 400) {
            groundOffsetX += ground.getRegionWidth();
        }

        rect1.set(planePosition.x + 20, planePosition.y, plane.getKeyFrames()[0].getRegionWidth() - 20, plane.getKeyFrames()[0].getRegionHeight());
        for (Rock r : rocks) {
            if (camera.position.x - r.position.x > 400 + r.image.getRegionWidth()) {
                boolean isDown = MathUtils.randomBoolean();
                r.position.x += 5 * 200;
                r.position.y = isDown ? heightPantalla - rock.getRegionHeight() : 0;
                r.image = isDown ? rockDown : rock;
                r.counted = false;
            }
            //Rectángulo de las rocks
            rect2.set(r.position.x + (r.image.getRegionWidth() -10) / 2 + 20, r.position.y, 15, r.image.getRegionHeight() - 20);

            if (rect1.overlaps(rect2)) {
                if (gameState != GameState.GameOver) explode.play();
                gameState = GameState.GameOver;
                planeVelocity.x = 0;
            }
            if (r.position.x < planePosition.x && !r.counted) {
                score++;
                r.counted = true;
            }
        }
        if (planePosition.y < ground.getRegionHeight() - 20 ||
                planePosition.y + plane.getKeyFrames()[0].getRegionHeight() > heightPantalla - nubes.getRegionHeight() + 20) {
            if (gameState != GameState.GameOver) explode.play();
            gameState = GameState.GameOver;
            planeVelocity.x = 0;
        }
    }

    private void drawWorld() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background, camera.position.x - widthPantalla / 2, 0, widthPantalla, heightPantalla); //fondo a pantalla completa

        //Dibujamos primero el suelo
        batch.draw(ground, groundOffsetX, 0);
        batch.draw(ground, groundOffsetX + ground.getRegionWidth(), 0);

        for (Rock rock : rocks) {
            batch.draw(rock.image, rock.position.x, rock.position.y);
        }

        batch.draw(nubes, groundOffsetX, heightPantalla - nubes.getRegionHeight());
        batch.draw(nubes, groundOffsetX + nubes.getRegionWidth(), heightPantalla - nubes.getRegionHeight());

        batch.draw(plane.getKeyFrame(planeStateTime), planePosition.x, planePosition.y);

        batch.end();
        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();

        if (gameState == GameState.Start) {
            batch.draw(ready, widthPantalla / 2 - ready.getRegionWidth() / 2, heightPantalla / 2 - ready.getRegionHeight() / 2);
        }
        if (gameState == GameState.GameOver) {
            batch.draw(gameOver, widthPantalla / 2 - gameOver.getRegionWidth() / 2, heightPantalla / 2 - gameOver.getRegionHeight() / 2);

            //Si la puntuación supera la máxima, se actualiza.
            if (highscore < score) {
                preferences.putInteger("max", score);
                highscore = score;
                preferences.flush(); //para guardar la nueva puntuación
            }
        }
        if (gameState == GameState.GameOver || gameState == GameState.Running) {
            font.draw(batch, "Puntos: " + score + "  Max: " + highscore, widthPantalla / 2, heightPantalla - 60); //Muestra la puntuación y récord
        }
        batch.end();
    }

    @Override
    public void render(float planeStateTime) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch = new SpriteBatch();
        //stage = new Stage(new StretchViewport(widthPantalla, heightPantalla), batch);
        updateWorld();
        drawWorld();
    }

    static class Rock {
        Vector2 position = new Vector2();
        TextureRegion image;
        boolean counted;

        public Rock(float x, float y, TextureRegion image) {
            this.position.x = x;
            this.position.y = y;
            this.image = image;
        }
    }

    static enum GameState {
        Start, Running, GameOver
    }


    /**
     * Método para cambiar el tamaño de la textura que se generará a partir del archivo
     *
     * @param file
     * @param nWidth
     * @param nHeight
     * @return la textura modificada
     */
    private Texture modificarSize(String file, int nWidth, int nHeight) {
        Pixmap pixmap200 = new Pixmap(Gdx.files.internal(file));
        Pixmap pixmap100 = new Pixmap(nWidth, nHeight, pixmap200.getFormat());
        pixmap100.drawPixmap(pixmap200,
                0, 0, pixmap200.getWidth(), pixmap200.getHeight(),
                0, 0, pixmap100.getWidth(), pixmap100.getHeight()
        );
        Texture textureSalida = new Texture(pixmap100);
        pixmap200.dispose();
        pixmap100.dispose();
        return textureSalida;
    }


    @Override
    public void buildStage() {
        super.dispose();
    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
