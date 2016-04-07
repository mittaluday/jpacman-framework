package in.mittaluday.jpacman.scenarios;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.board.BoardFactory;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.game.GameFactory;
import nl.tudelft.jpacman.game.SinglePlayerGame;
import nl.tudelft.jpacman.level.Level;
import nl.tudelft.jpacman.level.LevelFactory;
import nl.tudelft.jpacman.level.MapParser;
import nl.tudelft.jpacman.level.Pellet;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.level.PlayerFactory;
import nl.tudelft.jpacman.npc.ghost.Ghost;
import nl.tudelft.jpacman.npc.ghost.GhostFactory;
import nl.tudelft.jpacman.npc.ghost.Navigation;
import nl.tudelft.jpacman.sprite.PacManSprites;

public class PlayerMoveTest {
	
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
	 * Scenario 2.1
	 * Checks if player consumes adjacent pellet and the score increases accordingly
	 */
	@Test
	public void playerShouldConsumeAdjacentPellet(){
		SinglePlayerGame game = (SinglePlayerGame) gameLauncher.getGame();
		game.start();
		
		Player player = game.getPlayers().get(0);
		Square playerStartingPosition = player.getSquare();
		Square playerLeftNeighbour = playerStartingPosition.getSquareAt(Direction.WEST);
		
		if(playerLeftNeighbour.isAccessibleTo(player)){
			
			List<Unit> unitsOccupyingNeighbourhood = playerLeftNeighbour.getOccupants();
			for (Unit unit : unitsOccupyingNeighbourhood) {
				if(unit instanceof Pellet){
					int startingScore = player.getScore();
					int pelletValue = ((Pellet) unit).getValue();
					game.move(player, Direction.WEST);
					int endingScore = player.getScore();
					
					assertEquals("Score didn't increase on pellet consumption", startingScore + pelletValue, endingScore);
					assertNull("Pellet did not disappear from the square", unit.getSquare());
					
				}
			}			
		}
		game.stop();
	}
	
	/**
	 * Scenario 2.2
	 * Checks if score stays same on moving the player to an empty square
	 * 
	 * 		Finds the nearest Pellet
	 * 		Moves to that Pellet
	 * 		Moves one step back since that square will be empty now
	 * 		Checks if points remain same or not if the player is alive
	 */
	@Test
	public void pointsShouldRemainSameOnEmptySquareMove(){
		SinglePlayerGame game = (SinglePlayerGame) gameLauncher.getGame();
		game.start();
		
		Player player = game.getPlayers().get(0);
		assertTrue(player.isAlive());
		Unit nearestPellet = Navigation.findNearest(Pellet.class, player.getSquare());
		List<Direction> moves = Navigation.shortestPath(player.getSquare(), nearestPellet.getSquare(), player);
		for (Direction move : moves) {
			game.move(player, move);
		}
		
		Direction lastMove = moves.get(moves.size()-1);
		lastMove = getOppositeDirection(lastMove);
			
		int startingScore = player.getScore();
		game.move(player, lastMove);
		if(player.isAlive()){
			assertEquals("Score changes on moving to an empty square", startingScore, player.getScore());
		}
		game.stop();
	}
	
	/**
	 * Scenario 2.3
	 * Checks if player dies on coming in contact with a Ghost
	 */
	@Test
	public void playerShouldDieOnMovingOnGhost(){
		SinglePlayerGame game = (SinglePlayerGame) gameLauncher.getGame();
		game.start();
		
		Player player = game.getPlayers().get(0);
		Unit nearestGhost = Navigation.findNearest(Ghost.class, player.getSquare());
		for (Direction dir : Direction.values()) {
			Square adjacentSquare = nearestGhost.getSquare().getSquareAt(dir);
			if(adjacentSquare.isAccessibleTo(player)){
				player.occupy(adjacentSquare);
				Direction moveToMake = getOppositeDirection(dir);
				game.move(player, moveToMake);
				assertFalse("Player should die when it moves on a ghost", player.isAlive());
			}
		}
	}
	
	
	/**
	 * Scenario 2.4
	 * Tests that player does not move into a wall
	 */
	@Test
	public void playerShouldNotMoveOnAWall(){
		Level level = parser.parseMap(Lists.newArrayList("###", "#P#", "###"));
		SinglePlayerGame game = (SinglePlayerGame) gf.createSinglePlayerGame(level);
		game.start();
		Player player = game.getPlayers().get(0);
		Square playerPosition = player.getSquare();
		game.move(player, Direction.EAST);
		assertEquals("Player position changed on moving into a wall", player.getSquare(), playerPosition);
		
	}
	
	/**
	 * Scenario 2.5
	 * Tests that player wins the game on eating the last pellet
	 */
	@Test
	public void playerShouldWinWhenLastPelletIsEaten(){
		//Use a single pellet board to test 
		Level level = parser.parseMap(Lists.newArrayList("####", "#.P#", "####"));
		SinglePlayerGame game = (SinglePlayerGame) gf.createSinglePlayerGame(level);
		game.start();
		Player player = game.getPlayers().get(0);
		game.move(player, Direction.WEST);
		assertFalse("Game is still in progress even after eating the last pellet", game.isInProgress());
	}
	
	/**
	 * Get the opposite direction of a given direction
	 * @param currentDirection
	 * @return
	 */
	private Direction getOppositeDirection(Direction currentDirection){
		switch(currentDirection){
			case EAST: currentDirection = Direction.WEST; break;
			case WEST: currentDirection = Direction.EAST; break;
			case NORTH: currentDirection = Direction.SOUTH; break;
			case SOUTH: currentDirection = Direction.NORTH; break;
		}
		return currentDirection;
	}
}
