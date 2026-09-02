/*
 * Cool Reader Lite
 * Copyright (C) 2026 Cool Reader Lite contributors
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation, either version 2
 * of the License, or (at your option) any later version.
 */

package org.coolreader.crengine;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

final class BundledFonts {
	private static final Logger log = L.create("bf");
	private static final String ASSET_DIRECTORY = "fonts";
	private static final String[] FONT_FILES = {
			"Vollkorn-Regular.ttf",
			"Vollkorn-Italic.ttf",
			"Vollkorn-Bold.ttf",
			"Vollkorn-BoldItalic.ttf"
	};

	private static File directory;

	private BundledFonts() {
	}

	static void install(Context context) {
		directory = new File(context.getFilesDir(), ASSET_DIRECTORY);
		if (!directory.isDirectory() && !directory.mkdirs()) {
			log.e("Cannot create bundled fonts directory");
			return;
		}
		for (String fileName : FONT_FILES) {
			try {
				installFont(context, fileName);
			} catch (IOException e) {
				log.e("Cannot install bundled font " + fileName, e);
			}
		}
	}

	static File getDirectory() {
		return directory;
	}

	private static void installFont(Context context, String fileName) throws IOException {
		File destination = new File(directory, fileName);
		try (InputStream input = context.getAssets().open(ASSET_DIRECTORY + "/" + fileName)) {
			if (destination.isFile() && destination.length() == input.available())
				return;

			File temporary = new File(directory, fileName + ".tmp");
			try (FileOutputStream output = new FileOutputStream(temporary)) {
				byte[] buffer = new byte[8192];
				int count;
				while ((count = input.read(buffer)) >= 0)
					output.write(buffer, 0, count);
			}
			if (destination.exists() && !destination.delete())
				throw new IOException("Cannot replace " + destination.getAbsolutePath());
			if (!temporary.renameTo(destination))
				throw new IOException("Cannot install " + destination.getAbsolutePath());
		}
	}
}
