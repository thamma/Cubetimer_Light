package com.CubetimerLight;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Entry {

	private String scramble;
	private int time;
	private long date;
	private int penalty;

	public Entry (String resource) {
		this.time = Integer.parseInt(resource.split(";")[0]);
		this.date = Long.parseLong(resource.split(";")[1]);
		this.penalty = Integer.parseInt(resource.split(";")[2]);
		this.scramble = resource.split(";")[3];
	}
	
	public Entry(int arg0) {
		this.scramble = "";
		this.time = arg0;
		this.date = System.currentTimeMillis();
		this.penalty = 0;
	}

	public Entry(int arg0, String arg1) {
		this.scramble = arg1;
		this.time = arg0;
		this.date = System.currentTimeMillis();
		this.penalty = 0;
	}

	public Entry(int arg0, String arg1, int arg2) {
		this.scramble = arg1;
		this.time = arg0;
		this.penalty = arg2;
		this.date = System.currentTimeMillis();
	}

	public String getResource() {
		return this.time + ";" + this.date + ";" + this.penalty + ";"
				+ this.scramble;
	}

	public long getDate() {
		return this.date;
	}

	public String getDateString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"EEE, MMM dd yyyy, HH:mm:ss, z");
		Date date = new Date(this.date);
		return dateFormat.format(date);
	}

	public String getScramble() {
		return this.scramble;
	}

	public int getRawTime() {
		return this.time;
	}

	public String getCompact() {
		return Main2.times(12 - (this.getTimeStamp() + "").length(), ' ')
				+ this.getTimeStamp() + "   " + this.getDateString() + "  "
				+ this.getScramble();
	}

	public int getPenalty() {
		return this.penalty;
	}

	public int getTime() {
		if (this.getPenalty() < 0) {
			return -1;
			// DNF
		} else {
			return this.getRawTime() + this.getPenalty();
		}
	}

	public String getTimeStamp() {
		return Main2.toTimestamp(getTime());
	}

}
