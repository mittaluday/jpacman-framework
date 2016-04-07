package in.mittaluday.jpacman.scenarios;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.game.SinglePlayerGame;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.npc.ghost.Ghost;
import nl.tudelft.jpacman.npc.ghost.Navigation;

public class GameSuspendTest {
	private Launcher gameLauncher;

	@Before
	public void setUpPacman(){
		gameLauncher = new Launcher();
		gameLauncher.launch();
	}

	@After
	public void tearDown(){
		gameLauncher.dispose();
	}
	

	/**
	 * Scenario 4.1
	 * Tests if the game suspends 
	 * @throws InterruptedException
	 */
	@Test
	public void gameShouldSuspendOnStop() throws InterruptedException{
		SinglePlayerGame game = (SinglePlayerGame) gameLauncher.getGame();
		game.start();
		
		//Assert that the game is in progress
		assertTrue(game.isInProgress());
		
		Player player = game.getPlayers().get(0);
		Square playerPosition = player.getSquare();
		
		Unit ghost = Navigation.findNearest(Ghost.class, playerPosition);
		Square ghostPosition = ghost.getSquare();
		
		game.stop();
		
		//Assert that the game has paused
		assertFalse(game.isInProgress());
		
		//Wait for a few ticks
		Thread.sleep(300);
		
		//Assert that the positions of ghost and player stay fixed 
		assertEquals(ghostPosition, ghost.getSquare());
		assertEquals(playerPosition, player.getSquare());
	}
	
	/**
	 * Scenario 4.2
	 * Tests if the game resumes after a suspension
	 */
	@Test
	public void gameShouldRestartAfterSuspend(){
		SinglePlayerGame game = (SinglePlayerGame) gameLauncher.getGame();
		game.start();
		game.stop();
		
		//Assert that the game is suspended
		assertFalse(game.isInProgress());
		
		//Restart the game
		game.start();
		
		//Assert that the game is in progress after the suspend
		assertTrue(game.isInProgress());
	}
}
