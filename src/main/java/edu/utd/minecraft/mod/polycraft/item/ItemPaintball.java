package edu.utd.minecraft.mod.polycraft.item;

import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ItemPaintball extends ItemCustom {

	public ItemPaintball(CustomObject config) {
		super(config);
		this.setCreativeTab(CreativeTabs.tabMisc); // TODO: Take this out of CreativeTab and Make Command to access.
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getItemStackDisplayName(ItemStack parItemstack) {
		return "Paintball";
	}
}
