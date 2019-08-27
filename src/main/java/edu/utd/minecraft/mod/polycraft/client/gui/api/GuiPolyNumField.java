package edu.utd.minecraft.mod.polycraft.client.gui.api;

import org.apache.commons.lang3.math.NumberUtils;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class GuiPolyNumField extends GuiTextField{

	public GuiPolyNumField(FontRenderer p_i1032_1_, int p_i1032_2_, int p_i1032_3_, int p_i1032_4_, int p_i1032_5_) {
		super(100, p_i1032_1_, p_i1032_2_, p_i1032_3_, p_i1032_4_, p_i1032_5_);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean textboxKeyTyped(char c, int p) {
		String tempText = this.getText();
		super.textboxKeyTyped(c, p);
		
		
		//number can be number, minus sign (for typing negatives) or blank)
		if(NumberUtils.isNumber(this.getText()) || this.getText().equals("-") || this.getText().equals("")) {
			return true;
		}else {
			this.setText(tempText);
		}
		
		
		return false;
	}

}
