package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;

import com.google.common.base.Preconditions;

import edu.utd.minecraft.mod.polycraft.config.GrippedSyntheticTool;

public class ItemPickaxeSyntheticGripped extends PolycraftPickaxe
{
	public final GrippedSyntheticTool tool;
	public ItemPickaxeSyntheticGripped(ToolMaterial material, GrippedSyntheticTool tool)
	{
		super(material);
		setCreativeTab(CreativeTabs.tabCombat);
		this.tool = tool;
	}
}