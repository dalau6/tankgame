package tankgame.game;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.util.Observable;
import java.util.Observer;

import tankgame.GameWorld;
import tankgame.modifiers.AbstractGameModifier;
import tankgame.modifiers.motions.InputController;
import tankgame.modifiers.weapons.SimpleWeapon;

public class PlayerShip extends Ship implements Observer {
    int lives;
    int score;
    Point resetPoint;
    public int degree = 0;
    public int respawnCounter = 0;
    int lastFired=0;
    boolean isFiring=false;
    // movement flags
    public int left=0,right=0,up=0,down=0;
    String name;

    public PlayerShip(Point location, Point speed, Image img, int[] controls, String name) {
        super(location,speed,100,img);
        resetPoint = new Point(location);
        this.gunLocation = new Point(18,0);
        
        weapon = new SimpleWeapon();
        motion = new InputController(this, controls);
        lives = 2;
        health = 100;
        strength = 100;
        score = 0;
        respawnCounter=0;
        width = 64;
        height = 64;
        this.location = new Rectangle(location.x,location.y,width,height);
    }
      
    public void draw(Graphics g, ImageObserver observer) {
    	if(respawnCounter<=0){
            //gets tank image, location, and cuts tank image up
                g.drawImage(img, location.x, location.y, location.x+this.getSizeX(), location.y+this.getSizeY(), (degree/6)*this.getSizeX(), 0, 
                    ((degree/6)*this.getSizeX())+this.getSizeX(), this.getSizeY(), observer);
        } else if(respawnCounter==80){
    		GameWorld.getInstance().addClockObserver(this.motion);
    		respawnCounter -=1;
    	}
    	else if(respawnCounter<80){
            //respawns tank; gets tank image, location, and cuts tank image up
    		g.drawImage(img, location.x,location.y, location.x+this.getSizeX(), location.y+this.getSizeY(), (degree/6)*this.getSizeX(), 0,
        		((degree/6)*this.getSizeX())+this.getSizeX(), this.getSizeY(), observer);
    		respawnCounter -= 1;
    	}
    	else
    		respawnCounter -= 1;
    }
    
    public void damage(int damageDone){
    	if(respawnCounter<=0)
    		super.damage(damageDone);
    }

    //rotates player's object
    public void rotate(int angle){
    	this.degree += angle;
    	if(this.degree>=360){
    		this.degree=0;
    	}else if(this.degree<0){
    		this.degree=359;
    	}
    }
      
    public void update(int w, int h) {
    	if(isFiring){
    		int frame = GameWorld.getInstance().getFrameNumber();
    		if(frame>=lastFired+weapon.reload){
    			fire();
    			lastFired= frame;
    		}
    	}
    	
        if(right==1 || left==1){
    		this.rotate(3*(left-right));
    	}
        
    	if(down==1 || up==1){
        	int y = (int)(4*Math.cos(Math.toRadians(degree+90)));
        	int x = (int)(4*Math.sin(Math.toRadians(degree+90)));
    		location.x+=x*(up-down);
    		location.y+=y*(up-down);         
        }
        
        if(location.y<0){
            location.y=0;
        }
        
    	if(location.y>h-this.height){
            location.y=h-this.height;
        }
        
    	if(location.x<0){ 
            location.x=0;
        }
        
    	if(location.x>w-this.width){ 
            location.x=w-this.width;
        }
    }
    
    public void startFiring(){
    	isFiring=true;
    }
    
    public void stopFiring(){
    	isFiring=false;
    }
    
    public void fire()
    {
    	if(respawnCounter<=0){
    		weapon.fireWeapon(this);
    		GameWorld.getInstance().sound.play("Resources/snd_explosion1.wav");
    	}
    }
    
    public void die(){
    	this.show=false;
    	BigExplosion explosion = new BigExplosion(new Point(location.x,location.y));
    	GameWorld.getInstance().addBackground(explosion);
    	lives-=1;
    	if(lives>=0){
        	GameWorld.getInstance().removeClockObserver(this.motion);
    		reset();
    	}
    	else{
    		this.motion.delete(this);
    	}
    }
    
    public void reset(){
    	this.setLocation(resetPoint);
    	health=strength;
    	respawnCounter=160;
    	this.weapon = new SimpleWeapon();
    }
     
    public int getLives(){
    	return this.lives;
    }
    
    public int getScore(){
    	return this.score;
    }
    
    public String getName(){
    	return this.name;
    }
    
    public void incrementScore(int increment){
    	score += increment;
    }
    
    public boolean isDead(){
    	if(lives<0 && health<=0)
    		return true;
    	else
    		return false;
    }
    
    public void update(Observable o, Object arg) {
		AbstractGameModifier modifier = (AbstractGameModifier) o;
		modifier.read(this);
	}
}
