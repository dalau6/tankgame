package tankgame.game;

import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.awt.Point;

import tankgame.GameWorld;
import tankgame.modifiers.motions.MotionController;
import tankgame.modifiers.motions.SimpleFiringMotion;

/*Bullets fired by player and enemy weapons*/
public class Bullet extends MoveableObject {
	PlayerShip owner;
	
	public Bullet(Point location, Point speed, int strength, MotionController motion, PlayerShip owner){
		super(location, speed, GameWorld.sprites.get("bullet"));
		this.strength=strength;
		if(owner instanceof PlayerShip){
			this.owner = (PlayerShip) owner;
			this.setImage(GameWorld.sprites.get("bullet"));
		}
                motion = new SimpleFiringMotion(owner.degree);
		motion.addObserver(this);
	}
        
         public void draw(Graphics g, ImageObserver obs) {
    	        if(show){
    		    g.drawImage(img, location.x, location.y, null);
    	}
    }
        
	public PlayerShip getOwner(){
		return owner;
	}
}
