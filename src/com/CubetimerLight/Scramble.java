package com.CubetimerLight;

import java.util.Random;

public class Scramble {

	public static String Cube3x3(int lenght) {
		String scramblechars = "";
		Random r = new Random();
		String res0 = "UFLDBR";
		String res1 = "2'";
		while (scramblechars.length() < lenght) {
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
		return scramble;
	}
}
