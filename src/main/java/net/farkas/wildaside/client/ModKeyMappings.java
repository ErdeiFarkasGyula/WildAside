package net.farkas.wildaside.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class ModKeyMappings {
    public static final KeyMapping USE_ABILITY = new KeyMapping(
            "key.wildaside.use_ability",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            "key.categories.wildaside.general"
    );
    
    public static final KeyMapping GENE_EDITOR_LOCK_SCROLL = new KeyMapping(
            "key.wildaside.gene_editor.lock_scroll",
            KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_L,
            "key.categories.wildaside.gene_editor"
    );
    
    public static final KeyMapping GENE_EDITOR_EXECUTE = new KeyMapping(
            "key.wildaside.gene_editor.execute",
            KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_E,
            "key.categories.wildaside.gene_editor"
    );

    public static final KeyMapping GENE_EDITOR_RESET_SCROLL = new KeyMapping(
            "key.wildaside.gene_editor.reset_scroll",
            KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_R,
            "key.categories.wildaside.gene_editor"
    );

    public static final KeyMapping GENE_EDITOR_RESET_CHANGES = new KeyMapping(
            "key.wildaside.gene_editor.reset_changes",
            KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_X,
            "key.categories.wildaside.gene_editor"
    );

    public static final KeyMapping GENE_EDITOR_RESET_LAYOUT = new KeyMapping(
            "key.wildaside.gene_editor.reset_layout",
            KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_T,
            "key.categories.wildaside.gene_editor"
    );
}
