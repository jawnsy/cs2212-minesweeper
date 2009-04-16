import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class GameView
	extends View
{
	private static final long serialVersionUID = 1L;
	private Game m_game;
	private JMenu m_menu;
	private Container m_content;
	private JPanel m_field;
	private StatusBar m_status;

	public GameView(Game game) {
		super("CS2212 MineSweeper");
		m_game = game;

		m_content = getContentPane();
		m_content.setLayout(new BorderLayout());

		setMinimumSize(new Dimension(250, 250));

		buildMenuBar();
		buildGameArea();
		buildStatusBar();

		pack();
		setVisible(true);
	}
	
	public void buildMenuBar() {
		JMenuBar bar = new JMenuBar();
		
		m_menu = new JMenu("Game");
		bar.add(m_menu);

		m_content.add(bar);
		setJMenuBar(bar);
	}
	
	public void addMenuItem(String text, ActionListener trigger) {
		JMenuItem item = new JMenuItem(text);

		m_menu.add(item);
		item.addActionListener(trigger);
	}
	
	public void addMenuSeparator() {
		m_menu.addSeparator();
	}

	private void buildGameArea() {
		int width = m_game.width();
		int height = m_game.height();
		m_field = new JPanel(new GridLayout(height, width));

		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {
				m_field.add(m_game.getCellAt(row, col));
			}
		}
		m_content.add(m_field, BorderLayout.CENTER);
	}
	
	public void buildStatusBar() {
		// total cells to uncover = total cells [size()] - number of mines [mines()]
		JProgressBar progress = new JProgressBar(0, m_game.size() - m_game.mines());
		progress.setString("0%"); // game starts at 0% completed
		progress.setStringPainted(true); // makes the string show up
		m_status = new StatusBar(progress, new JLabel("Timer"));
		m_content.add(m_status, BorderLayout.SOUTH);
	}

	public void setCellsLeft(int remaining) {
		// If we don't have our StatusBar built yet, do nothing
		//if (m_status == null)
		//	return;

		JProgressBar progress = (JProgressBar) m_status.getLeft();
		/* The number of cells we have currently completed is equal to:
		 * - The total size of the board (number of cells); minus
		 * - The number of cells we have remaining on the board; minus
		 * - The number of cells that are actually mines
		 */
		int total = m_game.size() - m_game.mines(); // this is the total cells to uncover
		int complete = total - remaining; // cells we have already uncovered

		progress.setValue(complete);
		progress.setString(percent(complete, total));
	}

	// return a string representing percent completion
	private String percent(int complete, int total) {
		/* apparently Java has no builtin truncation option to round to X decimal
		 * places. what we can do to simulate this is find the exact percentage using
		 * complete/total, then multiply it by 100 and truncate it using (int).
		 * Then we can divide the 100 back out, which will give us a floating point
		 * result.
		 * 
		 * For example, if complete = 1, total = 3, then: complete/total = 0.666667 etc
		 * Multiply by 100: 66.66667
		 * Truncate: 67
		 * Divide by 100: 0.67
		 * But since this function wants a percentage, we can leave it as the truncated form.
		 * 
		 * So we've successfully "rounded" (in the colloquial mathematical sense) to two
		 * decimal places.
		 * 
		 * We can use float here because we don't need that much accuracy.
		 */
		return (int)(((float)complete / total) * 100) + "%";
	}

	// This method returns the time in a human-readable format.
	private String elapsed(int time) {
		// If more than a minute has elapsed, then it shows: 1m30s or similar
		if (time > 60)
			return ((int)(time / 60) + "m" + (time % 60) + "s");

		 // If less than 60 seconds has elapsed, then it shows time as: 30s
		return (time + "s");
	}

	public void setTimeDisplay(int time) {
		// If we don't have our StatusBar built yet, do nothing
		if (m_status == null)
			return;

		((JLabel) m_status.getRight()).setText(elapsed(time));
	}
	public Game game() {
		return m_game;
	}
}
