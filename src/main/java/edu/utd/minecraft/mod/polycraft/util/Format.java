package edu.utd.minecraft.mod.polycraft.util;

import java.awt.Color;

public class Format {
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
    
    public static int getIntegerFromColor(Color color) {
    	//MINECRAFT IS DUMB.
    	
    	int red = color.getRed();
    	int green = color.getGreen();
    	int blue = color.getBlue();
    	int alpha = color.getAlpha();
    	
    	System.out.println("Red, Green ,Blue, Alpha: " + red + " " + blue + " " + green + " " + alpha);
    	
    	int rgbInt = alpha;
    	rgbInt = (rgbInt << 8) + red;
    	rgbInt = (rgbInt << 8) + green;
    	rgbInt = (rgbInt << 8) + blue;
    	
    	System.out.println(rgbInt);
    	return rgbInt;
    	
    	
    	 //TODO: create a parser for the following function, so it's easy to set colors!
        /*p_78258_4_ is the integer that minecraft parses to get the color channels.
         * this.red = (float)(p_78258_4_ >> 16 & 255) / 255.0F;
            this.blue = (float)(p_78258_4_ >> 8 & 255) / 255.0F;
            this.green = (float)(p_78258_4_ & 255) / 255.0F;
            this.alpha = (float)(p_78258_4_ >> 24 & 255) / 255.0F;
         */
    	
    }
}