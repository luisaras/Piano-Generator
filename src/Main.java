import music.Composition;
import gen.Features;
import gen.Generator;
import gen.Individual;
import gen.Log;
import gen.Modifier;

public class Main {
	
	public static void main(String[] args) {
		testMutation("Sad1");
		//testRandomGeneration();
		//testWeights("Happy1");
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
	
	public static void testWeights(String templateFile) {
		Features.weighted = false;
		Modifier gen = getModifier(null, templateFile);
		saveResults(gen, "Features " + templateFile + "/", 0);
		Features.weighted = true;
		gen = getModifier(null, templateFile);
		saveResults(gen, "Features " + templateFile + " (weighted)/", 0);
	}
	
	// ==================================================================================
	// Generation Tests
	// ==================================================================================
	
	public static void testRandomGeneration() {
		for (int i = 1; i <= 2; i++) {
			Modifier gen1 = getGenerator("Sad" + i);
			saveResults(gen1, "Sad" + i + "/");
			Modifier gen2 = getGenerator("Happy" + i);
			saveResults(gen2, "Happy" + i + "/");
		}
	}
	
	public static void testMutation(String name, Modifier gen) {
		gen.generate(4000);
		Individual best = gen.getFittest();
		Individual worst = gen.getWorst();
		name = "results/Generated/Sad1/" + name;
		Log.save(name, gen.template, best, worst);
		midi.Writer.write(name, best.piece);
	}
	
	public static void testMutation(String template) {
		Generator best = getGenerator(template);
		testMutation("Final", best);
		
		Generator duration05 = getGenerator(template);
		duration05.durationMutation = 0.5f;
		testMutation("Duration 0,5", duration05);
		
		Generator attack05 = getGenerator(template);
		attack05.attackMutation = 0.5f;
		testMutation("Attack 0,5", attack05);
		
		Generator accidental05 = getGenerator(template);
		accidental05.melodyAccidentalMutation = 0.5f;
		testMutation("Melody Accidental 0,5", accidental05);
		
		Generator function005 = getGenerator(template);
		function005.melodyFunctionMutation = 0.05f;
		testMutation("Melody Function 0,05", function005);
		
		Generator function05 = getGenerator(template);
		function05.melodyFunctionMutation = 0.5f;
		testMutation("Melody Function 0,5", function05);
		
		Generator octave005 = getGenerator(template);
		octave005.melodyOctaveMutation = 0.05f;
		testMutation("Melody Octave 0,05", octave005);
		
		Generator octave05 = getGenerator(template);
		octave05.melodyOctaveMutation = 0.5f;
		testMutation("Melody Octave 0,5", octave05);
		
		Generator haccidental05 = getGenerator(template);
		haccidental05.harmonyAccidentalMutation = 0.5f;
		testMutation("Harmony Accidental 0,5", haccidental05);
		
		Generator hfunction005 = getGenerator(template);
		hfunction005.harmonyFunctionMutation = 0.05f;
		testMutation("Harmony Function 0,05", hfunction005);
		
		Generator hfunction08 = getGenerator(template);
		hfunction08.harmonyFunctionMutation = 0.8f;
		testMutation("Harmony Function 0,8", hfunction08);
		
		Generator hoctave005 = getGenerator(template);
		hoctave005.harmonyOctaveMutation = 0.05f;
		testMutation("Harmony Octave 0,05", hoctave005);
		
		Generator hoctave08 = getGenerator(template);
		hoctave08.harmonyOctaveMutation = 0.8f;
		testMutation("Harmony Octave 0,8", hoctave08);
		
		Generator signature05 = getGenerator(template);
		signature05.signatureMutation = 0.5f;
		testMutation("Signature 0,5", signature05);
		
		Generator signature005 = getGenerator(template);
		signature005.signatureMutation = 0.05f;
		testMutation("Signature 0,05", signature005);
		
		Generator signature0001 = getGenerator(template);
		signature0001.signatureMutation = 0.001f;
		testMutation("Signature 0,001", signature0001);
	}
	
	// ==================================================================================
	// Modification Tests
	// ==================================================================================
	
	public static void testBaseModification() {
		for (int i = 1; i <= 2; i++) {
			Modifier gen1 = getModifier("Sad" + i, "Happy" + i);
			gen1.generate(100);
			gen1.population[gen1.populationSize - 1] = gen1.base;
			saveResults(gen1, "Sad to Happy" + i + "/", 100);
			Modifier gen2 = getModifier("Happy" + i, "Sad" + i);
			gen2.generate(100);
			gen2.population[gen2.populationSize - 1] = gen2.base;
			saveResults(gen2, "Happy to Sad " + i + "/", 100);
		}
	}
	
	public static void testModMutation() {
		Modifier best = getModifier("Sad1", "Happy1");
		testModMutation("Final", best);
		
		Modifier accidental05 = getModifier("Sad1", "Happy1");
		accidental05.harmonyAccidentalMutation = 0.5f;
		testModMutation("Accidental 0,5", accidental05);
		
		Modifier function005 = getModifier("Sad1", "Happy1");
		function005.harmonyFunctionMutation = 0.05f;
		testModMutation("Function 0,05", function005);
		
		Modifier function08 = getModifier("Sad1", "Happy1");
		function08.harmonyFunctionMutation = 0.8f;
		testModMutation("Function 0,8", function08);
		
		Modifier octave005 = getModifier("Sad1", "Happy1");
		octave005.harmonyOctaveMutation = 0.05f;
		testModMutation("Octave 0,05", octave005);
		
		Modifier octave08 = getModifier("Sad1", "Happy1");
		octave08.harmonyOctaveMutation = 0.8f;
		testModMutation("Octave 0,8", octave08);
		
		Modifier signature05 = getModifier("Sad1", "Happy1");
		signature05.signatureMutation = 0.5f;
		testModMutation("Signature 0,5", signature05);
		
		Modifier signature005 = getModifier("Sad1", "Happy1");
		signature005.signatureMutation = 0.05f;
		testModMutation("Signature 0,05", signature005);
		
		Modifier signature0001 = getModifier("Sad1", "Happy1");
		signature0001.signatureMutation = 0.001f;
		testModMutation("Signature 0,001", signature0001);
	}
	
	public static void testModMutation(String name, Modifier gen) {
		gen.generate(20);
		Individual best = gen.getFittest();
		name = "results/Modified/" + name;
		Log.save(name, gen.template, best, gen.base);
		midi.Writer.write(name, best.piece);
	}
	
	// ==================================================================================
	// Generator
	// ==================================================================================
	
	private static Generator getGenerator(String target) {
		Composition templatePiece = midi.Reader.read(target);
		Generator gen = new Generator(templatePiece);
		gen.initializePopulation();
		return gen;
	}
	
	private static Modifier getModifier(String source, String target) {
		Composition templatePiece = midi.Reader.read(target);
		Composition basePiece = midi.Reader.read(source);
		if (basePiece == null) {
			throw new RuntimeException("Could not read base: " + source);
		}
		Modifier mod = new Modifier(templatePiece);
		mod.initializePopulation(basePiece);
		return mod;
	}
	
	// ==================================================================================
	// Log
	// ==================================================================================
	
	private static void saveResults(Modifier gen, String file) {
		saveResults(gen, file, 1);
		gen.generate(99);
		saveResults(gen, file, 100);
		gen.generate(900);
		saveResults(gen, file, 1000);
		gen.generate(3000);
		saveResults(gen, file, 4000);
	}
	
	private static void saveResults(Modifier gen, String file, int i) {
		file = "results/" + file;
		Individual best = gen.getFittest();
		Individual worst = gen.getWorst();
		Log.save(file + "Gen " + i, gen.template, best, worst);
		midi.Writer.write(file + "Gen " + i + " Best", best.piece);
		midi.Writer.write(file + "Gen " + i + " Worst", worst.piece);
	}

}
