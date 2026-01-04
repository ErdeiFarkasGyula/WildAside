package net.farkas.wildaside.dna.bioengineering_skill.category;

import net.farkas.wildaside.WildAside;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BioengineeringSkillCategory {
    private final ResourceLocation id;
    private final String name;
    private final ResourceLocation icon;

    private BioengineeringSkillCategory(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.icon = builder.icon;
    }

    public ResourceLocation getId() { return id; }
    public String getName() { return name; }
    public ResourceLocation getIcon() { return icon; }

    public Component getNameComponent() {
        return Component.translatable("skill.wildaside.category." + name);
    }

    public static class Builder {
        private ResourceLocation id;
        private String name;
        private ResourceLocation icon;

        private Builder id(ResourceLocation id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder icon(ResourceLocation icon) {
            this.icon = icon;
            return this;
        }

        public BioengineeringSkillCategory build() {
            if (name == null) {
                throw new IllegalStateException("BioengineeringSkillCategory must have a name");
            }

            if (id == null) {
                this.id = new ResourceLocation(WildAside.MOD_ID, name);
            }

            if (icon == null) {
                this.icon = new ResourceLocation(WildAside.MOD_ID, "textures/skill/category/" + name + ".png");
            }

            return new BioengineeringSkillCategory(this);
        }
    }
}
