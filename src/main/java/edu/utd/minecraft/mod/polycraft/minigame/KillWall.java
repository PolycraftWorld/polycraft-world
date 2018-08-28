package edu.utd.minecraft.mod.polycraft.minigame;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class KillWall extends CommandBase{
	

	
	private  final String chatKillWallStart = "start";
	private  final String chatKillWallStop = "stop";
	private  final String chatKillWallSet = "set";
	private final List aliases;
	
	public KillWall(){
		aliases = new ArrayList(); 
        aliases.add("KillWall"); 
	}
	
	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		World world = sender.getEntityWorld();
		
		if (world.isRemote) 
        { 
            System.out.println("Not processing on Client side"); 
        } 
		else
		{
			if(world.getBlock(0, 0, 0) instanceof BlockGameBlock)
			{
				BlockGameBlock gameBlock =((BlockGameBlock)world.getBlock(0, 0, 0));
				System.out.println("Processing on Server side"); 
				if (args.length > 0) {
	
					String tool[] = args[0].split("\\s+");
					String value[] = null;
					if(args.length>1) {
						value = args[1].split("\\s+");
					}
					if(tool.length>0){
						((EntityPlayer) player).addChatComponentMessage(new ChatComponentText("test: "+tool[0]));
						if(tool[0].equals("start")) {
							gameBlock.active=true;
						}
	
						
						else if(tool[0].equals("stop")) {
							gameBlock.active=false;
						}
						else if(tool[0].equals("set")) {
							if(value.length>0)
							{
								
								gameBlock.setRadius((double)Integer.parseInt(value[0]));
								
							}
						}
					}
				}
			}
		}		
	}

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "KillWall";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "/KillWall <command> <value>";
	}

	@Override
	public List getCommandAliases() {
		// TODO Auto-generated method stub
		return this.aliases;
	}
	
	

	@Override
	public int compareTo(Object arg0) {
		// TODO Auto-generated method stub
		return 0;
	}



//	@Override
//	public void processCommand(ICommandSender sender, String[] args) {
//		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
//		World world = sender.getEntityWorld();
//		if (world.isRemote) { 
//            System.out.println("Not processing on Client side"); 
//        } 
//		else{
//			System.out.println("Processing on Server side"); 
//		
//			if (args.length > 0){
//				String tool[] = args[0].split("\\s+");
//				String value[] = null;
//				if(args.length>1) {
//					value = args[1].split("\\s+");
//				}
//				if(tool.length>0){
//					((EntityPlayer) player).addChatComponentMessage(new ChatComponentText("test: "+tool[0]));
//					if(tool[0].equals("pos1")) {
//						this.x1=(int)Math.floor(player.posX);
//						this.y1=(int)Math.floor(player.posY);
//						this.z1=(int)Math.floor(player.posZ);
//						String s="Position 1: "+x1+", " +y1+", "+z1;
//				        ((EntityPlayer) player).addChatComponentMessage(new ChatComponentText(s));
//				        this.pos1=true;
//					}
//					else if(tool[0].equals("pos2")) {
//						this.x2=(int)Math.floor(player.posX);
//						this.y2=(int)Math.floor(player.posY);
//						this.z2=(int)Math.floor(player.posZ);
//						String s="Position 2: "+x2+", " +y2+", "+z2;
//				        ((EntityPlayer) player).addChatComponentMessage(new ChatComponentText(s));
//				        this.pos2=true;
//						
//					}else if(tool[0].equals("paste")) {
//						if(pos1)
//						{
//							short n = 0;
//							Schematic sch = new Schematic(new NBTTagList(), n, n, n, new int[] {0}, new int[] {0});
//							Schematic sh = sch.getTMP();
//							
//							int count=0;
//							
//							for (int k = 0; k < (int)sh.length; k++) {
//								for (int j = 0; j < (int)sh.height; j++) {
//									for (int i = 0; i < (int)sh.width; i++)
//									{
//										if(count==15361)
//										{
//											System.out.println("too big");
//										}
//										
//										world.setBlock(x1 + k, y1 + j , z1 + i, Block.getBlockById((int)sh.blocks[count]), sh.data[count], 2);
//										count++;
//										
//									}
//								}
//							}
//							for (int k = 0; k < (int)sh.tileentities.tagCount(); k++)
//							{
//								NBTTagCompound nbt = sh.tileentities.getCompoundTagAt(k);
//								TileEntity tile = world.getTileEntity(nbt.getInteger("x")+x1, nbt.getInteger("y")+y1, nbt.getInteger("z")+z1);
//								tile.readFromNBT(nbt);
//								world.setTileEntity(nbt.getInteger("x")+x1, nbt.getInteger("y")+y1, nbt.getInteger("z")+z1, tile);
//							}
//							
//						}
//						
//					}else if(tool[0].equals("print")) {
//						if(pos1 && pos2)
//						{
//							int minX;
//							int maxX;
//							int minY;
//							int maxY;
//							int minZ;
//							int maxZ;
//							int[] intArray;
//							short height;
//							short length;
//							short width;
//							
//							if(x1<x2) {
//								minX=x1;
//								maxX=x2;
//							}else{
//								minX=x2;
//								maxX=x1;
//							}
//							if(y1<y2) {
//								minY=y1;
//								maxY=y2;
//							}else{
//								minY=y2;
//								maxY=y1;
//							}
//							if(z1<z2) {
//								minZ=z1;
//								maxZ=z2;
//							}else{
//								minZ=z2;
//								maxZ=z1;
//							}
//							length=(short)(maxX-minX+1);
//							height=(short)(maxY-minY+1);
//							width=(short)(maxZ-minZ+1);
//							int[] blocks = new int[length*height*width];
//							int[] data = new int[length*height*width];
//							int count=0;
//							NBTTagCompound nbt = new NBTTagCompound();
//							NBTTagList tiles = new NBTTagList();
//							
//							TileEntity tile;
//							for(int i=0;i<length;i++) {
//								for(int j=0;j<height;j++) {
//									for(int k=0;k<width;k++) {
//										
//										tile = world.getTileEntity(minX+i, minY+j, minZ+k);
//										if(tile!=null){
//											NBTTagCompound tilenbt = new NBTTagCompound();
//											tile.xCoord=i;
//											tile.yCoord=j;
//											tile.zCoord=k;
//											tile.writeToNBT(tilenbt);
//											tiles.appendTag(tilenbt);
//											tile.xCoord=minX+i;
//											tile.yCoord=minY+j;
//											tile.zCoord=minZ+k;
//											
//										}
//											
//										Block blk = world.getBlock(minX+i, minY+j, minZ+k);
//										int id = blk.getIdFromBlock(blk);
//										blocks[count]=id;
//										data[count]=world.getBlockMetadata(minX+i, minY+j, minZ+k);
//										count++;
//										
//									}
//								}
//							}
//							nbt.setTag("TileEntity", tiles);
//							FileOutputStream fout = null;
//							try {
//								fout = new FileOutputStream("D:\\testout.schematic");
//							} catch (FileNotFoundException e1) {
//								// TODO Auto-generated catch block
//								e1.printStackTrace();
//							}    
//							
//							
//							
//							
//							nbt.setShort("Height", height);
//							nbt.setShort("Length", length);
//							nbt.setShort("Width", width);
//							nbt.setIntArray("Blocks", blocks);
//							nbt.setIntArray("Data", data);
//							try {
//								CompressedStreamTools.writeCompressed(nbt, fout);
//							} catch (IOException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//							
//						}
//					}else if(tool[0].equals("fill")) {
//						if(value.length>0)
//						{
//							if(pos1 && pos2)
//							{
//								int minX;
//								int maxX;
//								int minY;
//								int maxY;
//								int minZ;
//								int maxZ;
//								int[] intArray;
//								short height;
//								short length;
//								short width;
//								
//								if(x1<x2) {
//									minX=x1;
//									maxX=x2;
//								}else{
//									minX=x2;
//									maxX=x1;
//								}
//								if(y1<y2) {
//									minY=y1;
//									maxY=y2;
//								}else{
//									minY=y2;
//									maxY=y1;
//								}
//								if(z1<z2) {
//									minZ=z1;
//									maxZ=z2;
//								}else{
//									minZ=z2;
//									maxZ=z1;
//								}
//								length=(short)(maxX-minX+1);
//								height=(short)(maxY-minY+1);
//								width=(short)(maxZ-minZ+1);
//								
//								for(int i=0;i<length;i++) {
//									for(int j=0;j<height;j++) {
//										for(int k=0;k<width;k++) {
//											int id= Integer.parseInt(value[0]);
//											world.setBlock(minX+i, minY+j, minZ+k,Block.getBlockById(id));
//											
//										}
//									}
//								}
//								
//								
//							}
//							
//						}
//					
//					}
//					
//					
//					//
//				}
//			}
//		}
//	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		// TODO Auto-generated method stub
		return false;
	}

}
