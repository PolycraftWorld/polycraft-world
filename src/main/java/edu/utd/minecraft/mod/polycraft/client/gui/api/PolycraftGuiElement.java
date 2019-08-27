package edu.utd.minecraft.mod.polycraft.client.gui.api;

public interface PolycraftGuiElement {
	
	//should return the width of entire object
	public int GetWidth();	

	//return height of element
	public int getHeight();
	
	//draw the element
	public int draw();
	
	//Do we want save and load functions for transferring info generically?

}
