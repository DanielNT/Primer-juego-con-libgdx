package juego;

import com.badlogic.gdx.Game;
import util.ScreenEnum;
import util.ScreenManager;


/**
 * Realizado por Daniel el 13/01/2017.
 */

public class MiJuego extends Game {


	private MiJuego game;

	public Game getGame() {
		return game;
	}

	public MiJuego() {
		game = this;
	}

	@Override
	public void create () {
		ScreenManager.getInstance().initialize(this);
		ScreenManager.getInstance().showScreen(ScreenEnum.SPLASH_SCREEN, game);
	}


}
