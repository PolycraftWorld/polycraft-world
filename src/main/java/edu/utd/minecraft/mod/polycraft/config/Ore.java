package edu.utd.minecraft.mod.polycraft.config;

public class Ore extends Entity {

	public static final EntityRegistry<Ore> registry = new EntityRegistry<Ore>();

	// element ores
	public static final Ore magnesium = registry.register(new Ore(Element.Magnesium, 3, 5, 2, 5, 0, 30, 10, 10, Ingot.magnesium, 1, .1f));
	public static final Ore titanium = registry.register(new Ore(Element.Titanium, 3, 5, 1, 4, 0, 30, 10, 10, Ingot.titanium, 1, .1f));
	public static final Ore manganese = registry.register(new Ore(Element.Manganese, 3, 5, 2, 5, 0, 30, 10, 10, Ingot.manganese, 1, .1f));
	public static final Ore cobalt = registry.register(new Ore(Element.Cobalt, 3, 5, 2, 5, 0, 30, 10, 10, Ingot.cobalt, 1, .1f));
	public static final Ore copper = registry.register(new Ore(Element.Copper, 3, 5, 3, 7, 0, 30, 10, 10, Ingot.copper, 1, .1f));
	public static final Ore palladium = registry.register(new Ore(Element.Palladium, 3, 5, 1, 4, 0, 30, 10, 10, Ingot.palladium, 1, .1f));
	public static final Ore antimony = registry.register(new Ore(Element.Antimony, 3, 5, 2, 5, 0, 30, 10, 10, Ingot.antimony, 1, .1f));
	public static final Ore platinum = registry.register(new Ore(Element.Platinum, 3, 5, 1, 4, 0, 30, 10, 10, Ingot.platinum, 1, .1f));
	// mineral ores
	public static final Ore bauxite = registry.register(new Ore(Mineral.bauxite, 3, 5, 2, 5, 10, 30, 10, 10, Ingot.aluminum, 1, .1f));
	public static final Ore tarSand = registry.register(new Ore(Mineral.tarSand, 3, 5, 2, 5, 0, 30, 10, 20, Compound.bitumen, 1, .1f));
	public static final Ore shale = registry.register(new Ore(Mineral.shale, 3, 5, 2, 5, 0, 30, 10, 20));
	public static final Ore graphite = registry.register(new Ore(Mineral.graphite, 3, 5, 2, 5, 10, 30, 10, 10, Ingot.carbon, 1, .1f));

	public final Entity type;
	public final float hardness;
	public final float resistance;
	public final int dropExperienceMin;
	public final int dropExperienceMax;
	public final int generationStartYMin;
	public final int generationStartYMax;
	public final int generationVeinsPerChunk;
	public final int generationBlocksPerVein;
	public final boolean smeltingEntityIsItem;
	public final Entity smeltingEntity;
	public final int smeltingEntitiesPerBlock;
	public final float smeltingExperience;

	public Ore(final Entity type, final float hardness, final float resistance, final int dropExperienceMin, final int dropExperienceMax, final int generationStartYMin, final int generationStartYMax, final int generationVeinsPerChunk,
			final int generationBlocksPerVein) {
		this(type, hardness, resistance, dropExperienceMin, dropExperienceMax, generationStartYMin, generationStartYMax, generationVeinsPerChunk, generationBlocksPerVein, null, 0, 0);
	}

	public Ore(final Entity type, final float hardness, final float resistance, final int dropExperienceMin, final int dropExperienceMax, final int generationStartYMin, final int generationStartYMax, final int generationVeinsPerChunk,
			final int generationBlocksPerVein, final Entity smeltingEntity, final int smeltingEntitiesPerBlock, final float smeltingExperience) {
		super("ore_" + type.gameName, type.name);
		this.type = type;
		this.hardness = hardness;
		this.resistance = resistance;
		this.dropExperienceMin = dropExperienceMin;
		this.dropExperienceMax = dropExperienceMax;
		this.generationStartYMin = generationStartYMin;
		this.generationStartYMax = generationStartYMax;
		this.generationVeinsPerChunk = generationVeinsPerChunk;
		this.generationBlocksPerVein = generationBlocksPerVein;
		this.smeltingEntity = smeltingEntity;
		this.smeltingEntityIsItem = smeltingEntity instanceof Ingot;
		this.smeltingEntitiesPerBlock = smeltingEntitiesPerBlock;
		this.smeltingExperience = smeltingExperience;
	}
}
