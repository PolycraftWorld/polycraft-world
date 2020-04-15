package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.math.NumberUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.aitools.BotAPI;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyButtonCycle;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.client.gui.exp.creation.GuiExpCreator;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.util.Format;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDoor;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TutorialFeatureRoom extends TutorialFeature{

	private BlockPos pos2;

	private int blockFloorMeta, blockWallMeta, blockCeilMeta, blockRefMeta;
	private String blockFloorName, blockWallName, blockCeilName, blockRefName;
	private boolean spawnRefBlocks, recycleRefSeed;
	private int refSeed;
	private float refSpawnChance;
	private HashMap<EnumFacing, RoomSide> walls;
	
	//Gui Parameters
	@SideOnly(Side.CLIENT)
	protected GuiPolyNumField xPos2Field, yPos2Field, zPos2Field, metaField;
	@SideOnly(Side.CLIENT)
	protected GuiTextField blockNameField, slotDataField;
	
	public TutorialFeatureRoom() {}
	
	public TutorialFeatureRoom(String name, BlockPos pos){
		super(name, pos, Color.GREEN);
		this.pos2 = pos;
		this.featureType = TutorialFeatureType.ROOM;
	}
	
	@Override
	public void preInit(ExperimentTutorial exp) {
		super.preInit(exp);
		pos2 = pos2.add(exp.posOffset.xCoord, exp.posOffset.yCoord, exp.posOffset.zCoord);
	}
	
	@Override
	public void onServerTickUpdate(ExperimentTutorial exp) {
		int xMin = Math.min(pos.getX(), pos2.getX());
		int xMax = Math.max(pos.getX(), pos2.getX());
		int zMin = Math.min(pos.getZ(), pos2.getZ());
		int zMax = Math.max(pos.getZ(), pos2.getZ());
		int yMin = Math.min(pos.getY(), pos2.getY());
		int yMax = Math.max(pos.getY(), pos2.getY());
		
		Random rand = new Random(refSeed);
		
		// build floor and ceiling
		for(int x = xMin; x <= xMax; x++) {
			for(int z = zMin; z <= zMax; z++) {
				exp.world.setBlockState(new BlockPos(x,yMin,z), Block.getBlockFromName(blockFloorName).getStateFromMeta(blockFloorMeta), 2);
				exp.world.setBlockState(new BlockPos(x,yMax,z), Block.getBlockFromName(blockCeilName).getStateFromMeta(blockCeilMeta), 2);
			}
		}
		
		// build walls
		for(int y = yMin; y <= yMax; y++) {
			for(int x = xMin; x <= xMax; x++) {
				if(spawnRefBlocks) {
					if(rand.nextDouble() < refSpawnChance) {	// spawn ref block
						exp.world.setBlockState(new BlockPos(x,y,zMin), Block.getBlockFromName(blockRefName).getStateFromMeta(blockRefMeta), 2);
					}else {
						exp.world.setBlockState(new BlockPos(x,y,zMin), Block.getBlockFromName(blockWallName).getStateFromMeta(blockWallMeta), 2);
					}
					if(rand.nextDouble() < refSpawnChance) {	// spawn ref block
						exp.world.setBlockState(new BlockPos(x,y,zMax), Block.getBlockFromName(blockRefName).getStateFromMeta(blockRefMeta), 2);
					}else {
						exp.world.setBlockState(new BlockPos(x,y,zMax), Block.getBlockFromName(blockWallName).getStateFromMeta(blockWallMeta), 2);
					}
				}else {
					exp.world.setBlockState(new BlockPos(x,y,zMin), Block.getBlockFromName(blockWallName).getStateFromMeta(blockWallMeta), 2);
					exp.world.setBlockState(new BlockPos(x,y,zMax), Block.getBlockFromName(blockWallName).getStateFromMeta(blockWallMeta), 2);
				}
					
			}
			for(int z = zMin; z <= zMax; z++) {
				if(spawnRefBlocks) {
					if(rand.nextDouble() < refSpawnChance) {	// spawn ref block
						exp.world.setBlockState(new BlockPos(xMin,y,z), Block.getBlockFromName(blockRefName).getStateFromMeta(blockRefMeta), 2);
					}else {
						exp.world.setBlockState(new BlockPos(xMin,y,z), Block.getBlockFromName(blockWallName).getStateFromMeta(blockWallMeta), 2);
					}
					if(rand.nextDouble() < refSpawnChance) {	// spawn ref block
						exp.world.setBlockState(new BlockPos(xMax,y,z), Block.getBlockFromName(blockRefName).getStateFromMeta(blockRefMeta), 2);
					}else {
						exp.world.setBlockState(new BlockPos(xMax,y,z), Block.getBlockFromName(blockWallName).getStateFromMeta(blockWallMeta), 2);
					}
				}else {
					exp.world.setBlockState(new BlockPos(xMin,y,z), Block.getBlockFromName(blockWallName).getStateFromMeta(blockWallMeta), 2);
					exp.world.setBlockState(new BlockPos(xMax,y,z), Block.getBlockFromName(blockWallName).getStateFromMeta(blockWallMeta), 2);
				}
			}
		}
		

		canProceed = true;
		this.complete(exp);
	}
	
	@Override
	public void buildGuiParameters(GuiExpCreator guiDevTool, int x_pos, int y_pos) {
		// TODO Auto-generated method stub
		super.buildGuiParameters(guiDevTool, x_pos, y_pos);
		
		FontRenderer fr = guiDevTool.getFontRenderer();
        
        y_pos += 15;
        
        //add position text fields
        xPos2Field = new GuiPolyNumField(fr, x_pos + 40, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        xPos2Field.setMaxStringLength(32);
        xPos2Field.setText(Integer.toString((int)pos2.getX()));
        xPos2Field.setTextColor(16777215);
        xPos2Field.setVisible(true);
        xPos2Field.setCanLoseFocus(true);
        xPos2Field.setFocused(false);
        guiDevTool.textFields.add(xPos2Field);
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +85, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Y:"));
        yPos2Field = new GuiPolyNumField(fr, x_pos + 95, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        yPos2Field.setMaxStringLength(32);
        yPos2Field.setText(Integer.toString((int)pos2.getY()));
        yPos2Field.setTextColor(16777215);
        yPos2Field.setVisible(true);
        yPos2Field.setCanLoseFocus(true);
        yPos2Field.setFocused(false);
        guiDevTool.textFields.add(yPos2Field);
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +140, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Z:"));
        zPos2Field = new GuiPolyNumField(fr, x_pos + 150, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        zPos2Field.setMaxStringLength(32);
        zPos2Field.setText(Integer.toString((int)pos2.getZ()));
        zPos2Field.setTextColor(16777215);
        zPos2Field.setVisible(true);
        zPos2Field.setCanLoseFocus(true);
        zPos2Field.setFocused(false);
        guiDevTool.textFields.add(zPos2Field);
	}
	
	@Override
	public void updateValues() {
		this.pos2 = new BlockPos(Integer.parseInt(xPos2Field.getText())
				,Integer.parseInt(yPos2Field.getText())
				,Integer.parseInt(zPos2Field.getText()));
		
		// set deafults here
		if(blockFloorName == null) {
			blockFloorName = "minecraft:grass";
			blockFloorMeta = 0;
		}
		if(blockWallName == null) {
			blockWallName = "minecraft:bedrock";
			blockWallMeta = 0;
		}
		if(blockCeilName == null) {
			blockCeilName = "minecraft:air";
			blockCeilMeta = 0;
		}
		if(blockRefName == null) {
			blockRefName = "minecraft:air";
			blockRefMeta = 0;
		}
		spawnRefBlocks = false;
		recycleRefSeed = false;
		refSeed = 0;
		refSpawnChance = 0.05f;
		super.updateValues();
	}
	
	@Override
	public BlockPos getPos2() {
		return pos2;
	}

	
	public int getBlockFloorMeta() {
		return blockFloorMeta;
	}

	public void setBlockFloorMeta(int blockFloorMeta) {
		this.blockFloorMeta = blockFloorMeta;
	}

	public int getBlockWallMeta() {
		return blockWallMeta;
	}

	public void setBlockWallMeta(int blockWallMeta) {
		this.blockWallMeta = blockWallMeta;
	}

	public int getBlockCeilMeta() {
		return blockCeilMeta;
	}

	public void setBlockCeilMeta(int blockCeilMeta) {
		this.blockCeilMeta = blockCeilMeta;
	}

	public int getBlockRefMeta() {
		return blockRefMeta;
	}

	public void setBlockRefMeta(int blockRefMeta) {
		this.blockRefMeta = blockRefMeta;
	}

	public String getBlockFloorName() {
		return blockFloorName;
	}

	public void setBlockFloorName(String blockFloorName) {
		this.blockFloorName = blockFloorName;
	}

	public String getBlockWallName() {
		return blockWallName;
	}

	public void setBlockWallName(String blockWallName) {
		this.blockWallName = blockWallName;
	}

	public String getBlockCeilName() {
		return blockCeilName;
	}

	public void setBlockCeilName(String blockCeilName) {
		this.blockCeilName = blockCeilName;
	}

	public String getBlockRefName() {
		return blockRefName;
	}

	public void setBlockRefName(String blockRefName) {
		this.blockRefName = blockRefName;
	}

	public boolean isSpawnRefBlocks() {
		return spawnRefBlocks;
	}

	public void setSpawnRefBlocks(boolean spawnRefBlocks) {
		this.spawnRefBlocks = spawnRefBlocks;
	}

	public boolean isRecycleRefSeed() {
		return recycleRefSeed;
	}

	public void setRecycleRefSeed(boolean recycleRefSeed) {
		this.recycleRefSeed = recycleRefSeed;
	}

	public int getRefSeed() {
		return refSeed;
	}

	public void setRefSeed(int refSeed) {
		this.refSeed = refSeed;
	}

	public float getRefSpawnChance() {
		return refSpawnChance;
	}

	public void setRefSpawnChance(float refSpawnChance) {
		this.refSpawnChance = refSpawnChance;
	}

	public GuiTextField getBlockNameField() {
		return blockNameField;
	}

	public void setBlockNameField(GuiTextField blockNameField) {
		this.blockNameField = blockNameField;
	}

	@Override
	public NBTTagCompound save()
	{
		super.save();
		int pos2[] = {(int)this.pos2.getX(), (int)this.pos2.getY(), (int)this.pos2.getZ()};
		nbt.setIntArray("pos2",pos2);
		nbt.setInteger("blockFloorMeta", blockFloorMeta);
		nbt.setString("blockFloorName", blockFloorName);
		nbt.setInteger("blockWallMeta", blockWallMeta);
		nbt.setString("blockWallName", blockWallName);
		nbt.setInteger("blockCeilMeta", blockCeilMeta);
		nbt.setString("blockCeilName", blockCeilName);
		nbt.setInteger("blockRefMeta", blockRefMeta);
		nbt.setString("blockRefName", blockRefName);
		nbt.setBoolean("spawnRefBlocks", spawnRefBlocks);
		nbt.setBoolean("recycleRefSeed", recycleRefSeed);
		nbt.setInteger("refSeed", refSeed);
		nbt.setFloat("refSpawnChance", refSpawnChance);
		return nbt;
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
//		int featLookDir[]=nbtFeat.getIntArray("lookDir");
//		this.lookDir=new BlockPos(featLookDir[0], featLookDir[1], featLookDir[2]);
		int featPos2[]=nbtFeat.getIntArray("pos2");
		this.pos2=new BlockPos(featPos2[0], featPos2[1], featPos2[2]);
		this.blockFloorMeta = nbtFeat.getInteger("blockFloorMeta");
		this.blockFloorName = nbtFeat.getString("blockFloorName");
		this.blockWallMeta = nbtFeat.getInteger("blockWallMeta");
		this.blockWallName = nbtFeat.getString("blockWallName");
		this.blockCeilMeta = nbtFeat.getInteger("blockCeilMeta");
		this.blockCeilName = nbtFeat.getString("blockCeilName");
		this.blockRefMeta = nbtFeat.getInteger("blockRefMeta");
		this.blockRefName = nbtFeat.getString("blockRefName");
		this.spawnRefBlocks = nbtFeat.getBoolean("spawnRefBlocks");
		this.recycleRefSeed = nbtFeat.getBoolean("recycleRefSeed");
		this.refSeed = nbtFeat.getInteger("refSeed");
		this.refSpawnChance = nbtFeat.getFloat("refSpawnChance");
	}
	
	@Override
	public JsonObject saveJson()
	{
		super.saveJson();
		jobj.add("pos2", blockPosToJsonArray(pos2));
		jobj.addProperty("blockFloorMeta", blockFloorMeta);
		jobj.addProperty("blockFloorName", blockFloorName);
		jobj.addProperty("blockWallMeta", blockWallMeta);
		jobj.addProperty("blockWallName", blockWallName);
		jobj.addProperty("blockCeilMeta", blockCeilMeta);
		jobj.addProperty("blockCeilName", blockCeilName);
		jobj.addProperty("blockRefMeta", blockRefMeta);
		jobj.addProperty("blockRefName", blockRefName);
		jobj.addProperty("spawnRefBlocks", spawnRefBlocks);
		jobj.addProperty("recycleRefSeed", recycleRefSeed);
		jobj.addProperty("refSeed", refSeed);
		jobj.addProperty("refSpawnChance", refSpawnChance);
		
		return jobj;
	}
	
	@Override
	public void loadJson(JsonObject featJson)
	{
		super.loadJson(featJson);
		this.pos2 = blockPosFromJsonArray(featJson.get("pos2").getAsJsonArray());
		this.blockFloorMeta = featJson.get("blockFloorMeta").getAsInt();
		this.blockFloorName = featJson.get("blockFloorName").getAsString();
		this.blockWallMeta = featJson.get("blockWallMeta").getAsInt();
		this.blockWallName = featJson.get("blockWallName").getAsString();
		this.blockCeilMeta = featJson.get("blockCeilMeta").getAsInt();
		this.blockCeilName = featJson.get("blockCeilName").getAsString();
		this.blockRefMeta = featJson.get("blockRefMeta").getAsInt();
		this.blockRefName = featJson.get("blockRefName").getAsString();
		this.spawnRefBlocks = featJson.get("spawnRefBlocks").getAsBoolean();
		this.recycleRefSeed = featJson.get("recycleRefSeed").getAsBoolean();
		this.refSeed = featJson.get("refSeed").getAsInt();
		this.refSpawnChance = featJson.get("refSpawnChance").getAsFloat();
	}
	
	// replaced by TutorialFeatureWall
	private class RoomSide{
		public EnumFacing outDirection;		// outward facing direction
		public AxisAlignedBB box;
		public ArrayList<Pathway> pathways;
	}
	// replaced by TutorialFeatureWall
	private class Pathway{
		
	}
	
}
