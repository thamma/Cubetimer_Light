package com.CubetimerLight;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

public class Main2 extends JFrame {

	final int SCRWIDTH = (int) java.awt.Toolkit.getDefaultToolkit()
			.getScreenSize().getWidth();
	final int SCRHEIGHT = (int) java.awt.Toolkit.getDefaultToolkit()
			.getScreenSize().getHeight();
	public static String puzzle = "3x3";

	private static List<Entry> entries;
	private JPanel contentPane;
	public static JLabel timeLabel;
	public static JLabel scrambleLabel;
	private static JLabel[] labels = new JLabel[4];
	static int downtime;

	private static JProgressBar xp;
	private static JLabel levelLabel;
	private static JLabel xpLabel;

	private static Quest suggested;
	private static Quest active;

	// [S] new Session
	// [N] new Scramble
	// [DEL] delete last entry?

	public static void main(String[] args) {
		downtime = 0;
		loadEntries();
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
			labels[i].setFocusable(false);
			labels[i].setFont(new Font("Courier", Font.PLAIN, 18));
			labels[i].setHorizontalAlignment(SwingConstants.LEFT);
			labels[i].setVerticalAlignment(SwingConstants.TOP);
			contentPane.add(labels[i]);
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
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (!Timer.started()) {
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
		String scramblechars = "";
		Random r = new Random();
		String res0 = "UFLDBR";
		String res1 = "2'";
		while (scramblechars.length() < 23) {
			int cid = r.nextInt(6);
			if (scramblechars.length() > 0) {
				while (scramblechars.charAt(scramblechars.length() - 1) == res0
						.charAt(cid))
					cid = r.nextInt(6);
			}
			if (scramblechars.length() > 1) {
				while (scramblechars.charAt(scramblechars.length() - 1) == res0
						.charAt(cid)
						|| scramblechars.charAt(scramblechars.length() - 2) == res0
								.charAt((cid + 3) % 6)
						|| (scramblechars.charAt(scramblechars.length() - 2) == res0
								.charAt(cid) && scramblechars
								.charAt(scramblechars.length() - 1) == res0
								.charAt((cid + 3) % 6))) {
					cid = r.nextInt(6);
				}
			}
			scramblechars += res0.charAt(cid);
		}
		String scramble = "";
		for (int i = 0; i < scramblechars.length(); i++) {
			char c = scramblechars.charAt(i);
			int a = r.nextInt(4);
			scramble += c + (a > 1 ? "" : "" + res1.charAt(a)) + " ";
		}
		scrambleLabel.setText(scramble);
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
		int min = 0; // minutes
		while (i >= 60000) {
			i -= 60000; // i = i - 60000;
			min++; // min = min+1;
		}
		String sec = "" + (i / 1000) % 100;
		if (min > 0)
			while (sec.length() < 2)
				sec = "0" + sec;
		String millis = "" + i % 1000;
		while (millis.length() < 3)
			millis = "0" + millis;
		String out = ((min != 0) ? (min + ":") : ("")) + sec + "." + millis;
		// <condition>?<true case>:<false case>

		return (out);
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
}
