package utils;

import java.io.FileWriter;
import java.io.PrintWriter;

public class Writer {
	public void write(String path, String text){
		FileWriter f = null;
		PrintWriter pw = null;
		try {
			f = new FileWriter(path, true);
		} catch (Exception exception) {
			System.err.println("Messageexception: " + exception.getMessage());
		}
		pw = new PrintWriter(f);
		pw.println(text);
		pw.close();
	}

}
