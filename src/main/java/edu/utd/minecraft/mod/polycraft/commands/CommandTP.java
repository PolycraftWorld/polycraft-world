package edu.utd.minecraft.mod.polycraft.commands;

import java.util.ArrayList;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.privateproperty.Enforcer;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty;
import edu.utd.minecraft.mod.polycraft.privateproperty.PrivateProperty.PermissionSet.Action;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class CommandTP extends CommandBase{

	private static final String chatCommandTeleportArgUTD = "utd";
	private static final String chatCommandTeleportArgUser = "user";
	private static final String chatCommandTeleportArgPrivateProperty = "pp";
	private final List aliases;
  
	
	public CommandTP(){
		aliases = new ArrayList(); 
        aliases.add("teleport"); 
        aliases.add("goto"); 
	}
	
	@Override
	public int compareTo(ICommand arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return "teleport";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return "/goto <utd,pp> [x-coord] [y-coord]";
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
            //System.out.println("Not processing on Client side"); 
        } 
		else
		{
			//System.out.println("Processing on Server side"); 
			if (args.length > 0) {
				// teleport to UTD
				
				if (chatCommandTeleportArgUTD.equalsIgnoreCase(args[0])) {
					// only allow if the player is in a private property
					if (Enforcer.findPrivateProperty(player) != null) {
						player.setPositionAndUpdate(1 + .5,
								player.worldObj.getTopSolidOrLiquidBlock(new BlockPos(1,0, 1)).getY() + 3,
								1 + .5);
					}else {
						player.addChatComponentMessage(new ChatComponentText("You need to be in a Private Property!"));
						return;
					}
				}
				// only allow if the player is in chunk 0,0 (center of UTD), or if
				// they are in a private property already
				else if ((Math.abs(player.chunkCoordX) <= 5 && Math.abs(player.chunkCoordZ) <= 5 || Enforcer.findPrivateProperty(player) != null)){
					boolean valid = false;
					int x = 0, z = 0;
					// teleport to a user
					if (chatCommandTeleportArgUser.equalsIgnoreCase(args[0])) {
						if (args.length > 1) {
							// teleport to a player
							final EntityPlayer targetPlayer = player.worldObj
									.getPlayerEntityByName(args[1]);
							if (targetPlayer != null) {
								if (targetPlayer != player) {
									final PrivateProperty targetPrivateProperty = Enforcer.findPrivateProperty(targetPlayer);
									valid = targetPrivateProperty != null
											&& targetPrivateProperty.actionEnabled(
													player, Action.Enter);
									x = (int) targetPlayer.posX;
									z = (int) targetPlayer.posZ;
									if(!valid) {
										player.addChatComponentMessage(new ChatComponentText("Selected user is not in a Private Property."));
										return;
									}
								} else {
									player.addChatComponentMessage(new ChatComponentText("You are here!"));
									return;
								}
							} else {
								player.addChatComponentMessage(new ChatComponentText("Selected user is unavailable."));
								return;
							}
						} else {
							player.addChatComponentMessage(new ChatComponentText("Enter a username!"));
							return;
						}
					}
					// teleport to a private property
					else if (chatCommandTeleportArgPrivateProperty.equalsIgnoreCase(args[0])) {
						if (args.length < 3) {
							final List<PrivateProperty> ownerPrivateProperties = Enforcer.getPrivatePropertiesByOwner()
									.get(player.getDisplayNameString().toLowerCase());
							if (ownerPrivateProperties != null) {
								int index = 0;
								try {
									index = Integer.parseInt(args[1]);
									//check to see what argument they passed in (i.e. which private property)
									if (index < 0 || index >= ownerPrivateProperties.size()) {
										player.addChatComponentMessage(new ChatComponentText("Error: Invalid Private Property."));
										return;
									}
										
								} catch (final Exception e) {
								//edo nothibf	
								}

								final PrivateProperty targetPrivateProperty = ownerPrivateProperties
										.get(index);
								int minX = (targetPrivateProperty.boundTopLeft.x * 16) + 1;
								int minZ = (targetPrivateProperty.boundTopLeft.z * 16) + 1;
								int maxX = (targetPrivateProperty.boundBottomRight.x * 16) + 15;
								int maxZ = (targetPrivateProperty.boundBottomRight.z * 16) + 15;
								x = (minX + maxX) / 2;
								z = (minZ + maxZ) / 2;
								valid = true;
							} else { 
								//player doesn't own private properties lol.
								player.addChatComponentMessage(new ChatComponentText("Error: You don't own any Private Property."));
								return;
							}
							
						} else { 
							try {
								x = Integer.parseInt(args[1]);
								z = Integer.parseInt(args[2]); 
								final net.minecraft.world.chunk.Chunk chunk = player.worldObj
										.getChunkFromBlockCoords(new BlockPos(x,0, z));
								final PrivateProperty targetPrivateProperty = Enforcer.findPrivateProperty(
										player, chunk.xPosition, chunk.zPosition);
								valid = targetPrivateProperty != null
										&& targetPrivateProperty.actionEnabled(
												player, Action.Enter);
							} catch (final Exception e) {
								//catch a parsing error and just say you dun goofed.
								player.addChatComponentMessage(new ChatComponentText("Error: Invalid Private Property."));
								return;
							}
						}
					}

					if (valid) {
						player.setPositionAndUpdate(x + .5,
								player.worldObj.getTopSolidOrLiquidBlock(new BlockPos(x,0, z)).getY() + 3,
								z + .5);
					} else {
						player.addChatComponentMessage(new ChatComponentText("Error: Invalid Private Property."));
					}
				}else {
					player.addChatComponentMessage(new ChatComponentText("Error: You need to be inside a Private Property!"));
				}
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
