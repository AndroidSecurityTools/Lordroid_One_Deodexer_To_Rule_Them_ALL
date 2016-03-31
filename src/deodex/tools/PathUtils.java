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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import deodex.Cfg;
import deodex.S;

public class PathUtils {
	/**
	 * call this method to get where are we located on the file system don't
	 * abuse on it's use call it once on every execution and save the value
	 * because this will not change
	 * 
	 * @return our current location on the fileSystem
	 */
	public static String getExcutionPath() {
		String path = "";
		try {
			path = PathUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Somthing went wrong couldn't detemine our current location !");
		}
		return path.substring(0, path.lastIndexOf("/"));
	}

	/**
	 * 
	 * @return 7z the 7z binary path of string if 7z is in the system PATH
	 */
	public static String getSevenZBinPath() {
		String SevenZ = null;
		if (Cfg.getOs().equals(S.WINDOWS)) {
			SevenZ = new File(PathUtils.getExcutionPath() + "/bins/native/7z/windows/7za").getAbsolutePath();
		} else if (Cfg.getOs().equals(S.LINUX)) {
			SevenZ = "7z";
		} else if (Cfg.getOs().equals(S.MAC)) {
			SevenZ = "7z";
		}
		return SevenZ;
	}

	/**
	 * log the call location and our location to the log file
	 */
	public static void logCallingProcessLocation() {
		File f = new File("testFile11");

		try {
			f.createNewFile();
			f.delete();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			Logger.appendLog("[PathUtils][EX]  " + e1.getStackTrace());
		}
		String calledfrom = f.getAbsolutePath().substring(0, f.getAbsolutePath().lastIndexOf(File.separator));
		Logger.appendLog("[PathUtils][I] we were called from " + calledfrom);
		Logger.appendLog("[PathUtils][I] we are located in : " + getExcutionPath());
	}
}
