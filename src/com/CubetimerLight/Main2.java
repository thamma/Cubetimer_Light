package com.CubetimerLight;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Main2 extends JFrame {

	final int SCRWIDTH = (int) java.awt.Toolkit.getDefaultToolkit()
			.getScreenSize().getWidth();
	int SCRHEIGHT = (int) java.awt.Toolkit.getDefaultToolkit().getScreenSize()
			.getHeight();

	private static List<Entry> entries;
	private JPanel contentPane;

	private String[] strings = new String[] { "Ao5:   ", "Ao12:  ", "Ao100: ",
			"Median:" };
	private JLabel[] labels = new JLabel[strings.length];
	private Double[][] labelSizes = new Double[][] {
			new Double[] { 0.01, 0.0, 0.5, 0.05 },
			new Double[] { 0.01, 0.05, 0.5, 0.05 },
			new Double[] { 0.01, 0.10, 0.5, 0.05 },
			new Double[] { 0.01, 0.15, 0.5, 0.05 } };

	public static void main(String[] args) {
		loadEntries();
		new Main2();
		// EventQueue.invokeLater(new Runnable() {
		// public void run() {
		// try {
		// Main2 frame = new Main2();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		// });
	}

	public Main2() {
		setExtendedState(Frame.MAXIMIZED_BOTH);
		setUndecorated(true);
		setName("Cubetimer light");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);

		contentPane = new JPanel();
		contentPane.setBackground(new Color(190, 255, 130));
		contentPane.setLayout(null);

		setContentPane(contentPane);
		for (int i = 0; i < labels.length; i++) {
			String time = "";
			switch (i) {
			case 0:
				if (entries.size() < 5) {
					time = "NaN";
					break;
				}
				time = toTimestamp(aoN(5));
				break;
			case 1:
				if (entries.size() < 12) {
					time = "NaN";
					break;
				}
				time = toTimestamp(aoN(12));
				break;
			case 2:
				if (entries.size() < 100) {
					time = "NaN";
					break;
				}
				time = toTimestamp(aoN(5));
				break;
			case 3:
				if (entries.size()<1) {
					time = "NaN";
					break;
				}
				time = toTimestamp(median());
				break;
			}
			labels[i] = new JLabel(strings[i] + "  " + time);
			labels[i].setFocusable(false);
			labels[i].setFont(new Font("Courier", Font.PLAIN, 24));
			labels[i].setHorizontalAlignment(SwingConstants.LEFT);
			labels[i].setVerticalAlignment(SwingConstants.TOP);
			labels[i].setBounds((int) (labelSizes[i][0] * SCRWIDTH),
					(int) (labelSizes[i][1] * SCRHEIGHT),
					(int) (labelSizes[i][2] * SCRWIDTH),
					(int) (labelSizes[i][3] * SCRHEIGHT));
			contentPane.add(labels[i]);
		}

	}

	public static int median() {
		if (entries.size() == 0)
			return -1;
		List<Integer> temp = new ArrayList<Integer>();
		for (Entry e : entries) {
			temp.add(e.getTime());
		}
		Collections.sort(temp);
		double d = temp.size();
		int median = (temp.get((int) Math.ceil(d / 2)) - 1);
		return median;
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

	private static void loadEntries() {
		loadEntries("session.db");
	}

	private static void loadEntries(String sessionname) {
		entries = new ArrayList<Entry>();
		for (String s : FileUtils.loadFile(sessionname)) {
			entries.add(new Entry(s));
		}
	}
}
