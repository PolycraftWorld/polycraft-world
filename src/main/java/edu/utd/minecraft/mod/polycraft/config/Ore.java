package edu.utd.minecraft.mod.polycraft.config;

public class Ore extends SourcedEntity {

	public static final EntityRegistry<Ore> registry = new EntityRegistry<Ore>();

	public static void registerFromConfig(final String directory, final String extension, final String delimeter) {
		for (final String[] line : readConfig(directory, Ore.class.getSimpleName().toLowerCase(), extension, delimeter))
			registry.register(new Ore(
					line[0],
					Entity.find(line[1], line[2]), //source
					Float.parseFloat(line[3]), //hardness
					Float.parseFloat(line[4]), //resistance
					Integer.parseInt(line[5]), //dropExperienceMin
					Integer.parseInt(line[6]), //dropExperienceMax
					Integer.parseInt(line[7]), //generationVeinsPerChunk
					Integer.parseInt(line[8]), //generationBlocksPerVein
					Integer.parseInt(line[9]), //generationStartYMin
					Integer.parseInt(line[10]) //generationStartYMax
			));
	}

	public final float hardness;
	public final float resistance;
	public final int dropExperienceMin;
	public final int dropExperienceMax;
	public final int generationStartYMin;
	public final int generationStartYMax;
	public final int generationVeinsPerChunk;
	public final int generationBlocksPerVein;

	public Ore(final String name, final Entity source, final float hardness, final float resistance, final int dropExperienceMin, final int dropExperienceMax,
			final int generationVeinsPerChunk, final int generationBlocksPerVein, final int generationStartYMin, final int generationStartYMax) {
		super(name, source);
		this.hardness = hardness;
		this.resistance = resistance;
		this.dropExperienceMin = dropExperienceMin;
		this.dropExperienceMax = dropExperienceMax;
		this.generationStartYMin = generationStartYMin;
		this.generationStartYMax = generationStartYMax;
		this.generationVeinsPerChunk = generationVeinsPerChunk;
		this.generationBlocksPerVein = generationBlocksPerVein;
	}
}
