import java.awt.Component;
import java.awt.Container;

import javax.swing.JFrame;

public class View
	extends JFrame
{
	private static final long serialVersionUID = 1L;

	private Theme m_theme;

	public View(String title) {
		super(title);

		/* By setting DO_NOTHING_ON_CLOSE, we can handle the WindowClosing event manually
		 * and decide whether or not to allow the window to close. Thus, we can display
		 * a Confirmation dialog or something.
		 */
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setResizable(false);
	}

	public void apply(Theme theme) {
		m_theme = theme;

		/* In theory, this part should be sufficient to change the foreground
		 * and background images of everything. This is because we assume that the
		 * Component objects we are using are transparent, so that if we change
		 * the colour of the Content Pane, where most of our stuff is drawn, then
		 * everything else should 'just work' as well.
		 */
		Container cp = getContentPane();
		apply(cp);

		// After changing the colours, we have to trigger a repainting of the GUI
		cp.repaint();
	}

	/* Because we sometimes have the objects not on the actual Content Pane, but rather
	 * inside opaque (non-transparent) JPanel objects or other Container objects, then we
	 * have to loop through each sub-container to make sure /its/ child components have its
	 * colours set appropriately.
	 * 
	 * There is probably a better way to do this, but I haven't found any.  Web sites seem
	 * to suggest that simply applying the colours to the Content Pane is sufficient, but
	 * because of my use of JPanel, it does not seem so.
	 *
	 * So, if apply() is being executed on a Container and it finds a sub-container object,
	 * then it will recursively call itself to process the sub-containers. Otherwise, the
	 * base case is a call with a Component.
	 */

	// Apply a Theme to a Container of Component objects
	private void apply(Container container) {
		Component c[] = container.getComponents();
		for (int i = 0; i < c.length; i++) {
			// This test is necessary because a JPanel is both a Container and a Component
			if (c[i] instanceof Container) {
				apply((Container) c[i]);
			}
			apply(c[i]);
		}
	}

	// Apply a Theme to a given Component
	private void apply(Component c) {
		/* sometimes the Components returned are null. This doesn't make sense to me, but
		 * we have to check for it; otherwise NullPointerExceptions will be thrown.
		 */
		if (c == null)
			return;

		// don't change backgrounds of revealed Cells
		if (c instanceof Cell && ((Cell)c).visible())
			return;

		c.setBackground(m_theme.background());
		c.setForeground(m_theme.foreground());
	}
}
