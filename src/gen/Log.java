package gen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

public class Log {
	
	private static DecimalFormat df2 = new DecimalFormat("#0.00");

	public static void save(String name, Individual template, Individual ind1, Individual ind2) {
		File file = new File(name + ".txt");
		file.getParentFile().mkdirs();
		try {
			FileWriter writer = new FileWriter(file);
			String tab = "\t\t\t\t\t";
			String line = "Distance: " + tab + df2.format(ind1.distance) + tab + df2.format(ind2.distance) + "\n";
			writer.write(line);
			for (int i = 0; i < ind1.features.length; i++) {
				for(int j = 0; j < ind1.features[i].length; j++) {
					double v0 = template.features[i][j];
					double v1 = ind1.features[i][j];
					double v2 = ind2.features[i][j];
					line = i + " " + j + ": " + df2.format(v0) + tab + 
							df2.format(v1) + tab + df2.format(v2) + "\n";
					writer.write(line);
				}
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void save(String name, Individual template, Individual ind1) {
		File file = new File(name + ".txt");
		file.getParentFile().mkdirs();
		try {
			FileWriter writer = new FileWriter(file);
			String tab = "\t\t\t\t\t";
			String line = "Distance: " + tab + df2.format(ind1.distance) + "\n";
			writer.write(line);
			for (int i = 0; i < ind1.features.length; i++) {
				for(int j = 0; j < ind1.features[i].length; j++) {
					double v0 = template.features[i][j];
					double v1 = ind1.features[i][j];
					line = i + " " + j + ": " + df2.format(v0) + tab + 
							df2.format(v1) + "\n";
					writer.write(line);
				}
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
