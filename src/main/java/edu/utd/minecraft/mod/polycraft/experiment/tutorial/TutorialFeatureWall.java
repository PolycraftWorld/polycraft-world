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
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TutorialFeatureWall extends TutorialFeature{
	private BlockPos pos2;
	boolean spawnRand;
	
	//working parameters
	private int meta;
	private String blockName;
	private ArrayList<Boolean> doorSlots;
	
	//Gui Parameters
	@SideOnly(Side.CLIENT)
	protected GuiPolyNumField xPos2Field, yPos2Field, zPos2Field, metaField;
	@SideOnly(Side.CLIENT)
	protected GuiTextField blockNameField, slotDataField;
	
	public TutorialFeatureWall() {}
	
	public TutorialFeatureWall(String name, BlockPos pos){
		super(name, pos, Color.GREEN);
		this.pos2 = pos;
		this.spawnRand = false;
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
		
		//Determine direction
		boolean xAxis = false;
		if(Math.abs(pos.getX() - pos2.getX()) > Math.abs(pos.getZ() - pos2.getZ()))
			xAxis = true;
		// generate wall
		for(int x = xMin; x <= xMax; x++) {
			for(int z = zMin; z <= zMax; z++) {
				for(int y = yMin; y < yMax; y++) {
					//every three blocks could be a pathway
					boolean buildDoor = false;
					if(xAxis && (x + 1 - xMin) % 3 == 0 && //We want every 3 blocks to be a door, but offset by 1 so we don't start the count at 0
							(x/3) < doorSlots.size() && doorSlots.get(x/3) && y < yMin + 2)	{
						buildDoor = true;
					}else if(!xAxis && (z + 1 - zMin) % 3 == 0 && //We want every 3 blocks to be a door, but offset by 1 so we don't start the count at 0
							(z/3) < doorSlots.size() && doorSlots.get(z/3) && y < yMin + 2){
						buildDoor = true;
					}
					
					if(buildDoor) {
						// build a door path here
						if(y == yMin && (z == zMin && xAxis || x == xMin && !xAxis)) {
							Block door = Block.getBlockFromName("minecraft:wooden_door");
					        IBlockState iblockstate = door.getDefaultState().withProperty(BlockDoor.FACING, xAxis?EnumFacing.SOUTH: EnumFacing.EAST).withProperty(BlockDoor.HINGE, BlockDoor.EnumHingePosition.LEFT);
					        exp.world.setBlockState(new BlockPos(x, y, z), iblockstate.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.LOWER), 2);
					        exp.world.setBlockState(new BlockPos(x, y + 1, z), iblockstate.withProperty(BlockDoor.HALF, BlockDoor.EnumDoorHalf.UPPER), 2);
						}else if(z != zMin) {
							exp.world.setBlockToAir(new BlockPos(x, y, z));
						}
					}
					else {
						exp.world.setBlockState(new BlockPos(x, y, z), Block.getBlockFromName(blockName).getStateFromMeta(meta), 2);
					}
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
        
        y_pos += 15;
        
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Meta:"));
        metaField = new GuiPolyNumField(fr, x_pos + 40, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        metaField.setMaxStringLength(32);
        metaField.setText(Integer.toString(meta));
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
        blockNameField.setText(blockName != null? blockName:"");
        blockNameField.setTextColor(16777215);
        blockNameField.setVisible(true);
        blockNameField.setCanLoseFocus(true);
        blockNameField.setFocused(false);
        guiDevTool.textFields.add(blockNameField);
        
        y_pos += 15;
		
		guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Slots: "));
        slotDataField = new GuiTextField(420, fr, x_pos + 40, y_pos + 49, (int) (guiDevTool.X_WIDTH * .6), 10);
        slotDataField.setMaxStringLength(9999); //don't really want a max length here
        //generate default text value for door slot field
        String doorSlotsText = "";
        if(doorSlots != null && doorSlots.size() > 0) {
        	for(boolean slot: doorSlots)
        		doorSlotsText += "," + (slot ? 1:0); // 1 for true, 0 for false. separated by commas
        	if(doorSlotsText.startsWith(","))
        		doorSlotsText = doorSlotsText.substring(1, doorSlotsText.length());
        }
        slotDataField.setText(doorSlotsText);
        slotDataField.setTextColor(16777215);
        slotDataField.setVisible(true);
        slotDataField.setCanLoseFocus(true);
        slotDataField.setFocused(false);
        guiDevTool.textFields.add(slotDataField);
	}
	
	@Override
	public void updateValues() {
		this.pos2 = new BlockPos(Integer.parseInt(xPos2Field.getText())
				,Integer.parseInt(yPos2Field.getText())
				,Integer.parseInt(zPos2Field.getText()));
		this.meta = Integer.parseInt(metaField.getText());
		if(!blockNameField.getText().isEmpty()) {
			if(Block.getBlockFromName(blockNameField.getText()) != null)
				blockName = blockNameField.getText();
			else
				blockName = "";
		}
		doorSlots = new ArrayList<Boolean>();
		if(!slotDataField.getText().isEmpty()) {
			String[] dataSplit = slotDataField.getText().split(",");
			for(String slot: dataSplit) {
				if(NumberUtils.isNumber(slot)) {
					if(slot.equals("1"))
						doorSlots.add(new Boolean(true));
					else
						doorSlots.add(new Boolean(false));
				}
			}
		}
		super.updateValues();
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
		nbt.setInteger("meta", meta);
		nbt.setString("blockName", blockName);
		int[] doorSlotIntArray = new int[doorSlots.size()];
		for(int i = 0; i < doorSlots.size(); i++)
			doorSlotIntArray[i] = doorSlots.get(i)?1:0;
		nbt.setIntArray("doorSlotIntArray", doorSlotIntArray);
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
		this.meta = nbtFeat.getInteger("meta");
		this.blockName = nbtFeat.getString("blockName");
		this.doorSlots = new ArrayList<Boolean>();
		for(int slot: nbtFeat.getIntArray("doorSlotIntArray"))
			doorSlots.add(slot==1);
	}
	
	@Override
	public JsonObject saveJson()
	{
		super.saveJson();
		jobj.add("pos2", blockPosToJsonArray(pos2));
		jobj.addProperty("meta", meta);
		jobj.addProperty("blockName", blockName);
		
		return jobj;
	}
	
	@Override
	public void loadJson(JsonObject featJson)
	{
		super.loadJson(featJson);
		this.pos2 = blockPosFromJsonArray(featJson.get("pos2").getAsJsonArray());
		this.meta = featJson.get("meta").getAsInt();
		this.blockName = featJson.get("blockName").getAsString();
	}
	
}
