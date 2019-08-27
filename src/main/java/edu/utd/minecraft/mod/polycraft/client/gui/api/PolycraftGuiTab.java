package edu.utd.minecraft.mod.polycraft.client.gui.api;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class PolycraftGuiTab extends CreativeTabs{
	
	private boolean horizontal = true; //false for vertical tabs
	private static final ResourceLocation tabIcon = new ResourceLocation(PolycraftMod.MODID,
			"textures/gui/computer_stuff/icons.png");	//this is an example taken from ComputerGui
	
	public PolycraftGuiTab(String label) {
		super(label);
		this.setBackgroundImageName("temp.png");
	}

	@Override
	public Item getTabIconItem() {
		return Items.apple;//TODO: icon on tab
	}
	
	//TODO: Should we draw the tab contents here or somewhere else?

}
