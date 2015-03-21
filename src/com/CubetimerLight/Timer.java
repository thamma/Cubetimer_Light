package com.CubetimerLight;

import java.util.TimerTask;

public class Timer {

	private static long startTime;
	private static java.util.Timer timer;
	private static boolean started;

	public static void start() {
		Timer.startTime = System.currentTimeMillis();
		timer = new java.util.Timer();
		timer.schedule(getTask(), 0, 1);
		started = true;
	}

	public static int time() {
		return (int) (System.currentTimeMillis() - startTime);
	}

	public static int stop() {
		if (started) {
			setLabelTime();
			timer.cancel();
			timer.purge();
			started = false;
			int time = time();
			return time;
		} else {
			return time();
		}
	}

	public static boolean started() {
		return started;
	}

	private static TimerTask getTask() {

		return new TimerTask() {

			@Override
			public void run() {

				setLabelTime();
			}

		};
	}

	private static void setLabelTime() {
		Main2.timeLabel.setText(Main2.toTimestamp(time()));
	}

}
