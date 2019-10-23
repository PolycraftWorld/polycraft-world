package edu.utd.minecraft.mod.polycraft.client.gui.api;

import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiPolyButtonCycle<E extends Enum<E>> extends GuiButton{
	
	private E option;
	private String prefix;
	
	public enum Toggle{
		TRUE,
		FALSE;
		
		public static Toggle fromBool(boolean toggle) {
			if(toggle)
				return TRUE;
			else
				return FALSE;
		}
		
		public static boolean toBool(Toggle toggle) {
			if(toggle == TRUE)
				return true;
			else
				return false;
		}
	}

	public GuiPolyButtonCycle(int id, int xStart, int yStart, int width, int height,
			String text, final E option) {
		super(id, xStart, yStart, width, height, text + ": " + option.name());
		this.prefix = text;
		this.option = option;
	}
	
	public E getCurrentOption() {
		return option;
	}
	
	public void nextOption() {
		if (option.ordinal() == option.getDeclaringClass().getEnumConstants().length - 1)
	    	option = option.getDeclaringClass().getEnumConstants()[0];
		else
			option = option.getDeclaringClass().getEnumConstants()[option.ordinal() + 1];
		this.displayString = prefix + ": " + option.name();
	}

}
