package edu.utd.minecraft.mod.polycraft.privateproperty;

import io.netty.buffer.Unpooled;

import java.io.IOException;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.util.NetUtil;
import edu.utd.minecraft.mod.polycraft.util.SystemUtil;

public class ServerEnforcer extends Enforcer {
	public static final ServerEnforcer INSTANCE = new ServerEnforcer();

	private static final String portalRestUrl = System.getProperty("portal.rest.url");
	//refresh once per minecraft day by default
	private static final long portalRefreshTicksPrivateProperties = SystemUtil.getPropertyLong("portal.refresh.ticks.private.properties", 24000);
	private static final long portalRefreshTicksWhitelist = SystemUtil.getPropertyLong("portal.refresh.ticks.whitelist", 24000);

	@SubscribeEvent
	public void onWorldTick(final TickEvent.WorldTickEvent event) {
		//TODO not sure why this is getting called multiple times with different world java objects for the same world
		if (event.phase == TickEvent.Phase.END) {
			onWorldTickPrivateProperties(event);
			onWorldTickWhitelist(event);
		}
	}

	private void onWorldTickPrivateProperties(final TickEvent.WorldTickEvent event) {
		//refresh private property permissions at the start of each day, or if we haven't loaded them yet
		if (portalRestUrl != null && (event.world.getWorldTime() % portalRefreshTicksPrivateProperties == 0 || privatePropertiesJson == null)) {
			try {
				final String url = portalRestUrl.startsWith("file:")
						? portalRestUrl + "privateproperties.json"
						//TODO eventually send a timestamp of the last successful pull, so the server can return no-change (which is probably most of the time)
						: String.format("%s/worlds/%s/private_property_permissions/", portalRestUrl, event.world.getWorldInfo().getWorldName());
				updatePrivateProperties(NetUtil.getText(url));
				netChannel.sendToAll(getPrivatePropertiesPacket());
			} catch (final Exception e) {
				//TODO set up a log4j mapping to send emails on error messages (via mandrill)
				if (privatePropertiesJson == null) {
					PolycraftMod.logger.error("Unable to load private properties", e);
					System.exit(-1);
				}
				else {
					PolycraftMod.logger.error("Unable to refresh private properties", e);
				}
			}
		}
	}

	private FMLProxyPacket getPrivatePropertiesPacket() {
		return new FMLProxyPacket(Unpooled.buffer().writeBytes(privatePropertiesJson.getBytes()).copy(), netChannelName);
	}

	private void onWorldTickWhitelist(final TickEvent.WorldTickEvent event) {
		//refresh the whitelist at the start of each day, or if we haven't it yet
		if (portalRestUrl != null && (event.world.getWorldTime() % portalRefreshTicksWhitelist == 0 || whitelistJson == null)) {
			try {
				final String url = portalRestUrl.startsWith("file:")
						? portalRestUrl + "whitelist.json"
						: String.format("%s/worlds/%s/whitelist/", portalRestUrl, event.world.getWorldInfo().getWorldName());
				final Set<String> previousWhitelist = Sets.newHashSet(whitelist);
				updateWhitelist(NetUtil.getText(url));
				//reconcile whitelists
				final MinecraftServer minecraftserver = MinecraftServer.getServer();
				for (final String usernameToAdd : whitelist) {
					//if the user is new, add to the whitelist
					if (!previousWhitelist.remove(usernameToAdd)) {
						final GameProfile gameprofile = minecraftserver.func_152358_ax().func_152655_a(usernameToAdd);
						if (gameprofile != null)
							minecraftserver.getConfigurationManager().func_152601_d(gameprofile);
					}
				}
				//remove users from the whitelist that were not in the new whitelist
				for (final String usernameToRemove : previousWhitelist) {
					final GameProfile gameprofile = minecraftserver.getConfigurationManager().func_152599_k().func_152706_a(usernameToRemove);
					if (gameprofile != null)
						minecraftserver.getConfigurationManager().func_152597_c(gameprofile);
					//TODO don't worry about kicking them right now
				}
			} catch (final Exception e) {
				//TODO set up a log4j mapping to send emails on error messages (via mandrill)
				if (privatePropertiesJson == null) {
					PolycraftMod.logger.error("Unable to load whitelist", e);
					System.exit(-1);
				}
				else {
					PolycraftMod.logger.error("Unable to refresh whitelist", e);
				}
			}
		}
	}

	@SubscribeEvent
	public void onEntityJoinWorld(final EntityJoinWorldEvent event) {
		if (event.entity instanceof EntityPlayerMP) {
			final EntityPlayerMP player = (EntityPlayerMP) event.entity;
			netChannel.sendTo(getPrivatePropertiesPacket(), player);
			try {
				NetUtil.post(String.format("%s/players/%s/", portalRestUrl, player.getDisplayName()), ImmutableMap.of("last_world_seen", player.worldObj.getWorldInfo().getWorldName()));
			} catch (final IOException e) {
				PolycraftMod.logger.error("Unable to log player last world seen", e);
			}
		}
	}
}
