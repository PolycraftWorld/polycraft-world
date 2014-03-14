package edu.utd.minecraft.mod.polycraft.config;

public class Ore extends Entity {

	public static final EntityRegistry<Ore> registry = new EntityRegistry<Ore>();

	static {
		// element ores
		registry.register(new Ore(Element.registry.get("element_magnesium"), 3, 5, 2, 5, 0, 30, 10, 10, Ingot.registry.get("ingot_element_magnesium"), 1, .1f));
		registry.register(new Ore(Element.registry.get("element_titanium"), 3, 5, 1, 4, 0, 30, 10, 10, Ingot.registry.get("ingot_element_titanium"), 1, .1f));
		registry.register(new Ore(Element.registry.get("element_manganese"), 3, 5, 2, 5, 0, 30, 10, 10, Ingot.registry.get("ingot_element_manganese"), 1, .1f));
		registry.register(new Ore(Element.registry.get("element_cobalt"), 3, 5, 2, 5, 0, 30, 10, 10, Ingot.registry.get("ingot_element_cobalt"), 1, .1f));
		registry.register(new Ore(Element.registry.get("element_copper"), 3, 5, 3, 7, 0, 30, 10, 10, Ingot.registry.get("ingot_element_copper"), 1, .1f));
		registry.register(new Ore(Element.registry.get("element_palladium"), 3, 5, 1, 4, 0, 30, 10, 10, Ingot.registry.get("ingot_element_palladium"), 1, .1f));
		registry.register(new Ore(Element.registry.get("element_antimony"), 3, 5, 2, 5, 0, 30, 10, 10, Ingot.registry.get("ingot_element_antimony"), 1, .1f));
		registry.register(new Ore(Element.registry.get("element_platinum"), 3, 5, 1, 4, 0, 30, 10, 10, Ingot.registry.get("ingot_element_platinum"), 1, .1f));

		// mineral ores
		registry.register(new Ore(Mineral.registry.get("mineral_bauxite"), 3, 5, 2, 5, 10, 30, 10, 10, Ingot.registry.get("ingot_element_aluminum"), 1, .1f));
		registry.register(new Ore(Mineral.registry.get("mineral_tar_sand"), 3, 5, 2, 5, 0, 30, 10, 20, Compound.registry.get("compound_bitumen"), 1, .1f));
		registry.register(new Ore(Mineral.registry.get("mineral_shale"), 3, 5, 2, 5, 0, 30, 10, 20));
	}

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
