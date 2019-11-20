package edu.utd.minecraft.mod.polycraft.worldgen;

import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.CAVE;
import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.RAVINE;
import static net.minecraftforge.event.terraingen.InitMapGenEvent.EventType.SCATTERED_FEATURE;
import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ANIMALS;
import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.DUNGEON;
import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.ICE;
import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAKE;
import static net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAVA;

import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial.State;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialManager;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.SpawnerAnimals;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.MapGenBase;
import net.minecraft.world.gen.MapGenCaves;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraft.world.gen.NoiseGenerator;
import net.minecraft.world.gen.NoiseGeneratorOctaves;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.ChunkProviderEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.TerrainGen;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
public class PolycraftChunkProvider implements IChunkProvider {

	/** RNG. */
	private Random rand;

	/** A NoiseGeneratorOctaves used in generating terrain */
	private NoiseGeneratorOctaves noiseGen1;
	private NoiseGeneratorOctaves noiseGen2;
	private NoiseGeneratorOctaves noiseGen3;
	private NoiseGeneratorPerlin noisePerl;

	/** A NoiseGeneratorOctaves used in generating terrain */
	public NoiseGeneratorOctaves noiseGen5;

	/** A NoiseGeneratorOctaves used in generating terrain */
	public NoiseGeneratorOctaves noiseGen6;
	public NoiseGeneratorOctaves mobSpawnerNoise;

	/** Reference to the World object. */
	private World worldObj;

	/** are map structures going to be generated (e.g. strongholds) */
	private final boolean mapFeaturesEnabled;

	private WorldType worldType;
	private final double[] noiseArray;
	private final float[] parabolicField;
	private double[] stoneNoise = new double[256];
	private MapGenBase caveGenerator = new MapGenCaves();

	private MapGenScatteredFeature scatteredFeatureGenerator = new MapGenScatteredFeature();

	/** Holds ravine generator */
	private MapGenBase ravineGenerator = new MapGenRavine();

	/** The biomes that are used to generate the chunk */
	private BiomeGenBase[] biomesForGeneration;

	/** A double array that hold terrain noise */
	double[] noise3;
	double[] noise1;
	double[] noise2;
	double[] noise5;
	int[][] field_73219_j = new int[32][32];

	{
		caveGenerator = TerrainGen.getModdedMapGen(caveGenerator, CAVE);
		scatteredFeatureGenerator = (MapGenScatteredFeature) TerrainGen.getModdedMapGen(scatteredFeatureGenerator, SCATTERED_FEATURE);
		ravineGenerator = TerrainGen.getModdedMapGen(ravineGenerator, RAVINE);
	}

	
	public PolycraftChunkProvider(World world, long seed, boolean mapFeaturesEnabled)
	{
		//GameLogHelper.writeToLog(Level.INFO, "Loading Chunk Provider for dmension.");
		this.worldObj = world;
		this.mapFeaturesEnabled = mapFeaturesEnabled;
		this.worldType = world.getWorldInfo().getTerrainType();
		this.rand = new Random(seed);
		this.noiseGen1 = new NoiseGeneratorOctaves(this.rand, 16);
		this.noiseGen2 = new NoiseGeneratorOctaves(this.rand, 16);
		this.noiseGen3 = new NoiseGeneratorOctaves(this.rand, 8);
		this.noisePerl = new NoiseGeneratorPerlin(this.rand, 4);
		this.noiseGen5 = new NoiseGeneratorOctaves(this.rand, 10);
		this.noiseGen6 = new NoiseGeneratorOctaves(this.rand, 16);
		this.mobSpawnerNoise = new NoiseGeneratorOctaves(this.rand, 8);
		this.noiseArray = new double[825];
		this.parabolicField = new float[25];
		for (int j = -2; j <= 2; ++j) {
			for (int k = -2; k <= 2; ++k) {
				float f = 10.0F / MathHelper.sqrt_float((float)(j * j + k * k) + 0.2F);
				this.parabolicField[j + 2 + (k + 2) * 5] = f;
			}
		}
		NoiseGenerator[] noiseGens = {noiseGen1, noiseGen2, noiseGen3, noisePerl, noiseGen5, noiseGen6, mobSpawnerNoise};
		noiseGens = TerrainGen.getModdedNoiseGenerators(world, this.rand, noiseGens);
		this.noiseGen1 = (NoiseGeneratorOctaves)noiseGens[0];
		this.noiseGen2 = (NoiseGeneratorOctaves)noiseGens[1];
		this.noiseGen3 = (NoiseGeneratorOctaves)noiseGens[2];
		this.noisePerl = (NoiseGeneratorPerlin)noiseGens[3];
		this.noiseGen5 = (NoiseGeneratorOctaves)noiseGens[4];
		this.noiseGen6 = (NoiseGeneratorOctaves)noiseGens[5];
		this.mobSpawnerNoise = (NoiseGeneratorOctaves)noiseGens[6];
	}
	
	@Override
	public boolean chunkExists(int p_73149_1_, int p_73149_2_) {
		return true;
	}
	
	@Override
	public Chunk provideChunk(int par1, int par2) {
		this.rand.setSeed((long)par1 * 341873128712L + (long)par2 * 132897987541L);
		Block[] ablock = new Block[65536];
		byte[] abyte = new byte[65536];
		//this.generateBlocksForDimEight(par1, par2, ablock);
		//this.biomesForGeneration = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(this.biomesForGeneration, par1 * 16, par2 * 16, 16, 16);
		//this.replaceBlocksForBiome(par1, par2, ablock, abyte, this.biomesForGeneration);
		//this.caveGenerator.func_151539_a(this, this.worldObj, par1, par2, ablock);
		//this.ravineGenerator.func_151539_a(this, this.worldObj, par1, par2, ablock);
		if (this.mapFeaturesEnabled) {
			//this.scatteredFeatureGenerator.func_151539_a(this, this.worldObj, par1, par2, ablock);
		}
		
//		for(ExperimentTutorial exp:TutorialManager.experiments.values()) {
//			if(exp.currentState != State.Done) {
//				if(((int)exp.pos.xCoord >> 4) <= par1 && par1 <= ((int)(exp.pos.xCoord + exp.size.xCoord) >> 4)
//						&& ((int)exp.pos.zCoord >> 4) <= par2 && par2 <= ((int)(exp.pos.zCoord + exp.size.zCoord) >> 4)) {
//					for(Chunk chunk: exp.chunks) {
//						if(chunk.xPosition == par1 && chunk.zPosition == par2)
//							return chunk;
//					}
//				}
//			}
//		}
		
		Chunk chunk = new Chunk(this.worldObj, par1, par2);
		//byte[] abyte1 = chunk.getBiomeArray();
		//for (int k = 0; k < abyte1.length; ++k){
		//	abyte1[k] = (byte)this.biomesForGeneration[k].biomeID;
		//}
		chunk.generateSkylightMap();
		return chunk;
	}
	
	@Override
	public Chunk provideChunk(BlockPos blockPos) {
		return this.provideChunk(blockPos.getX(), blockPos.getZ());
	}
	
	@Override
	public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_) {
		// TODO Auto-generated method stub
	
	}

	@Override
	public boolean func_177460_a(IChunkProvider iChunkProvider, Chunk chunk, int i, int i1) {
		return false;
	}

//	@Override
//	public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z) {
//		return false;
//	}

	@Override
	public boolean saveChunks(boolean p_73151_1_, IProgressUpdate p_73151_2_) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean unloadQueuedChunks() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean canSave() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public String makeString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
		return null;
	}

//	@Override
//	public List getPossibleCreatures(EnumCreatureType p_73155_1_, int p_73155_2_, int p_73155_3_, int p_73155_4_) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
	@Override
	public BlockPos getStrongholdGen(World p_147416_1_, String p_147416_2_,
									 BlockPos pos) {
		// TODO Auto-generated method stub
	return null;
	}
	
	@Override
	public int getLoadedChunkCount() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void recreateStructures(Chunk chunk, int p_82695_1_, int p_82695_2_) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void saveExtraData() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Generates the shape of the terrain for the chunk though its all stone
	 * though the water is frozen if the temperature is low enough
	 */
	// TODO: generateTerrain?
	@SuppressWarnings("unused")
	public void generateBlocksForDimEight(int par1, int par2, Block[] blocks) {
		
		//DONT EDIT THS METHOD UNLES YOU KNOW WHAT UR DOING OR MAKE A COPY INCASE U MESS IT UP....
		//YOU HAVE BE WARNED !!!!!
		if(true)
			return;
		byte b0 = 63;
		this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, par1 * 4 - 2, par2 * 4 - 2, 10, 10);
		this.generateTerrainArtifacts(par1 * 4, 0, par2 * 4);
		for (int k = 0; k < 4; ++k) {
			int l = k * 5;
			int i1 = (k + 1) * 5;
			for (int j1 = 0; j1 < 4; ++j1) {
				int k1 = (l + j1) * 33;
				int l1 = (l + j1 + 1) * 33;
				int i2 = (i1 + j1) * 33;
				int j2 = (i1 + j1 + 1) * 33;
				for (int k2 = 0; k2 < 32; ++k2) {
					double d0 = 0.125D;
					double d1 = this.noiseArray[k1 + k2];
					double d2 = this.noiseArray[l1 + k2];
					double d3 = this.noiseArray[i2 + k2];
					double d4 = this.noiseArray[j2 + k2];
					double d5 = (this.noiseArray[k1 + k2 + 1] - d1) * d0;
					double d6 = (this.noiseArray[l1 + k2 + 1] - d2) * d0;
					double d7 = (this.noiseArray[i2 + k2 + 1] - d3) * d0;
					double d8 = (this.noiseArray[j2 + k2 + 1] - d4) * d0;
					for (int l2 = 0; l2 < 8; ++l2) {
						double d9 = 0.25D;
						double d10 = d1;
						double d11 = d2;
						double d12 = (d3 - d1) * d9;
						double d13 = (d4 - d2) * d9;
						for (int i3 = 0; i3 < 4; ++i3) {
							int j3 = i3 + k * 4 << 12 | 0 + j1 * 4 << 8 | k2 * 8 + l2;
							short short1 = 256;
							j3 -= short1;
							double d14 = 0.25D;
							double d16 = (d11 - d10) * d14;
							double d15 = d10 - d16;
							for (int k3 = 0; k3 < 4; ++k3) {
								if ((d15 += d16) > 0.0D) {
									blocks[j3 += short1] = Blocks.stone;//these can be set to custom blocks
								} else if (k2 * 8 + l2 < b0) {
									blocks[j3 += short1] = Blocks.water;//these can be set to custom blocks
								} else {
									blocks[j3 += short1] = null;//this is the air block i think.
								}
							}
							d10 += d12;
							d11 += d13;
						}
						d1 += d5;
						d2 += d6;
						d3 += d7;
						d4 += d8;
					}
				}
			}
		}
	}
	
	/**
	 * generates a subset of the level's terrain data. Takes 7 arguments: the
	 * [empty] noise array, the position, and the size.
	 */
	// TODO: initializeNoiseField?
	@SuppressWarnings("unused")
	private void generateTerrainArtifacts(int p_147423_1_, int p_147423_2_, int p_147423_3_) {

		//DONT EDIT THS METHOD UNLES YOU KNOW WHAT UR DOING OR MAKE A COPY INCASE U MESS IT UP....
		//YOU HAVE BE WARNED !!!!!
		if(true)
			return;
		this.noise5 = this.noiseGen6.generateNoiseOctaves(this.noise5, p_147423_1_, p_147423_3_, 5, 5, 200.0D, 200.0D, 0.5D);
		this.noise3 = this.noiseGen3.generateNoiseOctaves(this.noise3, p_147423_1_, p_147423_2_, p_147423_3_, 5, 33, 5, 8.555150000000001D, 4.277575000000001D, 8.555150000000001D);
		this.noise1 = this.noiseGen1.generateNoiseOctaves(this.noise1, p_147423_1_, p_147423_2_, p_147423_3_, 5, 33, 5, 684.412D, 684.412D, 684.412D);
		this.noise2 = this.noiseGen2.generateNoiseOctaves(this.noise2, p_147423_1_, p_147423_2_, p_147423_3_, 5, 33, 5, 684.412D, 684.412D, 684.412D);
		int l = 0;
		int i1 = 0;
		for (int j1 = 0; j1 < 5; ++j1) {
			for (int k1 = 0; k1 < 5; ++k1) {
				float f = 0.0F;
				float f1 = 0.0F;
				float f2 = 0.0F;
				byte b0 = 2;
				BiomeGenBase biomegenbase = this.biomesForGeneration[j1 + 2 + (k1 + 2) * 10];
				for (int l1 = -b0; l1 <= b0; ++l1) {
					for (int i2 = -b0; i2 <= b0; ++i2) {
						BiomeGenBase biomegenbase1 = this.biomesForGeneration[j1 + l1 + 2 + (k1 + i2 + 2) * 10];
						float f3 = biomegenbase1.minHeight;
						float f4 = biomegenbase1.maxHeight;
						if (this.worldType == WorldType.AMPLIFIED && f3 > 0.0F) {
							f3 = 1.0F + f3 * 2.0F;
							f4 = 1.0F + f4 * 4.0F;
						}
						float f5 = this.parabolicField[l1 + 2 + (i2 + 2) * 5] / (f3 + 2.0F);
						if (biomegenbase1.minHeight > biomegenbase.minHeight) {
							f5 /= 2.0F;
						}
						f += f4 * f5;
						f1 += f3 * f5;
						f2 += f5;
					}
				}
				f /= f2;
				f1 /= f2;
				f = f * 0.9F + 0.1F;
				f1 = (f1 * 4.0F - 1.0F) / 8.0F;
				double d12 = this.noise5[i1] / 8000.0D;
				if (d12 < 0.0D) {
					d12 = -d12 * 0.3D;
				}
				d12 = d12 * 3.0D - 2.0D;
				if (d12 < 0.0D) {
					d12 /= 2.0D;
					if (d12 < -1.0D) {
						d12 = -1.0D;
					}
					d12 /= 1.4D;
					d12 /= 2.0D;
				} else {
					if (d12 > 1.0D) {
						d12 = 1.0D;
					}
					d12 /= 8.0D;
				}
				++i1;
				double d13 = (double)f1;
				double d14 = (double)f;
				d13 += d12 * 0.2D;
				d13 = d13 * 8.5D / 8.0D;
				double d5 = 8.5D + d13 * 4.0D;
				for (int j2 = 0; j2 < 33; ++j2) {
					double d6 = ((double)j2 - d5) * 12.0D * 128.0D / 256.0D / d14;
					if (d6 < 0.0D) {
						d6 *= 4.0D;
					}
					double d7 = this.noise1[l] / 512.0D;
					double d8 = this.noise2[l] / 512.0D;
					double d9 = (this.noise3[l] / 10.0D + 1.0D) / 2.0D;
					double d10 = MathHelper.denormalizeClamp(d7, d8, d9) - d6;
					if (j2 > 29) {
						double d11 = (double)((float)(j2 - 29) / 3.0F);
						d10 = d10 * (1.0D - d11) + -10.0D * d11;
					}
					this.noiseArray[l] = d10;
					++l;
				}
			}
		}
	}
	
	/**
     * Writes the Chunk passed as an argument to the NBTTagCompound also passed, using the World argument to retrieve
     * the Chunk's last update time.
     */
    public static void writeChunkToNBT(Chunk chunkIn, World worldIn, NBTTagCompound p_75820_3_)
    {
        p_75820_3_.setByte("V", (byte)1);
        p_75820_3_.setInteger("xPos", chunkIn.xPosition);
        p_75820_3_.setInteger("zPos", chunkIn.zPosition);
        p_75820_3_.setLong("LastUpdate", worldIn.getTotalWorldTime());
        p_75820_3_.setIntArray("HeightMap", chunkIn.getHeightMap());
        p_75820_3_.setBoolean("TerrainPopulated", chunkIn.isTerrainPopulated());
        p_75820_3_.setBoolean("LightPopulated", chunkIn.isLightPopulated());
        p_75820_3_.setLong("InhabitedTime", chunkIn.getInhabitedTime());
        ExtendedBlockStorage[] aextendedblockstorage = chunkIn.getBlockStorageArray();
        NBTTagList nbttaglist = new NBTTagList();
        boolean flag = !worldIn.provider.getHasNoSky();

        for (ExtendedBlockStorage extendedblockstorage : aextendedblockstorage)
        {
            if (extendedblockstorage != null)
            {
                NBTTagCompound nbttagcompound = new NBTTagCompound();
                nbttagcompound.setByte("Y", (byte)(extendedblockstorage.getYLocation() >> 4 & 255));
                byte[] abyte = new byte[extendedblockstorage.getData().length];
                NibbleArray nibblearray = new NibbleArray();
                NibbleArray nibblearray1 = null;

                for (int i = 0; i < extendedblockstorage.getData().length; ++i)
                {
                    char c0 = extendedblockstorage.getData()[i];
                    int j = i & 15;
                    int k = i >> 8 & 15;
                    int l = i >> 4 & 15;

                    if (c0 >> 12 != 0)
                    {
                        if (nibblearray1 == null)
                        {
                            nibblearray1 = new NibbleArray();
                        }

                        nibblearray1.set(j, k, l, c0 >> 12);
                    }

                    abyte[i] = (byte)(c0 >> 4 & 255);
                    nibblearray.set(j, k, l, c0 & 15);
                }

                nbttagcompound.setByteArray("Blocks", abyte);
                nbttagcompound.setByteArray("Data", nibblearray.getData());

                if (nibblearray1 != null)
                {
                    nbttagcompound.setByteArray("Add", nibblearray1.getData());
                }

                nbttagcompound.setByteArray("BlockLight", extendedblockstorage.getBlocklightArray().getData());

                if (flag)
                {
                    nbttagcompound.setByteArray("SkyLight", extendedblockstorage.getSkylightArray().getData());
                }
                else
                {
                    nbttagcompound.setByteArray("SkyLight", new byte[extendedblockstorage.getBlocklightArray().getData().length]);
                }

                nbttaglist.appendTag(nbttagcompound);
            }
        }

        p_75820_3_.setTag("Sections", nbttaglist);
        p_75820_3_.setByteArray("Biomes", chunkIn.getBiomeArray());
        chunkIn.setHasEntities(false);
        NBTTagList nbttaglist1 = new NBTTagList();

        for (int i1 = 0; i1 < chunkIn.getEntityLists().length; ++i1)
        {
            for (Entity entity : chunkIn.getEntityLists()[i1])
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                try
                {
                if (entity.writeToNBTOptional(nbttagcompound1))
                {
                    chunkIn.setHasEntities(true);
                    nbttaglist1.appendTag(nbttagcompound1);
                }
                }
                catch (Exception e)
                {
                    net.minecraftforge.fml.common.FMLLog.log(org.apache.logging.log4j.Level.ERROR, e,
                            "An Entity type %s has thrown an exception trying to write state. It will not persist. Report this to the mod author",
                            entity.getClass().getName());
                }
            }
        }

        p_75820_3_.setTag("Entities", nbttaglist1);
        NBTTagList nbttaglist2 = new NBTTagList();

        for (TileEntity tileentity : chunkIn.getTileEntityMap().values())
        {
            NBTTagCompound nbttagcompound2 = new NBTTagCompound();
            try
            {
            tileentity.writeToNBT(nbttagcompound2);
            nbttaglist2.appendTag(nbttagcompound2);
            }
            catch (Exception e)
            {
                net.minecraftforge.fml.common.FMLLog.log(org.apache.logging.log4j.Level.ERROR, e,
                        "A TileEntity type %s has throw an exception trying to write state. It will not persist. Report this to the mod author",
                        tileentity.getClass().getName());
            }
        }

        p_75820_3_.setTag("TileEntities", nbttaglist2);
        List<NextTickListEntry> list = worldIn.getPendingBlockUpdates(chunkIn, false);

        if (list != null)
        {
            long j1 = worldIn.getTotalWorldTime();
            NBTTagList nbttaglist3 = new NBTTagList();

            for (NextTickListEntry nextticklistentry : list)
            {
                NBTTagCompound nbttagcompound3 = new NBTTagCompound();
                ResourceLocation resourcelocation = (ResourceLocation)Block.blockRegistry.getNameForObject(nextticklistentry.getBlock());
                nbttagcompound3.setString("i", resourcelocation == null ? "" : resourcelocation.toString());
                nbttagcompound3.setInteger("x", nextticklistentry.position.getX());
                nbttagcompound3.setInteger("y", nextticklistentry.position.getY());
                nbttagcompound3.setInteger("z", nextticklistentry.position.getZ());
                nbttagcompound3.setInteger("t", (int)(nextticklistentry.scheduledTime - j1));
                nbttagcompound3.setInteger("p", nextticklistentry.priority);
                nbttaglist3.appendTag(nbttagcompound3);
            }

            p_75820_3_.setTag("TileTicks", nbttaglist3);
        }
    }

    /**
     * Reads the data stored in the passed NBTTagCompound and creates a Chunk with that data in the passed World.
     * Returns the created Chunk.
     */
    public static Chunk readChunkFromNBT(World worldIn, NBTTagCompound p_75823_2_)
    {
    	return readChunkFromNBT(worldIn, p_75823_2_, 0, 0);
    }
    
    /**
     * Reads the data stored in the passed NBTTagCompound and creates a Chunk with that data in the passed World.
     * Returns the created Chunk.
     */
    public static Chunk readChunkFromNBT(World worldIn, NBTTagCompound p_75823_2_, int xOffset, int zOffset)
    {
        int i = p_75823_2_.getInteger("xPos") + xOffset;
        int j = p_75823_2_.getInteger("zPos") + zOffset;
        Chunk chunk = new Chunk(worldIn, i, j);
        chunk.setHeightMap(p_75823_2_.getIntArray("HeightMap"));
        chunk.setTerrainPopulated(p_75823_2_.getBoolean("TerrainPopulated"));
        chunk.setLightPopulated(p_75823_2_.getBoolean("LightPopulated"));
        chunk.setInhabitedTime(p_75823_2_.getLong("InhabitedTime"));
        NBTTagList nbttaglist = p_75823_2_.getTagList("Sections", 10);
        int k = 16;
        ExtendedBlockStorage[] aextendedblockstorage = new ExtendedBlockStorage[k];
        boolean flag = !worldIn.provider.getHasNoSky();

        for (int l = 0; l < nbttaglist.tagCount(); ++l)
        {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(l);
            int i1 = nbttagcompound.getByte("Y");
            ExtendedBlockStorage extendedblockstorage = new ExtendedBlockStorage(i1 << 4, flag);
            byte[] abyte = nbttagcompound.getByteArray("Blocks");
            NibbleArray nibblearray = new NibbleArray(nbttagcompound.getByteArray("Data"));
            NibbleArray nibblearray1 = nbttagcompound.hasKey("Add", 7) ? new NibbleArray(nbttagcompound.getByteArray("Add")) : null;
            char[] achar = new char[abyte.length];

            for (int j1 = 0; j1 < achar.length; ++j1)
            {
                int k1 = j1 & 15;
                int l1 = j1 >> 8 & 15;
                int i2 = j1 >> 4 & 15;
                int j2 = nibblearray1 != null ? nibblearray1.get(k1, l1, i2) : 0;
                achar[j1] = (char)(j2 << 12 | (abyte[j1] & 255) << 4 | nibblearray.get(k1, l1, i2));
            }

            extendedblockstorage.setData(achar);
            extendedblockstorage.setBlocklightArray(new NibbleArray(nbttagcompound.getByteArray("BlockLight")));

            if (flag)
            {
                extendedblockstorage.setSkylightArray(new NibbleArray(nbttagcompound.getByteArray("SkyLight")));
            }

            extendedblockstorage.removeInvalidBlocks();
            aextendedblockstorage[i1] = extendedblockstorage;
        }

        chunk.setStorageArrays(aextendedblockstorage);

        if (p_75823_2_.hasKey("Biomes", 7))
        {
            chunk.setBiomeArray(p_75823_2_.getByteArray("Biomes"));
        }

        // End this method here and split off entity loading to another method
        return chunk;
    }

}