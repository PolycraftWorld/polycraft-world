package edu.utd.minecraft.mod.polycraft.inventory.computer;

public class GuiRectangle {
	
	public int x,y,w,h;
	
	public GuiRectangle (int x, int y, int w, int h){
		
		this.x = x/2;
		this.y = y/2;
		this.w = w/2;
		this.h = h/2;
	}
	
	public boolean inRect(ComputerGui gui, int mouseX, int mouseY, int side){
		
//		mouseX -= gui.getLeft();
//		mouseY -= gui.getTop();
//		int temp1 = x + gui.getXSize();
//		int temp2 = x + w + gui.getXSize();
//		System.out.println("left: " + temp1 + " | " + mouseX + " | " + temp2);
//		System.out.println(gui.getTop());
		if (side == 2){
			//right side of GUI
			
			int leftBorder = gui.getLeft() + gui.getXSize();
			int rightBorder = gui.getLeft() + gui.getXSize() + w -10/2;
			int topBorder = gui.getTop() + y;
			int bottomBorder = gui.getTop() + y + h;

			return leftBorder <= mouseX &&
					mouseX <= rightBorder &&
					topBorder < mouseY &&
					mouseY < bottomBorder;
			
//			return x + gui.getXSize() <= mouseX &&
//					mouseX <= x + w + gui.getXSize() &&
//					y < mouseY &&
//					mouseY < y + h;
					
	}
		else {
			System.out.println("using side " + side);
			return (x <= mouseX) &&
					(mouseX <= x + w ) &&
					(y < mouseY) &&
					(mouseY < y + h);
			
		}
				
	}
	
	//currently only side 2 is being used
	public void draw (ComputerGui gui, int srcX, int srcY, int side){
		
		if (side == 0){
			//top
		}
		else if(side ==1){
			//left
			gui.drawTexturedModalRect(gui.getLeft() + x, gui.getTop() + y, srcX, srcY, w, h);
		}
		else if(side == 2){
			//right
			gui.drawTexturedModalRect(gui.getLeft() + gui.getXSize()-10/2, gui.getTop() + y, srcX, srcY, w, h);
			
		}
		else{
			//bottom
		}
	}

}
