package edu.utd.minecraft.mod.polycraft.trading;

import java.io.IOException;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.util.NetUtil;

public class InventorySwap {

	protected Collection<ItemStackSwitch> itemsToPull = Lists.newLinkedList();
	protected final Collection<ItemStackSwitch> itemsToPush = Lists.newLinkedList();
	protected final GsonBuilder gsonBuilder;

	public InventorySwap(EntityPlayer player)
	{
		gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(ItemStackSwitch.class,
				new ItemStackSwitch.Deserializer(player));
		gsonBuilder.registerTypeAdapter(ItemStackSwitch.class,
				new ItemStackSwitch.Serializer());
	}

	public void pushItemToPortal(final ItemStackSwitch itemStackSwitch)
	{
		if (itemStackSwitch.itemStack != null)
			itemsToPush.add(itemStackSwitch);
	}

	public ItemStack pullNextItemFromPortal()
	{
		return itemsToPull.iterator().next().itemStack;
	}

	public boolean doesNextItemFromPortalExist()
	{
		return itemsToPull.iterator().hasNext();
	}

	public boolean swapPlayerInventoryWithPortal(final EntityPlayer player)
	{

		try {

			String contentFromPortal = NetUtil.post(String.format("%s/players/%s/", ServerEnforcer.portalRestUrl, player.getDisplayName().toLowerCase()), ImmutableMap.of(player.getDisplayName().toLowerCase(),
					gsonBuilder.create().toJson(itemsToPush, new TypeToken<Collection<ItemStackSwitch>>() {
					}.getType())));

			final Gson gson = gsonBuilder.create();
			final Collection<ItemStackSwitch> pulledItemStackSwitches = gson.fromJson(
					contentFromPortal,
					new TypeToken<Collection<ItemStackSwitch>>() {
					}.getType());

			if (pulledItemStackSwitches != null) {
				itemsToPull.addAll(pulledItemStackSwitches);
			}

			return true;

		} catch (final IOException e) {
			PolycraftMod.logger.error("Unable to sync items", e);
			return false;
		}

	}

}
