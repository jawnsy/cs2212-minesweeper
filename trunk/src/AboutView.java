import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class AboutView
	extends View
{
	private static final long serialVersionUID = 1L;

	public AboutView() {
		super("About");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		Container cp = getContentPane();
		cp.setLayout(new BorderLayout());

		JPanel topText = new JPanel(new GridLayout(0, 1));
		topText.add(new JLabel("Copyright (c) 2009 Jonathan Yu <jon@luminescent.ca>"));
		topText.add(new JLabel("CS2212 MineSweeper version 1.0 final"));
		topText.add(new JLabel("Use of this software is governed by the following licensing conditions:"));
		cp.add(topText, BorderLayout.NORTH);

		JTextArea license = new JTextArea(
			"Permission is hereby granted, free of charge, to any person\n" +
			"obtaining a copy of this software and associated documentation\n" +
			"files (the 'Software'), to deal in the Software without\n" +
			"restriction, including without limitation the rights to use,\n" +
			"copy, modify, merge, publish, distribute, sublicense, and/or sell\n" +
			"copies of the Software, and to permit persons to whom the\n" +
			"Software is furnished to do so, subject to the following\n" +
			"conditions:\n" +
			"\n" +
			"The above copyright notice and this permission notice shall be\n" +
			"included in all copies or substantial portions of the Software.\n" +
			"\n" +
			"THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,\n" +
			"EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES\n" +
			"OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND\n" +
			"NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT\n" +
			"HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,\n" +
			"WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING\n" +
			"FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR\n" +
			"OTHER DEALINGS IN THE SOFTWARE.\n",
			10 /* rows */, 70 /* columns */
		);
		license.setFont(new Font("Monospaced", Font.PLAIN, 12));
		license.setEditable(false);
		license.setWrapStyleWord(true);
		cp.add(new JScrollPane(license, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER);

		JButton close = new JButton("Close Window");
		close.addActionListener(new CloseTrigger());
		cp.add(close, BorderLayout.SOUTH);

		pack();
		setVisible(true);
	}

	private class CloseTrigger
		implements ActionListener
	{
    	public void actionPerformed(ActionEvent ev)
		{
    		dispose();
		}
	}
}
