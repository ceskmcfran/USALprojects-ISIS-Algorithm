package utils;

import java.io.FileWriter;

public class Writer {
	public void write(String path, String text){
		FileWriter f = null;
		try {

			f = new FileWriter(path);
			f.write(text + "\n");
			f.close();

		} catch (Exception exception) {
			System.out.println("Messageexception: " + exception.getMessage());
		}
	}

}
