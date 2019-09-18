package edu.utd.minecraft.mod.polycraft.item;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.aitools.AIToolSettingsRoomGen;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialOptions;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.item.ItemDevTool.StateEnum;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemAITool extends ItemCustom {
	
	//protected NBTTagCompound nbt = new NBTTagCompound();
	public AIToolSettingsRoomGen roomGen = new AIToolSettingsRoomGen();
	protected int roomWidth;
	protected int roomLength;
	protected int roomHeight;
	protected BlockType blockType;
	protected boolean walls;
	
	//String outputFileName = "output";
	String outputFileExt = ".psm";
	
	public ItemAITool(CustomObject config) {
		super(config);
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
		this.roomLength=1;
		this.roomWidth=1;
		this.roomHeight=1;
		blockType = BlockType.STONE;
		this.walls=false;
	}
	
	public enum BlockType{
		STONE,
		GRASS,
		SAND,
		WATER,
		SNOW
	};
	
	@Override
	// Doing this override means that there is no localization for language
	// unless you specifically check for localization here and convert
	public String getItemStackDisplayName(ItemStack par1ItemStack)
	{
		return "AI Tool";
	}  
	
	@Override
	public ItemStack onItemRightClick(ItemStack p_77659_1_, World world, EntityPlayer player) {
			
		if(player.isSneaking()) {
			//currentState = currentState.next();
			//player.addChatMessage(new ChatComponentText(currentState.toString() + "Mode"));
			if(world.isRemote)
				PolycraftMod.proxy.openAIToolGui(player);
		}
		return super.onItemRightClick(p_77659_1_, world, player);
	}
	
	
	public void save(String fileName)
	{
		NBTTagCompound nbt = new NBTTagCompound();	//erase current nbt so we don't get duplicates?
		this.roomGen.width=this.roomWidth;
		this.roomGen.length=this.roomLength;
		this.roomGen.height=this.roomHeight;
		this.roomGen.walls=this.walls;
		this.roomGen.blockTypeID=this.blockType.ordinal();
		nbt.setTag("roomgen", this.roomGen.save());
		
		FileOutputStream fout = null;
		try {
			File configDir;
			configDir = PolycraftMod.configDirectory;
			
		 String directoryName = PolycraftMod.configDirectory.toString()+"\\AIToolSaves";
		    File directory = new File(directoryName);
		    if (! directory.exists()){
		        directory.mkdir();
		        // If you require it to make the entire directory path including parents,
		        // use directory.mkdirs(); here instead.
		    }
			
			File file = new File( PolycraftMod.configDirectory.toString()+"\\AIToolSaves\\"+fileName);
			fout = new FileOutputStream(file);
			
			if (!file.exists()) {
				file.createNewFile();
			}
			CompressedStreamTools.writeCompressed(nbt, fout);
			fout.flush();
			fout.close();
			
		}catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		
		}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}
	
	public Boolean load(String fileName)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		FileInputStream fin = null;
		try {
			File configDir;
			configDir = PolycraftMod.configDirectory;
			
			File file = new File( PolycraftMod.configDirectory.toString()+"\\AIToolSaves\\"+fileName);
			fin = new FileInputStream(file);
			
			if (!file.exists()) {
				fin.close();
				return false;
			}
			nbt=CompressedStreamTools.readCompressed(fin);
			fin.close();
			
		}catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return false;
		
		}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
		}
		
		NBTTagCompound nbtRoomGen = (NBTTagCompound) nbt.getTag("roomgen");
		this.roomWidth=nbtRoomGen.getInteger("width");
		this.roomLength=nbtRoomGen.getInteger("length");
		this.roomHeight=nbtRoomGen.getInteger("height");
		this.walls=nbtRoomGen.getBoolean("walls");
		
		this.blockType=BlockType.values()[nbtRoomGen.getInteger("block")];
		return true;
	}
	
	public List<String> getFileNames()
	{
		try (Stream<Path> walk = Files.walk(Paths.get(PolycraftMod.configDirectory.toString()+"\\AIToolSaves"))) {

			List<String> result = walk.filter(Files::isRegularFile)
					.map(x -> x.toString()).collect(Collectors.toList());

			return result;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public BlockType getBlockType()
	{
		return this.blockType;
	}
	
	public void setBlockType(BlockType blockType)
	{
		this.blockType=blockType;
	}
	
	public boolean getWalls() {
		return this.walls;
	}
	
	public void setWalls(boolean walls) {
		this.walls=walls;
	}
	
	public int getHeight() {
		return this.roomHeight;
	}
	
	public void setHeight(int height) {
		this.roomHeight=height;
	}

	public int getWidth() {
		return this.roomWidth;
	}
	
	public int getLength() {
		return this.roomLength;
	}
	
	public void setWidth(int width) {
		this.roomWidth=width;
	}
	
	public void setLength(int length) {
		this.roomLength=length;
	}
	
	

}
