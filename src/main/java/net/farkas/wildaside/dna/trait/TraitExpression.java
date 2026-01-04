package net.farkas.wildaside.dna.trait;

import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.allele.value.AlleleValue;

public class TraitExpression {
    public static AlleleValue evaluate(Allele a, Allele b) {
        if (a == null && b == null) return null;
        if (a == null) return b.getValueHolder();
        if (b == null) return a.getValueHolder();

        return a.getValueHolder().expressWith(b.getValueHolder(), a.getDominance(), b.getDominance());
    }
}
