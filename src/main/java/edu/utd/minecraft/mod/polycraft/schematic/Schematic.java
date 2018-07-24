package edu.utd.minecraft.mod.polycraft.schematic;

import java.io.FileInputStream;
import java.io.InputStream;


import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.client.resources.IResource;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;

public class Schematic {
        public  NBTTagList tileentities;
        public  short width;
        public  short height;
        public short length;
        public int[] blocks;
        public int[] data;
        public Schematic(NBTTagList tileentities, short width, short height, short length, int[] blocks2, int[] data2){
            this.tileentities = tileentities;
            this.width = width;
            this.height = height;
            this.length = length;
            this.blocks = blocks2;
            this.data = data2;
        }

    
	
	public Schematic get(String schemname){
        try {
        	InputStream is = this.getClass().getClassLoader().getResourceAsStream("assets/polycraft/schematics/"+schemname);

            NBTTagCompound nbtdata = CompressedStreamTools.readCompressed(is);
            short width = nbtdata.getShort("Width");
            short height = nbtdata.getShort("Height");
            short length = nbtdata.getShort("Length");

            int[] blocks = nbtdata.getIntArray("Blocks");
            int[] data = nbtdata.getIntArray("Data");


            System.out.println("schem size:" + width + " x " + height + " x " + length);
            NBTTagList tileentities = nbtdata.getTagList("TileEntity",10);
            is.close();

            return new Schematic(tileentities, width, height, length, blocks, data);
        } catch (Exception e) {
            System.out.println("I can't load schematic, because " + e.toString());
            return null;
        }
    }


}
