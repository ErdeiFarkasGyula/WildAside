# Chromosome-Based DNA System

## Overview

This is a major redesign of the WildAside DNA system to be more biologically accurate with complex gene regulation. The new system uses a chromosome-based architecture with maternal and paternal gene expression.

## Architecture

```
Genome
├── Maternal ChromosomeSet
│   ├── Chromosome (CORE)
│   │   ├── GeneSequence (ATTACK_DAMAGE)
│   │   │   ├── CodingRegion "base" → 8.5
│   │   │   ├── CodingRegion "scaling" → 1.2
│   │   │   ├── Activator "damage_activator"
│   │   │   └── Regulator "damage_cap"
│   │   └── GeneSequence (MAX_HEALTH)
│   │       └── ...
│   ├── Chromosome (RESISTANCE)
│   │   └── GeneSequence (FIRE_RESISTANCE)
│   │       └── ...
│   └── ... (BEHAVIOR, APPEARANCE, SPECIAL)
│
└── Paternal ChromosomeSet
    └── ... (mirrors maternal structure)
```

## Key Components

### 1. Genome
- Contains maternal and paternal chromosome sets
- Resolves gene expression by combining maternal/paternal alleles
- Applies dominance rules to determine final trait values

### 2. ChromosomeSet & Chromosome
- Organizes genes by chromosome type (CORE, RESISTANCE, BEHAVIOR, APPEARANCE, SPECIAL)
- Each chromosome holds multiple gene sequences
- Maternal and paternal chromosomes can have different sequences

### 3. GeneSequence
The heart of the system - represents a complete gene with:

#### Coding Regions
- Define the base value(s) of a gene
- Can combine using different methods (ADD, MULTIPLY, SET, MAX, MIN)
- Example: `base: 0.3` + `bonus: 0.1` = 0.4

#### Activators
- Control whether a gene is "turned on"
- Based on environmental/entity conditions
- Example: Only active when entity is on fire

#### Enhancers
- Boost gene expression when conditions are met
- Apply multipliers and flat bonuses
- Example: `×1.5 +0.1` when in the Nether

#### Silencers
- Suppress gene expression when conditions are met
- Apply multipliers and flat penalties
- Example: `×0.5 -0.1` when in water

#### Regulators
- Provide final regulation of expressed values
- Can cap min/max, clamp, round, or scale by health
- Example: Cap max value at 1.0

### 4. ExpressionContext
- Provides environmental and entity state information
- Used by activators, enhancers, silencers to determine conditions
- Includes: health, location (Nether/End), time (day/night), state (on fire, in water, sprinting, in combat)

### 5. GeneExpressionPair
- Combines maternal and paternal gene sequences
- Applies dominance rules (DOMINANT, RECESSIVE, CO_DOMINANT, INCOMPLETE)
- Returns final expressed value for a trait

## Dominance Rules

```java
DOMINANT × DOMINANT → Math.max(maternal, paternal)
DOMINANT × RECESSIVE → dominant value
CO_DOMINANT × CO_DOMINANT → (maternal + paternal) / 2
CO_DOMINANT × DOMINANT → weighted average (30% co-dom, 70% dom)
INCOMPLETE × INCOMPLETE → simple average
```

## Example: Fire Resistance Gene

```java
GeneSequence fireResistance = GeneSequence.builder()
    .trait(Traits.FIRE_RESISTANCE)
    .dominance(Dominance.CO_DOMINANT)
    .mutationRate(0.05f)
    .stability(1.0f)
    .source(GeneSource.NATURAL)
    
    // Base value: 0.3 + 0.1 = 0.4
    .codingRegion(new CodingRegion("base", 0.3f, CombineMethod.SET))
    .codingRegion(new CodingRegion("bonus", 0.1f, CombineMethod.ADD))
    
    // Always active
    .activator(new Activator("heat_sense", ActivationCondition.ALWAYS, 0f))
    
    // Boosted in Nether: 0.4 × 1.5 + 0.1 = 0.7
    .enhancer(new Enhancer("nether_adaptation", ActivationCondition.IN_NETHER, 1.5f, 0.1f))
    // Boosted when on fire: 0.7 × 1.2 = 0.84
    .enhancer(new Enhancer("fire_exposure", ActivationCondition.ON_FIRE, 1.2f, 0f))
    
    // Suppressed in water: 0.4 × 0.5 - 0.1 = 0.1
    .silencer(new Silencer("water_weakness", ActivationCondition.IN_WATER, 0.5f, 0.1f))
    
    // Clamped between 0.0 and 1.0
    .regulator(new Regulator("cap", RegulationType.CAP_MAX, 1.0f))
    .regulator(new Regulator("floor", RegulationType.CAP_MIN, 0.0f))
    
    .build();
```

## Usage Example

```java
// 1. Create gene sequences
GeneSequence maternalFireRes = GeneSequenceExamples.createFireResistanceExample();
GeneSequence paternalFireRes = GeneSequenceExamples.createFireResistanceExample();

// 2. Build genome
Genome genome = GenomeBuilder.forEntityType(EntityType.WOLF)
    .addMaternalSequence(TraitRegistry.FIRE_RESISTANCE, maternalFireRes)
    .addPaternalSequence(TraitRegistry.FIRE_RESISTANCE, paternalFireRes)
    .build();

// 3. Express genes with context
ExpressionContext context = new ExpressionContext(entity);
float fireResValue = genome.getExpressedValue(TraitRegistry.FIRE_RESISTANCE, context);

// 4. Apply to entity (handled by DnaImplementation)
trait.applyRaw(entity, fireResValue);
```

## Integration with Existing System

The new system maintains **backward compatibility** with the existing loci-based system:

- `IDna` interface extended with `getGenome()` and `setGenome()`
- `DnaImplementation` checks for genome sequences first, falls back to loci
- Legacy methods marked with `@Deprecated` but still functional
- NBT serialization supports both old and new formats

## Migration Path

1. **Current State**: Old saves use loci-based system
2. **Transition**: New genomes can coexist with old loci
3. **Future**: Breeding produces genome-based offspring
4. **Eventually**: All entities migrate to new system

## Benefits

1. **Biologically Accurate**: Mimics real gene regulation
2. **Environmentally Responsive**: Traits change based on conditions
3. **Rich Genetics**: Complex inheritance patterns
4. **Extensible**: Easy to add new regulatory components
5. **Performant**: Expression calculated on-demand with context
6. **Backward Compatible**: Doesn't break existing saves

## Files Structure

```
dna/
├── chromosome/
│   ├── ChromosomeType.java       # Enum: CORE, RESISTANCE, BEHAVIOR, APPEARANCE, SPECIAL
│   ├── Chromosome.java           # Holds gene sequences for one chromosome
│   ├── ChromosomeSet.java        # Collection of chromosomes
│   ├── Genome.java               # Main genome class (maternal + paternal)
│   ├── GenomeBuilder.java        # Builder utility for genomes
│   └── GenomeExample.java        # Complete usage example
├── sequence/
│   ├── GeneSequence.java         # Complete gene with regulatory components
│   ├── GeneSequenceExamples.java # Pre-built example sequences
│   ├── CodingRegion.java         # Defines base gene values
│   ├── CombineMethod.java        # How coding regions combine
│   └── GeneSource.java           # Origin of gene (natural, bred, engineered, etc.)
├── expression/
│   ├── ExpressionContext.java    # Environmental/entity state
│   ├── GeneExpressionPair.java   # Combines maternal/paternal with dominance
│   ├── Activator.java            # Turns genes on/off
│   ├── ActivationCondition.java  # When activators trigger
│   ├── Enhancer.java             # Boosts expression
│   ├── Silencer.java             # Suppresses expression
│   ├── Regulator.java            # Final value regulation
│   └── RegulationType.java       # Types of regulation
└── capability/dna/
    ├── IDna.java                 # Updated interface with genome support
    └── DnaImplementation.java    # Updated implementation with genome integration
```

## Next Steps

- Create breeding mechanics that use chromosome crossover
- Implement mutation system based on gene stability/mutation rates
- Add gene editor UI for manipulating gene sequences
- Create visualization for chromosome structure
- Add more activation conditions and regulation types
- Implement genetic algorithms for procedural gene generation
