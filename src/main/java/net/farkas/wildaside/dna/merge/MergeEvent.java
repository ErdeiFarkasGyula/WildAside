package net.farkas.wildaside.dna.merge;

import net.farkas.wildaside.dna.locus.GeneLocus;
import net.farkas.wildaside.dna.trait.Trait;

public record MergeEvent(Trait trait, GeneLocus invadingLocus, MergeOutcome outcome) {

}