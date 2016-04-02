package in.mittaluday.jpacman.scenarios;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.game.Game;

public class GameStartTest {

	
	private Launcher gameLauncher;
	
	@Before
	public void setUpPacman(){
		gameLauncher = new Launcher();
		gameLauncher.launch();
	}	
	
	@Test
	public void gameShouldStart(){
		Game game = gameLauncher.getGame();
		game.start();
		assertTrue("Game did not start", game.isInProgress());
	}
	
	@After
	public void tearDown(){
		gameLauncher.dispose();
	}
}
