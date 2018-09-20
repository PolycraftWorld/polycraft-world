package edu.utd.minecraft.mod.polycraft.worldgen;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManagerHell;
import net.minecraft.world.chunk.IChunkProvider;

public class PolycraftWorldProvider extends WorldProvider{

	@Override
	public String getDimensionName() {
		return "example World";
	}
	
	public void registerWorldChunkManager()
	{
		this.worldChunkMgr = new WorldChunkManagerHell(BiomeGenBase.river, 0.3F);
		this.dimensionId = ChallengeHouseDim.exampleDimensionId;
	}
	
	public IChunkProvider createChunkGenerator()
	{
		return new PolycraftChunkProvider(this.worldObj, this.worldObj.getSeed(), true);
	}
	
	/**
     * True if the player can respawn in this dimension (true = overworld, false = nether).
     */
    public boolean canRespawnHere()
    {
        return false;
    }

}