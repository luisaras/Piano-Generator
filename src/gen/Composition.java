package gen;

import java.util.Random;

import jm.JMC;
import jm.music.data.Score;

public class Composition implements JMC {
	
	private static class Sample {
		public int melodyID = 0;
		public int progressionID = 0;
	}
	
	private static class Node {
		public Node[] children;
		public Sample sample;
	}
	
	private Scale scale;
	private double tempo;
	
	private Progression[] progressions;
	private Melody[] melodies;
	private Node structure;
	
	private void init(Progression prog, Melody melody) {
		progressions = new Progression[] { prog };
		melodies = new Melody[] { melody };
		structure = new Node();
		structure.children = new Node[] {};
		structure.sample = new Sample();
	}
	
	// ==================================================================================
	// Default
	// ==================================================================================
	
	private Melody defaultMelody() {
		Melody melody = new Melody();
		// Seed
		melody.noteCountSeed = 0;
		melody.notePosSeed = 0;
		melody.notePitchSeed = 0;
		// Parameters
		melody.notesPerCrotchet = 4;
		melody.displacementFreq = 0.2; 
		melody.restFreq = 0.2;
		return melody;
	}
	
	private Progression defaultProgression() {
		int chordLen = 4;
		Progression prog = new Progression("Isus2-vi-IV7-V", scale, chordLen);
		//String[] arpeggio = new String[] { "01010101", "02020202" };
		//for (int i = 0; i < progression.chords.length; i++)
		//	progression.chords[i].setArpeggio(arpeggio);
		return prog;
	}
	
	public Composition() {
		scale = new Scale(0, c4);
		tempo = 90;
		init(defaultProgression(), defaultMelody());
	}
	
	// ==================================================================================
	// Template
	// ==================================================================================
	
	public Composition(Score template, int chordLen) throws Exception {
		scale = Analysis.deduceScale(template.getPart(0));
		tempo = template.getTempo();
		init(Analysis.deduceProgression(template.getPart(0), scale, chordLen),
				defaultMelody()); // TODO: analyze melody
	}
	
	// ==================================================================================
	// Random
	// ==================================================================================
	
	private Melody randomMelody() {
		Random rand = new Random(0);
		Melody melody = new Melody();
		// Seed
		melody.noteCountSeed = rand.nextInt();
		melody.notePosSeed = rand.nextInt();
		melody.notePitchSeed = rand.nextInt();
		// Parameters
		melody.notesPerCrotchet = rand.nextInt(4) + 2;
		melody.displacementFreq = rand.nextDouble() * 0.5; 
		melody.restFreq = rand.nextDouble() * 0.5;
		return melody;
	}
	
	public Composition(int len) {
		// Seeds.
		Random progRand = new Random(30);
		Random scaleRand = new Random(24);
		Random rythmRand = new Random(10);
		// Rythm.
		int numChords = rythmRand.nextInt(2) == 1 ? 4 : 8;
		int chordLen = (len * 4) / numChords;
		// Chords.
		String str = Chord.toRomanNumber(progRand.nextInt(7));
		for (int i = 0; i < numChords - 1; i++)
			str += "-" + Chord.toRomanNumber(progRand.nextInt(7));
		// Scale.
		//str = "I-VI-IV-V";
		int pattern = Math.abs(scaleRand.nextInt()) % Scale.patterns.length;
		int root = scaleRand.nextInt(48) + 48;
		
		scale = new Scale(pattern, root);
		tempo = rythmRand.nextInt(4) * 15 + 60;
		init(new Progression(str, scale, chordLen), randomMelody());
	}
	
	// ==================================================================================
	// Render
	// ==================================================================================
	
	private void addNode(Score score, Node node) {
		if (node.sample == null) {
			for (int i = 0; i < node.children.length; i++)
				addNode(score, node.children[i]);
		} else {
			Progression prog = progressions[node.sample.progressionID];
			Melody melody = melodies[node.sample.melodyID];
			score.add(prog.asPart("Harmony - Piano", 0, 0));
			score.add(melody.asPart("Melody - Piano", 0, 1, prog));
			System.out.println(prog.toString());
		}
	}
	
	public Score asScore(String name) {
		System.out.println("Created score: " + scale.toString());
		Score score = new Score(name);
		score.setTitle(name);
		score.setTempo(tempo);
		addNode(score, structure);
		return score;
	}
	
}
