package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
import edu.utd.minecraft.mod.polycraft.config.MoldedItem;

public class ItemCleats extends PolycraftArmorFeet {

	private static final ArmorSlot armorSlot = ArmorSlot.FEET;

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkArmor(player, armorSlot, ItemCleats.class);
	}

	public static ItemCleats getEquippedItem(final EntityPlayer player) {
		return PolycraftItemHelper.getArmorItem(player, armorSlot);
	}
	
	public ItemCleats(CustomObject config) {
		super(PolycraftMod.armorMaterialNone, ArmorAppearance.GOLD);
		this.setTextureName(PolycraftMod.getAssetName("cleats"));
		this.setCreativeTab(CreativeTabs.tabTransport);
		if (config.maxStackSize > 0)
			this.setMaxStackSize(config.maxStackSize);
	}
	
	

	@Override
	public String getArmorTexture(final ItemStack stack, final Entity entity, final int slot, final String type) {
		return PolycraftMod.getAssetName("textures/models/armor/running_shoes_layer_1.png");
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		// TODO Auto-generated method stub
		super.onArmorTick(world, player, itemStack);
		if(!world.isRemote) {
			player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 5, 1, true));
		}
	}

}