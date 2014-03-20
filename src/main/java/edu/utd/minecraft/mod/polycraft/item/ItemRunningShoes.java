package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;
import edu.utd.minecraft.mod.polycraft.config.Plastic;

public class ItemRunningShoes extends ItemArmor {

	private static final ArmorMaterial armorMaterial = EnumHelper.addArmorMaterial("running_shoes", 0, new int[] { 0, 0, 0, 0 }, 0);

	private final Plastic plastic;

	public ItemRunningShoes(final Plastic plastic) {
		super(armorMaterial, 0, 3);
		this.plastic = plastic;
	}

	public float getWalkSpeedBuff() {
		return plastic.runningShoesWalkSpeedBuff;
	}
}
