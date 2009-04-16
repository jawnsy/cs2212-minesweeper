import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ScoreView
	extends View
{
	private static final long serialVersionUID = 1L;

	private JTable m_table;
	private JLabel m_topscore;

	// The column identifier to name mapping
	private static final int C_NAME = 0;
	private static final int C_DIFFICULTY = 1;
	private static final int C_RESULT = 2;
	private static final int C_TIME = 3;
	
	// the Score collection
	private Collection<Score> m_scores;

	public ScoreView(Collection<Score> scores) {
		super("MineSweeper Scores");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		m_topscore = new JLabel(" ");
		cp.add(m_topscore, BorderLayout.NORTH);

		// Keep a reference to our score history
		m_scores = scores;

		update();

		pack();
		setLocationRelativeTo(null); // centers the frame
		setVisible(true);
	}
	public void update() {
		// This comes mostly from the JTable tutorial from java.sun.com
	    String[] columnNames = {
	    		"Player",
	    		"Difficulty",
	    		"Result",
	    		"Time"
	    };

	    // Copy all the data to a two-dimensional array
	    // There should be scores.size() rows, and columnNames.length columns per row
	    Object[][] data = new Object[m_scores.size()][columnNames.length];

	    // Loop the Score objects to find the best score, and copy the strings to data[][]
		Score best = null;
		Iterator<Score> iter = m_scores.iterator();
		int i = 0;
		while (iter.hasNext()) {
			Score current = iter.next();

			data[i][C_NAME] = current.player();
			data[i][C_DIFFICULTY] = current.difficulty();
			data[i][C_TIME] = current.elapsed();
			if (current.isVictory()) {
				// We don't have a best score defined yet, so just use the current time
				if (best == null)
					best = current;
				// otherwise, copy the new score if it's shorter than the best time
				else if (current.time() < best.time())
					best = current;

				data[i][C_RESULT] = "Victory";
			}
			else {
				data[i][C_RESULT] = "Defeat";
			}
			i++;
		}
		
		// Update our JLabel which shows who got the highest score
		if (best == null)
			m_topscore.setText("Nobody has won yet, so there is no top score.");
		else
			m_topscore.setText("Best score was for " + best.player() + ", who won in " + best.elapsed() +
					" on " + best.difficulty());

	    m_table = new JTable(data, columnNames);
	    m_table.setPreferredScrollableViewportSize(new Dimension(350, 300));
	    m_table.setFillsViewportHeight(true);

	    // Set all the columns as read-only
	    for (int n = 0; n < columnNames.length; n++) {
	    	Class<?> col = m_table.getColumnClass(n);
	    	m_table.setDefaultEditor(col, null);
	    }

	    m_table.doLayout(); // adjust column widths automatically
		
		// Add the Table to a ScrollPane, in case there are lots of scores
		JScrollPane viewport = new JScrollPane(m_table);

		// Add our ScollPane to the ContentPane
		add(viewport, BorderLayout.CENTER);
	}
}
