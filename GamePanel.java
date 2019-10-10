import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class GamePanel extends JPanel implements Runnable, MouseListener
{
	//game width and height
	private int width;
	private int height;
	//the game thread
	private Thread thread;
	//2D array of all of the blocks on the game field
	private Block[][] blocks;
	private Ball ball;
	private Paddle paddle;
	private Lives lives;
	private Bullet bullet;
	private Score score;
	private Graphics g;
	//ArrayList of game objects
	private ArrayList<GameObject> pieces;
	/*
	 * The Action objects are instantiations of anonymous classes 
	 * that extend the abstract class AbstractAction. If that doesn't make sense
	 * it's ok, just follow the syntax to make more. Note the semicolon
	 * at the end of the class declaration.
	 */
	private Action left = new AbstractAction("left") 
	{
		@Override
		public void actionPerformed(ActionEvent ae) 
		{
			paddle.setDir(Dir.LEFT);
		}
	};//<--- semicolon goes here!
	private Action right = new AbstractAction("right") 
	{
		@Override
		public void actionPerformed(ActionEvent ae) 
		{
			paddle.setDir(Dir.RIGHT);
		}
	};
	private Action stop = new AbstractAction("stop") 
	{
		@Override
		public void actionPerformed(ActionEvent ae) 
		{
			paddle.setDir(Dir.NONE);
		}
	};
	private Action fire = new AbstractAction("fire") 
	{
		@Override
		public void actionPerformed(ActionEvent ae) 
		{
			//random angle between 45 & 135 degrees
			ball.launch(Math.random() * Math.PI / 2 + Math.PI / 4);
		}
	};
	/**
	 * Initializes instance variables.
	 * @param width JPanel width in pixels.
	 * @param height JPanel height in pixels.
	 */
	public GamePanel(int width, int height) 
	{
		this.width = width;
		this.height = height;
		blocks = new Block[10][8];
		lives = new Lives(width, height);
		ball = new Ball(width, height, lives);
		paddle = new Paddle(width, height);
		score = new Score(width, height);
		pieces = new ArrayList<GameObject>();
		pieces.add(ball);
		pieces.add(paddle);
		pieces.add(lives);
		pieces.add(score);
		thread = new Thread(this);
		thread.start();
		setBackground(Color.black);
		addMouseListener(this);
		gameInit();
	}
	private void gameInit()
	{
		int blockWidth = width / blocks[0].length;
		int x = 0;
		int y = 0;
		int count = 0;
		int gap = 2 * Block.HEIGHT;
		for(int row = 0; row < blocks.length-5; row++)
		{
			for(int col = 0; col < blocks[row].length; col++)
			{
				int random = (int)(Math.random()*10);
				if(random == 0 || random == 1)
				{
					blocks[row][col] = new FallingBlock(x % width, y +gap, blockWidth);
				}
				else if(random == 2)
				{
					blocks[row][col] = new BombBlock(x % width, y +gap, blockWidth, blocks);
				}
				else if(random == 3)
				{
					blocks[row][col] = new BombBlock2(x % width, y +gap, 
							blockWidth, blocks, count, lives);
				}
				else
				{
					blocks[row][col] = new Block(x % width, y + gap, blockWidth);
				}
				x += blockWidth;
				y = x / width * Block.HEIGHT;
			}
		}
		blocks[9][0] = new Bonus(0, height/2, blockWidth/2, width); 
		Bonus bonus = new Bonus(0, height/2, blockWidth/2, width);
		bonus.move();
		
		//left arrow key press
        registerKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "left", left);
        //left arrow key release
        registerKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "stop", stop);
        registerKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "right", right);
        registerKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "stop", stop);
        registerKeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "fire", fire);
	}
	/**
	 * Helper method to map a KeyStroke to an action.
	 * @param keyStroke A KeyStroke object that represents a specific KeyEvent (key press)
	 * @param name The name is a String that is just used as a convenient object to tie a KeyStroke to an Action
	 * @param action An Action as defined in the instance variables section.
	 */
	private void registerKeyBinding(KeyStroke keyStroke, String name, Action action) 
	{
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        im.put(keyStroke, name);
        am.put(name, action);
    }
	/**
     * Iterates over all of the blocks and checks if the ball any of the blocks intersect.
     * If they do intersect the ball bounces off of the block (bounce() is defined in the Ball class).
     * It then calls hitBy() on the block (defined in the Block class) which
     * decreases the hardness by an amount depending on the color of the ball
     * and returns true if the block has been destroyed.
     * If the hitBy() returns true then the block is removed from the ArrayList of Blocks.
     */
    private void checkCollision() 
    {
    	for(int row = 0; row < blocks.length; row++) 
    	{
    		for(int col = 0; col < blocks[row].length; col++)
    		{
    			if(blocks[row][col] != null && blocks[row][col].intersects(ball))
    			{
    				ball.bounce(blocks[row][col]);
    				if(blocks[row][col].destroyedBy(ball)) 
    				{
    					blocks[row][col] = null;
    					score.score();
    				}
    			}
    			if(blocks[row][col] != null && blocks[row][col].intersects(ball) 
    					&& blocks[row][col] instanceof Bonus)
    			{
    				ball.bounce(blocks[row][col]);
    				if(blocks[row][col].destroyedBy(ball)) 
    				{
    					blocks[row][col] = null;
    					score.bonusScore();
    				}
    			}
    			if(blocks[row][col] != null && bullet != null 
    					&& blocks[row][col].intersects(bullet)) 
    			{
    				blocks[row][col] = null;
    				bullet = null;
    				score.score();
    			}
    			if(blocks[row][col] != null && bullet != null 
    					&& blocks[row][col].intersects(bullet) && 
    					blocks[row][col] instanceof Bonus)
    			{
    				blocks[row][col] = null;
    				bullet = null;
    				score.bonusScore();
    			}
    			if(blocks[row][col] instanceof FallingBlock && blocks[row][col].getY() > height)
				{
					blocks[row][col] = null;
				}
    			if(blocks[row][col] instanceof Bonus && blocks[row][col].getX() > width)
    			{
    				blocks[row][col] = null;
    			}
    		}
    	}
    	if(paddle.intersects(ball))
    	{
    		ball.bounce(paddle);
    	}
    }
    @Override
    public void paintComponent(Graphics g) 
    {
        super.paintComponent(g);
        boolean win = true;
        //iterate over the blocks and draw them
        for(Block[] blockRow : blocks) 
        {
        	for(Block block : blockRow)
        	{
        		if(block != null)
        		{
        			block.draw(g);
        			win = false;
        		}
        		if(block instanceof Movable)
        		{
        			((Movable) block).move();
        		}
        	}
        }
        //iterate over the game objects, draw and move them
        for(GameObject piece : pieces)
        {
        	if(piece != null) 
        	{
        		piece.draw(g);
        		if(piece instanceof Movable)
        		{
        			((Movable) piece).move();
        		}
        	}
        }
        checkCollision();
        //if there are no more lives left display a game over message
        //and stop the thread from running.
        if(lives.getLives() == 0)
        {
        	g.setColor(Color.red);
    		g.setFont(new Font(g.getFont().getName(), Font.BOLD, 50));
    		g.drawString("GAME OVER", width / 2 - 150, height / 2 - 10);
    		thread.interrupt();
        }
        if(win)
        {
        	g.setColor(Color.white);
        	g.setFont(new Font(g.getFont().getName(), Font.BOLD, 50));
        	g.drawString("YOU WIN!!", width/2 -150, height/2 -10);
        	thread.interrupt();
        }
        //smooths drawing on linux
        Toolkit.getDefaultToolkit().sync();
    }
    @Override
    public void run()
    {
    	while(true)
    	{
    		try 
    		{
    			Thread.sleep(20);
    			followBlock();
    		} catch (InterruptedException e) 
    		{ 
    			//System.out.println("Thread stopped");
    			//e.printStackTrace();
    			return;
    		}
    		repaint();
    	}
    }
    public void followBlock()
    {
    	if(ball.getX() > paddle.getX() + (paddle.width / 2) + 25)
    	{
    		paddle.setDir(Dir.RIGHT);
    	}
    	else if(ball.getX() < paddle.getX() + (paddle.width / 2) - 25)
    	{
    		paddle.setDir(Dir.LEFT);
    	}
    	else
    	{
    		paddle.setDir(Dir.NONE);
    	}
    }
	@Override
	public void mouseClicked(MouseEvent e) 
	{
		// TODO Auto-generated method stub
		if(bullet == null)
		{
			Bullet bullet = new Bullet(width, height, paddle);
			pieces.add(bullet);
		}
	}
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}
}