import java.awt.Color;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.border.LineBorder;

public class Cell
	extends JButton
{
	private static final long serialVersionUID = 1L;

	private int m_x, m_y;
	protected boolean m_visible;
	private int m_adjacent;

	public Cell(int x, int y)
	{
		// In its normal state, the tile should have a blank image
		setIcon(Images.BLANK);

		// Remove margins so squares can be put together
		setMargin(new Insets(0, 0, 0, 0));

		// There is no text, so we don't need a gap
		setIconTextGap(0);

		// Add a thin border to differentiate tiles
		setBorder(new LineBorder(Color.BLACK, 1));

		// The play area should show up Light Gray by default
		setBackground(Color.LIGHT_GRAY);

		// Track Cell state
		m_x = x;
		m_y = y;
		m_visible = false;
	}

	// Store positional information about this Cell
	public int x() {
		return m_x;
	}
	public void x(int x) {
		m_x = x;
	}

	public int y() {
		return m_y;
	}
	public void y(int y) {
		m_y = y;
	}

	// Has this Cell's contents been reveal()ed?
	public boolean visible() {
		return m_visible;
	}

	// Reveal this cell value - which is the number of adjacent mines
	public void reveal() {
		switch (m_adjacent) {
			case 0: setIcon(Images.BLANK); break;
			case 1: setIcon(Images.ONE);   break;
			case 2: setIcon(Images.TWO);   break;
			case 3: setIcon(Images.THREE); break;
			case 4: setIcon(Images.FOUR);  break;
			case 5: setIcon(Images.FIVE);  break;
			case 6: setIcon(Images.SIX);   break;
			case 7: setIcon(Images.SEVEN); break;
			case 8: setIcon(Images.EIGHT); break;
		}
		setBackground(Color.WHITE);
		//setEnabled(false); // can't do this or the numbers will be grayed out
		m_visible = true;
	}

	// This allows us to set how many mines are adjacent
	public int adjacent() {
		return m_adjacent;
	}
	public void adjacent(int adjacent)
	{
		m_adjacent = adjacent;
	}
}
