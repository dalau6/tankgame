package tankgame;

import java.awt.*;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import javax.swing.*;
import javax.swing.JFrame;

import tankgame.game.*;
import tankgame.modifiers.*;
import tankgame.modifiers.motions.*;
import tankgame.ui.*;

// extending JPanel to hopefully integrate this into an applet
// but I want to separate out the Applet and Application implementations
public final class GameWorld extends JPanel implements Runnable, Observer {

    private Thread thread;
    
    // GameWorld is a singleton class!
    private static final GameWorld game = new GameWorld();
    public static final GameSounds sound = new GameSounds();
    public static final GameClock clock = new GameClock();
    public Level level;
    
    private BufferedImage bimg, view, view2;
    int score = 0, life = 2;
    Point speed = new Point(0,0);
    Point size;
    Random generator = new Random();
    int sizeX, sizeY;
    
    /*Some ArrayLists to keep track of game things*/
    private ArrayList<BackgroundObject> background;
    private ArrayList<Bullet> bullets;
    private ArrayList<PlayerShip> players;
    private ArrayList<InterfaceObject> ui;
    
    
    public static HashMap<String,Image> sprites = new HashMap<String,Image>();
    public static HashMap<String, MotionController> motions = new HashMap<String, MotionController>();

    // is player still playing, did they win, and should we exit
    boolean gameOver, gameFinished;
    ImageObserver observer;
        
    // constructors makes sure the gameew HashMap<String,Image>() is focusable, then
    // initializes a bunch of ArrayLists
    private GameWorld(){
        this.setFocusable(true);
        background = new ArrayList<BackgroundObject>();
        bullets = new ArrayList<Bullet>();
        players = new ArrayList<PlayerShip>();
        ui = new ArrayList<InterfaceObject>();    
    }
    
    /* This returns a reference to the currently running game*/
    public static GameWorld getInstance(){
    	return game;
    }

    /*Game Initialization*/
    public void init() {
        setBackground(Color.white);
        loadSprites();
        
        level = new Level("Resources/level.txt");
        clock.addObserver(level);
        level.addObserver(this);
        size = new Point(level.w*32,level.h*32);
        gameOver = false;
        observer = this;

        addBackground(new Background(size.x,size.y,speed, sprites.get("water")));
        level.load();
    }
    
    /*Functions for loading image resources*/
    private void loadSprites(){    	
	    sprites.put("water", getSprite("Resources/Background.png"));
	    
	    sprites.put("bullet", getSprite("Resources/bullet.png"));
	    
	    sprites.put("player1", getSprite("Resources/Tank_blue_basic_strip60.png"));
	    sprites.put("player2", getSprite("Resources/Tank_red_basic_strip60.png"));
	    
	    sprites.put("explosion1_1", getSprite("Resources/explosion1_1.png"));
		sprites.put("explosion1_2", getSprite("Resources/explosion1_2.png"));
		sprites.put("explosion1_3", getSprite("Resources/explosion1_3.png"));
		sprites.put("explosion1_4", getSprite("Resources/explosion1_4.png"));
		sprites.put("explosion1_5", getSprite("Resources/explosion1_5.png"));
		sprites.put("explosion1_6", getSprite("Resources/explosion1_6.png"));
	    sprites.put("explosion2_1", getSprite("Resources/explosion2_1.png"));
		sprites.put("explosion2_2", getSprite("Resources/explosion2_2.png"));
		sprites.put("explosion2_3", getSprite("Resources/explosion2_3.png"));
		sprites.put("explosion2_4", getSprite("Resources/explosion2_4.png"));
		sprites.put("explosion2_5", getSprite("Resources/explosion2_5.png"));
		sprites.put("explosion2_6", getSprite("Resources/explosion2_6.png"));
		sprites.put("explosion2_7", getSprite("Resources/explosion2_7.png"));
		
		sprites.put("life1", getSprite("Resources/life1.png"));
		sprites.put("life2", getSprite("Resources/life2.png"));
                
                sprites.put("wall", getSprite("Resources/Blue_wall1.png"));
	        sprites.put("wall2", getSprite("Resources/Blue_wall2.png"));
    }
    
    public Image getSprite(String name) {
        URL url = GameWorld.class.getResource(name);
        Image img = java.awt.Toolkit.getDefaultToolkit().getImage(url);
        try {
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(img, 0);
            tracker.waitForID(0);
        } catch (Exception e) {
        }
        return img;
    }
    
    
    /********************************
     * 	These functions GET things	*
     * 		from the game world		*
     ********************************/
    
    public int getFrameNumber(){
    	return clock.getFrame();
    }
    
    public int getTime(){
    	return clock.getTime();
    }
    
    public void removeClockObserver(Observer theObject){
    	clock.deleteObserver(theObject);
    }
    
    public ListIterator<BackgroundObject> getBackgroundObjects(){
    	return background.listIterator();
    }
    
    public ListIterator<PlayerShip> getPlayers(){
    	return players.listIterator();
    }
    
    public ListIterator<Bullet> getBullets(){
    	return bullets.listIterator();
    }
        
    public void setDimensions(int w, int h){
    	this.sizeX = w;
    	this.sizeY = h;
    }
    
    /********************************
     * 	These functions ADD things	*
     * 		to the game world		*
     ********************************/
    
    public void addBullet(Bullet...newObjects){
    	for(Bullet bullet : newObjects){
    			bullets.add(bullet);
    	}
    }
    
    public void addPlayer(PlayerShip...newObjects){
    	for(PlayerShip player : newObjects){
    		players.add(player);
    		ui.add(new InfoBar(player,Integer.toString(players.size())));
    	}
    }
    
    // add background items (islands)
    public void addBackground(BackgroundObject...newObjects){
    	for(BackgroundObject object : newObjects){
    		background.add(object);
    	}
    }
    
    public void addClockObserver(Observer theObject){
    	clock.addObserver(theObject);
    }
    
    // this is the main function where game stuff happens!
    // each frame is also drawn here
    public void drawFrame(int w, int h, Graphics2D g2) {
        ListIterator<?> iterator = getBackgroundObjects();
        PlayerShip player1 = players.get(0);
        PlayerShip player2 = players.get(1);
        // iterate through all blocks
        while(iterator.hasNext()){
            BackgroundObject obj = (BackgroundObject) iterator.next();
            obj.update(w, h);
            obj.draw(g2, this);
            
            if(obj instanceof BigExplosion){
            	if(!obj.show) iterator.remove();
            	continue;
            }
            
            // check player-block collisions
            ListIterator<PlayerShip> players = getPlayers();
            while(players.hasNext() && obj.show){
            	PlayerShip player = (PlayerShip) players.next();
            	if(obj.collision(player)){
            		Rectangle location = obj.getLocation();
            		Rectangle playerLocation = player.getLocation();
                        if(playerLocation.x<location.x)
            			player.move(-2,0);
            		if(playerLocation.x>location.x)
            			player.move(2,0);
            		if(playerLocation.y<location.y)
            			player.move(0,-2);
            		if(playerLocation.y>location.y)
            			player.move(0,2);
            	}
            }
        }
        
        //bullets range
    	if (!gameFinished) {
            ListIterator<Bullet> bullets = this.getBullets();
            while(bullets.hasNext()){
        	Bullet bullet = bullets.next();
	        if(bullet.getY()>h || bullet.getX()<-300 || bullet.getX()>w+300){
	            bullets.remove();
	        }else {
                    //bullets collision 
	            iterator = this.getBackgroundObjects();
	            while(iterator.hasNext()){
	            	GameObject other = (GameObject) iterator.next();
	            	if(other.show && other.collision(bullet)){
            		    bullets.remove();
            		    break;
	            	}
	            }
	        }
	        bullet.draw(g2, this);
            }
            
            // update players and draw
            iterator = getPlayers();
            while(iterator.hasNext()){
            	PlayerShip player = (PlayerShip) iterator.next();
            	
            	if(player.isDead()){
        	    gameOver=true;
            	}
            	
            	bullets = this.getBullets();
            	while(bullets.hasNext()){
            	    Bullet bullet = bullets.next();
	            if(bullet.collision(player) && player.respawnCounter<=0 && bullet.getOwner() != player) {
	        	player.damage(bullet.getStrength());
	        	bullet.getOwner().incrementScore(bullet.getStrength());
	        	bullets.remove();
	            }
            	}
            }
                     
        player1.update(w, h);
        if(player1.collision(player2)){
            Rectangle location = player1.getLocation();
            Rectangle location2 = player2.getLocation();
        	if(location.y<location2.y)
        	    player1.move(0,-2);
        	if(location.y>location2.y)
        	    player1.move(0,2);
        	if(location.x<location2.x)
        	    player1.move(-2,0);
        	if(location.x>location2.x)
        	    player1.move(2,0);
        }
        
        player2.update(w, h);
        if(player2.collision(player1)){
            Rectangle location = player2.getLocation();
            Rectangle location2 = player1.getLocation();
        	if(location.y<location2.y)
        	    player2.move(0,-2);
        	if(location.y>location2.y)
        	    player2.move(0,2);
        	if(location.x<location2.x)
        	    player2.move(-2,0);
        	if(location.x>location2.x)
        	    player2.move(2,0);
        }
        
        player1.draw(g2, this);
        player2.draw(g2, this);
        }
        
        // end game stuff
        else {
    	    g2.setColor(Color.WHITE);
    	    g2.setFont(new Font("Calibri", Font.PLAIN, 24));
            if(!gameOver){
            }
        }
    }

    public Graphics2D createGraphics2D(int w, int h) {
        Graphics2D g2 = null;
        if (bimg == null || bimg.getWidth() != w || bimg.getHeight() != h) {
            bimg = (BufferedImage) createImage(w, h);
        }
        g2 = bimg.createGraphics();
        g2.setBackground(getBackground());
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_QUALITY);
        g2.clearRect(0, 0, w, h);
        return g2;
    }

    /* paint each frame */
    public void paint(Graphics g) {
        if(players.size()!=0)
        	clock.tick();
    	Dimension windowSize = getSize();
        Graphics2D g2 = createGraphics2D(size.x, size.y);
        drawFrame(size.x, size.y, g2);
        g2.dispose();
        
        //player1's view
        int player1x;
        int player1y;
        if(this.players.get(0).getX() - windowSize.width/4 > 0)
        {
            player1x = this.players.get(0).getX() - windowSize.width/4;
        }else{
            player1x = 0;
        }
        
        if(this.players.get(0).getY() - windowSize.height/2 > 0)
        {
            player1y=this.players.get(0).getY() - windowSize.height/2;
        }else{
            player1y=0;   
        }
        
        if(player1x > size.x-windowSize.width/2){
        	player1x = size.x-windowSize.width/2;
        }
        if(player1y > size.y-windowSize.height){
        	player1y = size.y-windowSize.height;
        }
        
        //player2's view
        int player2x;
        int player2y;
        if(this.players.get(1).getX() - windowSize.width/4 > 0)
        {
            player2x=this.players.get(1).getX() - windowSize.width/4;
        }else{
            player2x=0;
        }
        
        if(this.players.get(1).getY() - windowSize.height/2 > 0)
        {
            player2y=this.players.get(1).getY() - windowSize.height/2;
        }else{
            player2y=0;   
        }

        if(player2x > size.x-windowSize.width/2){
        	player2x = size.x-windowSize.width/2;
        }
        if(player2y > size.y-windowSize.height){
        	player2y = size.y-windowSize.height;
        }
        
        //creates minimap at bottom center of the map
        view = bimg.getSubimage(player1x, player1y, windowSize.width/2, windowSize.height);
        view2 = bimg.getSubimage(player2x, player2y, windowSize.width/2, windowSize.height);
        g.drawImage(view, 0, 0, this);
        g.drawImage(view2, windowSize.width/2, 0, this);
        g.drawRect(windowSize.width/2, 0, 0, windowSize.height);
        g.drawImage(bimg, windowSize.width/2-75, 400, 150, 150, observer);
        
    // interface stuff
    ListIterator<InterfaceObject> objects = ui.listIterator();
    int offset = 0;
    while(objects.hasNext()){
        InterfaceObject object = objects.next();
        object.draw(g, offset, windowSize.height);
        offset += 500;
        }
    }

    /* start the game thread*/
    public void start() {
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    /* run the game */
    public void run() {
    	
        Thread me = Thread.currentThread();
        while (thread == me) {
        	this.requestFocusInWindow();
            repaint();
          
          try {
                thread.sleep(23); // pause a little to slow things down
            } catch (InterruptedException e) {
                break;
            }
            
        }
    }
    
    /* End the game, and signal either a win or loss */
    public void endGame(boolean win){
    	this.gameOver = true;
    }
    
    public boolean isGameOver(){
    	return gameOver;
    }
    
    // signal that we can stop entering the game loop
    public void finishGame(){
    	gameFinished = true;
    }
    

    /*I use the 'read' function to have observables act on their observers.
     */
	@Override
	public void update(Observable o, Object arg) {
		AbstractGameModifier modifier = (AbstractGameModifier) o;
		modifier.read(this);
	}
	
	public static void main(String argv[]) {
	    final GameWorld game = GameWorld.getInstance();
	    JFrame f = new JFrame("Tank Game");
	    f.addWindowListener(new WindowAdapter() {
		    public void windowGainedFocus(WindowEvent e) {
		        game.requestFocusInWindow();
		    }
	    });
	    f.getContentPane().add("Center", game);
	    f.pack();
	    f.setSize(new Dimension(800, 600));
	    game.setDimensions(800, 600);
	    game.init();
	    f.setVisible(true);
	    f.setResizable(false);
	    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    game.start();
	}
}