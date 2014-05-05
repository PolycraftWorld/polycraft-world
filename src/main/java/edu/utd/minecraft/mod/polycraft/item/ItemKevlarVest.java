package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;

public class ItemKevlarVest extends PolycraftArmorChest {

	private static ArmorMaterial getArmorMaterial(final CustomObject config) {
		final float kevlarArmorBuffPercent = config.params.getFloat(0);
		final int[] kevlarArmorReductionAmounts = new int[] {
				(int) (3 * kevlarArmorBuffPercent),
				(int) (8 * kevlarArmorBuffPercent),
				(int) (6 * kevlarArmorBuffPercent),
				(int) (3 * kevlarArmorBuffPercent)
		};

		return EnumHelper.addArmorMaterial(
				"kevlar_" + config.gameID,
				(int) (33 * kevlarArmorBuffPercent), kevlarArmorReductionAmounts,
				(int) (ItemArmor.ArmorMaterial.DIAMOND.getEnchantability() * kevlarArmorBuffPercent)
				);
	}

	public ItemKevlarVest(final CustomObject config) {
		super(getArmorMaterial(config), ArmorAppearance.CHAIN);
		this.setTextureName(PolycraftMod.getAssetName("kevlar_vest"));
		this.setCreativeTab(CreativeTabs.tabCombat);
	}

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
		return PolycraftMod.getAssetName("textures/models/armor/kevlar_layer_1.png");
	}
}
