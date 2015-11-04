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

	protected final Collection<ItemStackSwitch> itemsToPull = Lists.newLinkedList();
	protected final Collection<ItemStackSwitch> itemsToPush = Lists.newLinkedList();
	protected final GsonBuilder gsonBuilder;

	public InventorySwap(EntityPlayer player)
	{
		gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(ItemStackSwitch.class,
				new ItemStackSwitch.Deserializer(player, null));

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

	public int pullPlayerInventoryFromPortal(final EntityPlayer player)
	{
		if (ServerEnforcer.portalRestUrl != null) {
			try {
				String url = ServerEnforcer.portalRestUrl.startsWith("file:")
						? ServerEnforcer.portalRestUrl + "playerinventoryswap.json"
						//TODO eventually send a timestamp of the last successful pull, so the server can return no-change (which is probably most of the time)
						: String.format("%s/private_properties/worlds/include/", ServerEnforcer.portalRestUrl);
				String playerItemStackSwitchJson = NetUtil.getText(url);

				final Gson gson = gsonBuilder.create();
				final Collection<ItemStackSwitch> pulledItemStackSwitches = gson.fromJson(
						playerItemStackSwitchJson,
						new TypeToken<Collection<ItemStackSwitch>>() {
						}.getType());

				if (pulledItemStackSwitches != null) {
					itemsToPull.addAll(pulledItemStackSwitches);
				}

				return pulledItemStackSwitches.size();

			} catch (final Exception e) {
				//TODO set up a log4j mapping to send emails on error messages (via mandrill)
				PolycraftMod.logger.error("Unable to sync inventory", e);
				return -1;

			}
		}
		return -1;

	}

	public int pushPlayerInventoryToPortal(final EntityPlayer player)
	{
		if (!ServerEnforcer.portalRestUrl.startsWith("file:")) {
			try {
				NetUtil.post(String.format("%s/players/%s/", ServerEnforcer.portalRestUrl, player.getDisplayName().toLowerCase()), ImmutableMap.of(player.getDisplayName().toLowerCase(), itemsToPush.toString()));
			} catch (final IOException e) {
				PolycraftMod.logger.error("Unable to log player last world seen", e);
			}
		}

		return itemsToPush.size();

	}

}
