package edu.utd.minecraft.mod.polycraft.entity;

import java.util.List;

import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameData;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.PolycraftEntity;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.EntityOilSlime;
import edu.utd.minecraft.mod.polycraft.entity.entityliving.PolycraftEntityLiving;
import edu.utd.minecraft.mod.polycraft.render.PolyParticleSpawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityOilSlimeBallProjectile extends EntitySnowball{
	private static PolycraftEntity config;
	private final static String OIL_SLIME_BALL = "1hl";

	public EntityOilSlimeBallProjectile(World p_i1773_1_)
    {
        super(p_i1773_1_);
        
    }

    public EntityOilSlimeBallProjectile(World p_i1774_1_, EntityLivingBase p_i1774_2_)
    {
        super(p_i1774_1_, p_i1774_2_);
    }

    public EntityOilSlimeBallProjectile(World p_i1775_1_, double p_i1775_2_, double p_i1775_4_, double p_i1775_6_)
    {
        super(p_i1775_1_, p_i1775_2_, p_i1775_4_, p_i1775_6_);
    }
    
    public static final void register(final PolycraftEntity polycraftEntity) {
		EntityOilSlimeBallProjectile.config = polycraftEntity;
		PolycraftEntityLiving.register(EntityOilSlimeBallProjectile.class, config.entityID, config.name);
	}

    

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    @Override
    protected void onImpact(MovingObjectPosition p_70184_1_)
    {
    	if (!this.worldObj.isRemote)
        {
	        if (p_70184_1_.entityHit != null)
	        {
	            byte b0 = 3;
	            
	
	            if (p_70184_1_.entityHit instanceof EntityOilSlime)
	            {
	                b0 = 0;
	                //((EntityOilSlime) p_70184_1_.entityHit).heal();
	            }
	
	            p_70184_1_.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.getThrower()), (float)b0);
	        }
	        else
	        {
	        	worldObj.setBlockState( new BlockPos((int)posX, (int)posY, (int)posZ),PolycraftMod.blockOil.getDefaultState());
	        }
	
	        
	        this.setDead();
        }
    	else
    	{
    		for (int i = 0; i < 8; ++i)
	        {
	        	PolyParticleSpawner.EntityBreakingParticle(GameData.getItemRegistry().getObject(PolycraftMod.getAssetName(OIL_SLIME_BALL)), this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
	           
	        }
    	}
    }

}
