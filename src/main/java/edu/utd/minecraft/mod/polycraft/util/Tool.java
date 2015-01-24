package edu.utd.minecraft.mod.polycraft.util;

import net.minecraft.item.Item.ToolMaterial;

public class Tool {
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
