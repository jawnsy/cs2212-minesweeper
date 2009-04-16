import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.EventObject;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.swing.JOptionPane;

public class Game {
	/* These could also be written as:
	 * private View p_game, p_options, p_about, p_scores;
	 */
	private GameView p_game;
	private OptionView p_options;
	private AboutView p_about;
	private ScoreView p_scores;

	private Vector<Score> m_scores;	// This vector keeps track of all scores during gameplay
	private int m_remaining; // number of tiles left to uncover
	private int m_time; // elapsed game time
	private Cell m_tiles[][]; // arrays have O(1) read time (fast reads, slow inserts)
	private Options m_options;
	private Timer m_timer; // updates the elapsed seconds counter

	public Game(Options opts) {
		m_options = opts;
		m_scores = new Vector<Score>();
		startGame();
	}

	private void placeMines() {
		/* We have m_width * m_height tiles and m_mines mines; the difference is the number
		 * of safe cells we need to find.
		 */
		m_remaining = width() * height() - mines();

		/* This algorithm slightly above O(n^2) because the mines are inserted first,
		 * then the empty regions are filled with Cells. This is likely not going to be
		 * an issue, performance-wise.
		 */
		int mines = mines(); // number of mines to place
		while (mines > 0) {
			int row = rand(0, height());
			int col = rand(0, width());

			// There is already something here, so check another tile
			if (m_tiles[row][col] != null)
				continue;

			// Place a mine
			Mine tile = new Mine(col, row);
			tile.addActionListener(new MineTrigger());
			m_tiles[row][col] = tile;
			mines--;
		}

		// Fill empty areas with Cells and calculate adjacent mines
		for (int row = 0; row < height(); row++) {
			for (int col = 0; col < width(); col++) {
				// If we have something here, skip to the next tile
				if (m_tiles[row][col] != null)
					continue;

				Cell tile = new Cell(col, row);
				tile.addActionListener(new CellTrigger());

				// store the tile before calling adjacentTo, so that it won't encounter null tiles
				m_tiles[row][col] = tile;

				tile.adjacent(adjacentTo(row, col));
			}
		}
	}

	// Determine how many mines are adjacent to this one (in the 8 surrounding squares)
	private int adjacentTo(int row, int col)
	{
		int adjacent = 0;

		// Check in a counterclockwise fashion.

		// NorthWest, North, NorthEast (tiles above this one)
		if (!(row == 0)) { // make sure we're not the top row
			// NorthWest
			if (!(col == 0)) // leftmost column
				if (m_tiles[row-1][col-1] instanceof Mine)
					adjacent++;

			// North
			if (m_tiles[row-1][col] instanceof Mine)
				adjacent++;

			// NorthEast
			if (!(col == width()-1)) // rightmost column
				if (m_tiles[row-1][col+1] instanceof Mine)
					adjacent++;
		}

		// SouthWest, South, SouthEast (tiles below this one)
		if (!(row == height()-1)) { // make sure we're not the bottom row
			// SouthWest
			if (!(col == 0)) // leftmost column
				if (m_tiles[row+1][col-1] instanceof Mine)
					adjacent++;

			// South
			if (m_tiles[row+1][col] instanceof Mine)
				adjacent++;

			// SouthEast
			if (!(col == width()-1)) // rightmost column
				if (m_tiles[row+1][col+1] instanceof Mine)
					adjacent++;
		}

		// Check the tiles to the sides (East and West)
		// East
		if (!(col == width()-1)) // rightmost column
			if (m_tiles[row][col+1] instanceof Mine)
				adjacent++;

		// West
		if (!(col == 0)) // leftmost column
			if (m_tiles[row][col-1] instanceof Mine)
				adjacent++;

		return adjacent;
	}

	// This method will recursively expand a selected Cell
	private void expand(Cell cell) {
		int col = cell.x();
		int row = cell.y();

		// If we have already revealed this cell, stop
		if (cell.visible())
			return;

		// Reveal the current cell we're visiting
		cell.reveal();

		// Decrement the number of cells remaining. This might trigger the victory condition
		m_remaining--; // track the number of tiles still covered
		p_game.setCellsLeft(m_remaining);
			
		// If we have a nonzero adjacency count, stop
		if (!(cell.adjacent() == 0))
			return;

		/* Check in a counterclockwise fashion. This is essentially the
		 * same code as adjacentTo, we have to check all 8 adjacent tiles.
		 */

		// NorthWest, North, NorthEast (tiles above this one)
		if (!(row == 0)) { // make sure we're not the top row
			// NorthWest
			if (!(col == 0)) // leftmost column
				expand(m_tiles[row-1][col-1]);

			// North
			expand(m_tiles[row-1][col]);

			// NorthEast
			if (!(col == width()-1)) // rightmost column
				expand(m_tiles[row-1][col+1]);
		}

		// SouthWest, South, SouthEast (tiles below this one)
		if (!(row == height()-1)) { // make sure we're not the bottom row
			// SouthWest
			if (!(col == 0)) // leftmost column
				expand(m_tiles[row+1][col-1]);

			// South
			expand(m_tiles[row+1][col]);

			// SouthEast
			if (!(col == width()-1)) // rightmost column
				expand(m_tiles[row+1][col+1]);
		}

		// Check the tiles to the sides (East and West)
		// East
		if (!(col == width()-1)) // rightmost column
			expand(m_tiles[row][col+1]);

		// West
		if (!(col == 0)) // leftmost column
			expand(m_tiles[row][col-1]);
	}
	
	// When the game is over, show the entire board and disable everything
	public void stopGame() {
		for (int row = 0; row < height(); row++) {
			for (int col = 0; col < width(); col++) {
				// Make sure there's something here first
				if (m_tiles[row][col] == null)
					continue;

				// Disable all the buttons after the game is over
				m_tiles[row][col].setEnabled(false);

				// Only reveal Mines that have not been revealed yet
				if (!(m_tiles[row][col] instanceof Mine))
					continue;

				m_tiles[row][col].reveal();
			}
		}
	}

	public int size() {
		return (width() * height());
	}
	public int width() {
		return m_options.width();
	}
	public int height() {
		return m_options.height();
	}
	public int mines() {
		return m_options.mines();
	}

	/* This convenience function returns an integer such that: min <= x < max
	 * As a result of excluding the max value, this is suitable for generating
	 * array indices given an array length (which is max index + 1)
	 */
	private int rand(int min, int max)
	{
		return (int) (max * Math.random() + min);
	}

	public Cell getCellAt(int row, int col)
	{
		return m_tiles[row][col];
	}
	
	// cleans up after a previous game and starts a new one
	private void newGame(boolean victory) {
		int choice = JOptionPane.NO_OPTION; // by default, assume the answer is NO

		if (victory) {
			choice = JOptionPane.showConfirmDialog(
				p_game,
				"Congratulations " + m_options.name() + ", " + "you won in " +
				m_time + " seconds.\n" +
				"Would you like to play again?", // box text
				"Victory!", // title
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE
			);
		}
		else {
			choice = JOptionPane.showConfirmDialog(
				p_game,
				"Sorry, " + m_options.name() + ", " + "you stepped on a mine!\n" +
				"Would you like to play again?", // box text
				"Defeat!", // title
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE
			);
		}

		// If the user picked yes, create a new game
		if (choice == JOptionPane.YES_OPTION) {
			p_game.dispose();
			p_game = null; // even though the window is disposed, we still hold a reference
			m_tiles = null;
			startGame();
		}
		else {
			// otherwise, do nothing
		}
	}

	/* This starts a new game.
	 * Preconditions:
	 *  - There is no currently open GameView
	 *  - The options (width, height, number of mines) are already known
	 *  - m_tiles is empty
	 */
	private void startGame() {
		// Check conditions
		if (p_game != null || m_tiles != null)
			return;

		m_tiles = new Cell[height()][width()];

		// Place the mines initially
		placeMines();

		// Give the GameView a reference, so it can get cells from the Game Controller
		p_game = new GameView(this);

		// If the theme is set, then apply it
		if (m_options.theme() != null)
			p_game.apply(m_options.theme());

		// Since the GameView inherits from JFrame, we can add arbitrary listeners
		p_game.addWindowListener(new CloseTrigger());

		// Add the menu bar
		deployMenu();

		// Now begin the countdown
		startTimer();
	}

	private void startTimer() {
		// If we have a timer running, stop it
		if (m_timer != null)
			m_timer.cancel();

		// Schedule a task to update the timer
		m_timer = new Timer(true); // true means this thread won't keep the program running
		m_timer.schedule(new UpdateTimer(), 0, 1000); // start immediately and repeat every 1000ms (1s)
	}

	private void deployMenu()
	{
		// Bind Game Menu Items to our ActionListeners
		p_game.addMenuItem("New Game", new NewGameTrigger());

		p_game.addMenuSeparator();

		p_game.addMenuItem("Options", new OptionOpen());
		p_game.addMenuItem("Scores", new ScoresOpen());
		p_game.addMenuItem("About", new AboutOpen());

		p_game.addMenuSeparator();

		p_game.addMenuItem("Exit", new CloseTrigger());
		
	}

	/* By extending both WindowAdapter and ActionListener, we can handle
	 * exit events from both the X in the upper right of the Frame, but
	 * also exit events triggered by the Game -> Exit menu item.
	 */
	private class CloseTrigger
		extends WindowAdapter
		implements ActionListener
	{
		public void windowClosing(WindowEvent ev) {
			closing(ev);
		}
		public void actionPerformed(ActionEvent ev) {
			closing(ev);
		}

		private void closing(EventObject ev) {
			// dispose the Frame, but don't exit; the main can do cleanup
			p_game.dispose();
		}
	}

	private class ScoresOpen
		implements ActionListener
	{
		public void actionPerformed(ActionEvent ev)
		{
			// If the Scores window is open, dispose it and destroy it
			if (p_scores != null) {
				p_scores.dispose();
				p_scores = null;
			}

    		p_scores = new ScoreView(m_scores);
		}
	}

	private class NewGameTrigger
		implements ActionListener
	{
		public void actionPerformed(ActionEvent ev)
		{
			m_timer.cancel(); // stop the timer
			m_scores.add(new Score(false, m_time, m_options)); // record the defeat

			p_game.dispose();
			p_game = null; // destroy the current GameView
			m_tiles = null;

			startGame();
		}
	}

	// This must trigger a defeat condition
    private class MineTrigger
		implements ActionListener
	{
		public void actionPerformed(ActionEvent ev)
		{
			Mine mine = (Mine)ev.getSource();
			mine.trigger();

			m_timer.cancel(); // stop the timer
			stopGame(); // show mines and lock the board
			m_scores.add(new Score(false, m_time, m_options)); // not a victory
			newGame(false); // start a new game, this one is a defeat
		}
	}

    // This is only triggered for safe cells
    private class CellTrigger
		implements ActionListener
	{
		public void actionPerformed(ActionEvent ev)
		{
			Cell cell = (Cell)ev.getSource();

			// Begin expanding the field beginning with this cell
			expand(cell);

			// Check if we have a victory condition
			if (m_remaining == 0) {
				m_timer.cancel(); // stop the timer
				stopGame(); // show mines and lock the board
				m_scores.add(new Score(true, m_time, m_options)); // is a victory
				newGame(true);
			}
		}
	}

    // Stuff to do with the About box
    private class AboutOpen
    	implements ActionListener
    {
    	public void actionPerformed(ActionEvent ev) {
    		// Open a new About window if it doesn't exist
    		if (p_about == null)
    			p_about = new AboutView();
    		// Otherwise, just bring it to the foreground again
    		else
    			p_about.setVisible(true);
    	}
    }

    // Stuff to do with the OptionView
    private class OptionOpen
    	implements ActionListener
    {
    	public void actionPerformed(ActionEvent ev)
    	{
    		// Open a new OptionWindow if it doesn't exist
    		if (p_options == null) {
    			// Give the OptionView a clone of our Options object, so that we can
    			// figure out if anything was changed after Accept
    			p_options = new OptionView(m_options.clone());
    			p_options.setAccept(new OptionAcceptTrigger());
    		}
    		// Otherwise, just bring it to the foreground again
    		else
    			p_options.setVisible(true);
    	}
    }

    /* By extending both WindowAdapter and ActionListener, we can handle
	 * exit events from both the X in the upper right of the Frame, but
	 * also clicking of the Accept button.
	 */
	private class OptionAcceptTrigger
		extends WindowAdapter
		implements ActionListener
	{
		public void windowClosing(WindowEvent ev) {
			save(ev);
		}
		public void actionPerformed(ActionEvent ev) {
			save(ev);
		}

		private void save(EventObject ev) {
			Options opts = p_options.options();

			// Check that our options were gathered successfully
			// Everything is enforced by the Options class, except for the Name
			if (opts.name() == null || opts.name().isEmpty()) {
				JOptionPane.showMessageDialog(p_options, "Please enter a name to continue.");
				return;
			}

			// Remove the Option frame
			p_options.dispose();
			p_options = null;

			// Check if our Theme or Difficulty was changed, and make the changes
			if ((opts.theme() != m_options.theme()) ||
				(opts.difficulty() != m_options.difficulty()))
			{
				if (opts.theme() != null)
					p_game.apply(opts.theme());

				m_options = opts; // for startGame

				p_game.dispose();
				p_game = null; // even though the window is disposed, we still hold a reference
				m_tiles = null;

				startGame();
			}
		}
	}

	// Updates the timer on the lower right
	private class UpdateTimer
		extends TimerTask
	{
		public UpdateTimer() {
			// reset the time
			m_time = 0;
		}
		protected void finalize()  {
			m_time = 0;
	    }

		// This is the method executed by Timer
		public void run() {
			// Do nothing if our GameView is gone or not fully instantiated
			if (p_game == null)
				return;

			p_game.setTimeDisplay(m_time);
			m_time++;
		}
	}
}
