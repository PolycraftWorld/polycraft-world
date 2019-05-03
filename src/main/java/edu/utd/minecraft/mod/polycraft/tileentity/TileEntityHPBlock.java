package edu.utd.minecraft.mod.polycraft.tileentity;

import net.minecraft.tileentity.TileEntity;

public class TileEntityHPBlock extends TileEntity {
	public double MaxHP, HP;
	public TileEntityHPBlock() {
		this.HP = 140400;
		this.MaxHP = 140400;
	}
	public double getMaxHP() {
		return MaxHP;
	}
	public void setMaxHP(double maxHP) {
		MaxHP = maxHP;
	}
	public double getHP() {
		return HP;
	}
	public void setHP(double d) {
		HP = d;
	}
	

	
	
}
