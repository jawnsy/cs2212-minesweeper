import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EventObject;

import javax.swing.JOptionPane;

public class CS2212MineSweeper {
	@SuppressWarnings("unused")
	private static Game m_game;
	private static OptionView m_welcome;

	public static void main(String args[]) {
		// On the first invocation, we have to ask the user parameters before
		// we start
		m_welcome = new OptionView();
		m_welcome.setAccept(new OptionAcceptTrigger());
	}

	private static void run() {
		Options opts = m_welcome.options();
		m_welcome = null; // we don't need the welcome box anymore

		m_game = new Game(opts);

		// Once m_game returns, we can do cleanup if necessary
	}

	/* By extending both WindowAdapter and ActionListener, we can handle
	 * exit events from both the X in the upper right of the Frame, but
	 * also clicking of the Accept button.
	 */
	private static class OptionAcceptTrigger
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
			Options opts = m_welcome.options();

			// Check that our options were gathered successfully
			// Everything is enforced by the Options class, except for the Name
			if (opts.name() == null || opts.name().isEmpty()) {
				JOptionPane.showMessageDialog(m_welcome, "Please enter a name to continue.");
				return;
			}

			// Remove our Welcome frame, and start the Game with our returned Options
			m_welcome.dispose();

			// Actually start running the game :-)
			run();
		}
	}
}
