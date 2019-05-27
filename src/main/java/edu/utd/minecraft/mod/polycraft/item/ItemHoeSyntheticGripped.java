package edu.utd.minecraft.mod.polycraft.item;

import edu.utd.minecraft.mod.polycraft.config.GrippedSyntheticTool;
import net.minecraft.creativetab.CreativeTabs;

public class ItemHoeSyntheticGripped extends PolycraftHoe {
	public final GrippedSyntheticTool tool;
	public ItemHoeSyntheticGripped(ToolMaterial material, GrippedSyntheticTool tool)
	{
		super(material);
		setCreativeTab(CreativeTabs.tabCombat);
		this.tool = tool;
	}
}
