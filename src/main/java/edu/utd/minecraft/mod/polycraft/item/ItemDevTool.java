package edu.utd.minecraft.mod.polycraft.item;

import java.util.ArrayList;
import java.util.Iterator;

import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.registry.GameData;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.block.BlockPolyPortal;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.entity.EntityOilSlimeBallProjectile;
import edu.utd.minecraft.mod.polycraft.entity.boss.AttackWarning;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.RenderBox;
import edu.utd.minecraft.mod.polycraft.inventory.territoryflag.TerritoryFlagBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.UseHoeEvent;

public class ItemDevTool extends ItemCustom  {
	
	int[] pos1= new int[3];
	int[] pos2 = new int[3];
	boolean pos1set = false;
	boolean pos2set = false;
	String tool;
	boolean setting;
	public static ArrayList renderboxes= new ArrayList();
	
	
	public ItemDevTool(CustomObject config) {
		super(config);
		this.setTextureName(PolycraftMod.getAssetName("gripped_engineered_diamond_axe"));
		//this.setCreativeTab(CreativeTabs.tabTools); //TODO: Take this out of CreativeTab and Make Command to access.
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
		this.setting =false;
	
	}
		
	@Override
	// Doing this override means that there is no localization for language
	// unless you specifically check for localization here and convert
	public String getItemStackDisplayName(ItemStack par1ItemStack)
	{
		return "Dev Tool";
	}  
	@Override
	public boolean  hitEntity(ItemStack p_77644_1_, EntityLivingBase p_77644_2_, EntityLivingBase p_77644_3_)
	{
		p_77644_2_.setHealth(0);
		return true;
	}

	
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
			float hitX, float hitY, float hitZ) {
		player.addChatMessage(new ChatComponentText("It worked right!  " + x + "::" + y + "::" + z));
		pos2[0] = x;
		pos2[1] = y;
		pos2[2] = z;
		pos2set = true;
		if(player.worldObj.isRemote)
			updateRenderBoxes();

		return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
	}
	
	@Override
	public boolean onBlockStartBreak(ItemStack itemstack, int X, int Y, int Z, EntityPlayer player) {
		// TODO Auto-generated method stub
		player.addChatMessage(new ChatComponentText("It worked left!  " + X + "::" + Y + "::" + Z));
		player.worldObj.scheduleBlockUpdate(X, Y, Z, player.worldObj.getBlock(X, Y, Z), 20);
		pos1[0] = X;
		pos1[1] = Y;
		pos1[2] = Z;
		pos1set = true;
		if(player.worldObj.isRemote)
			updateRenderBoxes();
		return true;
	}
	
	private void updateRenderBoxes() {
		renderboxes.clear();
		if(pos1[0] != 0 || pos1[1] != 0 ||pos1[2] != 0 || pos2[0] != 0 || pos2[1] != 0 ||pos2[2] != 0) {
			renderboxes.add(new RenderBox(pos1[0], pos1[2], pos2[0], pos2[2], pos1[1], 1, 2));
		}
	}
	
	@Override
	public void onUpdate(ItemStack itemstack, World world, Entity entity, int par4, boolean par5) {
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			
			
			
		}
		super.onUpdate(itemstack, world, entity, par4, par5);
	}
	
	public static void render(Entity entity) {
		synchronized (renderboxes) {
			Iterator<RenderBox> boxes = renderboxes.iterator();
			while (boxes.hasNext()) {
				RenderBox box = boxes.next();
				box.render(entity);
			}
		}
	}
}
