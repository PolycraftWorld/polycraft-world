package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;

public class ItemKevlarVest extends ItemArmor {

	private static final float kevlarArmorBuffPercent = 1 + PolycraftMod.itemKevlarArmorBuff;
	public static final int[] kevlarArmorReductionAmounts = new int[] { (int) (3 * kevlarArmorBuffPercent), (int) (8 * kevlarArmorBuffPercent), (int) (6 * kevlarArmorBuffPercent), (int) (3 * kevlarArmorBuffPercent) };
	public static final ArmorMaterial kevlarArmorType = EnumHelper.addArmorMaterial(
			"kevlar", (int) (33 * kevlarArmorBuffPercent), kevlarArmorReductionAmounts, (int) (ItemArmor.ArmorMaterial.DIAMOND.getEnchantability() * kevlarArmorBuffPercent));

	public ItemKevlarVest() {
		super(kevlarArmorType, 1, 1);
		this.setTextureName(PolycraftMod.getTextureName("kevlar_vest"));
	}

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
		return PolycraftMod.getTextureName("textures/models/armor/kevlar_layer_1.png");
	}
}
