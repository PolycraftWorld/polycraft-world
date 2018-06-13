package edu.utd.minecraft.mod.polycraft.item;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.entity.EntityOilSlimeBallProjectile;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemOilSlimeBall extends ItemCustom{
	
	
	public ItemOilSlimeBall(final CustomObject config, String iconName) {
		super(config);
		this.setTextureName(PolycraftMod.getAssetName(iconName));
		this.setCreativeTab(CreativeTabs.tabMisc);
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
	}
	
	

	
	 @Override
	    // Doing this override means that there is no localization for language
	    // unless you specifically check for localization here and convert
	    public String getItemStackDisplayName(ItemStack par1ItemStack)
	    {
	        return "Oil Slimeball";
	    }  

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack p_77659_1_, World p_77659_2_, EntityPlayer p_77659_3_)
    {
        if (!p_77659_3_.capabilities.isCreativeMode)
        {
            --p_77659_1_.stackSize;
        }

        p_77659_2_.playSoundAtEntity(p_77659_3_, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        if (!p_77659_2_.isRemote)
        {
            p_77659_2_.spawnEntityInWorld(new EntityOilSlimeBallProjectile(p_77659_2_, p_77659_3_));
        }

        return p_77659_1_;
    }

}
