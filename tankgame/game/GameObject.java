/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tankgame.game;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.ImageObserver;
import java.util.Observable;
import java.util.Observer;

import tankgame.modifiers.AbstractGameModifier;

abstract public class GameObject implements Observer {
	protected Point speed;
	protected Rectangle location;
	Image img;
	int height,width;
	ImageObserver observer;
	public boolean show;
	
	public GameObject(){}
	
	public GameObject(Point location, Point speed, Image img){
		this.speed = speed;
		this.img=img;
		this.show=true;
        height = img.getWidth(observer);
        width = img.getHeight(observer);
        this.location = new Rectangle(location.x,location.y,width,height);
	}
	
	public GameObject(Point location, Image img){
		this(location, new Point(0,0), img);
	}
	
    public void draw(Graphics g, ImageObserver obs) {
    	if(show)
    		g.drawImage(img, location.x, location.y, obs);
    }
    
    public void setImage(Image img){
    	this.img = img;
        this.height = img.getWidth(observer);
        this.width = img.getHeight(observer);
    }
    
    public void update(int w, int h) {
    	location.x += speed.x;
        location.y += speed.y;
        
        if(location.y<-100 || location.y==h+100){
        	this.show=false;
        }
        
    }
    
	@Override
	public void update(Observable o, Object arg) {
		AbstractGameModifier modifier = (AbstractGameModifier) o;
		modifier.read(this);
	}
    
    public int getX(){
    	return location.x;
    }
    
    public int getY(){
    	return location.y;
    }
    
    public int getSizeX(){
    	return width;
    }
    
    public int getSizeY(){
    	return height;
    }
    
    public Point getSpeed(){
    	return speed;
    }
    
    public void setLocation(Point newLocation){
    	location.setLocation(newLocation);
    }
    
    public Rectangle getLocation(){
    	return new Rectangle(this.location);
    }
    
    public Point getLocationPoint(){
    	return new Point(location.x, location.y);
    }
    
    public void move(int dx, int dy){
    	location.translate(dx, dy);
    }
    
    public void move(){
    	location.translate(speed.x,speed.y);
    }
    public boolean collision(GameObject otherObject) {
        if(location.intersects(otherObject.getLocation())){
        	return true;
        }
        
        return false;
    }
    
    public void hide(){
    	this.show = false;
    }
    
    public void show(){
    	this.show = true;
    }
}