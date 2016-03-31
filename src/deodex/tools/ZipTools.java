/*
 *  Lordroid One Deodexer To Rule Them All
 * 
 *  Copyright 2016 Rachid Boudjelida <rachidboudjelida@gmail.com>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package deodex.tools;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;

import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;

import deodex.S;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.FileHeader;

public class ZipTools {

	/**
	 * extract an odex file from .xz file
	 * 
	 * @returns success only if an odex file was extracted
	 * @param odex
	 *            the odex file to decompress
	 * @throws IOException
	 *             well we are using IOs Exception might be thrown
	 */
	public static boolean extractOdex(File odex) throws IOException {
		File Decomdex;
		if (odex.getName().endsWith(S.ODEX_EXT)) {
			Logger.appendLog("[ZipTools][I]Decompressing  " + odex.getName() + " not needed");
			return true;
		} else if (odex.getName().endsWith(S.COMP_GZ_ODEX_EXT)) {
			Logger.appendLog("[ZipTools][I]Decompressing  " + odex.getName() + " gzip detected ...");
			return TarGzUtils.unGzipOdex(odex, odex.getParentFile());
		} else {
			Logger.appendLog("[ZipTools][I]Decompressing  " + odex.getName() + " xz compression detected ...");
			Decomdex = new File(odex.getParentFile().getAbsolutePath() + "/"
					+ StringUtils.getCropString(odex.getName(), odex.getName().length() - 3));
			Logger.appendLog(
					"[ZipTools][I]Decompressing  " + odex.getAbsolutePath() + "  to  " + Decomdex.getAbsolutePath());
			FileInputStream fin = new FileInputStream(odex);
			BufferedInputStream in = new BufferedInputStream(fin);
			FileOutputStream out = new FileOutputStream(Decomdex);
			XZCompressorInputStream xzIn = new XZCompressorInputStream(in);
			final byte[] buffer = new byte[32768];
			int n = 0;
			while (-1 != (n = xzIn.read(buffer))) {
				out.write(buffer, 0, n);
			}
			out.close();
			xzIn.close();

		}
		Logger.appendLog("[ZipTools][I]Decompressing  " + odex.getAbsolutePath() + "  to  " + Decomdex.getAbsolutePath()
				+ " success ? " + Decomdex.exists());
		return Decomdex.exists();
	}

	/**
	 * search a filename is a zip file
	 * 
	 * @param fileName
	 * @param zipFile
	 * @return returns true is a file with the same name is in the zip file !
	 */
	public static boolean isFileinZip(String fileName, ZipFile zipFile) {
		try {
			Logger.appendLog(
					"[ZipTools][I] about to search " + fileName + " in " + zipFile.getFile().getAbsolutePath());
			// Get the list of file headers from the zip file
			@SuppressWarnings("rawtypes")
			List fileHeaderList = zipFile.getFileHeaders();

			// Loop through the file headers
			// TODO why some zips throw OutOfBoundsException ? weird zips ?

			for (int i = 0; i < fileHeaderList.size(); i++) {
				FileHeader fileHeader = (FileHeader) fileHeaderList.get(i);
				String name = fileHeader.getFileName();

				if (name.length() >= fileName.length())
					if (name.contains(fileName)) {
						return true;
					}

			}

		} catch (Exception e) {
			// e.printStackTrace(); don't print the Exception can be a throwable
			// and doesn't have sush method
			Logger.appendLog("[ZipTools][EX] isFileInZip fail trying fail safe mode instead ");
			File zip = zipFile.getFile();
			try {
				return ZipTools.isFileinZipFailSafe(fileName, new java.util.zip.ZipFile(zip));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * check if the given fileName matches a file in the given zipFile without
	 * extraction ,file name can contain full paths in zip like
	 * '/path/myFile.txt
	 * 
	 * @param fileName
	 *            file name to search fot
	 * @param zipFile
	 *            the zip in which we will search for the file
	 * @return isFileFound returns true is a file matches the given file
	 */
	public static boolean isFileinZipFailSafe(String fileName, java.util.zip.ZipFile zipFile) {
		try {

			Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();

			while (zipEntries.hasMoreElements()) {

				if (zipEntries.nextElement().getName().contains(fileName)) {
					return true;
				}
			}

		} catch (Exception e) {
			Logger.appendLog("[ZipTools][EX]" + e.getStackTrace());
			return false;
		}
		return false;
	}
}
