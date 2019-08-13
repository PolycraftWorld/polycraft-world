package edu.utd.minecraft.mod.polycraft.commands.dev;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;
import edu.utd.minecraft.mod.polycraft.util.NetUtil;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class CommandReset extends CommandBase{

	private final List aliases;
	private static final String chatCommandCollect = "collect";
	private static final String chatCommandSkillLevel = "skill_level";
	
  
	
	public CommandReset(){
		aliases = new ArrayList(); 
        aliases.add("reset"); 
	}
	
	@Override
	public int compareTo(ICommand arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "reset";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "/reset";
	}

	@Override
	public List getCommandAliases() {
		// TODO Auto-generated method stub
		return this.aliases;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) throws PlayerNotFoundException {
		
		EntityPlayerMP player = getCommandSenderAsPlayer(sender);
		World world = sender.getEntityWorld();
		
		if (world.isRemote) 
        { 
            System.out.println("Not processing on Client side"); 
        } 
		else
		{
			int x = 160, y = 4, z = 16;
			BlockPos pos = new BlockPos(160, 4, 16);
			String response;
			System.out.println("Processing on Server side"); 
			if(args.length > 0)
				switch(args[0].toLowerCase()) {
				case chatCommandCollect:
					Gson gson = new Gson();
					JsonObject jobject = new JsonObject();
					
					ArrayList<Integer> map = new ArrayList<Integer>();
					for(int i = 0; i < 16; i++) {
						for(int k = 0; k < 16; k++) {
							map.add(Block.getIdFromBlock(player.worldObj.getBlockState(pos.add(i, 0, k)).getBlock()));
						}
					}
					JsonElement result = gson.toJsonTree(map);
					jobject.add("map",result);
					jobject.addProperty("playerX", (int)player.posX - x);
					jobject.addProperty("playerZ", (int)player.posZ - z);
					player.addChatComponentMessage(new ChatComponentText(jobject.toString()));
					break;
				default:
					player.setPosition(x + Math.random() * 15, y, z + Math.random() * 15);
					break;
				}
			else {
				
				for(int i = 0; i < 16; i++) {
					for(int k = 0; k < 16; k++) {
						if(i == 0) {
							player.worldObj.setBlockState(pos.add(i - 1, 0, k), Blocks.wool.getStateFromMeta(k%16), 2);
							player.worldObj.setBlockState(pos.add(i - 1, 1, k), Blocks.wool.getStateFromMeta(k%16), 2);
							if(k==0) {
								player.worldObj.setBlockState(pos.add(i - 1, 0, k - 1), Blocks.wool.getStateFromMeta(k%16), 2);
								player.worldObj.setBlockState(pos.add(i - 1, 1, k - 1), Blocks.wool.getStateFromMeta(k%16), 2);
							}
						}
						if(i == 15) {
							player.worldObj.setBlockState(pos.add(i + 1, 0, k), Blocks.wool.getStateFromMeta(k%16), 2);
							player.worldObj.setBlockState(pos.add(i + 1, 1, k), Blocks.wool.getStateFromMeta(k%16), 2);
						}
						if(k == 0) {
							player.worldObj.setBlockState(pos.add(i + 1, 0, k - 1), Blocks.wool.getStateFromMeta(k%16), 2);
							player.worldObj.setBlockState(pos.add(i + 1, 1, k - 1), Blocks.wool.getStateFromMeta(k%16), 2);
						}
						if(k == 15) {
							player.worldObj.setBlockState(pos.add(i, 0, k + 1), Blocks.wool.getStateFromMeta(k%16), 2);
							player.worldObj.setBlockState(pos.add(i, 1, k + 1), Blocks.wool.getStateFromMeta(k%16), 2);
						}
						
						player.worldObj.setBlockToAir(pos.add(i, 0, k));
					}
				}
				player.setPositionAndUpdate(x + Math.random() * 15, y, z + Math.random() * 15);
				player.worldObj.setBlockState(pos.add(Math.random() * 15, 0, Math.random() * 15), Blocks.log.getDefaultState(), 2);
			}
			
		}		
	}
	
	@Override
	public boolean canCommandSenderUseCommand(ICommandSender p_71519_1_) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_, BlockPos blockPos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
		// TODO Auto-generated method stub
		return false;
	}

}
