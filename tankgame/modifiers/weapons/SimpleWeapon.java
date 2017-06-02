package tankgame.modifiers.weapons;

import java.awt.Point;

import tankgame.game.Bullet;
import tankgame.game.PlayerShip;
import tankgame.game.Ship;
import tankgame.modifiers.motions.SimpleMotion;

public class SimpleWeapon extends AbstractWeapon {
	int strength;
	
	public SimpleWeapon(){
		this(5,10);
	}
	
	public SimpleWeapon(int reload){
		this(5,reload);
	}
	
	public SimpleWeapon(int strength, int reload){
		super();
		this.reload = reload;
		this.strength = strength;
	}
	
	@Override
	public void fireWeapon(Ship theShip) {
		super.fireWeapon(theShip);
		Point location = theShip.getLocationPoint();
		Point offset = theShip.getGunLocation();
		location.x+=offset.x;
		location.y+=offset.y;
		Point speed = new Point(0,-15*direction);
                strength = 10;
		
		Bullet bullet = new Bullet(location, speed, strength, new SimpleMotion(), (PlayerShip) theShip);
		bullets = new Bullet[1];
		bullets[0] = bullet;
				
		this.setChanged();
		
		this.notifyObservers();
	}

}
