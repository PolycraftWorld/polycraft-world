package edu.utd.minecraft.mod.polycraft.trading;

import java.io.IOException;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.util.NetUtil;

public class InventorySwap {

	public Collection<ItemStackSwitch> itemsToPull = Lists.newLinkedList();
	protected final Collection<ItemStackSwitch> itemsToPush = Lists.newLinkedList();
	protected final GsonBuilder gsonBuilderPull;
	protected final GsonBuilder gsonBuilderPush;

	public InventorySwap(EntityPlayer player)
	{
		gsonBuilderPull = new GsonBuilder();
		gsonBuilderPull.registerTypeAdapter(ItemStackSwitch.class,
				new ItemStackSwitch.Deserializer(player));
		gsonBuilderPush = new GsonBuilder();
		gsonBuilderPush.registerTypeAdapter(ItemStackSwitch.class,
				new ItemStackSwitch.Serializer());
	}

	public void pushItemToPortal(final ItemStackSwitch itemStackSwitch)
	{
		if (itemStackSwitch.itemStack != null)
			itemsToPush.add(itemStackSwitch);
	}

	//	public ItemStack pullNextItemFromPortal()
	//	{
	//		return itemsToPull.iterator().next().itemStack;
	//	}
	//
	//	public boolean doesNextItemFromPortalExist()
	//	{
	//		return itemsToPull.iterator().hasNext();
	//	}

	public boolean swapPlayerInventoryWithPortal(final EntityPlayer player)
	{

		try {

			if (ServerEnforcer.portalRestUrl != null)
			{
				String jsonToSend = gsonBuilderPush.create().toJson(itemsToPush, new TypeToken<Collection<ItemStackSwitch>>() {
				}.getType());
				String sendString = String.format("%s/players/%s/inventory/",
						ServerEnforcer.portalRestUrl,
						player.getDisplayName().toLowerCase());

				String contentFromPortal = NetUtil.postInventory(sendString, jsonToSend);

				if (contentFromPortal == null)
					return false; //did not get a confirm string from the portal - don't sync

				final Gson gson = gsonBuilderPull.create();
				final Collection<ItemStackSwitch> pulledItemStackSwitches = gson.fromJson(
						contentFromPortal,
						new TypeToken<Collection<ItemStackSwitch>>() {
						}.getType());

				if (pulledItemStackSwitches != null) {
					itemsToPull.addAll(pulledItemStackSwitches);
				}

				return true;
			}
			else
				return false;

		} catch (final IOException e) {
			PolycraftMod.logger.error("Unable to sync items", e);
			return false;
		}

	}
}
