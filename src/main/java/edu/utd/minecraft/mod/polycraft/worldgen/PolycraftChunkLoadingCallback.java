package edu.utd.minecraft.mod.polycraft.worldgen;

import java.util.ArrayList;
import java.util.List;

import edu.utd.minecraft.mod.polycraft.experiment.tutorial.ExperimentTutorial.State;
import edu.utd.minecraft.mod.polycraft.experiment.tutorial.TutorialManager;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager.OrderedLoadingCallback;
import net.minecraftforge.common.ForgeChunkManager.Ticket;

public class PolycraftChunkLoadingCallback implements OrderedLoadingCallback{

	@Override
	public void ticketsLoaded(List<Ticket> tickets, World world) {
//		for (Ticket ticket : tickets) {
//			int id = ticket.getModData().getInteger("expID");
//			TileEntityMainframeCore entity = (TileEntityMainframeCore) world.getTileEntity(coreX, coreY, coreZ);
//			entity.forceChunkLoading(ticket);
//		}
	}

	@Override
	public List<Ticket> ticketsLoaded(List<Ticket> tickets, World world, int maxTicketCount) {
		List<Ticket> validTickets = new ArrayList();
		for (Ticket ticket : tickets) {
			int id = ticket.getModData().getInteger("expID");
			if (TutorialManager.getExperiment(id) != null && TutorialManager.getExperiment(id).currentState != State.Done)
				validTickets.add(ticket);
		}
		return validTickets;
	}
}
