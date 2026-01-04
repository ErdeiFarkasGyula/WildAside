package net.farkas.wildaside.dna.bioengineering_skill;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.dna.bioengineering_skill.category.BioengineeringSkillCategory;
import net.farkas.wildaside.dna.bioengineering_skill.requirement.AllRequirements;
import net.farkas.wildaside.dna.bioengineering_skill.requirement.IBioengineeringSkillRequirement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class BioengineeringSkill {
    private final ResourceLocation id;
    private final String name;
    private final ResourceLocation texture;
    private final IBioengineeringSkillRequirement requirement;
    private final BioengineeringSkillCategory category;

    private BioengineeringSkill(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.texture = builder.texture;
        this.requirement = builder.requirement;
        this.category = builder.category;
    }

    public ResourceLocation getId() { return id; }
    public String getName() { return name; }
    public ResourceLocation getTexture() { return texture; }
    public IBioengineeringSkillRequirement getRequirement() { return requirement; }
    public BioengineeringSkillCategory getCategory() { return category; }

    public Component getNameComponent() {
        return Component.translatable("skill.wildaside.bioengineering_skill.name." + name);
    }

    public Component getDescriptionComponent() {
        return Component.translatable("skill.wildaside.bioengineering_skill.description." + name);
    }

    public static class Builder {
        private ResourceLocation id;
        private String name;
        private ResourceLocation texture;
        private IBioengineeringSkillRequirement requirement = new AllRequirements(List.of());
        private BioengineeringSkillCategory category;

        public ResourceLocation id(ResourceLocation id) {
            this.id = id;
            return id;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder texture(ResourceLocation texture) {
            this.texture = texture;
            return this;
        }

        public Builder requirement(IBioengineeringSkillRequirement requirement) {
            this.requirement = requirement;
            return this;
        }

        public Builder category(BioengineeringSkillCategory category) {
            this.category = category;
            return this;
        }

        public BioengineeringSkill build() {
            if (name == null) {
                if (id != null) {
                    this.name = id.getPath();
                } else {
                    throw new IllegalStateException("BioengineeringSkill must have a name");
                }
            }

            if (id == null) {
                this.id = new ResourceLocation(WildAside.MOD_ID, name);
            }

            if (texture == null) {
                this.texture = new ResourceLocation(WildAside.MOD_ID, "textures/skill/" +  name + ".png");
            }

            if (requirement == null) {
                requirement = new AllRequirements(List.of());
            }


            return new BioengineeringSkill(this);
        }
    }
}