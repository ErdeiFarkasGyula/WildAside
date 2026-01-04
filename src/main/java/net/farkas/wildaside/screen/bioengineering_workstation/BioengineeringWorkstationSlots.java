package net.farkas.wildaside.screen.bioengineering_workstation;

import java.util.Set;

public class BioengineeringWorkstationSlots {
    public static final int ASSE_INPUT_1 = 0;
    public static final int ASSE_INPUT_2 = 1;
    public static final int ASSE_INPUT_3 = 2;
    public static final int ASSE_INPUT_4 = 3;
    public static final int ASSE_INPUT_5 = 4;
    public static final int ASSE_OUTPUT_1 = 5;

    public static final int ANA_INPUT_1 = 6;
    public static final int ANA_INPUT_2 = 7;
    public static final int ANA_INPUT_3 = 8;
    public static final int ANA_OUTPUT_1 = 9;

    public static final int EDITOR_INPUT_1 = 10;
    public static final int EDITOR_INPUT_2 = 11;
    public static final int EDITOR_OUTPUT_1 = 12;
    public static final int EDITOR_OUTPUT_2 = 13;

    public static final int EDITOR_GENE_COUNT = 13;
    public static final int EDITOR_TOP_GENE_START_INDEX = 14;
    public static final int EDITOR_BOTTOM_GENE_START_INDEX = EDITOR_TOP_GENE_START_INDEX + EDITOR_GENE_COUNT;

    public static final Set<Integer> ASSEMBLER_INPUTS = Set.of(ASSE_INPUT_1, ASSE_INPUT_2, ASSE_INPUT_3, ASSE_INPUT_4, ASSE_INPUT_5);
    public static final Set<Integer> ANALYSER_INPUTS = Set.of(ANA_INPUT_1, ANA_INPUT_2, ANA_INPUT_3);
    public static final Set<Integer> OUTPUTS = Set.of(ASSE_OUTPUT_1, ANA_OUTPUT_1, EDITOR_OUTPUT_1, EDITOR_OUTPUT_2);
    public static final Set<Integer> OUTPUTS_WITHOUT_EDITOR = Set.of(ASSE_OUTPUT_1, ANA_OUTPUT_1, EDITOR_OUTPUT_1, EDITOR_OUTPUT_2);
}
