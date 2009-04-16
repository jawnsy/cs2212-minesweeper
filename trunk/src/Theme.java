import java.awt.Color;
import java.awt.Component;

public class Theme {
	protected static Theme HIGHCONTRAST = new Theme(
		"High Contrast",
		Color.YELLOW,
		Color.BLACK
	);
	protected static Theme CLASSIC = new Theme(
		"Classic",
		Color.BLACK,
		Color.LIGHT_GRAY
	);
	protected static Theme SUNSET = new Theme(
		"Sunset",
		Color.RED,
		Color.ORANGE
	);

	private String m_name;
	private Color m_fgcolor, m_bgcolor;

	public Theme(String name, Color foreground, Color background)
	{
		m_name = name;
		m_fgcolor = foreground;
		m_bgcolor = background;
	}

	public void name(String name) {
		m_name = name;
	}
	public String name() {
		return m_name;
	}

	public void foreground(Color foreground) {
		m_fgcolor = foreground;
	}
	public Color foreground() {
		return m_fgcolor;
	}

	public void background(Color background) {
		m_bgcolor = background;
	}
	public Color background() {
		return m_bgcolor;
	}
	
	public void apply(Component c) {
		c.setBackground(m_bgcolor);
		c.setForeground(m_fgcolor);
	}
}
