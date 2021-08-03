package de.dragon.UsefulThings;

import de.dragon.UsefulThings.dir.DeleteOnExitReqCall;
import de.dragon.UsefulThings.misc.DebugPrinter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Part of UsefulThings project
 *
 * @author Dragon777/Darkness4191
 **/

public class ut {

	public static String getInfoFromAppdata(String folder, String filename) {
        String os = System.getProperty("os.name");
        String separator = System.getProperty("file.separator");
        String path = "";

        if(os.toLowerCase().contains("windows")) {
            path = System.getenv("APPDATA");
        } else {
            path = System.getProperty("user.home");
        }

        if(!new File(path + separator + folder).isDirectory()) {
            new File(path + separator + folder).mkdir();
        }

        String s = "";

        if(Files.exists(Paths.get(path + separator + folder + separator + filename + ".save"))) {
            try {
                InputStreamReader reader = new InputStreamReader(new FileInputStream(path + separator + folder + separator + filename + ".save"), "ISO-8859-1");
                while(true) {
                    int i = reader.read();
                    if(i == -1) {
                        break;
                    } else {
                        s += (char) i;
                    }
                }
                reader.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }

        return s;
	}

	public static void saveInfoToAppdata(String folder, String filename, String data) throws IOException {
	    String os = System.getProperty("os.name");
	    String separator = System.getProperty("file.separator");
	    String path = "";

	    if(os.toLowerCase().contains("windows")) {
	        path = System.getenv("APPDATA");
        } else {
	        path = System.getProperty("user.home");
        }

	    if(!new File(path + separator + folder).isDirectory()) {
	        new File(path + separator + folder).mkdir();
	    }
	    OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path + separator + folder + separator + (filename.contains("[.]") ? filename : filename + ".save")), "ISO-8859-1");
	    for(char c : data.toCharArray()) {
	        writer.write(c);
	    }
	    writer.close();
	}

	public static File createTempFile(String folder, String filename, boolean isDirectory) throws IOException {
		String os = System.getProperty("os.name");
		String separator = System.getProperty("file.separator");
		String path = "";

		if(os.toLowerCase().contains("windows")) {
			path = System.getenv("APPDATA");
		} else {
			path = System.getProperty("user.home");
		}

		if(!new File(path + separator + folder).exists()) {
			new File(path + separator + folder).mkdir();
		}

		File f = new File(path + separator + folder + separator + filename);
		DeleteOnExitReqCall.add(f);

		if(isDirectory) {
			f.mkdir();
		} else {
			f.createNewFile();
		}

		return f;
	}

	public static File createAbsolutTempFile(String path, boolean isDirectory) throws IOException {
		File f = new File(path);

		if(!f.exists()) {
			DeleteOnExitReqCall.add(f);
		}

		if(isDirectory) {
			f.mkdirs();
		} else {
			f.createNewFile();
		}

		return f;
	}

	public static File getTempFile(String folder, String filename) throws IOException {
		String os = System.getProperty("os.name");
		String separator = System.getProperty("file.separator");
		String path = "";

		if(os.toLowerCase().contains("windows")) {
			path = System.getenv("APPDATA");
		} else {
			path = System.getProperty("user.home");
		}

		return new File(path + separator + folder + separator + filename);
	}

	public static void deleteFileRec(File f) {
		if(f.exists() && f.isDirectory()) {
			for(File c : f.listFiles()) {
				deleteFileRec(c);
			}
		}

		if(f.exists()) {
			DebugPrinter.println(String.format("Deleted %s %s", f.isDirectory() ? "folder" : "file", f.getAbsolutePath()));
			f.delete();
		}
	}

	public static String merge(String[] array, String seperator) {
		String merge = "";
		for(String s : array) {
			merge += merge.equals("") ? s : seperator + s;
		}
		return merge;
	}
}
