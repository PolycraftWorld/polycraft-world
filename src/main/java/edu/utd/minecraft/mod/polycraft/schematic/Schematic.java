package edu.utd.minecraft.mod.polycraft.schematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Paths;

import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import net.minecraft.client.Minecraft;
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
        public byte[] data;
        public Schematic(NBTTagList tileentities, short width, short height, short length, int[] blocks2, byte[] data2){
            this.tileentities = tileentities;
            this.width = width;
            this.height = height;
            this.length = length;
            this.blocks = blocks2;
            this.data = data2;
        }

    
	
	public Schematic get(String schemname){
        try {
        	System.out.println(Paths.get(".").toAbsolutePath().normalize().toString());
        	ResourceLocation schematicLocation = new ResourceLocation(PolycraftMod.getAssetName("schematics/" + schemname));
        			//PolycraftMod.getAssetName("textures/entity/OilSlime.png"));
        	
        	InputStream is = this.getClass().getClassLoader().getResourceAsStream("assets/polycraft/schematics/" + schemname);
        	
        	System.out.println(is.available());
        	
        	//this.getClass().getClassLoader().get
        	//InputStream is = new FileInputStream("C:\\Users\\Triforce\\Documents\\polycraftworld\\eclipse\\stoop.schematic");
        	//InputStream is = Schematic.class.getResourceAsStream("/assets/polycraft/schematics/stoop.schematic");
        	//InputStream is = this.getClass().getClassLoader().getResourceAsStream("assets" + File.separator + "polycraft" + File.separator + "schematics" + File.separator + schemname);
        	//InputStream is = new FileInputStream("stoop.schematic");
            NBTTagCompound nbtdata = CompressedStreamTools.readCompressed(is);
            short width = nbtdata.getShort("Width");
            short height = nbtdata.getShort("Height");
            short length = nbtdata.getShort("Length");

            int[] blocks = nbtdata.getIntArray("Blocks");
            byte[] data = nbtdata.getByteArray("Data");

            System.out.println("schem size:" + width + " x " + height + " x " + length);
            NBTTagList tileentities = nbtdata.getTagList("TileEntity",10); //10 is actually Bytes (indicates that it is getting a list of "Short" entities)
            is.close();

            return new Schematic(tileentities, width, height, length, blocks, data);
        } catch (Exception e) {
            System.out.println("I can't load schematic, because " + e.toString());
            return null;
        }
    }
	
//	public Schematic getTMP(){
//        try {
//        	InputStream is = new FileInputStream("D:\\testout.schematic");
//
//            NBTTagCompound nbtdata = CompressedStreamTools.readCompressed(is);
//            short width = nbtdata.getShort("Width");
//            short height = nbtdata.getShort("Height");
//            short length = nbtdata.getShort("Length");
//
//            int[] blocks = nbtdata.getIntArray("Blocks");
//            byte[] data = nbtdata.getByteArray("Data");
//
//
//            System.out.println("schem size:" + width + " x " + height + " x " + length);
//            NBTTagList tileentities = nbtdata.getTagList("TileEntity",10);
//            is.close();
//
//            return new Schematic(tileentities, width, height, length, blocks, data);
//        } catch (Exception e) {
//            System.out.println("I can't load schematic, because " + e.toString());
//            return null;
//        }
//    }


}
