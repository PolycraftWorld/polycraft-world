package edu.utd.minecraft.mod.polycraft.item;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.config.PogoStick;
import edu.utd.minecraft.mod.polycraft.proxy.ClientProxy;

public class ItemPogoStick extends PolycraftUtilityItem {
	
	private int pogoStickPreviousContinuousActiveBounces = 0;
	private float pogoStickLastFallDistance = 0;

	public static boolean isEquipped(final EntityPlayer player) {
		return PolycraftItemHelper.checkCurrentEquippedItem(player, ItemPogoStick.class, true);
	}

	public static ItemPogoStick getEquippedItem(final EntityPlayer player) {
		return PolycraftItemHelper.getCurrentEquippedItem(player);
	}

	public static ItemStack getEquippedItemStack(final EntityPlayer player) {
		return player.getCurrentEquippedItem();
	}

	public static void damage(final EntityPlayer player, final Random random) {
		getEquippedItemStack(player).attemptDamageItem(1, random);
		if (!isEquipped(player))
			player.destroyCurrentEquippedItem();
	}

	public final PogoStick config;

	public ItemPogoStick(final PogoStick config) {
		//this.setTextureName(PolycraftMod.getAssetName(PolycraftMod.getFileSafeName(config.name)));
		this.setCreativeTab(CreativeTabs.tabTransport);
		this.setMaxDamage(config.maxBounces);
		this.setMaxStackSize(1);
		this.config = config;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
//		float jumpMovementFactor = ClientProxy.baseJumpMovementFactor;
//			jumpMovementFactor *= config.jumpMovementFactorBuff;
//			if (!pogoStick.config.restrictJumpToGround || player.onGround) {
		final boolean playerActivelyBouncing = noScreenOverlay() && !isPlayerLookingAtPogoCancellingBlock();
		final boolean playerActivelySupressing = !playerActivelyBouncing && GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak) && noScreenOverlay();
		if(player.onGround) {
			player.motionY = config.getMotionY(playerActivelySupressing ? 0 : pogoStickLastFallDistance, pogoStickPreviousContinuousActiveBounces, playerActivelyBouncing)+0.1;
		}
//		final double motionY = config.getMotionY(playerActivelySupressing ? 0 : pogoStickLastFallDistance, pogoStickPreviousContinuousActiveBounces, playerActivelyBouncing);
//		if (motionY > 0)
//			player.motionY = motionY;
//				if (playerActivelyBouncing)
//					pogoStickPreviousContinuousActiveBounces++;
//				else
//					pogoStickPreviousContinuousActiveBounces = 0;
//			}
////		} else {
////			playerState.pogoStickPreviousContinuousActiveBounces = 0;
////		}
//		playerState.pogoStickLastFallDistance = 0;
//
//		if (player.jumpMovementFactor != jumpMovementFactor)
//			player.jumpMovementFactor = jumpMovementFactor;
		return super.onItemRightClick(itemStackIn, worldIn, player);
	}
	
	private boolean isPlayerLookingAtPogoCancellingBlock() {
		if (Minecraft.getMinecraft().objectMouseOver != null) {
			final Block block = Minecraft.getMinecraft().theWorld.getBlockState(Minecraft.getMinecraft().objectMouseOver.getBlockPos()).getBlock();
			return block instanceof BlockContainer
					|| block instanceof BlockWorkbench
					|| block instanceof BlockDoor
					|| block instanceof BlockButton
					|| block == Blocks.bed;
		}
		return false;
	}

	private boolean noScreenOverlay() {
		return Minecraft.getMinecraft().currentScreen == null;
	}
}
