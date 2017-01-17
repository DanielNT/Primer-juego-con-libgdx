package util;

/**
 * Realizado por Daniel el 13/01/2017.
 */
import juego.MiJuego;
import screen.AbstractScreen;
import screen.GameScreen;
import screen.SplashScreen;

public enum ScreenEnum {

//    MAIN_MENU {
//        public SplashScreen getScreen(Object... params) {
//            return new MainMenuScreen();
//        }
//    },

    SPLASH_SCREEN{
        public AbstractScreen getScreen(MiJuego miJuego) {
            return new SplashScreen(miJuego);
        }
    },

    GAME {
        public AbstractScreen getScreen(MiJuego miJuego) {
            return new GameScreen(miJuego);
        }
    };

    public abstract AbstractScreen getScreen(MiJuego miJuego);
}