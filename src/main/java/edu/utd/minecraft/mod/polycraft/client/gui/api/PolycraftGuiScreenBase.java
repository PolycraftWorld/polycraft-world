package edu.utd.minecraft.mod.polycraft.client.gui.api;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public abstract class PolycraftGuiScreenBase extends GuiScreen {
	
	protected int btnXPos = 380;
	protected int btnYPos = 38;
	protected  GuiButton btnExit = new GuiButton(100000, btnXPos, btnYPos, 20, 20, "X");	//not sure this should be 100000.  0 might make more sense
	
	/**
	 * Reset the ButtonList and add the "exit" button
	 */
	protected void resetButtonList() {
		this.buttonList.clear();
		this.buttonList.add(btnExit);
	}
	
	public void addButton(GuiButton button)
	{
		this.buttonList.add(button);
	}
	
	public void removeButton(GuiButton button)
	{
		this.buttonList.remove(button);
	}
	
	public List<GuiButton> getButtonList()
	{
		return this.buttonList;
	}
	
	protected void actionPerformed(GuiButton btn) {
		if(btn.id == btnExit.id) {
			exitGuiScreen();
			return;
		}
	}
	
	protected void updateExitButton() {
		btnExit = new GuiButton(100000, btnXPos, btnYPos, 20, 20, "X");
	}
	
	protected abstract void exitGuiScreen();

}
