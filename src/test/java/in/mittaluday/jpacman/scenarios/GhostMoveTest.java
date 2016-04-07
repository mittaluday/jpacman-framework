package in.mittaluday.jpacman.scenarios;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.board.Board;
import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.game.GameFactory;
import nl.tudelft.jpacman.game.SinglePlayerGame;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.LevelFactory;
import nl.tudelft.jpacman.level.MapParser;
import nl.tudelft.jpacman.level.Pellet;
import nl.tudelft.jpacman.level.PlayerFactory;
import nl.tudelft.jpacman.npc.ghost.Ghost;
import nl.tudelft.jpacman.npc.ghost.GhostFactory;
import nl.tudelft.jpacman.npc.ghost.Navigation;
import nl.tudelft.jpacman.sprite.PacManSprites;

public class GhostMoveTest {

	private Launcher gameLauncher;
	private PacManSprites sprites;
	private MapParser parser;
	private GameFactory gf;
	
	@Before
	public void setUpPacman(){
		gameLauncher = new Launcher();
		gameLauncher.launch();
		sprites = new PacManSprites();
		parser = new MapParser(new LevelFactory(sprites, new GhostFactory(
				sprites)), new BoardFactory(sprites));	
		gf = new GameFactory(new PlayerFactory(sprites));
	}
	
	@After
	public void tearDown(){
		gameLauncher.dispose();
	}
	
	
	/**
	 * Scenario 3.1
	 * Tests whether ghosts move automatically 
	 * @throws InterruptedException
	 */
	@Test
	public void ghostShouldMove() throws InterruptedException{
		SinglePlayerGame game = (SinglePlayerGame) gameLauncher.getGame();
		
		//Have to start the game programmatically since otherwise this happens from the UI button action
		game.start();
		Board b = game.getLevel().getBoard();
		
		//Reference square
		Square s1 = b.squareAt(1, 1);
		Unit ghost = Navigation.findNearest(Ghost.class, s1);
		Square initialGhostPosition = ghost.getSquare();
		
		//Wait for a tick
		Thread.sleep(200);
		assertFalse(initialGhostPosition == ghost.getSquare());
	}
	
	/**
	 * Scenario 3.2
	 * Checks if a ghost can move over a pellet
	 */
	@Test
	public void ghostMovesOverPellet(){
		SinglePlayerGame game = (SinglePlayerGame) gameLauncher.getGame();
		
		game.start();
		Board b = game.getLevel().getBoard();

		Square s1 = b.squareAt(1, 1);
		Unit ghost = Navigation.findNearest(Ghost.class, s1);
		Square initialGhostPosition = ghost.getSquare();
		Unit pellet = Navigation.findNearest(Pellet.class, initialGhostPosition);
		ghost.occupy(pellet.getSquare());

		
		assertEquals(ghost.getSquare(), pellet.getSquare());
		
		assertEquals(ghost.getSquare().getOccupants().size(), 2);
		
	}
	
	/**
	 * Scenario 3.3
	 * Tests if a ghost leaves a cell with the pellet still there.
	 * @throws InterruptedException 
	 */
	@Test
	public void GhostLeavesCellWithPellet() throws InterruptedException{
		Level level = parser.parseMap(Lists.newArrayList("####", "G..P", "####"));
		SinglePlayerGame game = (SinglePlayerGame) gf.createSinglePlayerGame(level);
		game.start();
		Board b = level.getBoard();
		Square pelletSquare = b.squareAt(1, 1);
		Unit ghost = Navigation.findNearest(Ghost.class, pelletSquare);

		//Let ghost occupy a pellet position
		ghost.occupy(pelletSquare);
		
		//Tick event
		Thread.sleep(200);
		
		//Assert that the pellet square and ghost square has changed => Ghost left the pellet square
		assertNotSame(pelletSquare, ghost.getSquare());
		
		//Assert that the pellet square contains only one occupant not which is the pellet.
		assertEquals(pelletSquare.getOccupants().size(), 1);
		game.stop();
		
	}
	
	/**
	 * Scenario 3.4
	 * Test that player dies when ghost moves on it automatically
	 * @throws InterruptedException
	 */
	@Test
	public void playerShouldDieWhenGhostMovesOnIt() throws InterruptedException{
		Level level = parser.parseMap(Lists.newArrayList("####", ".GP#", "####"));
		SinglePlayerGame game = (SinglePlayerGame) gf.createSinglePlayerGame(level);
		game.start();
		assertTrue(game.getPlayers().get(0).isAlive());
		Thread.sleep(200);
		assertFalse(game.getPlayers().get(0).isAlive());
	}
	
	
	
	
	
}
