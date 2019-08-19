package main.java;

public class Debug {
	static class TimeWaypoint {
		void terminate() {
			waypointName[index] = "End";
			waypoints[index] = System.nanoTime();
		}

		void add(long nanotime) {
			waypoints[index++] = nanotime;
		}

		void add() {
			waypointName[index] = "";
			waypoints[index++] = System.nanoTime();
			if (index > 7)
				new Exception().printStackTrace();
		}

		void add(String s) {
			waypointName[index] = s;
			waypoints[index++] = System.nanoTime();
		}

		void init() {
			index = 0;
			waypoints = new long[32];
			waypointName = new String[32];
			add();
		}

		void init(String s) {
			index = 0;
			waypoints = new long[32];
			waypointName = new String[32];
			add(s);
		}

		void difference() {
			waypoints[index] = System.nanoTime() - waypoints[index - 1];
			index++;
		}

		int index;
		long[] waypoints;
		String[] waypointName;
	}
}
