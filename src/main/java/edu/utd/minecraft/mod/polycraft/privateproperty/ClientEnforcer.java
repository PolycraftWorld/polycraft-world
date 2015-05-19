package edu.utd.minecraft.mod.polycraft.privateproperty;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.util.Locale;

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
import edu.utd.minecraft.mod.polycraft.item.ItemFueledProjectileLauncher;
import edu.utd.minecraft.mod.polycraft.item.ItemJetPack;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaTank;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;
import edu.utd.minecraft.mod.polycraft.util.CompressUtil;

public class ClientEnforcer extends Enforcer {
	public static final ClientEnforcer INSTANCE = new ClientEnforcer();

	private static final int overlayStartX = 5;
	private static final int overlayStartY = 5;
	private static final int overlayDistanceBetweenX = 125;
	private static final int overlayDistanceBetweenY = 10;
	private static final int overlayMaxY = 175;
	private static final int overlayColor = 16777215;
	private static final KeyBinding keyBindingPrivateProperty = new KeyBinding("key.private.property", Keyboard.KEY_P, "key.categories.gameplay");
	private static final KeyBinding keyBindingPrivatePropertyRights = new KeyBinding("key.private.property.rights", Keyboard.KEY_O, "key.categories.gameplay");
	private static int actionPreventedWarningMessageTicks = 0;
	private static final int actionPreventedWarningMessageMaxTicks = PolycraftMod.convertSecondsToGameTicks(7);
	
	private final Minecraft client;
	private String statusMessage = null;
	private int statusMessageTicksRemaining = 0;
	private boolean showPrivateProperty = false;
	private int pendingPrivatePropertiesBytes = 0;
	private ByteBuffer pendingPrivatePropertiesBuffer = null;

	public ClientEnforcer() {
		client = FMLClientHandler.instance().getClient();
	}

	@SubscribeEvent
	public void KeyInputEvent(cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent event) {
		if (keyBindingPrivateProperty.isPressed()) {
			showPrivateProperty = !showPrivateProperty;
		}
	}

	@Override
	protected void setActionPrevented(final Action action, final PrivateProperty privateProperty) {
		super.setActionPrevented(action, privateProperty);
		actionPreventedWarningMessageTicks = 0;
	}
	
	private void showStatusMessage(final String message, final int seconds) {
		statusMessageTicksRemaining = PolycraftMod.convertSecondsToGameTicks(seconds);
		statusMessage = message;
		
	}

	@SubscribeEvent
	public void onClientPacket(final ClientCustomPacketEvent event) {
		try {
			final byte[] payload = event.packet.payload().array();
			if (pendingPrivatePropertiesBytes == 0) {
				pendingPrivatePropertiesBytes = ByteBuffer.wrap(payload).getInt();
				pendingPrivatePropertiesBuffer = ByteBuffer.allocate(pendingPrivatePropertiesBytes);
			}
			else {
				pendingPrivatePropertiesBytes -= payload.length;
				pendingPrivatePropertiesBuffer.put(payload);
				if (pendingPrivatePropertiesBytes == 0) {
					updatePrivateProperties(CompressUtil.decompress(pendingPrivatePropertiesBuffer.array()));
					pendingPrivatePropertiesBuffer = null;
					final NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
					showStatusMessage("Received " + format.format(privateProperties.size()) + " private properties (" + format.format(privatePropertiesByOwner.size()) + " players / " + format.format(privatePropertiesByChunk.size()) + " chunks)", 10);
				}
			}
		} catch (IOException e) {
			PolycraftMod.logger.error("Unable to decompress private properties", e);
		}
	}


	@SubscribeEvent
	public void onRenderTick(final TickEvent.RenderTickEvent tick) {
		if (tick.phase == Phase.END && client.theWorld != null) {
			final EntityPlayer player = client.thePlayer;
			if (player != null && player.isEntityAlive()) {
				final PrivateProperty insidePrivateProperty = findPrivateProperty(player);
				if (client.currentScreen == null)
				{
					final PrivateProperty targetPrivateProperty = actionPreventedPrivateProperty == null ? insidePrivateProperty : actionPreventedPrivateProperty;
					int x = overlayStartX;
					int y = overlayStartY;
					//offset if the item overlays are displayed
					if (ItemJetPack.isEquipped(player) || ItemScubaTank.isEquipped(player)) {
						y += overlayDistanceBetweenY;
					}
					if (ItemFueledProjectileLauncher.isEquipped(player)) {
						y += overlayDistanceBetweenY;
					}
					if (statusMessage != null) {
						client.fontRenderer.drawStringWithShadow(statusMessage, x, y, overlayColor);
						y += overlayDistanceBetweenY;
						if (statusMessageTicksRemaining-- <= 0) {
							statusMessage = null;
						}
					}
					if (targetPrivateProperty != null && (showPrivateProperty || actionPreventedPrivateProperty != null)) {
						if (insidePrivateProperty != null) {
							client.fontRenderer.drawStringWithShadow("Private Property - Inside", x, y, overlayColor);
							client.fontRenderer.drawStringWithShadow(insidePrivateProperty.name, x, y += overlayDistanceBetweenY, overlayColor);
						}
						if (actionPreventedPrivateProperty != null && insidePrivateProperty != actionPreventedPrivateProperty) {
							if (insidePrivateProperty != null) {
								y += overlayDistanceBetweenY + overlayDistanceBetweenY;
							}
							client.fontRenderer.drawStringWithShadow("Private Property - Beside", x, y, overlayColor);
							client.fontRenderer.drawStringWithShadow(targetPrivateProperty.name, x, y += overlayDistanceBetweenY, overlayColor);
						}
						client.fontRenderer.drawStringWithShadow("Owned by " + targetPrivateProperty.owner, x, y += overlayDistanceBetweenY, overlayColor);
						client.fontRenderer.drawStringWithShadow("Posted: " + targetPrivateProperty.message, x, y += overlayDistanceBetweenY, overlayColor);
						client.fontRenderer.drawStringWithShadow(String.format("Position: %d,%d,%d (%d, %d)", (int) player.posX, (int) player.posY, (int) player.posZ, player.chunkCoordX, player.chunkCoordZ), x,
								y += overlayDistanceBetweenY, overlayColor);
						y += overlayDistanceBetweenY;
						if (GameSettings.isKeyDown(keyBindingPrivatePropertyRights)) {
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
						else if (actionPrevented != null) {
							client.fontRenderer.drawStringWithShadow("Action Prevented: " + actionPrevented, x, y += overlayDistanceBetweenY, overlayColor);
							if (actionPreventedWarningMessageTicks++ == actionPreventedWarningMessageMaxTicks) {
								setActionPrevented(null, null);
							}
						}
					}
					else if (showPrivateProperty) {
						client.fontRenderer.drawStringWithShadow("Private Property - None", x, y, overlayColor);
					}
				}
				else
				{
					client.fontRenderer.drawStringWithShadow(String.format("Position: %d,%d,%d (%d, %d)", (int) player.posX, (int) player.posY, (int) player.posZ, player.chunkCoordX, player.chunkCoordZ), overlayStartX,
							overlayStartY, overlayColor);
				}

			}

		}
	}
}