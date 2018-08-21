import music.Composition;
import gen.Generator;

public class Main {
	
	public static final String inputFile = "Template";
	public static final String outputFile = "Result";
	
	public static void main(String[] args) {
		Composition inputPiece = midi.Reader.read(inputFile);
		if (inputPiece == null)
			System.out.println("Could not read file: " + inputFile);
		else {	
			Generator gen = new Generator(inputPiece);
			Composition outputPiece = gen.generate();
			midi.Writer.write(outputFile, outputPiece);
		}
    }

}
