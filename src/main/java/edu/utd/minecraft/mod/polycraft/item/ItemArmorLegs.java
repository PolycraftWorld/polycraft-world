package edu.utd.minecraft.mod.polycraft.item;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.Armor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemArmorLegs extends PolycraftArmorLeggings {

	private Armor armor;

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkArmor(player, ArmorSlot.LEGS, ItemArmorLegs.class);
	}

	public static ItemArmorLegs getEquippedItem(final EntityPlayer player) {
		return PolycraftItemHelper.getArmorItem(player, ArmorSlot.LEGS);
	}

	public ItemArmorLegs(final Armor armor, final ArmorMaterial material) {
		super(material, ArmorAppearance.CHAIN);
		this.armor = armor;
		//this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(armor.getFullComponentName(ArmorSlot.LEGS))));
		//this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(armor.name)));
		this.setCreativeTab(CreativeTabs.tabCombat);
		//		try {
		//			this.armor.saveArmorPNG(ArmorSlot.LEGS);
		//		} catch (IOException e) {
		//			// TODO Uncomment if you want to auto-generate armor textures
		//			e.printStackTrace();
		//		}
	}

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
		//return armor.getTexture();
		return PolycraftMod.getAssetNameString("textures/models/armor/" + PolycraftMod.getFileSafeName(armor.name) + "_layer2.png");
	}

	@Override
	public void onCreated(final ItemStack itemStack, final World world, final EntityPlayer player) {
		if (armor.aquaAffinityLevel > 0) {
			if (!itemStack.isItemEnchanted())
				itemStack.addEnchantment(Enchantment.aquaAffinity, armor.aquaAffinityLevel);
		}
	}

}
