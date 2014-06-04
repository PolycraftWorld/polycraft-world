package edu.utd.minecraft.mod.polycraft.config;

import net.minecraft.item.ItemStack;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class Ore extends SourcedConfig {

	public static final ConfigRegistry<Ore> registry = new ConfigRegistry<Ore>();

	public static void registerFromResource(final String directory) {
		for (final String[] line : PolycraftMod.readResourceFileDelimeted(directory, Ore.class.getSimpleName().toLowerCase()))
			if (line.length > 0) {
				int index = 0;
				registry.register(new Ore(
						PolycraftMod.getVersionNumeric(line[index++]),
						line[index++], //gameID
						line[index++], //name
						Config.find(line[index++], line[index++]), //source
						Float.parseFloat(line[index++]), //hardness
						Float.parseFloat(line[index++]), //resistance
						Integer.parseInt(line[index++]), //dropExperienceMin
						Integer.parseInt(line[index++]), //dropExperienceMax
						Integer.parseInt(line[index++]), //generationVeinsPerChunk
						Integer.parseInt(line[index++]), //generationBlocksPerVein
						Integer.parseInt(line[index++]), //generationStartYMin
						Integer.parseInt(line[index++]) //generationStartYMax
				));
			}
	}

	public final float hardness;
	public final float resistance;
	public final int dropExperienceMin;
	public final int dropExperienceMax;
	public final int generationStartYMin;
	public final int generationStartYMax;
	public final int generationVeinsPerChunk;
	public final int generationBlocksPerVein;

	public Ore(final int[] version, final String gameID, final String name, final Config source, final float hardness, final float resistance, final int dropExperienceMin, final int dropExperienceMax,
			final int generationVeinsPerChunk, final int generationBlocksPerVein, final int generationStartYMin, final int generationStartYMax) {
		super(version, gameID, name, source);
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
		return new ItemStack(PolycraftRegistry.getBlock(this), size);
	}
}
