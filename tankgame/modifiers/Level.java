package tankgame.modifiers;

import java.awt.event.KeyEvent;
import java.awt.Point;
import java.io.*;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import tankgame.GameWorld;
import tankgame.game.*;

/*This is where enemies are introduced into the game according to a timeline*/
public class Level extends AbstractGameModifier implements Observer {
	int start;
        BufferedReader level;
	Integer position;
	Random generator = new Random();
        String input;
        String line;
        public int w, h;
	int endgameDelay = 100;	// don't immediately end on game end
	
	/*Constructor sets up arrays of enemies in a LinkedHashMap*/
	public Level(String input){
		super();
		this.input = input;
                
		try {
			level = new BufferedReader(new InputStreamReader(GameWorld.class.getResource(input).openStream()));
			line = level.readLine();
			w = line.length();
			h=0;
			while(line != null){
				h++;
				line = level.readLine();
			}
			level.close();
		} catch (IOException e) {
                    System.err.println(e.getMessage());
		}
	}
	
	public void load(){
        GameWorld world = GameWorld.getInstance();
		
	    try {
		level = new BufferedReader(new InputStreamReader(GameWorld.class.getResource(input).openStream()));
	        line = level.readLine();
		w = level.readLine().length();
		h=0;
                
		while(line!=null){
		    for(int index=0; index<w; index++) { 
			char object = line.charAt(index); 
				    
		        if(object=='1'){
		            IndestructibleWall wall = new IndestructibleWall(index,h);
			    world.addBackground(wall);
			}
				    
			if(object=='2'){
			    DestructibleWall wall = new DestructibleWall(index,h);
			    world.addBackground(wall);
			}
 
			if(object=='3'){
			    int[] controls = {KeyEvent.VK_A,KeyEvent.VK_W, KeyEvent.VK_D, KeyEvent.VK_S, KeyEvent.VK_SPACE};
			    world.addPlayer(new PlayerShip(new Point(index*32, h*32), new Point(0,0), world.sprites.get("player1"), controls, "player1"));
			}
				    
			if(object=='4'){
			    int[] controls = new int[] {KeyEvent.VK_LEFT,KeyEvent.VK_UP, KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_ENTER};
			    world.addPlayer(new PlayerShip(new Point(index*32, h*32), new Point(0,0), world.sprites.get("player2"), controls, "player2"));
			}
		    }
		    h++;
		    line = level.readLine();
		}
		level.close();
                
	    } catch (IOException e) {
                    System.err.println(e.getMessage());
	    }
	}
	
	public void read(Object theObject){
	}

	/*Level observes GameClock and updates on every tick*/
	@Override
	public void update(Observable o, Object arg) {
		GameWorld world = GameWorld.getInstance();
              
		if(world.isGameOver()){
			if(endgameDelay<=0){
				GameWorld.getInstance().removeClockObserver(this);
				GameWorld.getInstance().finishGame();
			} else endgameDelay--;
		}
	}
}
