import music.Composition;
import gen.Generator;
import gen.Individual;
import gen.Log;
import gen.Modifier;

public class Main {
	
	public static void main(String[] args) {
		//testBaseGeneration();
		testRandomGeneration();
		System.out.println("Success!");
    }
	
	// ==================================================================================
	// Base Tests
	// ==================================================================================
	
	public static void testConverter(String templateFile) {
		Composition templatePiece = midi.Reader.read(templateFile);
		midi.Writer.write("results/" + templateFile, templatePiece);
	}
	
	public static void testFeatures(String templateFile) {
		Composition templatePiece = midi.Reader.read(templateFile);
		Individual individual = new Individual(templatePiece);
		individual.printFeatures();
	}
	
	// ==================================================================================
	// Generation Tests
	// ==================================================================================
	
	public static void testRandomGeneration() {
		for (int i = 1; i <= 2; i++) {
			Modifier gen1 = getGenerator(null, "Sad" + i);
			saveResults(gen1, "Sad " + i + "/");
			Modifier gen2 = getGenerator(null, "Happy" + i);
			saveResults(gen2, "Happy " + i + "/");
		}
	}
	
	public static void testBaseGeneration() {
		for (int i = 1; i <= 2; i++) {
			Modifier gen1 = getGenerator("Sad" + i, "Happy" + i);
			saveResults(gen1, "Sad to Happy" + i + "/");
			Modifier gen2 = getGenerator("Happy" + i, "Sad" + i);
			saveResults(gen2, "Happy to Sad " + i + "/");
		}
	}
	
	// ==================================================================================
	// Generator
	// ==================================================================================
	
	private static Modifier getGenerator(String source, String target) {
		Composition templatePiece = midi.Reader.read(target);
		if (source != null) {
			Composition basePiece = midi.Reader.read(source);
			if (basePiece == null) {
				throw new RuntimeException("Could not read base: " + source);
			}
			Modifier mod = new Modifier(templatePiece);
			mod.initializePopulation(basePiece);
			return mod;
		} else {
			Generator gen = new Generator(templatePiece);
			gen.initializePopulation();
			return gen;
		}
	}
	
	// ==================================================================================
	// Log
	// ==================================================================================
	
	private static void saveResults(Modifier gen, String file) {
		file = "results/" + file;
		saveResults(gen, file, 1);
		gen.generate(99);
		saveResults(gen, file, 100);
		gen.generate(900);
		saveResults(gen, file, 1000);
		gen.generate(3000);
		saveResults(gen, file, 4000);
	}
	
	private static void saveResults(Modifier gen, String file, int i) {
		Individual best = gen.getFittest();
		Individual worst = gen.getWorst();
		Log.save(file + "Gen " + i, gen.template, best, worst);
		midi.Writer.write(file + "Gen " + i + " Best", best.piece);
		midi.Writer.write(file + "Gen " + i + " Worst", worst.piece);
	}

}
