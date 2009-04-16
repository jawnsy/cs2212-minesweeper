
public class Options
	extends Object
	implements Cloneable
{
	private Theme m_theme;
	private int m_difficulty;
	private String m_name;

	public static final int D_EASY 		 = 0;
	public static final int D_MEDIUM     = 1;
	public static final int D_HARD       = 2;
	public static final int D_IMPOSSIBLE = 3;

	/* Beginner      4x4   (3 mines)
	 * Intermediate  6x4   (7 mines)
	 * Expert        8x8   (10 mines)
	 * Impossible    25x20 (120 mines)
	 */

	public Options() {
		m_difficulty = 0;
		m_theme = null;
	}
	
	public Theme theme() {
		return m_theme;
	}
	public void theme(Theme theme) {
		m_theme = theme;
	}
	public void theme(String theme) {
		if (theme == Theme.CLASSIC.name())
			m_theme = Theme.CLASSIC;
		else if (theme == Theme.SUNSET.name())
			m_theme = Theme.SUNSET;
		else if (theme == Theme.HIGHCONTRAST.name())
			m_theme = Theme.HIGHCONTRAST;
		// The last case should never happen, since we aren't allowed to return to Default
		else // Default Java Swing
			m_theme = null;
	}

	public int difficulty() {
		return m_difficulty;
	}
	public void difficulty(int difficulty) {
		m_difficulty = difficulty;
	}

	public String name() {
		return m_name;
	}
	public void name(String name) {
		m_name = name;
	}

	public int width() {
		switch (m_difficulty) {
			case D_EASY:
				return 4;
			case D_MEDIUM:
				return 6;
			case D_HARD:
				return 8;
			case D_IMPOSSIBLE:
				return 25;
		}
		return 0;
	}
	public int height() {
		switch (m_difficulty) {
			case D_EASY:
			case D_MEDIUM:
				return 4;
			case D_HARD:
				return 8;
			case D_IMPOSSIBLE:
				return 20;
		}
		return 0;
	}
	public int mines() {
		switch (m_difficulty) {
			case D_EASY:
				return 3;
			case D_MEDIUM:
				return 7;
			case D_HARD:
				return 10;
			case D_IMPOSSIBLE:
				return 120;
		}
		return 0;
	}

	public Options clone()
	{
		try {
			return (Options)super.clone();
		}
		catch (CloneNotSupportedException e) {
			// should never be thrown, since we implement Cloneable
		}
		// something weird happened, so just return a null object
		return null;
	}
}
