import music.Composition;
import gen.Generator;
import gen.Individual;
import gen.Log;

public class Main {
	
	public static final String templateFile = "Sad2";
	public static final String baseFile = "Happy1";
	public static final String outputFile = "tests/Result";
	
	public static void main(String[] args) {
		testGenerator();
    }
	
	// ==================================================================================
	// Base Tests
	// ==================================================================================
	
	public static void testConverter() {
		Composition templatePiece = midi.Reader.read(templateFile);
		midi.Writer.write(templateFile, templatePiece);
	}
	
	public static void testFeatures() {
		Composition templatePiece = midi.Reader.read(templateFile);
		Individual individual = new Individual(templatePiece);
		individual.printFeatures();
	}
	
	public static void testRandomGenerator() {
		Composition templatePiece = midi.Reader.read(templateFile);
		if (templatePiece == null) {
			System.out.println("Could not read template: " + templateFile);
			return;
		}
		Generator generator = new Generator(templatePiece);
		generator.initializePopulation();
		Composition outputPiece = generator.getFittest().piece;
		midi.Writer.write(outputFile, outputPiece);
	}
	
	// ==================================================================================
	// Generator Tests
	// ==================================================================================
	
	public static void testGenerator() {
		for (int i = 1; i <= 3; i++) {
			Generator gen1 = getGenerator("Sad" + i, "Happy" + i);
			saveResults(gen1, "Sad to Happy" + i + "/");
			Generator gen2 = getGenerator("Happy" + i, "Sad" + i);
			saveResults(gen2, "Happy to Sad " + i + "/");
		}
	}
	
	private static void saveResults(Generator gen, String file) {
		file = "results/" + file;
		Individual best0 = gen.getFittest();
		Individual worst0 = gen.getWorst();
		Log.save(file + "Gen 0", gen.template, best0, worst0);
		midi.Writer.write(file + "Gen 0 Best", best0.piece);
		midi.Writer.write(file + "Gen 0 Worst", worst0.piece);
		gen.generationCount = 100;
		gen.generate();
		Individual best1 = gen.getFittest();
		Individual worst1 = gen.getWorst();
		Log.save(file + " Gen 100", gen.template, best1, worst1);
		midi.Writer.write(file + " Gen 100 Best", best1.piece);
		midi.Writer.write(file + " Gen 100 Worst", worst1.piece);
		gen.generationCount = 900;
		gen.generate();
		Individual best2 = gen.getFittest();
		Individual worst2 = gen.getWorst();
		Log.save(file + " Gen 1000", gen.template, best2, worst2);
		midi.Writer.write(file + " Gen 1000 Best", best2.piece);
		midi.Writer.write(file + " Gen 1000 Worst", worst2.piece);
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
