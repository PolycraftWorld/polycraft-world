package edu.utd.minecraft.mod.polycraft.util;

import edu.utd.minecraft.mod.polycraft.crafting.PolycraftContainerType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ShiftClickEvent {

	EntityPlayer entityPlayer;
	ItemStack itemstack1;
	PolycraftContainerType containerType;

	public ShiftClickEvent(EntityPlayer entityPlayer, ItemStack itemstack1, PolycraftContainerType containerType) {
		// TODO Auto-generated constructor stub
		super();
    	this.entityPlayer=entityPlayer;
        this.itemstack1=itemstack1;
        this.containerType=containerType;
	}

}
