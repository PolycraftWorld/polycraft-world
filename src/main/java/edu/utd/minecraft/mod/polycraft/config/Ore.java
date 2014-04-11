package edu.utd.minecraft.mod.polycraft.config;

public class Ore extends Entity {

	public static final EntityRegistry<Ore> registry = new EntityRegistry<Ore>();

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
			final int generationBlocksPerVein, final Entity smeltingEntity, final int smeltingEntitiesPerBlock, final float smeltingExperience) {
		super(type.name);
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
		this.smeltingEntityIsItem = smeltingEntity != null && smeltingEntity instanceof Ingot;
		this.smeltingEntitiesPerBlock = smeltingEntitiesPerBlock;
		this.smeltingExperience = smeltingExperience;
	}
}
