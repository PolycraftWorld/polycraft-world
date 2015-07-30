package edu.utd.minecraft.mod.polycraft.privateproperty;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.CustomObject;
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

	private static class StatusMessage {
		public final String text;
		public int ticksRemaining;

		public StatusMessage(final String text, final int ticksRemaining) {
			this.text = text;
			this.ticksRemaining = ticksRemaining;
		}
	}

	private List<StatusMessage> statusMessages = Lists.newArrayList();
	private boolean showPrivateProperty = false;
	private DataPacketType pendingDataPacketType = DataPacketType.Unknown;
	private int pendingDataPacketTypeMetadata = 0;
	private int pendingDataPacketsBytes = 0;
	private ByteBuffer pendingDataPacketsBuffer = null;

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
		statusMessages.add(new StatusMessage(message, PolycraftMod.convertSecondsToGameTicks(seconds)));
	}

	@SubscribeEvent
	public void onClientPacket(final ClientCustomPacketEvent event) {
		try {
			final ByteBuffer payload = ByteBuffer.wrap(event.packet.payload().array());
			if (pendingDataPacketType == DataPacketType.Unknown) {
				pendingDataPacketType = DataPacketType.values()[payload.getInt()];
				pendingDataPacketTypeMetadata = payload.getInt();
				pendingDataPacketsBytes = payload.getInt();
				pendingDataPacketsBuffer = ByteBuffer.allocate(pendingDataPacketsBytes);
			}
			else {
				pendingDataPacketsBytes -= payload.array().length;
				pendingDataPacketsBuffer.put(payload);
				if (pendingDataPacketsBytes == 0) {
					switch (pendingDataPacketType) {
					case PrivateProperties:
						final int count = updatePrivateProperties(CompressUtil.decompress(pendingDataPacketsBuffer.array()), pendingDataPacketTypeMetadata == 1);
						final NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
						showStatusMessage("Received " + format.format(count) + " " + (pendingDataPacketTypeMetadata == 1 ? "master" : "other") + " private properties (" + format.format(privatePropertiesByOwner.size()) + " players / "
								+ format.format(privatePropertiesByChunk.size()) + " chunks)", 10);
						break;
					case Friends:
						updateFriends(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
						break;
					case Unknown:
					default:
						break;
					}
					pendingDataPacketType = DataPacketType.Unknown;
					pendingDataPacketsBuffer = null;
				}
			}
		} catch (IOException e) {
			PolycraftMod.logger.error("Unable to decompress data packetes", e);
		}
	}

	@SubscribeEvent
	public synchronized void onClientChatReceivedEvent(final ClientChatReceivedEvent event) {
		final String message = event.message.getUnformattedText();
		final int usernameIndex = message.indexOf("<");
		if (usernameIndex > -1) {
			final String username = message.substring(usernameIndex + 1, message.indexOf('>', usernameIndex + 1));
			final EntityPlayer sendingPlayer = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(username);
			final EntityPlayer receivingPlayer = Minecraft.getMinecraft().thePlayer;
			if (sendingPlayer != null)
			{
				//calculate distance and save
				//send message to anyone within 32 blocks

				if (Math.sqrt(Math.pow(sendingPlayer.posX - receivingPlayer.posX, 2) +
						Math.pow(sendingPlayer.posZ - receivingPlayer.posZ, 2) +
						Math.pow(sendingPlayer.posY - receivingPlayer.posY, 2)) <= PolycraftMod.maxChatBlockProximity)
				{
					return;
				}
				//if greater than 32 blocks
				//test to see if you holding voice cone (item)
				//send message 48 blocks
				int recFreqWT = -1;
				int sendFreqWT = -2;
				int recFreqHR = -1;
				int sendFreqHR = -2;
				for (int i = 0; i < 36; i++)
				{
					ItemStack itemStackSend = sendingPlayer.inventory.getStackInSlot(i); //TODO: why isnt this working?
					ItemStack itemStackRec = receivingPlayer.inventory.getStackInSlot(i);

					if (i < 9)
					{
						if (itemStackSend != null && ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("Voice Cone").getItemStack().getUnlocalizedName())))
						{
							if (Math.sqrt(Math.pow(sendingPlayer.posX - receivingPlayer.posX, 2) +
									Math.pow(sendingPlayer.posZ - receivingPlayer.posZ, 2) +
									Math.pow(sendingPlayer.posY - receivingPlayer.posY, 2)) <= PolycraftMod.maxChatBlockProximityVoiceCone)
							{
								return;
							}
						}

						//test to see if you holding a mega phone
						//send message 64 blocks
						if (itemStackSend != null && ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("Megaphone").getItemStack().getUnlocalizedName())))
						{
							if (Math.sqrt(Math.pow(sendingPlayer.posX - receivingPlayer.posX, 2) +
									Math.pow(sendingPlayer.posZ - receivingPlayer.posZ, 2) +
									Math.pow(sendingPlayer.posY - receivingPlayer.posY, 2)) <= PolycraftMod.maxChatBlockProximityMegaphone)
							{
								return;
							}
						}

						//test if sending and receiving player have walky talky on same frequency on the hotbar
						//send message if within 1024 blocks
						if (itemStackSend != null && ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("Walky Talky").getItemStack().getUnlocalizedName())))
						{
							sendFreqWT = itemStackSend.getItemDamage();
						}

						if (itemStackRec != null && ((itemStackRec.getUnlocalizedName()).equals(CustomObject.registry.get("Walky Talky").getItemStack().getUnlocalizedName())))
						{
							recFreqWT = itemStackRec.getItemDamage();
						}

						if (recFreqWT == sendFreqWT)
						{
							if (Math.sqrt(Math.pow(sendingPlayer.posX - receivingPlayer.posX, 2) +
									Math.pow(sendingPlayer.posZ - receivingPlayer.posZ, 2) +
									Math.pow(sendingPlayer.posY - receivingPlayer.posY, 2)) <= PolycraftMod.maxChatBlockProximityWalkyTalky)
							{
								return;
							}
						}
					}

					//test if sending and receiving player have ham radios on same frequency
					//send message to within 8096

					if (itemStackSend != null && ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("HAM Radio").getItemStack().getUnlocalizedName())))
					{
						sendFreqHR = itemStackSend.getItemDamage();
					}

					if (itemStackRec != null && ((itemStackRec.getUnlocalizedName()).equals(CustomObject.registry.get("HAM Radio").getItemStack().getUnlocalizedName())))
					{
						recFreqHR = itemStackRec.getItemDamage();
					}
					if (recFreqWT == sendFreqWT)
					{
						if (Math.sqrt(Math.pow(sendingPlayer.posX - receivingPlayer.posX, 2) +
								Math.pow(sendingPlayer.posZ - receivingPlayer.posZ, 2) +
								Math.pow(sendingPlayer.posY - receivingPlayer.posY, 2)) <= PolycraftMod.maxChatBlockProximityHAMRadio)
						{
							return;
						}
					}
					//test if sending and receiving player have a cell phone and are friends
					//send message to a specific user (tell command)
					if (friends.contains(getFriendPairKey(whitelist.get(sendingPlayer.getDisplayName()), whitelist.get(receivingPlayer.getDisplayName()))))
					{
						if (itemStackSend != null && ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("Cell Phone").getItemStack().getUnlocalizedName())))
						{
							if (itemStackRec != null && ((itemStackRec.getUnlocalizedName()).equals(CustomObject.registry.get("Cell Phone").getItemStack().getUnlocalizedName())))
							{
								final int beginIndex = message.indexOf(">");
								final String[] textStream = event.message.getUnformattedText().substring(beginIndex).split(" ");
								if (textStream.length > 1)
								{
									if (receivingPlayer.getDisplayName() == textStream[1])
									{
										return;
									}
								}
							}
						}
					}
					if (friends.contains(getFriendPairKey(whitelist.get(sendingPlayer.getDisplayName()), whitelist.get(receivingPlayer.getDisplayName()))))
					{
						if (itemStackSend != null && ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("Smart Phone").getItemStack().getUnlocalizedName())))
						{
							if (itemStackRec != null && ((itemStackRec.getUnlocalizedName()).equals(CustomObject.registry.get("Smart Phone").getItemStack().getUnlocalizedName())))
							{
								return;
							}
						}
					}
				}

				event.setCanceled(true);
			}
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
					if (statusMessages.size() > 0) {
						for (int i = statusMessages.size() - 1; i >= 0; i--) {
							if (--statusMessages.get(i).ticksRemaining <= 0) {
								statusMessages.remove(i);
							}
						}
						for (final StatusMessage statusMessage : statusMessages) {
							client.fontRenderer.drawStringWithShadow(statusMessage.text, x, y, overlayColor);
							y += overlayDistanceBetweenY;
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
