package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Tool;

public class ItemToolHoe extends PolycraftHoe {
	public final Tool tool;
	public ItemToolHoe(final Tool tool, final ToolMaterial material) {
		super(material);
		this.tool = tool;
		setCreativeTab(CreativeTabs.tabTools);
		//setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(tool.getFullTypeName(Tool.Type.HOE))));
	}
	
}
