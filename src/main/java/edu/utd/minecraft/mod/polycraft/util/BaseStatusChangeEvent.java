package edu.utd.minecraft.mod.polycraft.util;

import net.minecraft.entity.player.EntityPlayer;

public class BaseStatusChangeEvent {
	
	public String initial_state;
	public String final_state;
	public EntityPlayer entityPlayer;
	public String entities_in_base;
	
	public BaseStatusChangeEvent(EntityPlayer entityPlayer,String initial_state, String final_state, String entities_in_base) {
		// TODO Auto-generated constructor stub
		super();
		this.initial_state=initial_state;
		this.final_state=final_state;
		this.entityPlayer=entityPlayer;
		this.entities_in_base=entities_in_base;
	}
}
