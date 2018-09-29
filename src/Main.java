import music.Composition;
import gen.Generator;
import gen.Individual;
import gen.Log;

public class Main {
	
	public static void main(String[] args) {
		testBaseGeneration();
		//testRandomGeneration("Happy1");
		//testRandomGeneration("Sad1");
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
	// Creation Tests
	// ==================================================================================
	
	public static void testRandomGeneration(String file, int i) {
		file = "results/" + file + "/";
		Composition templatePiece = midi.Reader.read(file);
		if (templatePiece == null) {
			System.out.println("Could not read template: " + file);
			return;
		}
		Generator gen = new Generator(templatePiece);
		gen.initializePopulation();
		gen.generate(i);
		Individual best = gen.getFittest();
		Individual worst = gen.getWorst();
		Log.save(file, gen.template, best, worst);
		midi.Writer.write(file + "Best", best.piece);
		midi.Writer.write(file + "Worst", worst.piece);
	}
	
	public static void testRandomGeneration(String templateFile) {
		testRandomGeneration(templateFile, 0);
	}
	
	// ==================================================================================
	// Modification Tests
	// ==================================================================================
	
	public static void testBaseGeneration() {
		for (int i = 1; i <= 3; i++) {
			Generator gen1 = getGenerator("Sad" + i, "Happy" + i);
			saveResults(gen1, "Sad to Happy" + i + "/");
			Generator gen2 = getGenerator("Happy" + i, "Sad" + i);
			saveResults(gen2, "Happy to Sad " + i + "/");
		}
		System.out.println("Success!");
	}
	
	private static void saveResults(Generator gen, String file) {
		file = "results/" + file;
		Individual best0 = gen.getFittest();
		Individual worst0 = gen.getWorst();
		Log.save(file + "Gen 1", gen.template, best0, worst0);
		midi.Writer.write(file + "Gen 1 Best", best0.piece);
		midi.Writer.write(file + "Gen 1 Worst", worst0.piece);
		gen.generate(99);
		Individual best1 = gen.getFittest();
		Individual worst1 = gen.getWorst();
		Log.save(file + "Gen 100", gen.template, best1, worst1);
		midi.Writer.write(file + "Gen 100 Best", best1.piece);
		midi.Writer.write(file + "Gen 100 Worst", worst1.piece);
		gen.generate(900);
		Individual best2 = gen.getFittest();
		Individual worst2 = gen.getWorst();
		Log.save(file + "Gen 1000", gen.template, best2, worst2);
		midi.Writer.write(file + "Gen 1000 Best", best2.piece);
		midi.Writer.write(file + "Gen 1000 Worst", worst2.piece);
	}
	
	private static Generator getGenerator(String source, String target) {
		Composition templatePiece = midi.Reader.read(target);
		if (templatePiece == null) {
			System.out.println("Could not read template: " + target);
			return null;
		}
		Generator generator = new Generator(templatePiece);
		if (source != null) {
			Composition basePiece = midi.Reader.read(source);
			if (basePiece == null) {
				System.out.println("Could not read base: " + source);
				return null;
			}
			generator.initializePopulation(basePiece);
		} else {
			generator.initializePopulation();
		}
		return generator;
	}

}
