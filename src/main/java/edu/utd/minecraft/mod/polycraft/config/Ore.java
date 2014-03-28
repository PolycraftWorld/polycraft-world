package edu.utd.minecraft.mod.polycraft.config;

public class Ore extends Entity {

	public static final EntityRegistry<Ore> registry = new EntityRegistry<Ore>();

	// element ores
	public static final Ore magnesium = registry.register(new Ore(Element.magnesium, 3, 5, 2, 5, 0, 30, 10, 10, Ingot.magnesium, 1, .1f));
	public static final Ore titanium = registry.register(new Ore(Element.titanium, 3, 5, 1, 4, 0, 30, 10, 10, Ingot.titanium, 1, .1f));
	public static final Ore manganese = registry.register(new Ore(Element.manganese, 3, 5, 2, 5, 0, 30, 10, 10, Ingot.manganese, 1, .1f));
	public static final Ore cobalt = registry.register(new Ore(Element.cobalt, 3, 5, 2, 5, 0, 30, 10, 10, Ingot.cobalt, 1, .1f));
	public static final Ore copper = registry.register(new Ore(Element.copper, 3, 5, 3, 7, 0, 30, 10, 10, Ingot.copper, 1, .1f));
	public static final Ore palladium = registry.register(new Ore(Element.palladium, 3, 5, 1, 4, 0, 30, 10, 10, Ingot.palladium, 1, .1f));
	public static final Ore antimony = registry.register(new Ore(Element.antimony, 3, 5, 2, 5, 0, 30, 10, 10, Ingot.antimony, 1, .1f));
	public static final Ore platinum = registry.register(new Ore(Element.platinum, 3, 5, 1, 4, 0, 30, 10, 10, Ingot.platinum, 1, .1f));
	// mineral ores
	public static final Ore bauxite = registry.register(new Ore(Mineral.bauxite, 3, 5, 2, 5, 10, 30, 10, 10, Ingot.aluminium, 1, .1f));
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

	@Override
	public String export(final String delimiter) {
		final String export = String.format("%2$s%1$s%3$s.%4$s%1$s%5$s%1$s%6$s%1$s%7$s%1$s%8$s%1$s%9$s%1$s%10$s%1$s%11$s%1$s%12$s",
				delimiter, super.export(delimiter), type.getClass().getSimpleName(), Entity.getVariableName(type.name), hardness, resistance,
				dropExperienceMin, dropExperienceMax, generationStartYMin, generationStartYMax, generationVeinsPerChunk, generationBlocksPerVein);
		if (smeltingEntity != null)
			return String.format("%2$s%1$s%3$s.%4$s%1$s%5$s%1$s%6$s", delimiter, export,
					smeltingEntity.getClass().getSimpleName(), smeltingEntity.name.toLowerCase(), smeltingEntitiesPerBlock, smeltingExperience);
		return export;
	}

	public static String generate(final String[] entity) {
		String[] params = null;
		if (entity.length == 10)
			params = new String[] { entity[1], entity[2] + "f", entity[3] + "f", entity[4], entity[5], entity[6], entity[7], entity[8], entity[9] };
		else
			params = new String[] { entity[1], entity[2] + "f", entity[3] + "f", entity[4], entity[5], entity[6], entity[7], entity[8], entity[9], entity[10], entity[11], entity[12] + "f" };
		return Entity.generate(Ore.class.getSimpleName(), getVariableName(entity[0]), params);
	}
}
