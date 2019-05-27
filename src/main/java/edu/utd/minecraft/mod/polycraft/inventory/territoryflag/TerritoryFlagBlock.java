package edu.utd.minecraft.mod.polycraft.inventory.territoryflag;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;


import java.io.IOException;
import java.util.Collection;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.reflect.TypeToken;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityTerritoryFlag;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.SuperChunk;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.Chunk;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;
import edu.utd.minecraft.mod.polycraft.trading.ItemStackSwitch;
import edu.utd.minecraft.mod.polycraft.util.NetUtil;
import ibxm.Player;

public class TerritoryFlagBlock extends PolycraftInventoryBlock {
	
	public Collection<String> itemsToPull = Lists.newLinkedList();
	protected  Collection<String> itemsToPush = Lists.newLinkedList(); // were changed to not final
	protected  GsonBuilder gsonBuilderPull;// were changed to not final
	protected  GsonBuilder gsonBuilderPush;// were changed to not final
	private PrivateProperty privateProperty;
	private EntityPlayer player;
	private World world;
	private SuperChunk SC;

//	no icons in classes in 1.8
//	@SideOnly(Side.CLIENT)
//	public IIcon iconOutside;
//	@SideOnly(Side.CLIENT)
//	public IIcon iconTop;
//	@SideOnly(Side.CLIENT)
//	public IIcon iconInside;

	public TerritoryFlagBlock(final Inventory config, final Class tileEntityClass) {
		super(config, tileEntityClass, Material.iron, 7.5F);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		gsonBuilderPull= new GsonBuilder();
		gsonBuilderPush= new GsonBuilder();
		
		
	}
	
	
//	@Override
//	public void dropBlockAsItem(World world, BlockPos blockPos, IBlockState state, int fortunr)
//	{
//
//	}
	
	@Override
	public void onBlockPlacedBy(World worldObj, BlockPos blockPos, IBlockState state, EntityLivingBase entity, ItemStack itemToPlace)
	{
		if(!worldObj.isRemote)
		{
			this.world =worldObj;
			
			boolean placable=true;
			if(entity instanceof EntityPlayer )
			{
				this.player=(EntityPlayer) entity;
				worldObj.setBlockState( blockPos,Blocks.air.getDefaultState());
				for(int i=-7;i<((-7)+15);i++)
				{
					for(int k=-7;k<((-7)+15);k++)
					{
						
							
						if(!(worldObj.canBlockSeeSky(blockPos.add(i, 0, k)) && worldObj.getBlockState(blockPos.add(i, 0, k)).getBlock().isOpaqueCube()))
						{
							placable=false;	
						}
							
						
					}
				}
				if(placable)
				{
					int Cx;
					int Cz;
					Cx = (int)Math.floor(blockPos.getX()/32.0);
				    Cz = (int)Math.floor(blockPos.getZ()/32.0);
					this.SC= new SuperChunk(Cx,Cz);
					
					
					
					
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
									this.player.getDisplayNameString().toLowerCase());
							
							
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
					
					
					CreatePrivateProperty(this.SC);
					
					
					worldObj.setBlockState(blockPos,Blocks.air.getDefaultState());
					
					EntityTerritoryFlag flag = new EntityTerritoryFlag(worldObj,(EntityPlayer)entity, this.privateProperty);
					flag.setPosition(blockPos.getX()+.5D, blockPos.getY(), blockPos.getZ()+0.5D);
					//flag.setSuperChunk(this.SC);
					worldObj.spawnEntityInWorld(flag);
					//flag.onSpawnWithEgg((IEntityLivingData)null);
					super.onBlockPlacedBy(worldObj, blockPos, state, entity, itemToPlace);
					
					
					worldObj.setBlockState( blockPos.add(+3, 0, -3),Blocks.obsidian.getDefaultState());
					worldObj.setBlockState( blockPos.add(+3, 0, +3),Blocks.obsidian.getDefaultState());
					worldObj.setBlockState( blockPos.add(-3, 0, -3),Blocks.obsidian.getDefaultState());
					worldObj.setBlockState( blockPos.add(-3, 0, +3),Blocks.obsidian.getDefaultState());
					
					for(int i=-3;i<((-3)+7);i++)
					{
						for(int k=-3;k<((-3)+7);k++)
						{
							worldObj.setBlockState( blockPos.add(i, -1, k),Blocks.obsidian.getDefaultState());
						}
					}
					
					worldObj.setBlockState( blockPos.add(+3, +1, -3),Blocks.torch.getDefaultState());
					worldObj.setBlockState( blockPos.add(+3, +1, +3),Blocks.torch.getDefaultState());
					worldObj.setBlockState( blockPos.add(-3, +1, -3),Blocks.torch.getDefaultState());
					worldObj.setBlockState( blockPos.add(-3, +1, +3),Blocks.torch.getDefaultState());
					
					
					
					
				}
				else
				{
					((EntityPlayer) entity).addChatComponentMessage(new ChatComponentText("The area is not valid."));
					worldObj.setBlockState(blockPos,Blocks.air.getDefaultState());
				}
			}
			else 
			{
				super.onBlockPlacedBy(worldObj, blockPos, state, entity, itemToPlace);
			}
		}
	}


	private void CreatePrivateProperty(SuperChunk sC) {
		
		if(!this.world.isRemote) {
			int[][] c= sC.getChunks();
			PrivateProperty pp =  new PrivateProperty(
					false,
					(EntityPlayerMP) this.player,
					"Territory",
					"Temp",
					new Chunk(c[0][3],c[1][3]),
					new Chunk(c[0][1],c[1][1]),
					new int[] {0,3,4,5,6,44},
					0);
			//int[] perms = new int[] {0,3};
			//PermissionSet ps = new PermissionSet(perms);
			//pp.permissionOverridesByUser
			//pp.defaultPermissions
			//pp.masterPermissions
			
			Enforcer.addPrivateProperty(pp);	
			this.privateProperty=pp;	
			ServerEnforcer.INSTANCE.sendTempPPDataPackets();
		}
		
	}

}
