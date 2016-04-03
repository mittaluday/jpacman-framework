package in.mittaluday.jpacman.scenarios;


import static org.junit.Assert.*;

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
	}
}
