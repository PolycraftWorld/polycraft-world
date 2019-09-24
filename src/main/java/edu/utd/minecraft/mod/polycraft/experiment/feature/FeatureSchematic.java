package edu.utd.minecraft.mod.polycraft.experiment.feature;

import java.util.ArrayList;
import java.util.Random;

import edu.utd.minecraft.mod.polycraft.entity.entityliving.ResearchAssistantEntity;
import edu.utd.minecraft.mod.polycraft.experiment.old.ExperimentFlatCTB;
import edu.utd.minecraft.mod.polycraft.inventory.InventoryHelper;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.inventory.fueledlamp.FueledLampInventory;
import edu.utd.minecraft.mod.polycraft.schematic.Schematic;
import edu.utd.minecraft.mod.polycraft.worldgen.ResearchAssistantLabGenerator;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;

public class FeatureSchematic extends ExperimentFeature {
	
	protected Schematic schematic;
//	protected int genTick;
	public ArrayList<ExperimentFeature> spawnPoints;
	
	private Random random;
	ResearchAssistantEntity dummy;
	
	
	public FeatureSchematic(String schematic) {
		super(0, 16, 0);
		//this.genTick = 0;
		spawnPoints = new ArrayList<>();
		random = new Random();
		//dummy = new ResearchAssistantEntity(world, true);
		
		short n = 0;
		Schematic sch = new Schematic(new NBTTagList(), n, n, n, new int[] {0}, new byte[] {0});
		
		if(schematic.equals("stoop")) {
			this.schematic =  sch.get("stoopUpdated.psm");
			this.name = "stoopUpdated.psm";
			this.xPos += 10000;
			this.zPos += 10000;

		}else {
			this.schematic = sch.get("flatctb.psm");
			this.name = "flatctb.psm";
		}
	}
	
	@Override
	public void build(World world) {
		dummy = new ResearchAssistantEntity(world, true);
		int genTick = 0;
		while(!generateArea(world, genTick))
			genTick++;

	}
	
	protected boolean generateArea(World world, int genTick) {
		//TODO: update to 1.8
		/*
		final int maxBlocksPerTick = 65536;
		Schematic sh = this.schematic;
		
		//number of "lengths" to generate per tick (max X blocks), iterating through at least 1 X per tick, in case the height and width are really big.
		//we don't want the game to lag too much.
		final int maxXPerTick = (int)(Math.max(Math.floor((float)maxBlocksPerTick/(sh.height*sh.width)),1.0));
		
		//the position to begin counting in the blocks[] array.
		int count=(genTick*maxXPerTick)*sh.height*sh.width;
		
		
		if(count >= sh.blocks.length ) { //we've generated all blocks already! or We don't need to generate the next area TODO: remove this.id > 1
			
			//hasBeenGenerated = true;
//			//lets put in the chests!
//			for(int i = 0; i < ExperimentFlatCTB.spawnlocations.length; i++) {
//				int x = ExperimentFlatCTB.spawnlocations[i][0];
//				int y = ExperimentFlatCTB.spawnlocations[i][1];
//				int z = ExperimentFlatCTB.spawnlocations[i][2];
//				TileEntity entity;
//				if(world.blockExists(x, y, z)) {
//					entity = (TileEntity) world.getTileEntity(x, y , z);
//					if(entity != null && entity instanceof TileEntityChest) {
//						//clear chest contents.
//						TileEntityChest chest = (TileEntityChest) InventoryHelper.clearChestContents(entity);
//						entity = chest; //set this updated chest to the entity object.
//					}
//					
//				} else {
//					world.setBlock(x, y, z, Block.getBlockFromName("chest"));
//					entity = (TileEntity) world.getTileEntity(x, y , z);
//				}
//				
//				if(entity != null && entity instanceof TileEntityChest) {
//					System.out.println("I put in a chest!");
//					ItemStack someIce = new ItemStack(Block.getBlockFromName("packed_ice"), 4);
//					ItemStack someWood = new ItemStack(Block.getBlockById(17), 4); //Oak Wood Logs
//					ItemStack someAluminum = new ItemStack(Block.getBlockById(209), 4); //Aluminum Blocks
//					ItemStack someNR = new ItemStack(Block.getBlockById(428), 4); //Black Natural Rubber -
//					TileEntityChest chest = (TileEntityChest) entity;
//					chest.setInventorySlotContents(0, someIce);
//					chest.setInventorySlotContents(1, someWood);
//					chest.setInventorySlotContents(2, someAluminum);
//					chest.setInventorySlotContents(3, someNR);
//				}
//				
//			}
			
			return true; 
		}
	
		//still have blocks in the blocks[] array we need to add to the world
		for(int x = (genTick*maxXPerTick); x < (genTick*maxXPerTick)+ maxXPerTick; x++){
			for(int y = 0; y<(int)sh.height; y++){
				for(int z = 0; z<(int)sh.width; z++){
					if(count>=sh.blocks.length) { //in case the array isn't perfectly square (i.e. rectangular area was selected)
						return false;
					}
					int curblock = (int)sh.blocks[count];
					
					if(curblock == 0 || curblock == 76) {
						count++;
						continue;
					}
					else if(curblock == 759) {
						count++;
						continue; //these are Gas Lamps - we don't care for these.
						
					}else if(curblock == 123 || curblock == 124) { //replace redstone lamps (inactive or active) with glowstone.
						world.setBlock(x + this.xPos, y + this.yPos , z + this.zPos, Block.getBlockById(89), 0, 2);
					}
					
					else if(curblock == 95) {
						world.setBlock(x + this.xPos, y + this.yPos , z + this.zPos, Block.getBlockById(curblock), sh.data[count], 2);
						if(sh.data[count] == 5) {
							world.setBlock(x + this.xPos, y + this.yPos + 1, z + this.zPos, Block.getBlockById(171), sh.data[count], 2); //add lime carpet
						}
	
						
					}else if(curblock == 35) {
						world.setBlock(x + this.xPos, y + this.yPos , z + this.zPos, Block.getBlockById(curblock), sh.data[count], 2);
						//System.out.println(x);
						if(sh.data[count] == 5) {
							world.setBlock(x + this.xPos, y + this.yPos + 1, z + this.zPos, Block.getBlockById(171), sh.data[count], 2); //add lime carpet
						}else if(sh.data[count] == 0) {
							world.setBlock(x + this.xPos, y + this.yPos + 1, z + this.zPos, Block.getBlockById(171), sh.data[count], 2); //add white carpet
						}
						
					}
					
					
					else if(curblock == 754) { //spotlights - we like these
						//world.setBlock(x + this.xPos, y + this.yPos , z + this.zPos, Block.getBlockById(0), 0, 2);
						world.setBlock(x + this.xPos, y + this.yPos , z + this.zPos, Block.getBlockById(curblock), sh.data[count], 2);
						//ResearchAssistantEntity dummy = new ResearchAssistantEntity(world, true);
						PolycraftInventoryBlock pbi = (PolycraftInventoryBlock) world.getBlock(x + this.xPos, y + this.yPos , z + this.zPos);
						System.out.println(String.format("Found a tile entity & xyz: %s %d %d %d", pbi.getUnlocalizedName(), x + this.xPos,  y + this.yPos , z + this.zPos));
						//System.out.println("Coordinates: ");
						ItemStack item = new ItemStack(Block.getBlockById((int)sh.blocks[count]));
						pbi.onBlockPlacedBy(world, x + this.xPos, y + this.yPos, z + this.zPos, dummy, new ItemStack(Block.getBlockById((int)sh.blocks[count])));
						
						FueledLampInventory lightInv = (FueledLampInventory) pbi.getInventory(world, x + this.xPos, y + this.yPos, z + this.zPos);
						lightInv.setInventorySlotContents(0,
								new ItemStack(random.nextFloat() > 0.5 ? ResearchAssistantLabGenerator.BUTANOL : ResearchAssistantLabGenerator.ETHANOL, 8 + random.nextInt(3)));
	
					
					}else if(curblock == 19){ //sponges mark the spawn locations, but are located two blocks below the surface.
						this.spawnPoints.add(new FeatureSpawn(x + this.xPos, y+this.yPos + 2, z+this.zPos));	
						
					}else {
						world.setBlock(x + this.xPos, y + this.yPos , z + this.zPos, Block.getBlockById(curblock), sh.data[count], 2);
					}
					
					count++;
				}
			}
		}*/
		
		return false;
	
	}

}
