package com.CubetimerLight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortUtils {

	public static int bestAoN(List<Entry> entries, int n) {
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

	public static int best(List<Entry> entries) {
		if (entries.size() == 0)
			return -1;
		int min = -1;
		for (Entry e : entries) {
			if (min == -1 || e.getTime() < min) {
				min = e.getTime();
			}
		}
		return min;
	}

	public static List<String> getEntryResources(List<Entry> entries) {
		List<String> l = new ArrayList<String>();
		for (Entry e : entries) {
			l.add(e.getResource());
		}
		return l;
	}

	public static int median(List<Entry> entries) {
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

	public static int aoN(List<Entry> entries, int n) {
		if (entries.size() < 3 || entries.size() < n) {
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

	public static int mean(List<Entry> entries) {
		if (entries.size() == 0)
			return -1;
		int sum = 0;
		for (Entry e : entries)
			sum += e.getTime();
		return sum / entries.size();
	}
}
