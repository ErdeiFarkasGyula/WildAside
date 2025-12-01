package net.farkas.wildaside.dna.bioengineering_skill;

import net.farkas.wildaside.WildAside;
import net.farkas.wildaside.dna.bioengineering_skill.requirement.IBioengineeringSkillRequirement;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class BioengineeringSkill {
    private final ResourceLocation id;
    private final String name;
    private final ResourceLocation texture;
    private final int minValue, maxValue;
    private final List<IBioengineeringSkillRequirement> requirements;
    private final int cost;
    private final boolean oneTimeCost;
    private final BioengineeringSkill parent;
    private final BioengineeringSkillCategory category;

    private BioengineeringSkill(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.texture = builder.texture;
        this.minValue = builder.minValue;
        this.maxValue = builder.maxValue;
        this.requirements = builder.requirements;
        this.cost = builder.cost;
        this.oneTimeCost = builder.oneTimeCost;
        this.parent = builder.parent;
        this.category = builder.category;
    }

    public ResourceLocation getId() { return id; }
    public String getName() { return name; }
    public ResourceLocation getTexture() { return texture; }
    public int getMinValue() { return minValue; }
    public int getMaxValue() { return maxValue; }
    public List<IBioengineeringSkillRequirement> getRequirements() { return requirements; }
    public int getCost() { return cost; }
    public boolean isOneTimeCost() { return oneTimeCost; }
    public BioengineeringSkill getParent() { return parent; }
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
        private int minValue = 0;
        private int maxValue = 1;
        private List<IBioengineeringSkillRequirement> requirements;
        private int cost;
        private boolean oneTimeCost;
        private BioengineeringSkill parent;
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

        public Builder minValue(int minValue) {
            this.minValue = minValue;
            return this;
        }

        public Builder maxValue(int maxValue) {
            this.maxValue = maxValue;
            return this;
        }

        public Builder requirements(List<IBioengineeringSkillRequirement> requirements) {
            this.requirements = requirements;
            return this;
        }

        public Builder cost(int cost) {
            this.cost = cost;
            return this;
        }

        public Builder oneTimeCost(boolean oneTimeCost) {
            this.oneTimeCost = oneTimeCost;
            return this;
        }

        public Builder parent(BioengineeringSkill parent) {
            this.parent = parent;
            return this;
        }

        public Builder category(BioengineeringSkillCategory category) {
            this.category = category;
            return this;
        }

        public BioengineeringSkill build() {
            if (name == null) {
                throw new IllegalStateException("BioengineeringSkill must have a name");
            }

            if (id == null) {
                this.id = new ResourceLocation(WildAside.MOD_ID, name);
            }

            if (texture == null) {
                WildAside.LOGGER.warn("BioengineeringSkill {}: missing texture, trying to get it based on name.", name);
                this.texture = new ResourceLocation(WildAside.MOD_ID, "textures/skill/" +  name + ".png");
            }

            if (maxValue < minValue) {
                WildAside.LOGGER.warn("BioengineeringSkill {}: max value ({}) is less than min value ({}), correcting max value to min value + 1.", name, maxValue, minValue);
                this.maxValue = minValue + 1;
            }

            if (category == null) {
                this.category = parent.getCategory();
            }

            return new BioengineeringSkill(this);
        }
    }
}