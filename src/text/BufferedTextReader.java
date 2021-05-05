package text;

import java.io.*;

public class BufferedTextReader {

	public String readTestFile(String resourceName) {
		try {
			InputStream in = this.getClass().getResourceAsStream(resourceName);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line + "\n");
			}
			in.close();
			return builder.toString();
		} catch (Exception e) {
			System.out.println("FileError!");
		}
		return null;
	}

}
