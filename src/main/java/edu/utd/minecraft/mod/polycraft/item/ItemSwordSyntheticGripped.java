package edu.utd.minecraft.mod.polycraft.item;

import edu.utd.minecraft.mod.polycraft.config.GrippedSyntheticTool;
import net.minecraft.creativetab.CreativeTabs;

public class ItemSwordSyntheticGripped extends PolycraftSword
{
	public final GrippedSyntheticTool tool;
	public ItemSwordSyntheticGripped(ToolMaterial material, GrippedSyntheticTool tool)
	{
		super(material);
		setCreativeTab(CreativeTabs.tabCombat);
		this.tool = tool;
	}
}
