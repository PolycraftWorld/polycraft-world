package edu.utd.minecraft.mod.polycraft.aitools.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult;
import edu.utd.minecraft.mod.polycraft.aitools.APICommandResult.Result;
import edu.utd.minecraft.mod.polycraft.aitools.APIHelper.CommandResult;
import edu.utd.minecraft.mod.polycraft.block.BlockMacGuffin;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialManager;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.InventoryMessage;
import edu.utd.minecraft.mod.polycraft.privateproperty.network.TeleportMessage;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class APICommandUseItem extends APICommandBase{

	private boolean useHand;
	
	public APICommandUseItem(float cost, boolean useHand) {
		super(cost);
		this.useHand = useHand;
	}

	@Override
	public APICommandResult serverExecute(String[] args, EntityPlayerMP player) {
		// Don't do anything for server side
		return null;
	}

	@Override
	public APICommandResult clientExecute(String[] args) {
		// Process on client side
		
		//Initialize some vars
		EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
		ItemStack itemstack = null;
		if(!useHand) 
			itemstack = player.inventory.getCurrentItem();
		
		// code from Minecraft.rightClickMouse()
		switch (Minecraft.getMinecraft().objectMouseOver.typeOfHit)
        {
            case ENTITY:

                if (Minecraft.getMinecraft().playerController.func_178894_a(player, Minecraft.getMinecraft().objectMouseOver.entityHit, Minecraft.getMinecraft().objectMouseOver))
                {
                	return new APICommandResult(args, APICommandResult.Result.FAIL, "Unhandled Case1", this.stepCost);
                }
                else if (Minecraft.getMinecraft().playerController.interactWithEntitySendPacket(player, Minecraft.getMinecraft().objectMouseOver.entityHit))
                {
                	return new APICommandResult(args, APICommandResult.Result.FAIL, "Unhandled Case2", this.stepCost);
                }

                return new APICommandResult(args, APICommandResult.Result.FAIL, "Unhandled Case3", this.stepCost);
            case MISS:
            case BLOCK:
                BlockPos blockpos = new BlockPos(player.posX + player.getHorizontalFacing().getFrontOffsetX(),
        				player.posY + player.getHorizontalFacing().getFrontOffsetY(),
        				player.posZ + player.getHorizontalFacing().getFrontOffsetZ());

                if (!Minecraft.getMinecraft().theWorld.isAirBlock(blockpos))
                {
                    int i = itemstack != null ? itemstack.stackSize : 0;


                    boolean result = !net.minecraftforge.event.ForgeEventFactory.onPlayerInteract(player, net.minecraftforge.event.entity.player.PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, Minecraft.getMinecraft().theWorld, blockpos, Minecraft.getMinecraft().objectMouseOver.sideHit, Minecraft.getMinecraft().objectMouseOver.hitVec).isCanceled();
                    if (result) { //Forge: Kept separate to simplify patch
                    if (Minecraft.getMinecraft().playerController.onPlayerRightClick(player, Minecraft.getMinecraft().theWorld, itemstack, blockpos, 
                    		Minecraft.getMinecraft().objectMouseOver.sideHit, Minecraft.getMinecraft().objectMouseOver.hitVec))
                    {
                        player.swingItem();
                    	return new APICommandResult(args, APICommandResult.Result.SUCCESS, "", this.stepCost);
                    }
                    }

                    if (itemstack == null)
                    {
                    	return new APICommandResult(args, APICommandResult.Result.FAIL, "Unhandled Case4", this.stepCost);
                    }

                    if (itemstack.stackSize == 0)
                    {
                        player.inventory.mainInventory[player.inventory.currentItem] = null;
                    }
                    else if (itemstack.stackSize != i || Minecraft.getMinecraft().playerController.isInCreativeMode())
                    {
                    	Minecraft.getMinecraft().entityRenderer.itemRenderer.resetEquippedProgress();
                    }
                }
		default:
			return new APICommandResult(args, APICommandResult.Result.FAIL, "Unhandled Case5", this.stepCost);
        }
	}
}
