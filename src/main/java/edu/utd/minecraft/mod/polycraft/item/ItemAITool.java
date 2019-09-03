package edu.utd.minecraft.mod.polycraft.item;

import java.awt.Color;
import java.util.ArrayList;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialOptions;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialFeature.TutorialFeatureType;
import edu.utd.minecraft.mod.polycraft.item.ItemDevTool.StateEnum;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class ItemAITool extends ItemCustom {
	
	protected NBTTagCompound nbt = new NBTTagCompound();
	protected int roomWidth;
	protected int roomLength;
	protected boolean walls;
	
	public ItemAITool(CustomObject config) {
		super(config);
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
		this.roomLength=1;
		this.roomWidth=1;
		this.walls=false;
	}
	
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
	
	
	public NBTTagCompound save()
	{
		nbt = new NBTTagCompound();	//erase current nbt so we don't get duplicates?
		
		nbt.setInteger("width", this.roomWidth);
		nbt.setInteger("length", this.roomLength);
		nbt.setBoolean("walls", this.walls);
		return nbt;
	}
	
	public void load(NBTTagCompound nbtFeat)
	{
		this.roomWidth=nbtFeat.getInteger("width");
		this.roomLength=nbtFeat.getInteger("length");
		this.walls=nbtFeat.getBoolean("walls");
	}
	
	public boolean getWalls() {
		return this.walls;
	}
	
	public void setWalls(boolean walls) {
		this.walls=walls;
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
