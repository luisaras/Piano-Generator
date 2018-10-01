import music.Composition;
import gen.Generator;
import gen.Individual;
import gen.Log;

public class Main {
	
	public static void main(String[] args) {
		//testBaseGeneration();
		testRandomGeneration();
		//testFeatures("Sad1", "Sad2", "Happy1");
		//testFeatures("Happy1", "Happy2", "Sad2");
		System.out.println("Success!");
    }
	
	// ==================================================================================
	// Base Tests
	// ==================================================================================
	
	public static void testConverter(String templateFile) {
		Composition templatePiece = midi.Reader.read(templateFile);
		midi.Writer.write("results/" + templateFile, templatePiece);
	}
	
	public static void testFeatures(String file1, String file2, String file3) {
		Individual template1 = new Individual(midi.Reader.read(file1));
		Individual template2 = new Individual(midi.Reader.read(file2), template1);
		Individual template3 = new Individual(midi.Reader.read(file3), template1);
		Log.save("results/" + file1, template1, template2, template3);
	}
	
	public static void testFeatures(String file) {
		Individual template = new Individual(midi.Reader.read(file));
		template.printFeatures();
	}
	
	// ==================================================================================
	// Generation Tests
	// ==================================================================================
	
	public static void testRandomGeneration() {
		for (int i = 1; i <= 2; i++) {
			Generator gen1 = getGenerator(null, "Sad" + i);
			saveResults(gen1, "Sad " + i + "/");
			Generator gen2 = getGenerator(null, "Happy" + i);
			saveResults(gen2, "Happy " + i + "/");
		}
	}
	
	public static void testBaseGeneration() {
		for (int i = 1; i <= 2; i++) {
			Generator gen1 = getGenerator("Sad" + i, "Happy" + i);
			saveResults(gen1, "Sad to Happy" + i + "/");
			Generator gen2 = getGenerator("Happy" + i, "Sad" + i);
			saveResults(gen2, "Happy to Sad " + i + "/");
		}
	}
	
	// ==================================================================================
	// Generator
	// ==================================================================================
	
	private static Generator getGenerator(String source, String target) {
		Composition templatePiece = midi.Reader.read(target);
		Generator generator = new Generator(templatePiece);
		if (source != null) {
			Composition basePiece = midi.Reader.read(source);
			if (basePiece == null) {
				throw new RuntimeException("Could not read base: " + source);
			}
			generator.initializePopulation(basePiece);
		} else {
			generator.initializePopulation();
		}
		return generator;
	}
	
	// ==================================================================================
	// Log
	// ==================================================================================
	
	private static void saveResults(Generator gen, String file) {
		file = "results/" + file;
		saveResults(gen, file, 1);
		gen.generate(99);
		saveResults(gen, file, 100);
		gen.generate(900);
		saveResults(gen, file, 1000);
		gen.generate(4000);
		saveResults(gen, file, 5000);
		//gen.generate(10000);
		//saveResults(gen, file, 15000);
	}
	
	private static void saveResults(Generator gen, String file, int i) {
		Individual best = gen.getFittest();
		Individual worst = gen.getWorst();
		Log.save(file + "Gen " + i, gen.template, best, worst);
		midi.Writer.write(file + "Gen " + i + " Best", best.piece);
		midi.Writer.write(file + "Gen " + i + " Worst", worst.piece);
	}

}
