/*
 * Copyright (c) Virtusa Corporation 2018. All Rights Reserved.<br><br>
 *
 * BT Innovation Hub (BTASMiHub@virtusa.com)
 */
package com.ihub.asm360.adaptor.app;

import java.io.PrintStream;

/**
 * Application banner.
 *
 */
class AppBanner {

	/** The Constant BANNER. */
	private static final String[] BANNER = { "  _ _    _       _     ", " (_) |  | |     | |    ",
			"  _| |__| |_   _| |__  ", " | |  __  | | | | '_ \\ ", " | | |  | | |_| | |_) |",
			" |_|_|  |_|\\__,_|_.__/ " };

	/**
	 * Prints the banner.
	 */
	static void printBanner() {
		final PrintStream printStream = System.out;
		for (final String lineLocal : BANNER) {
			printStream.println(lineLocal);
		}

		printStream.printf("%23s", "Feed Adaptor");
		printStream.println();
	}

}
