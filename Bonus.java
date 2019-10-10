import java.awt.Color;
import java.awt.Graphics;
public class Bonus extends Block implements Movable, BonusScore{
	private Color color;
	private int time;
	private int score;
	private Score sc;
	public Bonus(int x, int y, int width, int gameWidth)
	{
		super(x, y, width);
		this.x = x;
		this.y = y;
		this.width = width;
		color = Color.red;
		speed = 5;
		time = (int)(Math.random() *500);
		sc = new Score(gameWidth, gameHeight);
		score = sc.getScore();
	}
	@Override
	public void move()
	{
		if(time > 0)
		{
			time--;
		}
		else
		{
			x += speed;
		}
	}
	@Override
	public boolean destroyedBy(Ball ball)
	{
		return true;
	}
	@Override
	public void draw(Graphics g)
	{
		g.setColor(color);
        g.fillRect(x, y, width, height);
	}
	@Override
	public void bonusScore()
	{
		score += 1000;
	}
}
