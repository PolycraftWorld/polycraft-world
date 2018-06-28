package edu.utd.minecraft.mod.polycraft.inventory.territoryflag;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;


import java.io.IOException;
import java.util.Collection;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityTerritoryFlag;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.SuperChunk;
import edu.utd.minecraft.mod.polycraft.trading.ItemStackSwitch;
import edu.utd.minecraft.mod.polycraft.util.NetUtil;
import ibxm.Player;

public class TerritoryFlagBlock extends PolycraftInventoryBlock {
	
	public Collection<String> itemsToPull = Lists.newLinkedList();
	protected  Collection<String> itemsToPush = Lists.newLinkedList(); // were changed to not final
	protected  GsonBuilder gsonBuilderPull;// were changed to not final
	protected  GsonBuilder gsonBuilderPush;// were changed to not final


	@SideOnly(Side.CLIENT)
	public IIcon iconOutside;
	@SideOnly(Side.CLIENT)
	public IIcon iconTop;
	@SideOnly(Side.CLIENT)
	public IIcon iconInside;

	public TerritoryFlagBlock(final Inventory config, final Class tileEntityClass) {
		super(config, tileEntityClass, Material.iron, 7.5F);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		gsonBuilderPull= new GsonBuilder();
		gsonBuilderPush= new GsonBuilder();
		
	}
	
	
	@Override
	protected void dropBlockAsItem(World world, int x, int y, int z, ItemStack itemstack)
	{
		
	}
	
	@Override
	public void onBlockPlacedBy(World worldObj, int xPos, int yPos, int zPos, EntityLivingBase entity, ItemStack itemToPlace) 
	{
		if(!worldObj.isRemote)
		{
			
			boolean placable=true;
			if(entity instanceof EntityPlayer )
			{
				worldObj.setBlock( xPos, yPos, zPos,Blocks.air);
				int j=yPos;
				for(int i=xPos-7;i<((xPos-7)+15);i++)
				{
					for(int k=zPos-7;k<((zPos-7)+15);k++)
					{
						
							
						if(!(worldObj.canBlockSeeTheSky(i, j, k) && worldObj.getBlock(i, j-1, k).isOpaqueCube()))
						{
							placable=false;	
						}
							
						
					}
				}
				if(placable)
				{
					int Cx;
					int Cz;
					if(xPos<0)
						Cx = (int)Math.floor(xPos/32.0);
					else
						Cx = (int)Math.ceil(xPos/32.0);
					if(zPos<0)
				        Cz = (int)Math.floor(zPos/32.0);
					else
						Cz = (int)Math.ceil(zPos/32.0);
					SuperChunk SC= new SuperChunk(Cx,Cz);
					
					
					
					
					try {

						if (ServerEnforcer.portalRestUrl != null)
						{
							
							JsonObject info = new JsonObject();
							info.addProperty("schunkx", Cx);
							info.addProperty("schunkz", Cz);
							info.addProperty("government", 1);
							
							
				
							String jsonToSend = gsonBuilderPush.create().toJson(info, new TypeToken<JsonObject>() {
							}.getType());
							String sendString = String.format("%s/players/%s/government/1/attempt_claim/",
									ServerEnforcer.portalRestUrl,
									((EntityPlayer) entity).getDisplayName().toLowerCase());
							
							
							String contentFromPortal = NetUtil.postInventory(sendString, jsonToSend);

							if (contentFromPortal == null)
							{
								//return false
							}//did not get a confirm string from the portal - don't sync

							//return true;
						}

					} catch (final IOException e) {
						PolycraftMod.logger.error("Unable to send territoryFlag info", e);
						//return false;
					}
					
					///////////////////////////////////////////////////////////////////////
					
					
					
					worldObj.setBlock( xPos, yPos, zPos,Blocks.air);
					
					
					EntityTerritoryFlag flag = new EntityTerritoryFlag(worldObj);
					flag.setPosition(((double)xPos)+.5D, (double)yPos, ((double)zPos)+0.5D);
					flag.setSuperChunk(SC);
					worldObj.spawnEntityInWorld(flag);
					//flag.onSpawnWithEgg((IEntityLivingData)null);
					super.onBlockPlacedBy(worldObj, xPos, yPos, zPos, entity, itemToPlace);
					
					
					worldObj.setBlock( xPos+3, yPos, zPos-3,Blocks.obsidian);
					worldObj.setBlock( xPos+3, yPos, zPos+3,Blocks.obsidian);
					worldObj.setBlock( xPos-3, yPos, zPos-3,Blocks.obsidian);
					worldObj.setBlock( xPos-3, yPos, zPos+3,Blocks.obsidian);
					
					for(int i=xPos-3;i<((xPos-3)+7);i++)
					{
						for(int k=zPos-3;k<((zPos-3)+7);k++)
						{
							worldObj.setBlock( i, yPos-1, k,Blocks.obsidian);
						}
					}
					
					worldObj.setBlock( xPos+3, yPos+1, zPos-3,Blocks.torch);
					worldObj.setBlock( xPos+3, yPos+1, zPos+3,Blocks.torch);
					worldObj.setBlock( xPos-3, yPos+1, zPos-3,Blocks.torch);
					worldObj.setBlock( xPos-3, yPos+1, zPos+3,Blocks.torch);
					
					
				}
				else
				{
					((EntityPlayer) entity).addChatComponentMessage(new ChatComponentText("The area is not valid."));
					worldObj.setBlock( xPos, yPos, zPos,Blocks.air);
				}
			}
			else 
			{
				super.onBlockPlacedBy(worldObj, xPos, yPos, zPos, entity, itemToPlace);
			}
		}
	}

}
