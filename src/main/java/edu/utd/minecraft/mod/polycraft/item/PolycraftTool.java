package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.item.Item.ToolMaterial;

public class PolycraftTool {
	public enum Material {
		Wooden(ToolMaterial.WOOD),
		Stone(ToolMaterial.STONE),
		Iron(ToolMaterial.IRON),
		Golden(ToolMaterial.GOLD),
		Diamond(ToolMaterial.EMERALD),
		Magic(ToolMaterial.EMERALD);

		public final ToolMaterial minecraftMaterial;

		Material(final ToolMaterial minecraftMaterial) {
			this.minecraftMaterial = minecraftMaterial;
		}
	}
}
