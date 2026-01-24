package net.farkas.wildaside.dna.merge;

import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.trait.Trait;

public record MergeEvent(Trait trait, GeneSequence invadingSequence, MergeOutcome outcome) {

}