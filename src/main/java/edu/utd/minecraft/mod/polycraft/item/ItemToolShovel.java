package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Tool;

public class ItemToolShovel extends PolycraftSpade {
	public final Tool tool;
	public ItemToolShovel(final Tool tool, final ToolMaterial material) {
		super(material);
		this.tool = tool;
		setCreativeTab(CreativeTabs.tabTools);
		setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(tool.getFullTypeName(Tool.Type.SHOVEL))));
	}
}
