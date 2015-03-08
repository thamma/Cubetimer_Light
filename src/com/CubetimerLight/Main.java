package com.CubetimerLight;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Main extends JFrame {

	private JPanel contentPane;
	public static JLabel labelTime;
	public static List<Entry> entries;

	public static void main(String[] args) {
		entries = new ArrayList<Entry>();
		for (String s : loadFile("session.db")) {
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
		log();
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
		if (entries.size() > 0) {

			int sum = 0;
			for (Entry e : entries) {
				sum += e.getTime();
			}
			double avg = sum / entries.size();
			System.out.println("\nYour current average is "
					+ toTimestamp((int) avg) + " (avg of " + entries.size()
					+ ")");
		}
		saveFile("session.db", getEntryResources());
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
							System.out.println("   Removed last entry (#" + rem
									+ ")");
						}
					} else if (arg0.getKeyCode() == KeyEvent.VK_N) {
						saveFile(System.currentTimeMillis() + ".db",
								getEntryResources());
						File f = new File(root + "session.db");
						if (f.exists())
							f.delete();
						entries = new ArrayList<Entry>();
						for (String s : loadFile("session.db")) {
							entries.add(new Entry(s));
						}
						saveFile(root + "session.db", getEntryResources());
						log();
						Main.labelTime.setText("0.000");
						System.out.println("Session cleared!");
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

		labelTime = new JLabel(toTimestamp(0));
		labelTime.setBounds(0, 0, (int) java.awt.Toolkit.getDefaultToolkit()
				.getScreenSize().getWidth(), (int) java.awt.Toolkit
				.getDefaultToolkit().getScreenSize().getHeight());
		labelTime.setHorizontalTextPosition(SwingConstants.CENTER);
		labelTime.setHorizontalAlignment(SwingConstants.CENTER);
		// labelTime.setHorizontalAlignment(SwingConstants.CENTER);
		labelTime.setFocusable(false);
		labelTime.setFont(new Font("Courier New", Font.PLAIN, 128));

		contentPane = new JPanel();
		contentPane.setBackground(new Color(244, 164, 96));
		contentPane.setLayout(null);

		contentPane.add(labelTime);
		setContentPane(contentPane);
	}

	public final static String root = System.getenv("APPDATA") + "/CTL/";

	public static List<String> loadFile(String subpath) {
		List<String> lines = new ArrayList<String>();
		File f = new File(root + subpath);
		if (f.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String line;
				while ((line = br.readLine()) != null) {
					lines.add(line);
				}
				br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Error loading file: \"" + e.getMessage()
						+ "\"");
			}

		} else {
			System.out.println("File \"" + subpath + "\" could not be found.");
		}
		return lines;
	}

	public static boolean saveFile(String subpath, List<String> lines) {
		File f = new File(root + subpath);
		f.mkdirs();
		if (f.exists()) {
			f.delete();
		}
		try {
			FileWriter writer;
			writer = new FileWriter(f);
			for (String s : lines) {
				writer.write(s + "\n");
			}
			writer.close();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error saving file: \"" + e.getMessage() + "\"");
			return false;
		}
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

}
