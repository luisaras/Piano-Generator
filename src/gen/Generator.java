package gen;

public class Generator {
	
	public static final String inputFile = "Template.mid";
	public static final String outputFile = "Result.mid";
	
	// Generate the static hard-coded song.
	public Generator() {
		Composition inputPiece = midi.Reader.read(inputFile);
		if (inputPiece == null)
			System.out.println("Could not read file: " + inputFile);
		else {	
			Composition outputPiece = inputPiece; // TODO
			midi.Writer.write(outputFile, outputPiece);
		}
	}

}