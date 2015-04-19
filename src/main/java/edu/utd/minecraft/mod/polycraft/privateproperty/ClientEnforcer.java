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
import edu.utd.minecraft.mod.polycraft.item.ItemFlameThrower;
import edu.utd.minecraft.mod.polycraft.item.ItemJetPack;
import edu.utd.minecraft.mod.polycraft.item.ItemScubaTank;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;

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
	private boolean showPrivateProperty = false;

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

	@SubscribeEvent
	public void onClientPacket(final ClientCustomPacketEvent event) {
		updatePrivateProperties(new String(event.packet.payload().array()));
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
					if (ItemFlameThrower.isEquipped(player)) {
						y += overlayDistanceBetweenY;
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
