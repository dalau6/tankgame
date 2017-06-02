package tankgame.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import tankgame.GameWorld;
import tankgame.game.*;


public class InfoBar extends InterfaceObject {
	PlayerShip player;
	String name;
	
	public InfoBar(PlayerShip player, String name){
		this.player = player;
		this.name = name;
	}
	
	public void draw(Graphics g2, int x, int y){
        g2.setFont(new Font("Calibri", Font.PLAIN, 24));
        if(player.getHealth()>40){
        	g2.setColor(Color.GREEN);
        }
        else if(player.getHealth()>20){
        	g2.setColor(Color.YELLOW);
        }
        else{
        	g2.setColor(Color.RED);
        }
        g2.fillRect(x+2, y-25, (int) Math.round(player.getHealth()*1.1), 20);
        
        for(int i=0;i<player.getLives();i++){
        	g2.drawImage(GameWorld.sprites.get("life" + name), x+110 +i*30, y-30, observer);
        }
        g2.setColor(Color.WHITE);
        g2.drawString(Integer.toString(player.getScore()), x+250, y-8);
	}

}
