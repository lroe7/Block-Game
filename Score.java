import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class Score extends GameObject
{
	private int score;
	public Score(int gameWidth, int gameHeight)
	{
		super(gameWidth, gameHeight);
		color = Color.white;
		x = 50; 
		y = 50;
		score = 0;
	}
	public void score()
	{
		score += 100;
	}
	public void bonusScore()
	{
		score +=1000;
	}
	public int getScore()
	{
		return score;
	}
	@Override
	public void draw(Graphics g)
	{
		g.setColor(color);
		g.setFont(new Font(g.getFont().getName(), Font.BOLD, 24));
		g.drawString(score + "", x, y);
	}
}
