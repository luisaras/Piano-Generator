# Piano Generator

Project developed for research at Federal University of Rio Grande do Norte.
This program generates piano pieces based on a provide template, using a Genetic Algorithm.

Limitations:
* Using only diatonic scales;
* A single track for the melody (monophonic);
* A single track for the harmonic chords;
* No percussion tracks;
* Piano as the only possible instrument;
* Only one chord per measure;
* A single arpeggio pattern for all measures.

The best generated pieces are uploaded in the _chosensolution_ branch.
The template pieces used are Happy1, Happy2, Sad1 and Sad2 from the folder "templates".
Best samples generated from each template are the ones in the folders "results/Seed 0 (60)" and "results/Seed1 (60)".

## Initial Oopulation

A set of initial pieces are created with the same number of notes and measures as the template, the same scale and the same arpeggio pattern.
Chord tonics and melody notes are chosen randomly.

## Fitness Function

A set of 109 musical features from the template are extracted and compared with other piece's features. 
The fittest individuals are the most similar to the template - computed with an eucledean distance between the feature vectors.

## Genetic Operations

There is only a single crossover operation: the selection of three parents - 
one for the tempo and scale, one for the harmony chords and one for the melody.

The mutation operations are:
- **Change mode**: changes the scale mode, keeping the root key and the melodic functions of each note in the melody, using a random number from 0 to 6;
- **Change BPM**: changes a new BPM value, from 50% to 150% of the current BPM;
- **Change note start time**: moves the note start time, using a random float between the end of the previous and the next notes in the melody;
- **Change note duration**: changes the note end time, using a random float from the end of the previous note to the start of the next note in the melody;
- **Change note function**: changes the function of the note (melody or chord tonic), decreasing or increasing it by one step;
- **Change note accidental**: changes the accidental of the note (melody or chord tonic), increasing or decreasing the pitch by half a tone;
- **Change octave**: increases or decreases the all notes' (melody or chord tonics) pitches by one octave.
