package edu.utd.minecraft.mod.polycraft.aitools.domain;

import java.util.ArrayList;

import edu.utd.minecraft.mod.polycraft.aitools.BotAPI.APICommand;

public class DomainPogoStick implements Domain{

	ArrayList<APICommand> actionSpace;
	
	public DomainPogoStick() {
		actionSpace = new ArrayList<APICommand>();
		actionSpace.add(APICommand.LOOK_NORTH);
		actionSpace.add(APICommand.LOOK_SOUTH);
		actionSpace.add(APICommand.LOOK_EAST);
		actionSpace.add(APICommand.LOOK_WEST);
		actionSpace.add(APICommand.MOVE_FORWARD);
		actionSpace.add(APICommand.MOVE_NORTH);
		actionSpace.add(APICommand.MOVE_SOUTH);
		actionSpace.add(APICommand.MOVE_EAST);
		actionSpace.add(APICommand.MOVE_WEST);
		actionSpace.add(APICommand.JUMP);
		actionSpace.add(APICommand.BREAK_BLOCK);
		actionSpace.add(APICommand.COLLECT_FROM_BLOCK);
		actionSpace.add(APICommand.ATTACK);
		actionSpace.add(APICommand.USE);
		actionSpace.add(APICommand.PLACE_BLOCK);
		actionSpace.add(APICommand.PLACE_CRAFTING_TABLE);
		actionSpace.add(APICommand.PLACE_TREE_TAP);
		actionSpace.add(APICommand.CRAFT_PLANKS);
		actionSpace.add(APICommand.CRAFT_STICKS);
		actionSpace.add(APICommand.CRAFT_AXE);
		actionSpace.add(APICommand.CRAFT_TREE_TAP);
		actionSpace.add(APICommand.CRAFT_POGO_STICK);
		actionSpace.add(APICommand.SELECT_AXE);
	}
	
	@Override
	public ArrayList<APICommand> getActionSpace() {
		return actionSpace;
	}

	@Override
	public int getReward() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub
		
	}
	
}
