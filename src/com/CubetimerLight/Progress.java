package com.CubetimerLight;

import java.util.ArrayList;
import java.util.List;

public class Progress {

	public static int xpAt(int level) {
		double d = Double.valueOf(level);
		return (int) (Math.floor((7.0 * d * d + 5165.0 * d + 8400.0) / 2262.0));
	}

	public static int getLevel() {
		List<String> l = FileUtils.loadFile("rpg/level.db");
		int out = 1;
		if (l.size() == 0) {
			setLevel(out);
			return out;
		} else {
			out = Integer.valueOf(l.get(0));
			return out;
		}
	}

	public static void setLevel(int level) {
		List<String> l = new ArrayList<String>();
		l.add(level + "");
		FileUtils.saveFile("rpg/level.db", l);
	}

	public static int getXp() {
		List<String> l = FileUtils.loadFile("rpg/xp.db");
		int out = 0;
		if (l.size() == 0) {
			setXp(out);
			return out;
		} else {
			out = Integer.valueOf(l.get(0));
			return out;
		}
	}

	public static void setXp(int xp) {
		List<String> l = new ArrayList<String>();
		l.add(xp + "");
		FileUtils.saveFile("rpg/xp.db", l);
	}

	public static void levelUp() {
		setXp(0);
		setLevel(getLevel() + 1);
		System.out.println("Levelup");
	}

	public static void addXp(int amount) {
		int xp = getXp();
		int lv = getLevel();
		System.out.println("xp: " + xp + "  level: " + lv + " to be added: "
				+ amount);
		if (xp + amount >= xpAt(lv)) {
			levelUp();
			addXp((xp + amount)-xpAt(lv));
		} else {
			setXp(xp + amount);
		}
	}
}
