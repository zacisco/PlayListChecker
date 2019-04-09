package org.plchecker;

public class Grouping {
	private static volatile Grouping instance;

	private Grouping() {
		
	}

	public static Grouping getInstance() {
		Grouping localInstance = instance;
		if (localInstance == null) {
			synchronized (Grouping.class) {
				localInstance = instance;
				if (localInstance == null) {
					instance = localInstance = new Grouping();
				}
			}
		}
		return localInstance;
	}

	
}
