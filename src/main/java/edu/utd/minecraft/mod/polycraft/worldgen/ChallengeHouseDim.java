package edu.utd.minecraft.mod.polycraft.worldgen;

import net.minecraftforge.common.DimensionManager;

public class ChallengeHouseDim {

	public static int exampleDimensionId = 8;
	public static int exampleDimensionType = 7;

	public static void init() {
	DimensionManager.registerProviderType(exampleDimensionType, PolycraftWorldProvider.class, false);
	DimensionManager.registerDimension(exampleDimensionId, exampleDimensionType);
	}
	
}
