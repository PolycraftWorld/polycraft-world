package edu.utd.minecraft.mod.polycraft.minigame;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.registry.GameData;
import edu.utd.minecraft.mod.polycraft.PolycraftMod;
import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class CommandGame extends CommandBase{
	private final List aliases;
	
	public CommandGame(){
		aliases = new ArrayList(); 
        aliases.add("Game"); 
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
						world.setBlock(0, 0, 0, GameData.getBlockRegistry().getObject(PolycraftMod.getAssetName("1hA")));
						int rad=(int)((BlockGameBlock) world.getBlock(0, 0, 0)).getRadius();
						 
						for(int i=0;i<world.playerEntities.size();i++)
						{
							EntityPlayer p =(EntityPlayer) world.playerEntities.get(i);
							int x = ThreadLocalRandom.current().nextInt(-rad+20, rad-20 + 1);
							int z = ThreadLocalRandom.current().nextInt(-rad+20, rad-20 + 1);
							p.setPositionAndUpdate(x, world.getTopSolidOrLiquidBlock(x, z), z);
							p.setCurrentItemOrArmor(3, new ItemStack(GameData.getItemRegistry().getObject(PolycraftMod.getAssetName("3p"))));
							p.setCurrentItemOrArmor(0,  new ItemStack(GameData.getItemRegistry().getObject(PolycraftMod.getAssetName("3n"))));
						}
					}
					
				}
			}
		}
	}
	
	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "Game";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "/Game <command> <value>";
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
