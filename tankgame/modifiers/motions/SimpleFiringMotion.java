package tankgame.modifiers.motions;

import tankgame.game.MoveableObject;

public class SimpleFiringMotion extends MotionController {
	int x;
        int y;
        
	public SimpleFiringMotion(int degree) {
        //rotates bullets to align with player's direction 
    	y = (int)(20*Math.cos(Math.toRadians(degree+90)));
    	x = (int)(20*Math.sin(Math.toRadians(degree+90)));
    }
	
	public void read(Object theObject){
		MoveableObject object = (MoveableObject) theObject;
		object.move(x, y);
	}
}
