import java.awt.Color;

public class Mine
	extends Cell
{
	private static final long serialVersionUID = 1L;

	public Mine(int x, int y) {
		super(x, y);
	}
	public void reveal() {
		setIcon(Images.MINE);
		setBackground(Color.WHITE);
		//setEnabled(false); // can't do this, or the mine will be greyed out
		m_visible = true;
	}
	public void trigger() {
		reveal();
		setBackground(Color.RED);
	}

	/* These are only meaningful for Cells */
	public int adjacent() {
		return 0;
	}
	public void adjacent(int adjacent) {
		return;
	}
}
