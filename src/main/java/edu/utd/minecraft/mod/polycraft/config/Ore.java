package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class Ore extends SourcedConfig {

	public static final ConfigRegistry<Ore> registry = new ConfigRegistry<Ore>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Ore.class.getSimpleName().toLowerCase()))
			if (line.length > 0)
				registry.register(new Ore(
						line[0], //gameID
						line[1], //name
						Config.find(line[2], line[3]), //source
						Float.parseFloat(line[4]), //hardness
						Float.parseFloat(line[5]), //resistance
						Integer.parseInt(line[6]), //dropExperienceMin
						Integer.parseInt(line[7]), //dropExperienceMax
						Integer.parseInt(line[8]), //generationVeinsPerChunk
						Integer.parseInt(line[9]), //generationBlocksPerVein
						Integer.parseInt(line[10]), //generationStartYMin
						Integer.parseInt(line[11]) //generationStartYMax
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

	public Ore(final String gameID, final String name, final Config source, final float hardness, final float resistance, final int dropExperienceMin, final int dropExperienceMax,
			final int generationVeinsPerChunk, final int generationBlocksPerVein, final int generationStartYMin, final int generationStartYMax) {
		super(gameID, name, source);
		this.hardness = hardness;
		this.resistance = resistance;
		this.dropExperienceMin = dropExperienceMin;
		this.dropExperienceMax = dropExperienceMax;
		this.generationStartYMin = generationStartYMin;
		this.generationStartYMax = generationStartYMax;
		this.generationVeinsPerChunk = generationVeinsPerChunk;
		this.generationBlocksPerVein = generationBlocksPerVein;
	}

	@Override
	public ItemStack getItemStack(int size) {
		return new ItemStack(PolycraftMod.getBlock(this), size);
	}
}
