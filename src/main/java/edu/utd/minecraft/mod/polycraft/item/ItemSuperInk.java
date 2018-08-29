package edu.utd.minecraft.mod.polycraft.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ItemSuperInk extends ItemCustom{
	
	public ItemSuperInk(CustomObject config) {
		super(config);
		this.setTextureName(PolycraftMod.getAssetName("super_Ink"));
		this.setCreativeTab(CreativeTabs.tabMisc); //TODO: Take this out of CreativeTab and Make Command to access.
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
		
	}
	
	 @Override
		// Doing this override means that there is no localization for language
		// unless you specifically check for localization here and convert
		public String getItemStackDisplayName(ItemStack par1ItemStack)
		{
		    return "Indelible Ink";
		}
	 

	 @SideOnly(Side.CLIENT)
	    public boolean hasEffect(ItemStack p_77636_1_)
	    {
		 	return false;
	    }

}
