package tankgame.game;

import java.awt.Point;

import tankgame.GameWorld;

public class DestructibleWall extends BackgroundObject {
	
    public DestructibleWall(int x, int y){
	super(new Point(x*32, y*32), new Point(0,0), GameWorld.sprites.get("wall2"));
    }
  
    public boolean collision(GameObject object) {
        if(location.intersects(object.getLocation())){
        	if(object instanceof Bullet)
        		this.show = false;
        	return true;
        } 
        return false;
    }
}