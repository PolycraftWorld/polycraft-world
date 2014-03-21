package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Plastic;

public class ItemScubaMask extends ItemArmor {

	private static final ArmorMaterial armorMaterial = EnumHelper.addArmorMaterial("scuba_mask", 0, new int[] { 0, 0, 0, 0 }, 0);

	private final Plastic plastic;

	public ItemScubaMask(final Plastic plastic) {
		super(armorMaterial, 1, 0);
		this.plastic = plastic;
	}

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
		return PolycraftMod.getTextureName("textures/models/armor/scuba_layer_1.png");
	}
}
