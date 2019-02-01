package edu.utd.minecraft.mod.polycraft.tileentity;

import net.minecraft.tileentity.TileEntity;

public class TileEntityHPBlock extends TileEntity {
	public int MaxHP, HP;
	public TileEntityHPBlock() {
		this.HP = 69;
		this.MaxHP = 1000000;
	}
	public int getMaxHP() {
		return MaxHP;
	}
	public void setMaxHP(int maxHP) {
		MaxHP = maxHP;
	}
	public int getHP() {
		return HP;
	}
	public void setHP(int hP) {
		HP = hP;
	}
	

	
	
}
