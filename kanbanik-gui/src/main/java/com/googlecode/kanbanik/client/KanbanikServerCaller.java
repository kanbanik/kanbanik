package com.googlecode.kanbanik.client;


public class KanbanikServerCaller {
	public KanbanikServerCaller(Runnable runnable) {
		KanbanikProgressBar.show();
		runnable.run();
	}
}
