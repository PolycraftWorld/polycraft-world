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
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;

import org.lwjgl.input.Keyboard;

import com.google.common.collect.Lists;
import com.sun.security.ntlm.Client;

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
import edu.utd.minecraft.mod.polycraft.minigame.KillWall;
import edu.utd.minecraft.mod.polycraft.minigame.PolycraftMinigameManager;
import edu.utd.minecraft.mod.polycraft.minigame.RaceGame;
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
	private static final int actionPreventedWarningMessageMaxTicks = PolycraftMod.convertSecondsToGameTicks(4);

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
	private static boolean showPrivateProperty = false;
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
						final int count = updatePrivateProperties(CompressUtil.decompress(pendingDataPacketsBuffer.array()), pendingDataPacketTypeMetadata == 1, false);
						final NumberFormat format = NumberFormat.getNumberInstance(Locale.getDefault());
						showStatusMessage("Received " + format.format(count) + " " + (pendingDataPacketTypeMetadata == 1 ? "master" : "other") + " private properties (" + format.format(privatePropertiesByOwner.size()) + " players / "
								+ format.format(privatePropertiesByChunk.size()) + " chunks)", 10);
						break;
					case Friends:
						updateFriends(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
						break;
					case Broadcast:
						onClientBroadcastReceivedEvent(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
						break;
					case TempPrivatProperties:
						final int countPP = updateTempPrivateProperties(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
						final NumberFormat formatPP = NumberFormat.getNumberInstance(Locale.getDefault());
						showStatusMessage("Received " + formatPP.format(countPP) + " " + (pendingDataPacketTypeMetadata == 1 ? "master" : "other") + " private properties (" + formatPP.format(privatePropertiesByOwner.size()) + " players / "
								+ formatPP.format(privatePropertiesByChunk.size()) + " chunks)", 10);
						break;
					case Governments:	
						final int govCount = updateGovernments(CompressUtil.decompress(pendingDataPacketsBuffer.array()), false);	
						final NumberFormat govformat = NumberFormat.getNumberInstance(Locale.getDefault());	
						showStatusMessage("Received " + govformat.format(govCount) + "::roles:" + ((Government) governments.toArray()[0]).getRoles()[0], 10);	
						break;
					case playerID:
						this.playerID = updatePlayerID(CompressUtil.decompress(pendingDataPacketsBuffer.array()));
						break;
					case GenericMinigame:
						PolycraftMinigameManager.UpdatePackets(CompressUtil.decompress(pendingDataPacketsBuffer.array()),pendingDataPacketTypeMetadata);
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
			EntityPlayer sendingPlayer = Minecraft.getMinecraft().theWorld.getPlayerEntityByName(username);

			final EntityPlayer receivingPlayer = Minecraft.getMinecraft().thePlayer;
			if (receivingPlayer.capabilities.isCreativeMode)
				return;

			if (sendingPlayer != null)
			{
				//calculate distance and save
				if (arePlayersWithinDistance(sendingPlayer, receivingPlayer, PolycraftMod.maxChatBlockProximity))
				{
					return;
				}

				final ItemStack itemStackSend = sendingPlayer.inventory.getCurrentItem();

				//is the sender holding a voice cone
				if (itemStackSend != null && ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("Voice Cone").getItemStack().getUnlocalizedName())))
				{
					if (arePlayersWithinDistance(sendingPlayer, receivingPlayer, PolycraftMod.maxChatBlockProximityVoiceCone))
					{
						return;
					}
				}

				//is the sender holding a megaphone
				if (itemStackSend != null && ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("Megaphone").getItemStack().getUnlocalizedName())))
				{
					if (arePlayersWithinDistance(sendingPlayer, receivingPlayer, PolycraftMod.maxChatBlockProximityMegaphone))
					{
						return;
					}
				}

			}
			event.setCanceled(true);

		}
	}

	public void onClientBroadcastReceivedEvent(String complexMessage) throws IOException
	{
		String[] parsed = complexMessage.split(":");
		if (parsed.length > 5)
		{
			final int frequency = Integer.parseInt(parsed[0]);
			final double sourceX = Double.parseDouble(parsed[1]);
			final double sourceY = Double.parseDouble(parsed[2]);
			final double sourceZ = Double.parseDouble(parsed[3]);
			final String itemName = parsed[4].trim();
			final ItemStack itemStackSend = CustomObject.registry.get(itemName).getItemStack();
			final String usernameSender = parsed[5];
			String message = "";
			for (int i = 6; i < parsed.length; i++)
			{
				message += parsed[i];
				if (i < parsed.length - 1)
					message += ":";
			}

			final EntityPlayer receivingPlayer = Minecraft.getMinecraft().thePlayer;
			if (receivingPlayer.capabilities.isCreativeMode) //dont need to broadcast in creative mode, because normal chat will
				return;

			if (receivingPlayer.getDisplayName().equalsIgnoreCase(usernameSender)) //don't need to broadcast to yourself
				return;

			//see what the receiver is holding

			for (int i = 0; i < 36; i++)
			{
				int recFreq = -1;
				ItemStack itemStackRec = receivingPlayer.inventory.getStackInSlot(i);

				if (i < 9)
				{
					//test if  receiving player has walky talky on the hotbar	
					if (itemStackRec != null && ((itemStackRec.getUnlocalizedName()).equals(CustomObject.registry.get("Walky Talky").getItemStack().getUnlocalizedName())))
					{
						recFreq = itemStackRec.getItemDamage();
						if (recFreq == frequency)
							if (arePlayersWithinBroadcastRange(sourceX, sourceY, sourceZ, receivingPlayer, PolycraftMod.maxChatBlockProximityWalkyTalky))
								printBroadcastOnClient(receivingPlayer, usernameSender, message);
					}
				}

				//test if sending and receiving player have ham radios on same frequency
				if (itemStackRec != null && ((itemStackRec.getUnlocalizedName()).equals(CustomObject.registry.get("HAM Radio").getItemStack().getUnlocalizedName())))
				{
					recFreq = itemStackRec.getItemDamage();
					if (recFreq == frequency)
						if (arePlayersWithinBroadcastRange(sourceX, sourceY, sourceZ, receivingPlayer, PolycraftMod.maxChatBlockProximityHAMRadio))
							printBroadcastOnClient(receivingPlayer, usernameSender, message);
				}

				//test if sending player holding phone and receiving player has a cell phone on hotbar and they are friends
				//send message to a specific user (tell command)
				//if (friends.contains(getFriendPairKey(whitelist.get(usernameSender), whitelist.get(receivingPlayer.getDisplayName()))))
				//{
				if (itemStackSend != null && ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("Cell Phone").getItemStack().getUnlocalizedName())))
				{
					if (itemStackRec != null && ((itemStackRec.getUnlocalizedName()).equals(CustomObject.registry.get("Cell Phone").getItemStack().getUnlocalizedName())))
					{
						final int beginIndex = message.indexOf(":") + 1;
						final String[] textStream = message.split(":");
						if (textStream != null && textStream.length > 1)
						{
							if (receivingPlayer.getDisplayName().equalsIgnoreCase(textStream[0]))
							{
								printBroadcastOnClient(receivingPlayer, usernameSender, message.substring(beginIndex).trim());
							}
						}
					}
				}
				//}

				//if (friends.contains(getFriendPairKey(whitelist.get(usernameSender), whitelist.get(receivingPlayer.getDisplayName()))))
				//{
				if (itemStackSend != null && ((itemStackSend.getUnlocalizedName()).equals(CustomObject.registry.get("Smart Phone").getItemStack().getUnlocalizedName())))
				{
					if (itemStackRec != null && ((itemStackRec.getUnlocalizedName()).equals(CustomObject.registry.get("Smart Phone").getItemStack().getUnlocalizedName())))
					{
						printBroadcastOnClient(receivingPlayer, usernameSender, message);
					}
				}
				//}
			}
		}

	}

	private void printBroadcastOnClient(EntityPlayer receivingPlayer, String username, String message) {

		receivingPlayer.addChatMessage(new ChatComponentText("<" + username + "> " + message));
	}

	private boolean arePlayersWithinBroadcastRange(double sourceX, double sourceY, double sourceZ, EntityPlayer receivingPlayer, int distanceBlocks) {

		if (Math.sqrt(Math.pow(sourceX - receivingPlayer.posX, 2) +
				Math.pow(sourceZ - receivingPlayer.posZ, 2) +
				Math.pow(sourceY - receivingPlayer.posY, 2)) <= distanceBlocks)
			return true;

		return false;
	}

	private boolean arePlayersWithinDistance(EntityPlayer sendingPlayer, EntityPlayer receivingPlayer, int distanceBlocks) {

		if (Math.sqrt(Math.pow(sendingPlayer.posX - receivingPlayer.posX, 2) +
				Math.pow(sendingPlayer.posZ - receivingPlayer.posZ, 2) +
				Math.pow(sendingPlayer.posY - receivingPlayer.posY, 2)) <= distanceBlocks)
			return true;

		return false;
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

	public static boolean getShowPP() {
		
		return showPrivateProperty;
	}
}
