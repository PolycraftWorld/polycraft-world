package edu.utd.minecraft.mod.polycraft.item;

import edu.utd.minecraft.mod.polycraft.config.GrippedSyntheticTool;
import net.minecraft.creativetab.CreativeTabs;

public class ItemShovelSyntheticGripped extends PolycraftSpade
{
	public final GrippedSyntheticTool tool;
	public ItemShovelSyntheticGripped(ToolMaterial material, GrippedSyntheticTool tool)
	{
		super(material);
		setCreativeTab(CreativeTabs.tabTools);
		this.tool = tool;
	}
}
