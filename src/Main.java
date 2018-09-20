import music.Composition;
import gen.Generator;
import gen.Individual;

public class Main {
	
	public static final String templateFile = "Calm";
	public static final String baseFile = null;//"Sad";
	public static final String outputFile = "tests/Result";
	
	public static void main(String[] args) {
		testGenerator();
    }
	
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
		Composition outputPiece = generator.getFittest();
		midi.Writer.write(outputFile, outputPiece);
	}
	
	public static void testGenerator() {
		Composition templatePiece = midi.Reader.read(templateFile);
		if (templatePiece == null) {
			System.out.println("Could not read template: " + templateFile);
			return;
		}
		Generator generator = new Generator(templatePiece);
		if (baseFile != null) {
			Composition basePiece = midi.Reader.read(baseFile);
			if (basePiece == null) {
				System.out.println("Could not read base: " + baseFile);
				return;
			}
			generator.initializePopulation(basePiece);
		} else {
			generator.initializePopulation();
		}
		Composition outputPiece = generator.generate();
		midi.Writer.write(outputFile, outputPiece);
	}

}
