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
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.util.PathConfiguration;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.util.PathConfiguration.Location;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.util.PathConfiguration.PathType;
import edu.utd.minecraft.mod.polycraft.util.Format;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
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
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TutorialFeatureWall extends TutorialFeature{
	private BlockPos pos2;
	
	
	//working parameters
	private String wallBlockName;		// material used for wall
	private int wallBlockMeta;
	private String doorBlockName;		// door type
	private int doorBlockMeta;
	private String buttonBlockName;	// button type
	private int buttonBlockMeta;
	private String pressurePlateBlockName;	// pressure plate type
	private int pressurePlateBlockMeta;
	private String leverBlockName;	// lever type
	private int leverBlockMeta;
	private HashMap<Integer, PathConfiguration> wallConfiguration;	// <slot (x or z value), pathtype> store pathway type per block length on wall
	
	//Gui Parameters
	@SideOnly(Side.CLIENT)
	protected GuiPolyNumField xPos2Field, yPos2Field, zPos2Field, metaField;
	@SideOnly(Side.CLIENT)
	protected GuiTextField blockNameField, slotDataField;
	
	public TutorialFeatureWall() {}
	
	public TutorialFeatureWall(String name, BlockPos pos){
		super(name, pos, Color.GREEN);
		this.pos2 = pos;
		this.featureType = TutorialFeatureType.WALL;
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
		
		// definitions for testing 
		//wallConfiguration = new HashMap<Integer, PathConfiguration>();
		wallConfiguration.put(0, new PathConfiguration(PathType.OPEN_FULL_HEIGHT, false));
		wallConfiguration.put(1, new PathConfiguration(PathType.OPEN, false));
		wallConfiguration.put(3, new PathConfiguration(PathType.DOOR, false));
		wallConfiguration.put(5, new PathConfiguration(PathType.DOOR, true));
		wallConfiguration.put(7, new PathConfiguration(PathType.DOOR, false));
		wallConfiguration.put(9, new PathConfiguration(PathType.DOOR, false));
		wallConfiguration.put(11, new PathConfiguration(PathType.DOOR, false));
		wallConfiguration.get(7).setButtonLocation(Location.OUTSIDE);
		wallConfiguration.get(7).setPressurePlateLocation(Location.OUTSIDE);
		wallConfiguration.get(9).setButtonLocation(Location.INSIDE);
		wallConfiguration.get(9).setPressurePlateLocation(Location.INSIDE);
		wallConfiguration.get(11).setButtonLocation(Location.BOTH);
		wallConfiguration.get(11).setPressurePlateLocation(Location.BOTH);
		
		//Determine direction
		boolean xAxis = false;
		if(Math.abs(pos.getX() - pos2.getX()) > Math.abs(pos.getZ() - pos2.getZ()))
			xAxis = true;
		// generate wall
		for(int v1 = xAxis? xMin:zMin; v1 <= (xAxis? xMax:zMax); v1++) {
			// Build the wall first, just in case we need to place something like a button (needs a wall)
			for(int v2 = !xAxis? xMin:zMin; v2 <= (!xAxis? xMax:zMax); v2++) {
				// initialize x and z
				int x = xAxis? v1:v2;
				int z = !xAxis? v1:v2;
				for(int y = yMin; y < yMax; y++) {
					exp.world.setBlockState(new BlockPos(x, y, z), Block.getBlockFromName(wallBlockName).getStateFromMeta(wallBlockMeta), 2);
				}
			}
			
			// build custom pathways
			int y = yMin;
			int x = xAxis? v1:xMin;
			int z = !xAxis? v1:zMin;
			// get current path config or default to WALL if missing
			PathConfiguration path = wallConfiguration.get(v1 - (xAxis? xMin: zMin)) != null ? wallConfiguration.get(v1 - (xAxis? xMin: zMin)): 
				new PathConfiguration(PathConfiguration.PathType.WALL, false);
			switch(path.getType()) {
			case BREAKABLE_WALL:
				break;
			case DOOR:
				// build a door path here
				// first clear path
				if(xAxis)
					for(int z2 = z;z2<=zMax;z2++) {
				        exp.world.setBlockToAir(new BlockPos(x, y, z2));
						exp.world.setBlockToAir(new BlockPos(x, y + 1, z2));
					}
				else
					for(int x2 = x;x2<=xMax;x2++) {
				        exp.world.setBlockToAir(new BlockPos(x2, y, z));
						exp.world.setBlockToAir(new BlockPos(x2, y + 1, z));
					}
				
				// place door on front or back, depending if reversed
				if(path.isReversed()) {
					x = xAxis? v1:xMax;
					z = !xAxis? v1:zMax;
				}
				Block door = Block.getBlockFromName(doorBlockName);
		        IBlockState iblockstate = door.getDefaultState().withProperty(BlockDoor.FACING, xAxis? 
		        		path.isReversed() ? EnumFacing.NORTH: EnumFacing.SOUTH: 
		        		path.isReversed() ? EnumFacing.WEST: EnumFacing.EAST).withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT);
		        exp.world.setBlockState(new BlockPos(x, y, z), iblockstate.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER), 2);
		        exp.world.setBlockState(new BlockPos(x, y + 1, z), iblockstate.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), 2);
		        
		        // spawn buttons
		        if(path.getButtonLocation() == Location.BOTH || path.getButtonLocation() == Location.OUTSIDE) {
		        	exp.world.setBlockState(new BlockPos(x + (!xAxis?(!path.isReversed()? -1:1):0), y + 2, z + (xAxis?(!path.isReversed()? -1:1):0)), 
		        			Block.getBlockFromName(buttonBlockName).getDefaultState().withProperty(BlockButton.FACING, xAxis? 
		    		        		!path.isReversed() ? EnumFacing.NORTH: EnumFacing.SOUTH: 
		    			        		!path.isReversed() ? EnumFacing.WEST: EnumFacing.EAST), 2);
		        }
		        if(path.getButtonLocation() == Location.BOTH || path.getButtonLocation() == Location.INSIDE) {
		        	exp.world.setBlockState(new BlockPos(x + (!xAxis?(path.isReversed()? -1:1):0), y + 1, z + (xAxis?(path.isReversed()? -1:1):0)), 
		        			Block.getBlockFromName(buttonBlockName).getDefaultState().withProperty(BlockButton.FACING, xAxis? 
		    		        		!path.isReversed() ? EnumFacing.WEST: EnumFacing.EAST: 
		    			        		!path.isReversed() ? EnumFacing.NORTH: EnumFacing.SOUTH), 2);
		        }
		        
		        // spawn pressure plates
		        if(path.getPressurePlateLocation() == Location.BOTH || path.getPressurePlateLocation() == Location.OUTSIDE) {
		        	exp.world.setBlockState(new BlockPos(x + (!xAxis?(!path.isReversed()? -1:1):0), y, z + (xAxis?(!path.isReversed()? -1:1):0)), 
		        			Block.getBlockFromName(pressurePlateBlockName).getDefaultState(), 2);
		        }
		        if(path.getPressurePlateLocation() == Location.BOTH || path.getPressurePlateLocation() == Location.INSIDE) {
		        	exp.world.setBlockState(new BlockPos(x + (!xAxis?(path.isReversed()? -1:1):0), y, z + (xAxis?(path.isReversed()? -1:1):0)), 
		        			Block.getBlockFromName(pressurePlateBlockName).getDefaultState(), 2);
		        }
		        
				break;
			case DOOR_FLIPPED:
				break;
			case FALSE_DOOR:
				break;
			case HIDDEN_DOOR:
				break;
			case ONE_WAY_DOOR:
				break;
			case ONE_WAY_SECRET_DOOR:
				break;
			case OPEN:
				// clear path
//				if(xAxis)
//					for(;z<=zMax;z++) {
//				        exp.world.setBlockToAir(new BlockPos(x, y, z));
//						exp.world.setBlockToAir(new BlockPos(x, y + 1, z));
//					}
//				else
//					for(;x<=xMax;x++) {
//				        exp.world.setBlockToAir(new BlockPos(x, y, z));
//						exp.world.setBlockToAir(new BlockPos(x, y + 1, z));
//					}
				for(int v2 = !xAxis? xMin:zMin; v2 <= (!xAxis? xMax:zMax); v2++) {
					// clear path to be open
					for(y = yMin; y < yMin + 2; y++) {
						if(xAxis)
							exp.world.setBlockToAir(new BlockPos(x, y, v2));
						else
							exp.world.setBlockToAir(new BlockPos(v2, y, z));
					}
				}
				break;
			case OPEN_FULL_HEIGHT:
				// clear path full height
				if(xAxis)
					for(;z<=zMax;z++) {
						for(y = yMin; y < yMax; y++) {
							exp.world.setBlockToAir(new BlockPos(x, y, z));
						}
					}
				else
					for(;x<=xMax;x++) {
						for(y = yMin; y < yMax; y++) {
							exp.world.setBlockToAir(new BlockPos(x, y, z));
						}
					}
				break;
			case SECRET_DOOR:
				break;
			case WALL:
				// do nothing, we already built the wall
				break;
			default:
				break;
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
        
        y_pos += 15;
        
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"WallMeta:"));
        metaField = new GuiPolyNumField(fr, x_pos + 40, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        metaField.setMaxStringLength(32);
        metaField.setText(Integer.toString(wallBlockMeta));
        metaField.setTextColor(16777215);
        metaField.setVisible(true);
        metaField.setCanLoseFocus(true);
        metaField.setFocused(false);
        guiDevTool.textFields.add(metaField);
        
        y_pos += 15;
		
		guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Block: "));
        blockNameField = new GuiTextField(420, fr, x_pos + 40, y_pos + 49, (int) (guiDevTool.X_WIDTH * .6), 10);
        blockNameField.setMaxStringLength(9999); //don't really want a max length here
        blockNameField.setText(wallBlockName != null? wallBlockName:"");
        blockNameField.setTextColor(16777215);
        blockNameField.setVisible(true);
        blockNameField.setCanLoseFocus(true);
        blockNameField.setFocused(false);
        guiDevTool.textFields.add(blockNameField);
	}
	
	@Override
	public void updateValues() {
		this.pos2 = new BlockPos(Integer.parseInt(xPos2Field.getText())
				,Integer.parseInt(yPos2Field.getText())
				,Integer.parseInt(zPos2Field.getText()));
		this.wallBlockMeta = Integer.parseInt(metaField.getText());
		if(!blockNameField.getText().isEmpty()) {
			if(Block.getBlockFromName(blockNameField.getText()) != null)
				wallBlockName = blockNameField.getText();
			else
				wallBlockName = "";
		}
		

		wallBlockName = wallBlockName != null? wallBlockName: "minecraft:stained_hardened_clay";
		doorBlockName = doorBlockName != null? doorBlockName: "minecraft:wooden_door";
		buttonBlockName = buttonBlockName != null? buttonBlockName: "minecraft:wooden_button";
		pressurePlateBlockName = pressurePlateBlockName != null? pressurePlateBlockName: "minecraft:wooden_pressure_plate";
		leverBlockName = leverBlockName != null? leverBlockName: "minecraft:lever";
		
		if(wallConfiguration == null || wallConfiguration.isEmpty())
		{
			wallConfiguration = new HashMap<Integer, PathConfiguration>();
//			wallConfiguration.put(4, new PathConfiguration(PathType.DOOR, false));

			wallConfiguration.put(0, new PathConfiguration(PathType.OPEN_FULL_HEIGHT, false));
			wallConfiguration.put(1, new PathConfiguration(PathType.OPEN, false));
			wallConfiguration.put(3, new PathConfiguration(PathType.DOOR, false));
			wallConfiguration.put(5, new PathConfiguration(PathType.DOOR, true));
			wallConfiguration.put(7, new PathConfiguration(PathType.DOOR, false));
			wallConfiguration.put(9, new PathConfiguration(PathType.DOOR, false));
			wallConfiguration.put(11, new PathConfiguration(PathType.DOOR, false));
			wallConfiguration.get(7).setButtonLocation(Location.OUTSIDE);
			wallConfiguration.get(7).setPressurePlateLocation(Location.OUTSIDE);
			wallConfiguration.get(9).setButtonLocation(Location.INSIDE);
			wallConfiguration.get(9).setPressurePlateLocation(Location.INSIDE);
			wallConfiguration.get(11).setButtonLocation(Location.BOTH);
			wallConfiguration.get(11).setPressurePlateLocation(Location.BOTH);
		}
		
		super.updateValues();
	}
	
	
	
	public String getWallBlockName() {
		return wallBlockName;
	}

	public void setWallBlockName(String wallBlockName) {
		this.wallBlockName = wallBlockName;
	}

	public int getWallBlockMeta() {
		return wallBlockMeta;
	}

	public void setWallBlockMeta(int wallBlockMeta) {
		this.wallBlockMeta = wallBlockMeta;
	}

	public String getDoorBlockName() {
		return doorBlockName;
	}

	public void setDoorBlockName(String doorBlockName) {
		this.doorBlockName = doorBlockName;
	}

	public int getDoorBlockMeta() {
		return doorBlockMeta;
	}

	public void setDoorBlockMeta(int doorBlockMeta) {
		this.doorBlockMeta = doorBlockMeta;
	}

	public String getButtonBlockName() {
		return buttonBlockName;
	}

	public void setButtonBlockName(String buttonBlockName) {
		this.buttonBlockName = buttonBlockName;
	}

	public int getButtonBlockMeta() {
		return buttonBlockMeta;
	}

	public void setButtonBlockMeta(int buttonBlockMeta) {
		this.buttonBlockMeta = buttonBlockMeta;
	}

	public String getPressurePlateBlockName() {
		return pressurePlateBlockName;
	}

	public void setPressurePlateBlockName(String pressurePlateBlockName) {
		this.pressurePlateBlockName = pressurePlateBlockName;
	}

	public int getPressurePlateBlockMeta() {
		return pressurePlateBlockMeta;
	}

	public void setPressurePlateBlockMeta(int pressurePlateBlockMeta) {
		this.pressurePlateBlockMeta = pressurePlateBlockMeta;
	}

	public String getLeverBlockName() {
		return leverBlockName;
	}

	public void setLeverBlockName(String leverBlockName) {
		this.leverBlockName = leverBlockName;
	}

	public int getLeverBlockMeta() {
		return leverBlockMeta;
	}

	public void setLeverBlockMeta(int leverBlockMeta) {
		this.leverBlockMeta = leverBlockMeta;
	}

	public GuiTextField getBlockNameField() {
		return blockNameField;
	}

	public void setBlockNameField(GuiTextField blockNameField) {
		this.blockNameField = blockNameField;
	}

	@Override
	public BlockPos getPos2() {
		return pos2;
	}

	
	@Override
	public NBTTagCompound save()
	{
		super.save();
		int pos2[] = {(int)this.pos2.getX(), (int)this.pos2.getY(), (int)this.pos2.getZ()};
		nbt.setIntArray("pos2",pos2);
		nbt.setString("wallBlockName", wallBlockName);
		nbt.setInteger("wallBlockMeta", wallBlockMeta);
		nbt.setString("doorBlockName", doorBlockName);
		nbt.setInteger("doorBlockMeta", doorBlockMeta);
		nbt.setString("buttonBlockName", buttonBlockName);
		nbt.setInteger("buttonBlockMeta", buttonBlockMeta);
		nbt.setString("pressurePlateBlockName", pressurePlateBlockName);
		nbt.setInteger("pressurePlateBlockMeta", pressurePlateBlockMeta);
		nbt.setString("leverBlockName", leverBlockName);
		nbt.setInteger("leverBlockMeta", leverBlockMeta);
		NBTTagList wallConfig = new NBTTagList();
		for(int slot: wallConfiguration.keySet()) {
			NBTTagCompound slotConfig = new NBTTagCompound();	// define slot config to store data
			slotConfig.setInteger("slot", slot);
			slotConfig.setTag("config", wallConfiguration.get(slot).save());
			wallConfig.appendTag(slotConfig);	// add slot config to wall config
		}
		nbt.setTag("wallConfiguration", wallConfig);
		return nbt;
	}
	
	@Override
	public void load(NBTTagCompound nbtFeat)
	{
		super.load(nbtFeat);
//		int featLookDir[]=nbtFeat.getIntArray("lookDir");
//		this.lookDir=new BlockPos(featLookDir[0], featLookDir[1], featLookDir[2]);
		int featPos2[]=nbtFeat.getIntArray("pos2");
		pos2=new BlockPos(featPos2[0], featPos2[1], featPos2[2]);
		wallBlockName = nbtFeat.getString("wallBlockName");
		wallBlockMeta = nbtFeat.getInteger("wallBlockMeta");
		doorBlockName = nbtFeat.getString("doorBlockName");
		doorBlockMeta = nbtFeat.getInteger("doorBlockMeta");
		buttonBlockName = nbtFeat.getString("buttonBlockName");
		buttonBlockMeta = nbtFeat.getInteger("buttonBlockMeta");
		pressurePlateBlockName = nbtFeat.getString("pressurePlateBlockName");
		pressurePlateBlockMeta = nbtFeat.getInteger("pressurePlateBlockMeta");
		leverBlockName = nbtFeat.getString("leverBlockName");
		leverBlockMeta = nbtFeat.getInteger("leverBlockMeta");
		
		wallConfiguration = new HashMap<Integer, PathConfiguration>();
		NBTTagList wallConfig = nbtFeat.getTagList("wallConfiguration", 10);
		for(int i = 0; i < wallConfig.tagCount(); i++) {
			NBTTagCompound slotConfig = wallConfig.getCompoundTagAt(i);
			PathConfiguration path = new PathConfiguration();
			path.load(slotConfig.getCompoundTag("config"));
			wallConfiguration.put(slotConfig.getInteger("slot"), path);
		}
	}
	
	@Override
	public JsonObject saveJson()
	{
		super.saveJson();
		jobj.add("pos2", blockPosToJsonArray(pos2));
		jobj.addProperty("wallBlockName", wallBlockName);
		jobj.addProperty("wallBlockMeta", wallBlockMeta);
		jobj.addProperty("doorBlockName", doorBlockName);
		jobj.addProperty("doorBlockMeta", doorBlockMeta);
		jobj.addProperty("buttonBlockName", buttonBlockName);
		jobj.addProperty("buttonBlockMeta", buttonBlockMeta);
		jobj.addProperty("pressurePlateBlockName", pressurePlateBlockName);
		jobj.addProperty("pressurePlateBlockMeta", pressurePlateBlockMeta);
		jobj.addProperty("leverBlockName", leverBlockName);
		jobj.addProperty("leverBlockMeta", leverBlockMeta);
		JsonArray wallConfig = new JsonArray();
		for(int slot: wallConfiguration.keySet()) {
			JsonObject slotConfig = new JsonObject();	// define slot config to store data
			slotConfig.addProperty("slot", slot);
			slotConfig.add("config", wallConfiguration.get(slot).saveJson());
			wallConfig.add(slotConfig);	// add slot config to wall config
		}
		jobj.add("wallConfiguration", wallConfig);
		
		return jobj;
	}
	
	@Override
	public void loadJson(JsonObject featJson)
	{
		super.loadJson(featJson);
		this.pos2 = blockPosFromJsonArray(featJson.get("pos2").getAsJsonArray());
		wallBlockName = featJson.get("wallBlockName").getAsString();
		wallBlockMeta = featJson.get("wallBlockMeta").getAsInt();
		doorBlockName = featJson.get("doorBlockName").getAsString();
		doorBlockMeta = featJson.get("doorBlockMeta").getAsInt();
		buttonBlockName = featJson.get("buttonBlockName").getAsString();
		buttonBlockMeta = featJson.get("buttonBlockMeta").getAsInt();
		pressurePlateBlockName = featJson.get("pressurePlateBlockName").getAsString();
		pressurePlateBlockMeta = featJson.get("pressurePlateBlockMeta").getAsInt();
		leverBlockName = featJson.get("leverBlockName").getAsString();
		leverBlockMeta = featJson.get("leverBlockMeta").getAsInt();
		
		wallConfiguration = new HashMap<Integer, PathConfiguration>();
		JsonArray wallConfig = featJson.get("wallConfiguration").getAsJsonArray();
		for(int i = 0; i < wallConfig.size(); i++) {
			JsonObject slotConfig = wallConfig.get(i).getAsJsonObject();
			PathConfiguration path = new PathConfiguration();
			path.loadJson(slotConfig.get("config").getAsJsonObject());
			wallConfiguration.put(slotConfig.get("slot").getAsInt(), path);
		}
	}
	
	
}
