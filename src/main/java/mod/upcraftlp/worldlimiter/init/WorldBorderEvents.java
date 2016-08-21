package mod.upcraftlp.worldlimiter.init;

import java.util.Iterator;

import mod.upcraftlp.worldlimiter.util.SysUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.Side;

public class WorldBorderEvents {

	private static int border = 0;
	private static int notRange;
	private static boolean showNotification;
		
	public static void init() {
		border = ModConfig.maxWorldSize;
		showNotification = ModConfig.enableNotification;
		notRange = ModConfig.notificationRange;
		if(border > 0) MinecraftForge.EVENT_BUS.register(new WorldBorderEvents());
	}
	
	private void notifyPlayer(EntityPlayer player) {
		if(player.getTags() != null) {
			Iterator<String> i = player.getTags().iterator();
			while(i.hasNext()) {
				String tag = i.next();
				if(tag.equals("closeToBorder")) return;
			}
		}
		player.addChatComponentMessage(new TextComponentString("You're close to the world border, expect teleporting"));
		player.getTags().add("closeToBorder");
	}
	
	public static void worldBorder(FMLServerStartedEvent event) {
		WorldServer[] worlds = FMLCommonHandler.instance().getMinecraftServerInstance().getServer().worldServers;
		for (WorldServer worldServer : worlds) {
			if(worldServer.provider.getDimension() == 0) {
				int wBorder = border*2 + 1;
				worldServer.getWorldBorder().setSize(wBorder);
				worldServer.getWorldBorder().setCenter(0.0D, 0.0D);
				worldServer.getWorldBorder().setWarningDistance(notRange);
				SysUtils.printlineFML("Successfully set world border for dimension 0 to " + worldServer.getWorldBorder().getDiameter()/2 + " Blocks.");
			}
		}
	}
	
	@SubscribeEvent
	public void notifyPlayers(PlayerTickEvent event) {
		if(!showNotification || event.side == Side.CLIENT || event.player == null) return;
		EntityPlayer player = event.player;
		
		//+X
		if(player.posX > border - notRange) {
			notifyPlayer(player);
			return;
		}
		
		//-X
		if(player.posX < -border + notRange) {
			notifyPlayer(player);
			return;
		}
		
		//+Z
		if(player.posZ > border - notRange) {
			notifyPlayer(player);
			return;
		}
		
		//-Z
		if(player.posZ < -border + notRange) {
			notifyPlayer(player);
			return;
		}
		if(player.getTags() != null) {
			Iterator<String> i = player.getTags().iterator();
			while(i.hasNext()) {
				String tag = i.next();
				if(tag.equals("closeToBorder")) i.remove();
			}
		}
	}
	
	@SubscribeEvent
	public void teleportEntities(LivingUpdateEvent event) {
		Entity entity = event.getEntity();
		World world = entity.worldObj;
		if(world.isRemote) return;
		
		//+X
		if(entity.posX > border) {
			BlockPos newPos = new BlockPos(-border + notRange + 5.0D, entity.posY, entity.posZ);
			if(!entity.worldObj.isAirBlock(newPos) || !entity.worldObj.isAirBlock(newPos.up())) newPos = world.getTopSolidOrLiquidBlock(newPos).up();
			entity.setPositionAndUpdate(newPos.getX(), newPos.getY(), newPos.getZ());
			return;
		}
		
		//-X
		if(entity.posX < -border) {
			BlockPos newPos = new BlockPos(border - notRange - 5.0D, entity.posY, entity.posZ);
			if(!entity.worldObj.isAirBlock(newPos) || !entity.worldObj.isAirBlock(newPos.up())) newPos = world.getTopSolidOrLiquidBlock(newPos).up();
			entity.setPositionAndUpdate(newPos.getX(), newPos.getY(), newPos.getZ());
			return;
		}
		
		//+Z
		if(entity.posZ > border) {
			BlockPos newPos = new BlockPos(entity.posX, entity.posY, -border + notRange + 5.0D);
			if(!entity.worldObj.isAirBlock(newPos) || !entity.worldObj.isAirBlock(newPos.up())) newPos = world.getTopSolidOrLiquidBlock(newPos).up();
			entity.setPositionAndUpdate(newPos.getX(), newPos.getY(), newPos.getZ());
			return;
		}
				
		//-Z
		if(entity.posZ < -border) {
			BlockPos newPos = new BlockPos(entity.posZ, entity.posY, border - notRange - 5.0D);
			if(!entity.worldObj.isAirBlock(newPos) || !entity.worldObj.isAirBlock(newPos.up())) newPos = world.getTopSolidOrLiquidBlock(newPos).up();
			entity.setPositionAndUpdate(newPos.getX(), newPos.getY(), newPos.getZ());
			return;
		}
	}
}
