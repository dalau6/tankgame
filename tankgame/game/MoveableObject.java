package tankgame.game;

import java.awt.Image;
import java.awt.Point;

import tankgame.modifiers.motions.MotionController;

/*MoveableObjects have movement behaviors*/
public class MoveableObject extends GameObject {
	protected int strength;
	protected MotionController motion;
	
	public MoveableObject(Point location, Point speed, Image img){
		super(location, speed, img);
		this.strength=0;
	}
	
    public int getStrength()
    {
    	return strength;
    }
    
    public void setStrength(int strength){
    	this.strength = strength;
    }
    
    public void update(int w, int h){
    	motion.read(this);
    }
    
    public void start(){
    	motion.addObserver(this);
    }
}
