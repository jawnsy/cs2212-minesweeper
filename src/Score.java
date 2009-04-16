// This is a simple class to keep information about scores together
public class Score
	implements Cloneable
{
	private boolean m_victory;
	private int m_time, m_difficulty;
	private String m_name;

	public Score(boolean victory, int time, Options opts) {
		m_victory = victory;
		m_time = time;
		m_difficulty = opts.difficulty();
		m_name = opts.name();
	}
	
	public int time() {
		return m_time;
	}

	// This method returns the time in a human-readable format.
	public String elapsed() {
		// If more than a minute has elapsed, then it shows: 1m30s or similar
		if (m_time > 60)
			return ((int)(m_time / 60) + "m" + (m_time % 60) + "s");

		 // If less than 60 seconds has elapsed, then it shows time as: 30s
		return (m_time + "s");
	}

	public boolean isVictory() {
		return m_victory;
	}
	public boolean isDefeat() {
		return !m_victory;
	}

	public String difficulty() {
		switch (m_difficulty) {
			case Options.D_EASY:
				return "Beginner";
			case Options.D_MEDIUM:
				return "Intermediate";
			case Options.D_HARD:
				return "Expert";
			case Options.D_IMPOSSIBLE:
				return "Impossible";
		}
		return null;
	}
	public String player() {
		return m_name;
	}
}
