import java.awt.Graphics;

public class BombBlock extends Block 
{
	private Block[][] blocks;
	public BombBlock(int x, int y, int width, Block[][] blocks)
	{
		super(x , y , width);
		this.blocks = blocks;
	}
	public boolean destroyedBy(Ball ball)
	{
		for(int row = 0; row < blocks.length; row++)
		{
			for(int col = 0; col < blocks[0].length; col++)
			{
				if(blocks[row][col] == this)
				{
					System.out.printf("row = %d\tcol = %d\n", row, col);
					if(row+1 <blocks.length)
					{
						blocks[row+1][col] = null;
					}
					if(row-1 >=0)
					{
						blocks[row-1][col] = null;
					}
					if(col+1 < blocks[0].length)
					{
						blocks[row][col+1] = null;
					}
					if(col-1 >= 0)
					{
						blocks[row][col-1] = null;
					}
				}
			}
		}
		return true;
	}
	@Override
	public void draw(Graphics g)
	{
		super.draw(g);
		g.drawString("B", x + width /2 , y + 10);
	}
}
