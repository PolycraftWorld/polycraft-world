package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;
import edu.utd.minecraft.mod.polycraft.config.Plastic;

public class ItemJetPack extends ItemArmor {

	private static final ArmorMaterial armorMaterial = EnumHelper.addArmorMaterial("jet_pack", 0, new int[] { 0, 0, 0, 0 }, 0);

	private final Plastic plastic;

	public ItemJetPack(final Plastic plastic, final int fuelSeconds) {
		super(armorMaterial, 1, 1);
		this.setMaxDamage(fuelSeconds);
		this.plastic = plastic;
	}

	public float getFlySpeedBuff() {
		return plastic.jetPackFlySpeedBuff;
	}
}
