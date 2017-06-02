package tankgame.modifiers.motions;

import java.util.Observable;
import java.util.Observer;

import tankgame.GameWorld;
import tankgame.game.*;
import tankgame.modifiers.AbstractGameModifier;

/*MotionControllers move around objects!*/
public abstract class MotionController extends AbstractGameModifier implements Observer {
	int fireInterval;
	
	public MotionController(){
		GameWorld.getInstance().addClockObserver(this);
		fireInterval = -1;
	}
	
	public void delete(Observer theObject){
		GameWorld.getInstance().removeClockObserver(this);
		this.deleteObserver(theObject);
	}
	
	/*Motion Controllers observe the GameClock and fire on every clock tick
	 * The default controller doesn't do anything though*/
	public void update(Observable o, Object arg){
		this.setChanged();
		this.notifyObservers();
	}
	
	public void read(Object theObject){
		Ship object = (Ship) theObject;
		object.move();
		
		if(GameWorld.getInstance().getFrameNumber()%fireInterval==0){
			object.fire();
		}
	}
}
