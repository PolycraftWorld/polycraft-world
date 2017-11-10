package edu.utd.minecraft.mod.polycraft.inventory.territoryflag;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.config.Inventory;
import edu.utd.minecraft.mod.polycraft.entity.npc.EntityTerritoryFlag;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import edu.utd.minecraft.mod.polycraft.privateproperty.SuperChunk;
import ibxm.Player;

public class TerritoryFlagBlock extends PolycraftInventoryBlock {

	@SideOnly(Side.CLIENT)
	public IIcon iconOutside;
	@SideOnly(Side.CLIENT)
	public IIcon iconTop;
	@SideOnly(Side.CLIENT)
	public IIcon iconInside;

	public TerritoryFlagBlock(final Inventory config, final Class tileEntityClass) {
		super(config, tileEntityClass, Material.iron, 7.5F);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
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
					EntityTerritoryFlag flag = new EntityTerritoryFlag(worldObj);
					flag.setPosition(((double)xPos)+.5D, (double)yPos, ((double)zPos)+0.5D);
					flag.setSuperChunk(SC);
					worldObj.spawnEntityInWorld(flag);
					super.onBlockPlacedBy(worldObj, xPos, yPos, zPos, entity, itemToPlace);
					worldObj.setBlock( xPos, yPos, zPos,Blocks.air);
					
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
