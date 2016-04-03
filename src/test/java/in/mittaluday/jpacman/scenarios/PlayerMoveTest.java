package in.mittaluday.jpacman.scenarios;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nl.tudelft.jpacman.Launcher;
import nl.tudelft.jpacman.board.Direction;
import nl.tudelft.jpacman.board.Square;
import nl.tudelft.jpacman.board.Unit;
import nl.tudelft.jpacman.game.SinglePlayerGame;
import nl.tudelft.jpacman.level.Pellet;
import nl.tudelft.jpacman.level.Player;
import nl.tudelft.jpacman.npc.ghost.Navigation;

public class PlayerMoveTest {
	
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
		Unit nearestPellet = Navigation.findNearest(Pellet.class, player.getSquare());
		List<Direction> moves = Navigation.shortestPath(player.getSquare(), nearestPellet.getSquare(), player);
		for (Direction move : moves) {
			game.move(player, move);
		}
		
		Direction lastMove = moves.get(moves.size()-1);
		switch(lastMove){
			case EAST: lastMove = Direction.WEST; break;
			case WEST: lastMove = Direction.EAST; break;
			case NORTH: lastMove = Direction.SOUTH; break;
			case SOUTH: lastMove = Direction.NORTH; break;
		}
			
		int startingScore = player.getScore();
		game.move(player, lastMove);
		if(player.isAlive()){
			assertEquals("Score changes on moving to an empty square", startingScore, player.getScore());
		}
		game.stop();
	}
}
