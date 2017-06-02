package tankgame.game;

import java.awt.Image;
import java.awt.Point;

import tankgame.GameWorld;
import tankgame.modifiers.motions.MotionController;
import tankgame.modifiers.weapons.AbstractWeapon;

/* Ships are things that have weapons and health */
public class Ship extends MoveableObject {
	protected AbstractWeapon weapon;
	protected int health;
	protected Point gunLocation;
	
    public Ship(Point location, Point speed, int strength, Image img){
    	super(location,speed,img);
    	this.strength=strength;
    	this.health=strength;
    	this.gunLocation = new Point(15,20);
    }
    
    public Ship(int x, Point speed, int strength, Image img){
    	this(new Point(x,-90), speed, strength, img);
    }
    
    public void setWeapon(AbstractWeapon weapon){
    	this.weapon.remove();
    	this.weapon = weapon;
    }
    
    public AbstractWeapon getWeapon(){
    	return this.weapon;
    }
    
    public void damage(int damageDone){
    	this.health -= damageDone;
    	if(health<=0){
    		this.die();
    	}
    	return;
    }
    
    public void die(){
    	this.show=false;
    	weapon.deleteObserver(this);
    	motion.deleteObserver(this);
    	GameWorld.getInstance().removeClockObserver(motion);
    }
    
    public void collide(GameObject otherObject){
    }
    
    public void fire()
    {
    	weapon.fireWeapon(this);
    }
    
    /* some setters and getters!*/
    public void setHealth(int health){
    	this.health = health;
    }
    
    public int getHealth(){
    	return health;
    }
    
    public MotionController getMotion(){
    	return this.motion;
    }
    
    public void setMotion(MotionController motion){
    	this.motion = motion;
    }
    
    public Point getGunLocation(){
    	return this.gunLocation;
    }
}