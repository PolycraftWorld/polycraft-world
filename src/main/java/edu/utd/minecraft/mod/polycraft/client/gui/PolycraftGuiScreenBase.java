package edu.utd.minecraft.mod.polycraft.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public abstract class PolycraftGuiScreenBase extends GuiScreen {
	
	protected  GuiButton btnExit = new GuiButton(100000, 20, 20, 20, 20, "X");
	
	/**
	 * Reset the ButtonList and add the "exit" button
	 */
	protected void resetButtonList() {
		this.buttonList.clear();
		this.buttonList.add(btnExit);
	}
	
	protected void actionPerformed(GuiButton btn) {
		if(btn.id == btnExit.id) {
			exitGuiScreen();
			return;
		}
	}
	
	protected abstract void exitGuiScreen();

}
