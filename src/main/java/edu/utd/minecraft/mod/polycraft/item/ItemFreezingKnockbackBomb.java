package edu.utd.minecraft.mod.polycraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ItemFreezingKnockbackBomb  extends ItemCustom{
	
	public ItemFreezingKnockbackBomb(CustomObject config) {
		super(config);
		this.setTextureName(PolycraftMod.getAssetName("freezing_knockback_bomb"));
		this.setCreativeTab(CreativeTabs.tabTools); //TODO: Take this out of CreativeTab and Make Command to access.
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
		
	}
	

}