package edu.utd.minecraft.mod.polycraft.privateproperty;

import io.netty.buffer.Unpooled;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.util.NetUtil;
import edu.utd.minecraft.mod.polycraft.util.SystemUtil;

public class ServerEnforcer extends Enforcer {
	public static final ServerEnforcer INSTANCE = new ServerEnforcer();
	
	//refresh once per minecraft day
	public static final long refreshTicks = SystemUtil.getPropertyLong("private.properties.refresh.ticks", 24000);
	
	@SubscribeEvent
	public void onWorldTick(final TickEvent.WorldTickEvent event) {
		//refresh private property permissions at the start of each day, or if we haven't loaded them yet
		if (event.phase == TickEvent.Phase.END && (event.world.getWorldTime() % refreshTicks == 0 || privatePropertiesJson == null)) {
			try {
				final String privatePropertiesUrl = System.getProperty("private.properties.url");
				updatePrivateProperties(NetUtil.getText(privatePropertiesUrl + (privatePropertiesUrl.startsWith("file:") ? "" : event.world.getWorldInfo().getWorldName())));
				netChannel.sendToAll(getPrivatePropertiesPacket());
			}
			catch (final IOException e) {
				//TODO set up a log4j mapping to send emails on error messages (via mandrill)
				if (privatePropertiesJson == null) {
					PolycraftMod.logger.error("Unable to load private properties: " + e.getMessage());
					System.exit(-1);
				}
				else {
					PolycraftMod.logger.error("Unable to refresh private properties: " + e.getMessage());
				}
			}
		}
	}
	
	private FMLProxyPacket getPrivatePropertiesPacket() {
		return new FMLProxyPacket(Unpooled.buffer().writeBytes(privatePropertiesJson.getBytes()).copy(), netChannelName);
	}
	
	@SubscribeEvent
	public void onEntityJoinWorld(final EntityJoinWorldEvent event) {
		if (event.entity instanceof EntityPlayerMP) {
			netChannel.sendTo(getPrivatePropertiesPacket(), (EntityPlayerMP)event.entity);
		}
	}
}
