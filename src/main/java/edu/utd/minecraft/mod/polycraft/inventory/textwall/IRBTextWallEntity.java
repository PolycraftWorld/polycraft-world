package edu.utd.minecraft.mod.polycraft.inventory.textwall;

import cpw.mods.fml.common.registry.GameRegistry;
import edu.utd.minecraft.mod.polycraft.PolycraftRegistry;
import edu.utd.minecraft.mod.polycraft.entity.PolycraftEntityHanging;
import edu.utd.minecraft.mod.polycraft.inventory.PolycraftInventoryBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class IRBTextWallEntity extends PolycraftEntityHanging {


	public IRBTextWallEntity(World world) {
		super(world);
		// TODO Auto-generated constructor stub
	}

	public IRBTextWallEntity(World world, int xPos, int yPos, int zPos, int facingDirection) {
		super(world, xPos, yPos, zPos, facingDirection);
		// TODO Auto-generated constructor stub
	}


	@Override
	public int getWidthPixels() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getHeightPixels() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void onBroken(Entity p_110128_1_) {
		// TODO Auto-generated method stub
		
	}
	
	/**
     * Called when the entity is attacked.
     */
	@Override
    public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_)
    {
		
		//If this is a player, then 
		
		
        if (this.isEntityInvulnerable())
        {
            return false;
        }
        else
        {
            if (!this.isDead && !this.worldObj.isRemote)
            {
                //this.setDead();
                this.setBeenAttacked();
                this.onBroken(p_70097_1_.getEntity());
            }

            return true;
        }
    }

}
