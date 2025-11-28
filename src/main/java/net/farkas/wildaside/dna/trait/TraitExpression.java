package net.farkas.wildaside.dna.trait;

import net.farkas.wildaside.dna.allele.Allele;
import net.farkas.wildaside.dna.allele.Dominance;

import static net.farkas.wildaside.dna.allele.Dominance.*;

public class TraitExpression {
    public static float evaluate(Allele a, Allele b) {
        if (a == null && b == null) return 0f;
        if (a == null) return b.getValue();
        if (b == null) return a.getValue();

        Dominance da = a.getDominance();
        Dominance db = b.getDominance();
        float va = a.getValue();
        float vb = b.getValue();

        if (da == DOMINANT && db == DOMINANT)
            return Math.max(va, vb);

        if (da == RECESSIVE && db == RECESSIVE)
            return (va + vb) / 2f;

        if (da == CO_DOMINANT && db == CO_DOMINANT)
            return (va + vb) / 2f;

        if (da == INCOMPLETE && db == INCOMPLETE)
            return (va + vb) / 2f;

        if (da == DOMINANT && db == RECESSIVE) return va;
        if (db == DOMINANT && da == RECESSIVE) return vb;

        if (da == CO_DOMINANT && db == DOMINANT)
            return (va + 2 * vb) / 3f;
        if (db == CO_DOMINANT && da == DOMINANT)
            return (vb + 2 * va) / 3f;

        if (da == CO_DOMINANT && db == RECESSIVE)
            return (2 * va + vb) / 3f;
        if (db == CO_DOMINANT && da == RECESSIVE)
            return (2 * vb + va) / 3f;

        if (da == CO_DOMINANT && db == INCOMPLETE)
            return (va + vb) / 2f;
        if (db == CO_DOMINANT && da == INCOMPLETE)
            return (va + vb) / 2f;

        if (da == INCOMPLETE && db == RECESSIVE)
            return (2 * va + vb) / 3f;
        if (db == INCOMPLETE && da == RECESSIVE)
            return (2 * vb + va) / 3f;

        if (da == INCOMPLETE && db == DOMINANT)
            return (va + 2 * vb) / 3f;
        if (db == INCOMPLETE && da == DOMINANT)
            return (vb + 2 * va) / 3f;

        return (va + vb) / 2f;
    }
}
