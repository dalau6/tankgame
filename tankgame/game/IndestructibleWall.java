package tankgame.game;

import java.awt.Point;
import tankgame.GameWorld;

public class IndestructibleWall extends BackgroundObject {
	public IndestructibleWall(int x, int y){
		super(new Point(x*32, y*32), new Point(0,0), GameWorld.sprites.get("wall"));
	}
}
