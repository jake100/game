package game.object.component;

import game.Game;
import game.object.GameBoard;
import game.object.inventory.FireBomb;
import game.object.inventory.FireStorm;
import game.object.inventory.InsectDrop;
import game.object.inventory.PoisonBomb;

import java.util.ArrayList;
import java.util.Random;

import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
/*
 * Class that handles world generation.
 * it starts as all stone then it adds mountains and dirt.
 * it then adds enemies to the board and simuates turns.
 */
public class BoardGen
{
	private GameBoard board;
	private int[][] grid, terrainGrid, burnGrid;
	private Random rnd = new Random();
	private int x, y, counter;
	//weopons used to modify the land
	private FireBomb fireBomb;
	private PoisonBomb poisonBomb;
	private InsectDrop insectDrop;
	private FireStorm fireStorm;
	
	public BoardGen(GameBoard board)
	{
		this.board = board;
		fireBomb = new FireBomb(board);
		poisonBomb = new PoisonBomb(board);
		insectDrop = new InsectDrop(board);
		fireStorm = new FireStorm(board);
		poisonBomb.setCount(1000);
		fireBomb.setCount(1000);
		insectDrop.setCount(1000);
		fireStorm.setCount(1000);
		grid = board.getGrid();
		terrainGrid = board.getTerrainGrid();
		burnGrid = board.getBurnGrid();
	}
	public void simulateTurns(int count)
	{
		for(int i = 0; i < count; i++)
		{
			board.tileUpdate();
			board.setTurn(0);
		}
	}
	public void prepareForGame()
	{
		board.setGrid(grid);
		board.setTerrainGrid(terrainGrid);
		board.getParticleGen().setParticleGen(true);
		for(int i = 0;i < 400;i++)
		{
			board.spawnCload(Game.rndX(), Game.rndY(), rnd.nextInt(3) + 1);
		}
	}
	public void clearBurnGrid()
	{
		for(int x = 0; x < grid.length;x++)
		{
			for(int y = 0; y < grid[0].length;y++)
			{
				burnGrid[x][y] = 0;
			}
		}
	}
	public void weoponFire(int count)
	{
		for(int i = 0; i < count;i++)
		{
			fireBomb.fire(Game.rndX(), Game.rndY());
			poisonBomb.fire(Game.rndX(), Game.rndY());
			fireStorm.fire(Game.rndX(), Game.rndY());
		}

	}
	//removes certain tiles that are just by themselves and are not touching the edge.
	public void removeSingleTiles()
	{
		for(int x = 0; x < Game.TWidth;x++)
		{
			for(int y = 0; y < Game.THeight;y++)
			{
				if(terrainGrid[x][y] == GameBoard.stone || terrainGrid[x][y] == GameBoard.mountain)
				{
					if(isAlone(x, y, GameBoard.stone, GameBoard.mountain))terrainGrid[x][y] = GameBoard.grass;
				}
				if(terrainGrid[x][y] == GameBoard.dirt || terrainGrid[x][y] == GameBoard.grass)
				{
					if(isAlone(x, y, GameBoard.dirt, GameBoard.grass))terrainGrid[x][y] = GameBoard.stone;
				}
			}
		}
	}
	public boolean isAlone(int x, int y, int id, int altId)
	{
		if(x - 1 < 0 || x + 1 >= Game.TWidth || y - 1 < 0 || y + 1 >= Game.THeight)return false;
		
		if(terrainGrid[x + 1][y] == id || terrainGrid[x + 1][y] == altId)return false;
		if(terrainGrid[x - 1][y] == id || terrainGrid[x - 1][y] == altId)return false;
		
		if(terrainGrid[x][y + 1] == id || terrainGrid[x][y + 1] == altId)return false;
		if(terrainGrid[x][y - 1] == id || terrainGrid[x][y - 1] == altId)return false;
		
		if(terrainGrid[x + 1][y + 1] == id || terrainGrid[x + 1][y + 1] == altId)return false;
		if(terrainGrid[x + 1][y - 1] == id || terrainGrid[x + 1][y - 1] == altId)return false;
		if(terrainGrid[x - 1][y + 1] == id || terrainGrid[x - 1][y + 1] == altId)return false;
		if(terrainGrid[x - 1][y - 1] == id || terrainGrid[x - 1][y - 1] == altId)return false;
		return true;
	}
	public void clearBoard()
	{
		for(int x = 0; x < grid.length;x++)
		{
			for(int y = 0; y < grid[0].length;y++)
			{
				grid[x][y] = GameBoard.air;
				burnGrid[x][y] = 0;
			}
		}
	}
	public void addEnemy(ArrayList<Vector2f> list)
	{
		list = rndTile(list);
		if(grid[x][y] == GameBoard.air)grid[x][y] = GameBoard.boss;
		else addEnemy(list);
	}
	public void addEnemies(int count, ArrayList<Vector2f> list)
	{
		for(int i = 0;i < count;i++)
		{
			addEnemy(list);
		}
	}
	public void addId(int id, ArrayList<Vector2f> list)
	{
		list = rndTile(list);
		switch(id)
		{
		case GameBoard.mountain:
			if(terrainGrid[x][y] == GameBoard.stone)terrainGrid[x][y] = GameBoard.mountain;
			else addId(id, x, y, list);
			break;
		case GameBoard.dirt:
			if(terrainGrid[x][y] == GameBoard.stone)terrainGrid[x][y] = GameBoard.dirt;
			else addId(0, x, y, list);
			break;
		}
	}
	public void addId(int id, int xx, int yy, ArrayList<Vector2f> list)
	{
		for(int i = 0;i < list.size() - 1;i++)
		{
			if((int)list.get(i).x == xx && (int)list.get(i).y == yy)
			{
				switch(id)
				{
				case GameBoard.mountain:
					if(terrainGrid[x][y] == GameBoard.stone)terrainGrid[x][y] = GameBoard.mountain;
					else addId(id, list);
					break;
				case GameBoard.dirt:
					if(terrainGrid[x][y] == GameBoard.stone)terrainGrid[x][y] = GameBoard.dirt;
					else addId(0, list);
					break;
				}
				return;
			}
		}
	}
	public void addObsidian(ArrayList<Vector2f> list)
	{
		list = rndTile(list);
		if(grid[x][y] == GameBoard.air)grid[x][y] = GameBoard.meteor;
		else addObsidian(list);
	}
	public void extendMountains(int count, ArrayList<Vector2f> list)
	{
		for(int i = 0;i < count;i++)
		{
			extendMountain(list);
		}
	}
	public void extendMountain(ArrayList<Vector2f> list)
	{
		if(list.size() == 0)return;
		list = rndTile(list);
		if(terrainGrid[x][y] == GameBoard.mountain)addId(GameBoard.mountain, x, y, list);
		else extendMountain(list);
	}
	public void addGrass(ArrayList<Vector2f> list)
	{
		list = rndTile(list);
		if(terrainGrid[x][y] == GameBoard.dirt)terrainGrid[x][y] = GameBoard.grass;
		else addGrass(list);
	}
	public void addGrass(int x, int y, ArrayList<Vector2f> list)
	{
		rndShift();
		if(x < 0 || x >= grid.length || y < 0 || y >= grid[0].length)addGrass(list);
		else if(terrainGrid[x][y] == GameBoard.dirt)terrainGrid[x][y] = GameBoard.grass;
		else addGrass(list);
	}
	public void rndShift()
	{
		int num = rnd.nextInt(4);
		if(num == 0)x++;
		if(num == 1)x--;
		if(num == 2)y++;
		if(num == 3)y--;
	}
	public ArrayList<Vector2f> rndTile(ArrayList<Vector2f> list)
	{
		int i = list.size() - 1;
		if(i > 1)i = rnd.nextInt(i);
		x = (int) list.get(i).x;
		y = (int) list.get(i).y;
		list.remove(i);
		return list;
	}
	public int getCounter()
	{
		return counter;
	}
	public void setCounter(int counter)
	{
		this.counter = counter;
	}
	
}
