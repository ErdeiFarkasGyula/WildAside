package net.farkas.wildaside.dna.expression;

import net.farkas.wildaside.dna.allele.dominance.Dominance;
import net.farkas.wildaside.dna.sequence.GeneSequence;
import net.farkas.wildaside.dna.trait.Trait;

public class GeneExpressionPair {
    private final Trait trait;
    private final GeneSequence maternal;
    private final GeneSequence paternal;

    public GeneExpressionPair(Trait trait, GeneSequence maternal, GeneSequence paternal) {
        this.trait = trait;
        this.maternal = maternal;
        this.paternal = paternal;
    }

    public float express(ExpressionContext context) {
        if (maternal == null && paternal == null) {
            return 0f; // Will use trait's default value in application
        }
        if (maternal == null) {
            return paternal.express(context);
        }
        if (paternal == null) {
            return maternal.express(context);
        }

        float maternalValue = maternal.express(context);
        float paternalValue = paternal.express(context);

        Dominance maternalDom = maternal.getDominance();
        Dominance paternalDom = paternal.getDominance();

        return resolveDominance(maternalValue, maternalDom, paternalValue, paternalDom);
    }

    private float resolveDominance(float mVal, Dominance mDom, float pVal, Dominance pDom) {
        return switch (mDom) {
            case DOMINANT -> switch (pDom) {
                case DOMINANT -> Math.max(mVal, pVal);
                case RECESSIVE, INCOMPLETE -> mVal;
                case CO_DOMINANT -> (mVal * 0.7f) + (pVal * 0.3f);
            };
            case RECESSIVE -> switch (pDom) {
                case DOMINANT -> pVal;
                case RECESSIVE -> Math.min(mVal, pVal);
                case CO_DOMINANT, INCOMPLETE -> pVal;
            };
            case CO_DOMINANT -> switch (pDom) {
                case DOMINANT -> (mVal * 0.3f) + (pVal * 0.7f);
                case RECESSIVE -> mVal;
                case CO_DOMINANT -> (mVal + pVal) / 2f;
                case INCOMPLETE -> (mVal * 0.6f) + (pVal * 0.4f);
            };
            case INCOMPLETE -> switch (pDom) {
                case DOMINANT -> pVal;
                case RECESSIVE -> mVal;
                case CO_DOMINANT -> (mVal * 0.4f) + (pVal * 0.6f);
                case INCOMPLETE -> (mVal + pVal) / 2f;
            };
        };
    }

    public Trait getTrait() {
        return trait;
    }

    public GeneSequence getMaternal() {
        return maternal;
    }

    public GeneSequence getPaternal() {
        return paternal;
    }

    public float getMaternalBaseValue() {
        return maternal != null ? maternal.calculateBaseValue() : 0f;
    }

    public float getPaternalBaseValue() {
        return paternal != null ? paternal.calculateBaseValue() : 0f;
    }
}
