package edu.utd.minecraft.mod.polycraft.inventory.textwall;

import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemHangingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;

public class IRBTextWallItem extends ItemHangingEntity {

	public IRBTextWallItem(Class p_i45342_1_) {
		super(p_i45342_1_);
		// TODO Auto-generated constructor stub
	}
	
	public boolean OnItemUse(ItemStack playerItemStack, EntityPlayer player, World world, int tileX, int tileY, int tileZ, int facingDirection, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
		 if (facingDirection == 0)
	        {
	            return false;
	        }
	        else if (facingDirection == 1)
	        {
	            return false;
	        }
	        else
	        {
	            int i1 = Direction.facingToDirection[facingDirection];
	            EntityHanging entityhanging = this.createHangingEntity(world, tileX, tileY, tileZ, i1);

	            if (!player.canPlayerEdit(tileX, tileY, tileZ, facingDirection, playerItemStack))
	            {
	                return false;
	            }
	            else
	            {
	                if (entityhanging != null && entityhanging.onValidSurface())
	                {
	                    if (!world.isRemote)
	                    {
	                        world.spawnEntityInWorld(entityhanging);
	                    }

	                    --playerItemStack.stackSize;
	                }

	                return true;
	            }
	        }
	}
	
	 /**
     * Create the hanging entity associated to this item.
     */
    private EntityHanging createHangingEntity(World world, int xPos, int yPos, int zPos, int facingDirection)
    {
        return (EntityHanging) new IRBTextWallEntity(world, xPos, yPos, zPos, facingDirection);
    }
}
