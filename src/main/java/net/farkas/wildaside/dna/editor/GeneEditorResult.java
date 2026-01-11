package net.farkas.wildaside.dna.editor;

public class GeneEditorResult {
    private final boolean success;
    private final String message;
    private final float stressGenerated;

    private GeneEditorResult(boolean success, String message, float stressGenerated) {
        this.success = success;
        this.message = message;
        this.stressGenerated = stressGenerated;
    }

    public static GeneEditorResult success(String message, float stressGenerated) {
        return new GeneEditorResult(true, message, stressGenerated);
    }

    public static GeneEditorResult failure(String message) {
        return new GeneEditorResult(false, message, 0f);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public float getStressGenerated() {
        return stressGenerated;
    }
}