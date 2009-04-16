import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.util.EventListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public class OptionView
	extends View
{
	private static final long serialVersionUID = 1L;

	// Here we use the p_ prefix when referring to JPanels, to disambiguate from other variables
	private JPanel p_difficulty, p_theme, p_name;

	private Options m_options;
	private JButton m_accept;
	private JTextField m_name;
	private JRadioButton difficulties[];
	private JRadioButton themes[];

	public OptionView() {
		// Invoke the construction but create a default Options class
		this(new Options());

		/* Since we don't have any options yet, this must mean it's our first
		 * invocation -- Game hasn't been started yet
		 */
		m_accept.setText("Start Game!");

		// Change the title to our default Welcome screen
		setTitle("CS2212 MineSweeper");
		setLocationRelativeTo(null); // centers the frame
	}

	public OptionView(Options opts)
	{
		super("Game Options");

		// store a reference to the given Options
		m_options = opts;

		Container cp = getContentPane();

		/* The top-level ContentPane can only hold one JPanel at a time, so we need
		 * a parent JPanel to hold all our other controls.
		 * 
		 * The GridLayout ensures that we have unlimited (ie, 0) rows, but 1 column.
		 */
		JPanel parent = new JPanel(new BorderLayout());

		p_name = new JPanel(new FlowLayout());
		p_name.add(new JLabel("Your name: "));
		m_name = new JTextField(20);
		m_name.setText(m_options.name());
		p_name.add(m_name);
		parent.add(p_name, BorderLayout.NORTH);

		JPanel options = new JPanel(new GridLayout(0,1));
		p_difficulty = difficultyButtons();
		options.add(p_difficulty);

		p_theme = themeButtons();
		options.add(p_theme);

		parent.add(options, BorderLayout.CENTER);

		m_accept = new JButton("Accept Changes");
		parent.add(m_accept, BorderLayout.SOUTH);
		cp.add(parent);

		pack();
		setVisible(true);
	}

	public Options options() {
		m_options.name(m_name.getText());
	
		for (int i = 0; i < difficulties.length; i++) {
			// Loop until we hit the selected difficulty
			if (!(difficulties[i].isSelected()))
				continue;

			m_options.difficulty(i);
		}

		// The Theme option is set in the ThemeChange ActionListener
		return m_options;
	}

	protected JPanel difficultyButtons() {
		JPanel difficulty = new JPanel(new GridLayout(0,1));
		difficulty.setBorder(BorderFactory.createTitledBorder("Difficulty"));

		difficulties = new JRadioButton[4]; // 4 difficulty levels

		difficulties[0] = new JRadioButton("Beginner");
		difficulties[1] = new JRadioButton("Intermediate");
		difficulties[2] = new JRadioButton("Expert");
		difficulties[3] = new JRadioButton("Impossible");

		ButtonGroup group = new ButtonGroup();
		difficulties[m_options.difficulty()].setSelected(true);
		for (int i = 0; i < difficulties.length; i++) {
			group.add(difficulties[i]);
			difficulty.add(difficulties[i]);
		}

		return difficulty;
	}

	protected JPanel themeButtons() {
		JPanel theme = new JPanel(new GridLayout(0,1));
		theme.setBorder(BorderFactory.createTitledBorder("Theme"));

		themes = new JRadioButton[4];

		themes[0] = new JRadioButton("Default Java Swing");
		themes[1] = new JRadioButton(Theme.CLASSIC.name());
		themes[2] = new JRadioButton(Theme.SUNSET.name());
		themes[3] = new JRadioButton(Theme.HIGHCONTRAST.name());

		ButtonGroup group = new ButtonGroup();
		// A null theme means the default Swing features
		themes[0].setEnabled(false); // cannot be selected
		if (m_options.theme() == null) {
			themes[0].setSelected(true);
		}
		else {
			// Skip the first theme (Java Swing defaults)
			for (int i = 1; i < themes.length; i++) {
				// If our theme name matches the option name, select it
				if (m_options.theme().name() == themes[i].getText())
					themes[i].setSelected(true);
			}
		}

		for (int i = 0; i < themes.length; i++) {
			group.add(themes[i]);
			theme.add(themes[i]);
			themes[i].addActionListener(new ThemeChange());
		}

		return theme;
	}

	public void setAccept(EventListener action) {
		addWindowListener((WindowListener)action);
		m_accept.addActionListener((ActionListener)action);
	}

	// This event is triggered when the theme selection is changed
    private class ThemeChange
    	implements ActionListener
	{
		public void actionPerformed(ActionEvent ev)
		{
			JRadioButton item = (JRadioButton) ev.getSource();
			m_options.theme(item.getText());
			apply(m_options.theme());
		}
	}
}
