package edu.utd.minecraft.mod.polycraft.worldgen;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.layer.GenLayer;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class GenLayerAddOilDesert extends GenLayerBiomeReplacer {

	protected static final double NOISE_FIELD_SCALE = 0.001;
	protected static final double NOISE_FIELD_THRESHOLD = 0.7;

	public GenLayerAddOilDesert(final long worldSeed, final long seed, final GenLayer parent) {
		super(worldSeed, seed, parent, NOISE_FIELD_SCALE, NOISE_FIELD_THRESHOLD, PolycraftMod.biomeOilDesert.biomeID);
	}

	@Override
	protected boolean canReplaceBiome(int biomeId) {
		return biomeId == BiomeGenBase.desert.biomeID;
	}
}
