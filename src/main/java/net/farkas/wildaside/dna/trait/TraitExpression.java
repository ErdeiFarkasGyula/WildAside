package net.farkas.wildaside.dna.trait;

import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.allele.value.AlleleValue;
import net.farkas.wildaside.dna.dominance.Dominance;

import static net.farkas.wildaside.dna.dominance.Dominance.*;

public class TraitExpression {
    public static AlleleValue evaluate(Allele a, Allele b) {
        if (a == null && b == null) return null;
        if (a == null) return b.getValue();
        if (b == null) return a.getValue();

        return a.getValue().expressWith(b.getValue(), a.getDominance(), b.getDominance());
    }
}
