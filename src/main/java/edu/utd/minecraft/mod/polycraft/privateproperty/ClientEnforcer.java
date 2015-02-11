package edu.utd.minecraft.mod.polycraft.privateproperty;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;

public class ClientEnforcer extends Enforcer {
	public static final ClientEnforcer INSTANCE = new ClientEnforcer();
	
	private static final int overlayStartX = 5;
	private static final int overlayStartY = 5;
	private static final int overlayDistanceBetweenX = 125;
	private static final int overlayDistanceBetweenY = 10;
	private static final int overlayMaxY = 175;
	private static final int overlayColor = 16777215;
	private static final KeyBinding keyBindingRights1 = new KeyBinding("key.private.property.rights.1", Keyboard.KEY_P, "key.categories.gameplay");
	private static final KeyBinding keyBindingRights2 = new KeyBinding("key.private.property.rights.2", Keyboard.KEY_R, "key.categories.gameplay");
	private static final KeyBinding keyBindingChunks1 = new KeyBinding("key.private.property.chunks.1", Keyboard.KEY_P, "key.categories.gameplay");
	private static final KeyBinding keyBindingChunks2 = new KeyBinding("key.private.property.chunks.2", Keyboard.KEY_C, "key.categories.gameplay");
	private static int actionPreventedWarningMessageTicks = 0;
	private static final int actionPreventedWarningMessageMaxTicks = PolycraftMod.convertSecondsToGameTicks(20);
	
	private final Minecraft client;

	public ClientEnforcer() {
		client = FMLClientHandler.instance().getClient();
	}
	
	@Override
	protected void setActionPrevented(final Action action, final PrivateProperty privateProperty) {
		super.setActionPrevented(action, privateProperty);
		actionPreventedWarningMessageTicks = 0;
	}

	@SubscribeEvent
	public void onClientPacket(final ClientCustomPacketEvent event) {
		updatePrivateProperties(new String(event.packet.payload().array()));
	}
	
	@SubscribeEvent
	public void onRenderTick(final TickEvent.RenderTickEvent tick) {
		if (client.currentScreen == null && tick.phase == Phase.END && client.theWorld != null) {
			final EntityPlayer player = client.thePlayer;
			if (player != null && player.isEntityAlive()) {
				final PrivateProperty insidePrivateProperty = findPrivateProperty(player);
				final PrivateProperty targetPrivateProperty = actionPreventedPrivateProperty == null ? insidePrivateProperty : actionPreventedPrivateProperty;
				if (targetPrivateProperty != null) {
					int x = overlayStartX;
					int y = overlayStartY;
					if (actionPreventedPrivateProperty != null && insidePrivateProperty != actionPreventedPrivateProperty) {
						client.fontRenderer.drawStringWithShadow("Private Property (Beside)", x, y, overlayColor);
					}
					else {
						client.fontRenderer.drawStringWithShadow("Private Property (Inside)", x, y, overlayColor);
					}
					client.fontRenderer.drawStringWithShadow(targetPrivateProperty.name, x, y += overlayDistanceBetweenY, overlayColor);
					client.fontRenderer.drawStringWithShadow("Owned by " + targetPrivateProperty.owner, x, y += overlayDistanceBetweenY, overlayColor);
					client.fontRenderer.drawStringWithShadow("Posted: " + targetPrivateProperty.message, x, y += overlayDistanceBetweenY, overlayColor);
					y += overlayDistanceBetweenY;
					if (GameSettings.isKeyDown(keyBindingRights1) && GameSettings.isKeyDown(keyBindingRights2)) {
						client.fontRenderer.drawStringWithShadow("Property Rights:", x, y += overlayDistanceBetweenY, overlayColor);
						final int startY = y;
						boolean any = false;
						for (final Action action : Action.values()) {
							if (targetPrivateProperty.actionEnabled(client.thePlayer, action)) {
								client.fontRenderer.drawStringWithShadow(action.toString(), x, y += overlayDistanceBetweenY, overlayColor);
								any = true;
							}
							//move over to the next column
							if (y > overlayMaxY) {
								x += overlayDistanceBetweenX;
								y = startY;
							}
						}
						if (!any) {
							client.fontRenderer.drawStringWithShadow("None", x, y += overlayDistanceBetweenY, overlayColor);
						}
					}
					else if (GameSettings.isKeyDown(keyBindingChunks1) && GameSettings.isKeyDown(keyBindingChunks2)) {
						client.fontRenderer.drawStringWithShadow("Property Chunks:", x, y += overlayDistanceBetweenY, overlayColor);
						final int startY = y;
						for (final PrivateProperty.Chunk chunk : targetPrivateProperty.chunks) {
							client.fontRenderer.drawStringWithShadow(String.format("[%d, %d]",  chunk.x, chunk.z), x, y += overlayDistanceBetweenY, overlayColor);
							//move over to the next column
							if (y > overlayMaxY) {
								x += overlayDistanceBetweenX;
								y = startY;
							}
						}
					}
					else if (actionPrevented != null) {
						client.fontRenderer.drawStringWithShadow("Action Prevented: " + actionPrevented, x, y += overlayDistanceBetweenY, overlayColor);
						client.fontRenderer.drawStringWithShadow("Hold down " + Keyboard.getKeyName(keyBindingRights1.getKeyCode()) + Keyboard.getKeyName(keyBindingRights2.getKeyCode()) + " to see property rights", x, y += overlayDistanceBetweenY, overlayColor);
						if (actionPreventedWarningMessageTicks++ == actionPreventedWarningMessageMaxTicks) {
							setActionPrevented(null, null);
						}
					}
				}
			}
		}
	}
}
