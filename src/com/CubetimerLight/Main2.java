package com.CubetimerLight;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TimerTask;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class Main2 extends JFrame {

	//

	private static MediaPlayer nyan;

	final int SCRWIDTH = (int) java.awt.Toolkit.getDefaultToolkit()
			.getScreenSize().getWidth();
	final int SCRHEIGHT = (int) java.awt.Toolkit.getDefaultToolkit()
			.getScreenSize().getHeight();
	public static String puzzle = "3x3";

	private static List<Entry> entries;
	public static JPanel contentPane;
	public static JLabel timeLabel;
	public static JLabel scrambleLabel;
	private static JLabel[] labels;
	private static JLabel[] helpLabels;
	static int downtime;
	private static boolean help;

	private static JProgressBar xp;
	private static JLabel levelLabel;
	private static JLabel xpLabel;

	private static Quest suggested;
	private static Quest active;

	private static int nyandown;

	// [S] new Session
	// [N] new Scramble
	// [DEL] delete last entry?

	public static void main(String[] args) throws URISyntaxException {
		new javafx.embed.swing.JFXPanel();

		String uriString2 = Main2.class.getResource("nyan.mp3").toURI()
				.toString();
		nyan = new MediaPlayer(new Media(uriString2));
		help = false;
		downtime = 0;
		loadEntries();
		labels = new JLabel[4];
		helpLabels = new JLabel[4];
		// H for help / Press and hold space to start
		// N for new scramble
		new Main2();
	}

	public Main2() {
		setExtendedState(Frame.MAXIMIZED_BOTH);
		setUndecorated(true);
		setName("Cubetimer light");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		contentPane = new JPanel();
		contentPane.setLayout(null);
		contentPane.setBackground(new Color(190, 255, 130));
		for (int i = 0; i < labels.length; i++) {
			labels[i] = new JLabel("");
			labels[i].setBounds(20, 30 * i + 20, 750, 20);
			labels[i].setFont(new Font("Courier", Font.PLAIN, 18));
			labels[i].setHorizontalAlignment(SwingConstants.LEFT);
			labels[i].setVerticalAlignment(SwingConstants.TOP);
			contentPane.add(labels[i]);
		}
		{
			int w = 450;
			int marg = 20;
			int h = 17;
			for (int i = 0; i < helpLabels.length; i++) {
				helpLabels[i] = new JLabel("");
				helpLabels[i].setBounds(SCRWIDTH - (w + marg), marg + i * h, w,
						h);
				helpLabels[i].setFont(new Font("Courier", Font.PLAIN, 14));
				helpLabels[i].setHorizontalAlignment(SwingConstants.RIGHT);
				helpLabels[i].setVerticalAlignment(SwingConstants.TOP);
				contentPane.add(helpLabels[i]);
			}
		}
		timeLabel = new JLabel(toTimestamp(0));
		timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		timeLabel.setVerticalAlignment(SwingConstants.CENTER);
		timeLabel.setBounds(0, 0, SCRWIDTH, SCRHEIGHT);
		timeLabel.setFont(new Font("Courier", Font.PLAIN, 120));
		contentPane.add(timeLabel);

		scrambleLabel = new JLabel("");
		scrambleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scrambleLabel.setVerticalAlignment(SwingConstants.CENTER);
		scrambleLabel.setBounds(0, SCRHEIGHT / 2, SCRWIDTH, SCRHEIGHT / 4);
		scrambleLabel.setFont(new Font("Courier", Font.PLAIN, 24));
		contentPane.add(scrambleLabel);

		levelLabel = new JLabel("Lv " + 1);
		levelLabel.setFont(new Font("Courier", Font.BOLD, 22));
		xpLabel = new JLabel("42/69 Exp.");
		xpLabel.setFont(new Font("Courier", Font.PLAIN, 12));
		xp = new JProgressBar(0, 100);
		{
			int marg = 20;
			int h = 40;
			int w = 75;
			xp.setBounds(2 * marg + w, SCRHEIGHT - (h + marg), SCRWIDTH
					- (w + 3 * marg), h);
			levelLabel.setBounds(marg, SCRHEIGHT - (h + marg), w, h - 20);
			xpLabel.setBounds(marg, SCRHEIGHT - (h + marg), w, h + 20);
		}
		xp.setValue(45);
		contentPane.add(xp);
		contentPane.add(levelLabel);
		contentPane.add(xpLabel);

		setContentPane(contentPane);
		updateStrings();
		updateScramble();
		updateXp();
		updateHelp();
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_C) {
					nyandown++;
					Random r = new Random();
					contentPane.setBackground(new Color(Constants.MINCOLOR
							+ r.nextInt(256 - Constants.MINCOLOR),
							Constants.MINCOLOR
									+ r.nextInt(256 - Constants.MINCOLOR),
							Constants.MINCOLOR
									+ r.nextInt(256 - Constants.MINCOLOR)));
					updateHelp();
					if (nyandown == Constants.NYANCAP)
						nyan.play();
				}
				if (!Timer.started()) {
					if (arg0.getKeyCode() == KeyEvent.VK_H) {
						help = !help;
						updateHelp();
					}
					if (arg0.getKeyCode() == KeyEvent.VK_SPACE) {
						if (downtime < Constants.DOWNCAP) {// notready
							timeLabel.setForeground(new Color(255, 25, 25));
							downtime++;
						} else if (downtime == Constants.DOWNCAP) {// ready
							timeLabel.setForeground(new Color(75, 75, 255));
						}
					} else if (arg0.getKeyCode() == KeyEvent.VK_N) {
						updateScramble();
					}
				} else {
					downtime++;
					Timer.stop();
					entries.add(new Entry(Timer.time(), scrambleLabel.getText()));
					saveEntries();
					updateStrings();
					updateScramble();
					Progress.addXp(evalSolve(Timer.time()));
					updateXp();
				}
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_C) {
					if (nyandown >= Constants.NYANCAP) {
						nyandown = 0;
						nyan.stop();
					}
				}
				if (arg0.getKeyCode() == KeyEvent.VK_SPACE) {
					if (downtime < Constants.DOWNCAP) {// forget it
						timeLabel.setForeground(new Color(0, 0, 0));
						downtime = 0;
					} else if (downtime == Constants.DOWNCAP) {// ready, go!
						Timer.start();
						timeLabel.setForeground(new Color(0, 0, 0));
					}
				}
				if (downtime > Constants.DOWNCAP) {
					downtime = 0;
				}
			}
		});
	}

	private static String getColor() {
		return contentPane.getBackground().getRed() + ","
				+ contentPane.getBackground().getBlue() + ","
				+ contentPane.getBackground().getGreen();
	}

	private static void updateHelp() {
		String[] tab = table(3, new String[][] {
				new String[] { "[SPACE]", "hold and release to start" },
				new String[] { "[N]", "generate new Scramble" },
				new String[] { "[P]", "Switch Puzzle (wip)" },
				new String[] { "[C]",
						"to alternate Color (current: " + getColor() + ")" } });

		if (help) {
			for (int i = 0; i < helpLabels.length; i++)
				helpLabels[i].setText(tab[i]);
		} else {
			for (int i = 0; i < helpLabels.length; i++)
				helpLabels[i].setText("");
			helpLabels[0].setText("[H]   for help");
		}
	}

	private static int evalSolve(int time) {
		Random r = new Random();
		if (entries.size() > 10 && time == SortUtils.best(entries))
			return 1 + r.nextInt(Constants.TIME_PB);
		if (time < SortUtils.median(entries))
			return 1 + r.nextInt(Constants.TIME_GOOD);
		return Constants.TIME_NORMAL;
	}

	public static void updateStrings() {
		String[] lines0 = tableToLines(
				3,
				new String[][] {
						new String[] { "Ao5",
								toTimestamp(SortUtils.aoN(entries, 5)) },
						new String[] { "Ao12",
								toTimestamp(SortUtils.aoN(entries, 12)) },
						new String[] { "Ao100",
								toTimestamp(SortUtils.aoN(entries, 100)) } });
		String[] lines1 = tableToLines(
				3,
				new String[][] {
						new String[] { "Best Ao5:",
								toTimestamp(SortUtils.bestAoN(entries, 5)) },
						new String[] { "Best Ao12:",
								toTimestamp(SortUtils.bestAoN(entries, 12)) },
						new String[] { "Best:",
								toTimestamp(SortUtils.best(entries)) }

				});
		String[] lines2 = tableToLines(3, new String[][] {
				new String[] { "Mean:", toTimestamp(SortUtils.mean(entries)) },
				new String[] { "Median:",
						toTimestamp(SortUtils.median(entries)) },
				new String[] { "Solves:", "" + entries.size() } });
		for (int i = 0; i < lines0.length; i++) {
			labels[i]
					.setText(lines0[i] + "   " + lines1[i] + "   " + lines2[i]);
		}
	}

	public static void updateXp() {
		int level = Progress.getLevel();
		levelLabel.setText("Lv " + level);
		xpLabel.setText(Progress.getXp() + "/"
				+ Progress.xpAt(Progress.getLevel()) + " exp.");
		xp.setMaximum(Progress.xpAt(level));
		xp.setValue(Progress.getXp());
	}

	public static void updateScramble() {
		scrambleLabel.setText(Scramble.Cube3x3(Constants.SCRAMBLE_LENGTH));
	}

	public static String[] tableToLines(int min, String[][] lines) {
		int maxcols = 0;
		for (int i = 0; i < lines.length; i++) {
			if (maxcols < lines[i].length)
				maxcols = lines[i].length;
		}
		int[] maxcollength = new int[maxcols];
		for (int i = 0; i < lines.length; i++) {
			for (int j = 0; j < lines[i].length; j++) {
				if (maxcollength[j] < lines[i][j].length()) {
					maxcollength[j] = lines[i][j].length();
				}
			}
		}
		for (int i = 0; i < lines.length; i++) {
			for (int j = 0; j < lines[i].length; j++) {
				while (lines[i][j].length() < maxcollength[j]) {
					lines[i][j] = lines[i][j] + " ";
				}
			}
		}
		String[] out = new String[lines.length];
		String s = "";
		while (s.length() < min)
			s += " ";
		for (int i = 0; i < lines.length; i++) {
			for (int j = 0; j < lines[i].length; j++) {
				if (out[i] == null)
					out[i] = "";
				out[i] = out[i] + lines[i][j] + s;
			}
		}
		return out;
	}

	public static String toTimestamp(int i) {
		if (i == -1)
			return "NaN";
		int min = i/60000;
		i -= min;
		String sec = "" + (i / 1000) % 100;
		if (min > 0)
			while (sec.length() < 2)
				sec = "0" + sec;
		String millis = "" + i % 1000;
		while (millis.length() < 3)
			millis = "0" + millis;
		String out = ((min != 0) ? (min + ":") : ("")) + sec + "." + millis;

		return (out);
	}

	public static String[] table(int min, String[]... lines) {
		String[] out = new String[lines.length];
		int[] max = getMaxLenghts(lines);
		for (int j = 0; j < lines.length; j++) {
			String[] s = lines[j];
			for (int i = 0; i < s.length; i++) {
				if (s[i].length() < max[i] + min) {
					s[i] += times(max[i] + ((i != (s.length - 1)) ? min : 0)
							- s[i].length(), ' ');
					System.out.println(s[i]);
				}
				if (out[j] == null) {
					out[j] = "";
				}
				out[j] = out[j] + s[i];
			}
		}
		return out;
	}

	public static int[] getMaxLenghts(String[][] in) {
		int[] out = new int[in.length];
		for (int i = 0; i < in.length; i++) {
			for (int j = 0; j < in[i].length; j++) {
				if (out[j] == 0 || in[i][j].length() > out[j])
					out[j] = in[i][j].length();
			}
		}
		return out;
	}

	public static void broadcast(String out, int time) {
		// labelResponse.setText(out);
		new java.util.Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				// labelResponse.setText("");
			}
		}, time);
	}

	private static void loadEntries() {
		loadEntries("/puzzle/" + puzzle + ".db");
	}

	private static void saveEntries() {
		saveEntries("/puzzle/" + puzzle + ".db");
	}

	private static void saveEntries(String sessionname) {
		FileUtils.saveFile(sessionname, SortUtils.getEntryResources(entries));
	}

	private static void loadEntries(String sessionname) {
		entries = new ArrayList<Entry>();
		for (String s : FileUtils.loadFile(sessionname)) {
			entries.add(new Entry(s));
		}
	}

	public static String times(int n, char c) {
		return times(n, "" + c);
	}

	public static String times(int n, String c) {
		String out = "";
		for (int i = 0; i < n; i++) {
			out = out + c;
			// out += c;
		}
		return out;
	}
}
