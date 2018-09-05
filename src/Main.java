import music.Composition;
import gen.Generator;

public class Main {
	
	public static final String templateFile = "Happy";
	public static final String baseFile = "Sad";
	public static final String outputFile = "tests/Result";
	
	public static void main(String[] args) {
		testConverter();
    }
	
	public static void testConverter() {
		Composition templatePiece = midi.Reader.read(templateFile);
		midi.Writer.write(templateFile, templatePiece);
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
