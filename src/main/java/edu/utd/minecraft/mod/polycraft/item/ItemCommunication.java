package edu.utd.minecraft.mod.polycraft.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;

public class ItemCommunication extends ItemCustom implements PolycraftCommunicationItem {

	public ItemCommunication(CustomObject config) {
		super(config);
		this.setMaxDamage(30000);
		// TODO Auto-generated constructor stub
	}

	@Override
	public CustomObject getCustomObject() {
		// TODO Auto-generated method stub
		return config;
	}

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World worldObj, EntityPlayer player)
	{

		if (itemStack.getDisplayNameString().equalsIgnoreCase("cell phone"))
		{
			this.setDamage(itemStack, 8060);
		}

		else if (itemStack.getDisplayNameString().equalsIgnoreCase("smart phone"))
		{
			this.setDamage(itemStack, 24960);
		}
		else
		{

			if (player.isSneaking())
			{
				if (this.getDamage(itemStack) < 1069)
					this.setDamage(itemStack, (this.getDamage(itemStack) + 10));
				else
					this.setDamage(itemStack, 879);

			}
			else if (player.isSprinting())
			{
				if (this.getDamage(itemStack) > 881)
					this.setDamage(itemStack, (this.getDamage(itemStack) - 2));
				else
					this.setDamage(itemStack, 1079);

			}
			else
			{
				if (this.getDamage(itemStack) < 1078)
					this.setDamage(itemStack, (this.getDamage(itemStack) + 2));
				else
					this.setDamage(itemStack, 879);
			}
		}

		return itemStack;
	}

	@Override
	public void onUpdate(ItemStack itemStack, World worldObj, Entity player, int p_77663_4_, boolean p_77663_5_) {

		super.onUpdate(itemStack, worldObj, player, p_77663_4_, p_77663_5_);
		if (this.getDamage(itemStack) < 879)
			this.setDamage(itemStack, 879);
	}

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkCurrentEquippedItem(player, ItemCommunication.class);
	}

	public static ItemStack getEquippedItemStack(final EntityPlayer player) {
		return player.getCurrentEquippedItem();
	}

	public static double getFrequency(final EntityPlayer player) {
		return isEquipped(player) ? getEquippedItem(player).getFrequency(getEquippedItemStack(player)) : 0;
	}

	public static ItemCommunication getEquippedItem(final EntityPlayer player) {
		return PolycraftItemHelper.getCurrentEquippedItem(player);
	}

	private double getFrequency(final ItemStack itemStack) {

		return itemStack.getItemDamage();
	}
}
