package edu.utd.minecraft.mod.polycraft.config;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;

public class Ore extends SourcedConfig {

	public static final ConfigRegistry<Ore> registry = new ConfigRegistry<Ore>();
	
	public static final Collection<Ore> getByDescendingAbundance()
	{
		final List<Ore> oresByDescendingAbundance = Lists.newArrayList(Ore.registry.values());
		Collections.sort(oresByDescendingAbundance, new Comparator<Ore>() {
		    public int compare(Ore o1, Ore o2) {
		        return (o2.generationBlocksPerVein * o2.generationVeinsPerChunk) - (o1.generationBlocksPerVein * o1.generationVeinsPerChunk);
		    }
		});
		return oresByDescendingAbundance;
	}

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

	public List<String> PROPERTY_NAMES = ImmutableList.of("Hardness", "Resistance", "Veins per Chunk", "Blocks per Vein", "Depth Min", "Depth Max");

	@Override
	public List<String> getPropertyNames() {
		return PROPERTY_NAMES;
	}

	@Override
	public List<String> getPropertyValues() {
		return ImmutableList.of(
				PolycraftMod.numFormat.format(hardness),
				PolycraftMod.numFormat.format(resistance),
				PolycraftMod.numFormat.format(generationVeinsPerChunk),
				PolycraftMod.numFormat.format(generationBlocksPerVein),
				PolycraftMod.numFormat.format(generationStartYMin),
				PolycraftMod.numFormat.format(generationStartYMax));
	}
}
