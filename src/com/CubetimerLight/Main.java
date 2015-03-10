package com.CubetimerLight;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Main extends JFrame {

	private JPanel contentPane;
	public static JLabel labelTime;
	public static JLabel labelAverage;
	public static JLabel labelResponse;
	public static List<Entry> entries;

	public static void main(String[] args) {
		entries = new ArrayList<Entry>();
		for (String s : FileUtils.loadFile("session.db")) {
			entries.add(new Entry(s));
		}
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main frame = new Main();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	// stopped, keydown, counts
	// stopped, keyup , counts > x, timer start
	// started, keydown any, timer stops

	public static String times(int i, char c) {
		String out = "";
		for (int j = 0; j < i; j++) {
			out += c;
		}
		return out;
	}

	public static void purge() {
		for (int i = 0; i < 50; i++) {
			System.out.println();
		}
	}

	public static void log() {
		purge();
		System.out.print(times(25, '-') + "\n    Current session:\n"
				+ times(25, '-') + "\n");
		for (int i = 0; i < entries.size(); i++) {
			Entry e = entries.get(i);
			System.out.println(i + times(6 - (i + "").length(), ' ')
					+ e.getCompact());
		}
		if (entries.size() >= 12) {
			int avg = aoN(12);
			System.out.println("\nYour current ao12 is " + toTimestamp(avg)
					+ ".");
		} else if (entries.size() >= 5) {
			int avg = aoN(5);
			System.out.println("\nYour current ao5 is " + toTimestamp(avg)
					+ ".");
		}
		FileUtils.saveFile("session.db", getEntryResources());
		labelAverage.setText(averageString());
	}

	public static String averageString() {
		if (entries.size() >= 12) {
			return "Ao12: " + toTimestamp(aoN(12));
		} else if (entries.size() >= 5) {
			return "Ao5: " + toTimestamp(aoN(5));
		}
		return "";
	}

	public static int aoN(int n) {
		if (entries.size() < 3) {
			return -1;
		}
		List<Entry> temp = new ArrayList<Entry>();
		for (int i = 0; i < n; i++) {
			temp.add(entries.get(entries.size() - i - 1));
		}
		Entry min = null;
		Entry max = null;
		for (int i = 0; i < temp.size(); i++) {
			if (min == null || temp.get(i).getTime() < min.getTime()) {
				min = temp.get(i);
			}
		}
		for (int i = 0; i < temp.size(); i++) {
			if (max == null || temp.get(i).getTime() > max.getTime()) {
				max = temp.get(i);
			}
		}
		temp.remove(max);
		temp.remove(min);
		int sum = 0;
		for (Entry e : temp) {
			sum += e.getTime();
		}
		return sum / temp.size();
	}

	public static int bestAoN(int n) {
		if (entries.size() < n) {
			return -1;
		}
		int minavg = 0;
		for (int j = 0; j < (entries.size() + 1 - n); j++) {
			if (entries.size() < 3) {
				return -1;
			}
			List<Entry> temp = new ArrayList<Entry>();
			iter: for (int i = j; i < j + n; i++) {
				temp.add(entries.get(entries.size() - i - 1));
			}
			Entry min = null;
			Entry max = null;
			searchmin: for (int i = 0; i < temp.size(); i++) {
				if (min == null || temp.get(i).getTime() < min.getTime()) {
					min = temp.get(i);
				}
			}
			searchmax: for (int i = 0; i < temp.size(); i++) {
				if (max == null || temp.get(i).getTime() > max.getTime()) {
					max = temp.get(i);
				}
			}
			temp.remove(max);
			temp.remove(min);
			int sum = 0;
			for (Entry e : temp) {
				sum += e.getTime();
			}
			int avg = sum / temp.size();
			if (minavg == 0 || avg < minavg) {
				minavg = avg;
			}
		}
		return minavg;
	}

	public static List<String> getEntryResources() {
		List<String> l = new ArrayList<String>();
		for (Entry e : entries) {
			l.add(e.getResource());
		}
		return l;
	}

	public Main() {
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent arg0) {
				if (!Timer.started()) {
					if (arg0.getKeyCode() == KeyEvent.VK_SPACE) {
						Timer.start();
					} else if (arg0.getKeyCode() == KeyEvent.VK_DELETE) {
						if (entries.size() != 0) {
							int rem = entries.size() - 1;
							entries.remove(rem);
							log();
							Main.labelTime.setText("0.000");
							System.out.println("   Removed last entry (#" + rem
									+ ")");
							broadcast("Last time deleted");
						}
					} else if (arg0.getKeyCode() == KeyEvent.VK_N) {
						FileUtils.saveFile(System.currentTimeMillis() + ".db",
								getEntryResources());
						File f = new File(FileUtils.root + "session.db");
						if (f.exists())
							f.delete();
						entries = new ArrayList<Entry>();
						FileUtils.saveFile(FileUtils.root + "session.db",
								getEntryResources());
						log();
						Main.labelTime.setText("0.000");
						System.out.println("Session cleared!");
						broadcast("New session created");
					} else if (arg0.getKeyCode() == KeyEvent.VK_B) {
						int n = 0;
						if (entries.size() >= 12) {
							n = 12;
						} else if (entries.size() >= 5) {
							n = 5;
						}
						if (n != 0) {
							broadcast("Best Ao" + n + ": "
									+ toTimestamp(bestAoN(n)) + "      (" + entries.size() + " entries)", 5000);
						}

					}
				} else {
					Timer.stop();
					log();
				}
			}
		});

		setExtendedState(Frame.MAXIMIZED_BOTH);
		setUndecorated(true);
		setName("Cubetimer light");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		int scrWidth = (int) java.awt.Toolkit.getDefaultToolkit()
				.getScreenSize().getWidth();
		int scrHeight = (int) java.awt.Toolkit.getDefaultToolkit()
				.getScreenSize().getHeight();

		labelTime = new JLabel(toTimestamp(0));
		labelTime.setBounds(0, 0, scrWidth, scrHeight);
		// labelTime.setHorizontalTextPosition(SwingConstants.CENTER);
		labelTime.setHorizontalAlignment(SwingConstants.CENTER);
		labelTime.setFocusable(false);
		labelTime.setFont(new Font("Courier New", Font.PLAIN, 160));
		labelAverage = new JLabel(averageString());
		labelAverage.setBounds(5, 5, 200, 25);
		labelAverage.setFocusable(false);
		labelAverage.setFont(new Font("Courier New", Font.PLAIN, 28));

		labelResponse = new JLabel("");
		labelResponse.setBounds(0, 10, scrWidth, 60);
		labelResponse.setFocusable(false);
		labelResponse.setFont(new Font("Courier New", Font.PLAIN, 52));
		labelResponse.setHorizontalAlignment(SwingConstants.CENTER);

		contentPane = new JPanel();
		contentPane.setBackground(new Color(190, 255, 130));
		contentPane.setLayout(null);

		contentPane.add(labelTime);
		contentPane.add(labelAverage);
		contentPane.add(labelResponse);
		setContentPane(contentPane);
		log();
	}

	public static String toTimestamp(int i) {

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
			millis += "0";
		String out = ((min != 0) ? (min + ":") : ("")) + sec + "." + millis;
		// <condition>?<true case>:<false case>

		return (out);
	}

	public static void broadcast(String out) {
		labelResponse.setText(out);
		new java.util.Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				labelResponse.setText("");
			}
		}, 2000);
	}

	public static void broadcast(String out, int time) {
		labelResponse.setText(out);
		new java.util.Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				labelResponse.setText("");
			}
		}, time);
	}
}
