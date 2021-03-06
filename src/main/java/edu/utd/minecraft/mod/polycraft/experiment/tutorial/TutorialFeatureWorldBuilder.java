package edu.utd.minecraft.mod.polycraft.experiment.tutorial;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.aitools.BotAPI;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyButtonCycle;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyLabel;
import edu.utd.minecraft.mod.polycraft.client.gui.api.GuiPolyNumField;
import edu.utd.minecraft.mod.polycraft.client.gui.exp.creation.GuiExpCreator;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.util.BlockDef;
import edu.utd.minecraft.mod.polycraft.util.Format;
import edu.utd.minecraft.mod.polycraft.worldgen.PolycraftTeleporter;
import net.minecraft.block.Block;
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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TutorialFeatureWorldBuilder extends TutorialFeature{
	private BlockPos pos2;
	
	//working parameters
	private int count = 30;
	private ConcurrentHashMap<BlockPos, BlockDef> blockListByPos;
	
	//Gui Parameters
	@SideOnly(Side.CLIENT)
	protected GuiPolyNumField pitchField, yawField, xPos2Field, yPos2Field, zPos2Field, countField;
	@SideOnly(Side.CLIENT)
	protected GuiPolyButtonCycle<GenType> cycleGenType;
	@SideOnly(Side.CLIENT)
	protected GuiTextField dataField;
	
	public enum GenType{
		TREES,
		STUMPS,
		BLOCK_LIST,
		AREA,
		HILLS
	}
	
	private GenType genType = GenType.TREES;
	
	public TutorialFeatureWorldBuilder() {}
	
	public TutorialFeatureWorldBuilder(String name, BlockPos pos, BlockPos lookDir, GenType gentype){
		super(name, pos, Color.GREEN);
		this.pos2 = pos;
		this.featureType = TutorialFeatureType.WORLDGEN;
		this.genType = gentype;
		blockListByPos = new ConcurrentHashMap<BlockPos, BlockDef>();	// initialize blockList to prevent errors 
	}
	
	@Override
	public void preInit(ExperimentTutorial exp) {
		super.preInit(exp);
		pos2 = pos2.add(exp.posOffset.xCoord, exp.posOffset.yCoord, exp.posOffset.zCoord);
	}
	
	@Override
	public void onServerTickUpdate(ExperimentTutorial exp) {
		int xPos, zPos;	//working variables
		switch(this.genType) {
		case HILLS:
			break;
		case STUMPS:
			while(count-- > 0) {
				for(int x=0;x<5;x++){	//try to generate the tree 5 times before giving up
					xPos = rand.nextInt(pos2.getX() - pos.getX());
					zPos = rand.nextInt(pos2.getZ() - pos.getZ());
					if(exp.world.isAirBlock(pos.add(xPos, 0, zPos))) {	//If the position is clear, set to log
							exp.world.setBlockState(pos.add(xPos, 0, zPos), Blocks.log.getDefaultState(), 2);
							//System.out.println("Adding Log at: " + pos.add(xPos, 0, zPos).toString());
							break;	//get out of the for loop
					}
				}
			}
			break;
		case TREES:
			while(count-- > 0) {
				IBlockState iblockstate = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
	            IBlockState iblockstate1 = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
	            //WorldGenerator worldgenerator = new WorldGenTrees(true, 4 + rand.nextInt(7), iblockstate, iblockstate1, false);
	            WorldGenerator worldgenerator = new WorldGenTrees(true, 5, iblockstate, iblockstate1, false);	// use static height trees
	            for(int x=0;x<5;x++){	//try to generate the tree 5 times before giving up
	            	if(worldgenerator.generate(exp.world, rand, pos.add(rand.nextInt(pos2.getX() - pos.getX()), 0, rand.nextInt(pos2.getZ() - pos.getZ()))))
	            		break;	//break for loop
	            }
			}
			break;
		case BLOCK_LIST:
			for(BlockPos blockPos: blockListByPos.keySet()){
				if(exp.world.isAirBlock(blockPos.add(exp.posOffset.xCoord, exp.posOffset.yCoord, exp.posOffset.zCoord))) {	//If the position is clear, set to block
					if(blockListByPos.get(blockPos).blockName.startsWith("tree")) {	
						BlockPlanks.EnumType treeType = BlockPlanks.EnumType.OAK;
						switch(blockListByPos.get(blockPos).blockName) {
						case "tree":
						case "treeOak":
							treeType = BlockPlanks.EnumType.OAK;
							break;
						case "treeJungle":
							treeType = BlockPlanks.EnumType.JUNGLE;
						}
						IBlockState iblockstate = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, treeType);
			            IBlockState iblockstate1 = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, treeType).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));
			            WorldGenerator worldgenerator = new WorldGenTrees(true, 6, iblockstate, iblockstate1, false);	// use static height trees
			           	worldgenerator.generate(exp.world, new Random(0), blockPos.add(exp.posOffset.xCoord, exp.posOffset.yCoord, exp.posOffset.zCoord));
					}else {
						exp.world.setBlockState(blockPos.add(exp.posOffset.xCoord, exp.posOffset.yCoord, exp.posOffset.zCoord), Block.getBlockFromName(blockListByPos.get(blockPos).blockName).getDefaultState(), 2);
					}
				}
			}
			break;
		case AREA:
			if(blockListByPos != null && !blockListByPos.values().isEmpty())	// must have something in block list
				for(int x = pos.getX(); x <= pos2.getX(); x++) {
					for(int z = pos.getZ(); z <= pos2.getZ(); z++) {
						for(int y = pos.getY(); y < pos2.getY(); y++) {
							exp.world.setBlockState(new BlockPos(x,y,z), Block.getBlockFromName(blockListByPos.values().iterator().next().blockName).getDefaultState(), 2);
						}
					}
				}
		default:
			break;
		}
		
		// remove any leaves near bot navigation space
		for(int i = 0; i <= pos2.getX(); i++) {
			for(int k = 0; k <= pos2.getZ(); k++) {
				for(int j = 0; j < 3; j++) {
					if(exp.getWorld().getBlockState(pos.add(i, j, k)).getBlock() == Blocks.leaves)
						exp.getWorld().setBlockToAir(pos.add(i, j, k));
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
        
        cycleGenType = new GuiPolyButtonCycle<GenType>(
        		guiDevTool.buttonCount++, x_pos + 10, y_pos + 45, (int) (guiDevTool.X_WIDTH * .9), 14, 
        		"Random Spawn",  genType);
        guiDevTool.addBtn(cycleGenType);
        y_pos += 15;
        //Add random spawn area setting
        //add some labels for position fields 
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Pos"));
        guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +30, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"X:"));
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
        		"Count:"));
        countField = new GuiPolyNumField(fr, x_pos + 40, y_pos + 49, (int) (guiDevTool.X_WIDTH * .2), 10);
        countField.setMaxStringLength(32);
        countField.setText(Integer.toString(count));
        countField.setTextColor(16777215);
        countField.setVisible(true);
        countField.setCanLoseFocus(true);
        countField.setFocused(false);
        guiDevTool.textFields.add(countField);
        
        y_pos += 15;
		
		guiDevTool.labels.add(new GuiPolyLabel(fr, x_pos +5, y_pos + 50, Format.getIntegerFromColor(new Color(90, 90, 90)), 
        		"Data: "));
        dataField = new GuiTextField(420, fr, x_pos + 40, y_pos + 49, (int) (guiDevTool.X_WIDTH * .6), 10);
        dataField.setMaxStringLength(9999); //don't really want a max length here
        String dataText = ""; //build text for data field
        for(BlockPos blockPos: blockListByPos.keySet()) {
        	dataText += dataText.isEmpty()?"":";"; // first add a ";" separator if there is already text in the field
        	dataText += blockListByPos.get(blockPos) + "," + blockPos.getX() + "," + blockPos.getY() + "," + blockPos.getZ();
        }
        dataField.setText(dataText);
        dataField.setTextColor(16777215);
        dataField.setVisible(true);
        dataField.setCanLoseFocus(true);
        dataField.setFocused(false);
        guiDevTool.textFields.add(dataField);
        
	}
	
	@Override
	public void updateValues() {
		this.pos2 = new BlockPos(Integer.parseInt(xPos2Field.getText())
				,Integer.parseInt(yPos2Field.getText())
				,Integer.parseInt(zPos2Field.getText()));
		this.count = Integer.parseInt(countField.getText());
		this.genType = cycleGenType.getCurrentOption();
		if(!dataField.getText().isEmpty()) {
			blockListByPos.clear();
			String[] blockAndPosArray = dataField.getText().split(";");
			for(String blockAndPos: blockAndPosArray) {
				String[] blockAndPosSplit = blockAndPos.split(",");
				if(blockAndPosSplit.length == 4) {
					blockListByPos.put(new BlockPos(Integer.parseInt(blockAndPosSplit[1]), 
							Integer.parseInt(blockAndPosSplit[2]), 
							Integer.parseInt(blockAndPosSplit[3])), new BlockDef(blockAndPosSplit[0], 0));
				}else {
					System.out.println("Invalid Data input for World Builder Element");
				}
			}
		}
		super.updateValues();
	}
	
	public Map<BlockPos, BlockDef> getBlockList(){
		return blockListByPos;
	}
	
	public GenType getGenType() {
		return genType;
	}
	
	@Override
	public NBTTagCompound save()
	{
		super.save();
		int pos2[] = {(int)this.pos2.getX(), (int)this.pos2.getY(), (int)this.pos2.getZ()};
		nbt.setIntArray("pos2",pos2);
		nbt.setInteger("count", count);
		nbt.setString("gentype", genType.name());
		if(blockListByPos.size() > 0) {
			NBTTagList nbtList = new NBTTagList();
			for(BlockPos blockPos: blockListByPos.keySet()) {
				NBTTagCompound blockEntry = new NBTTagCompound();
				int posArray[] = {blockPos.getX(), blockPos.getY(), blockPos.getZ()};
				blockEntry.setIntArray("blockPos",posArray);
//				blockEntry.setString("blockName", blockListByPos.get(blockPos).blockName);
				blockEntry.setTag("blockDef", blockListByPos.get(blockPos).save());
				nbtList.appendTag(blockEntry);
			}
			nbt.setTag("blockList", nbtList);
		}
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
		this.count = nbtFeat.getInteger("count");
		//In case we try to load an older NBT without this field
		if(!nbtFeat.getString("gentype").isEmpty())
			this.genType = GenType.valueOf(nbtFeat.getString("gentype"));
		else
			this.genType = GenType.TREES;	//trees is default because that's the only thing we had before adding this field
		NBTTagList blockList = (NBTTagList) nbtFeat.getTag("blockList");
		blockListByPos = new ConcurrentHashMap<BlockPos, BlockDef>();
		if(blockList != null) {
			for(int i = 0; i < blockList.tagCount(); i++) {
				NBTTagCompound blockEntry = blockList.getCompoundTagAt(i);
				int posArray[]=blockEntry.getIntArray("blockPos");
				if(blockEntry.hasKey("blockDef")) {
					blockListByPos.put(new BlockPos(posArray[0], posArray[1], posArray[2]), new BlockDef(blockEntry.getCompoundTag("blockDef")));
				}else	// include old loading version for backwards compatibility 
					blockListByPos.put(new BlockPos(posArray[0], posArray[1], posArray[2]), new BlockDef(blockEntry.getString("blockName"), 0));
			}
		}
	}
	
	@Override
	public BlockPos getPos2() {
		return pos2;
	}
	
	@Override
	public JsonObject saveJson()
	{
		super.saveJson();
		jobj.add("pos2", blockPosToJsonArray(pos2));
		jobj.addProperty("count", count);
		jobj.addProperty("genType", genType.name());
		if(blockListByPos.size() > 0) {
			JsonArray jarray = new JsonArray();
			for(BlockPos blockPos: blockListByPos.keySet()) {
				JsonObject blockEntry = new JsonObject();
				blockEntry.add("blockPos", blockPosToJsonArray(blockPos));
//				blockEntry.addProperty("blockName", blockListByPos.get(blockPos));
				blockEntry.add("blockDef", blockListByPos.get(blockPos).toJson());
				jarray.add(blockEntry);
			}
			jobj.add("blockList", jarray);
		}
		return jobj;
	}
	
	@Override
	public void loadJson(JsonObject featJson)
	{
		super.loadJson(featJson);
		this.pos2 = blockPosFromJsonArray(featJson.get("pos2").getAsJsonArray());
		this.count = featJson.get("count").getAsInt();
		this.genType = GenType.valueOf(featJson.get("genType").getAsString());
		blockListByPos = new ConcurrentHashMap<BlockPos, BlockDef>();	// initialize blockList to prevent errors 
		JsonElement blockList = featJson.get("blockList");
		if(blockList != null)
			for(JsonElement blockEntry: blockList.getAsJsonArray()) {
				JsonObject blockObj = blockEntry.getAsJsonObject();
				if((blockObj.has("blockName") || blockObj.has("blockDef")) && blockObj.has("blockPos"))
					if(blockObj.has("blockDef"))
						blockListByPos.put(blockPosFromJsonArray(blockObj.get("blockPos").getAsJsonArray()), new BlockDef(blockObj.get("blockDef").getAsJsonObject()));
					else	// include old loading version for backwards compatibility 
						blockListByPos.put(blockPosFromJsonArray(blockObj.get("blockPos").getAsJsonArray()), new BlockDef(blockObj.get("blockName").getAsString(), 0));
				else
					System.out.println("Block entry missing expected elements. Skipping block entry");
			}
	}
	
}
