import java.awt.Graphics;

public class BombBlock2 extends BombBlock 
{
	private int count;
	private Lives lives;
	public BombBlock2(int x, int y, int width, Block[][] blocks, int count, Lives lives)
	{
		super(x, y, width, blocks);
		this.count = count;
		this.lives = lives;
	}
	public boolean destroyedBy(Ball ball)
	{
		count++;
		if(count >1)
		{
			lives.increaseLives();
			super.destroyedBy(ball);
			return true;
		}
		else
		{
			return false;
		}
		
	}
	@Override
	public void draw(Graphics g)
	{
		super.draw(g);
		g.drawString("B2", x + width /2 , y + 10);
	}
}

