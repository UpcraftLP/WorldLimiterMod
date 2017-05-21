package mod.upcraftlp.worldlimiter;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.border.WorldBorder;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = Reference.MODID)
public class WorldBorderEvents {

    private static Map<Integer, BlockPos> spawns = Maps.newConcurrentMap();

    public static BlockPos getSpawn(int dimension) {
        if(!spawns.containsKey(dimension)) {
            spawns.put(dimension, FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(dimension).getSpawnPoint());
        }
        return spawns.get(dimension);
    }

    @SubscribeEvent
    public static void tick(TickEvent.ServerTickEvent event) {
        if(!ModConfig.isDirty) return;
        ModConfig.isDirty = false;
        WorldServer[] worlds = FMLCommonHandler.instance().getMinecraftServerInstance().worlds;
        spawns.clear();
        int border = ModConfig.radius;
        for (WorldServer worldServer : worlds) {
            int dimID = worldServer.provider.getDimension();
            spawns.put(dimID, worldServer.getSpawnPoint());
            if(ModConfig.dimensions.contains(dimID)) {
                WorldBorder wb = worldServer.getWorldBorder();
                int wBorder = 29999984;
                if(border > 0 && ModConfig.enableWorldBorder) {
                    wBorder = border * 2 + 1;
                    BlockPos spawn = worldServer.getSpawnPoint();
                    wb.setCenter(spawn.getX() + 0.5D, spawn.getZ() + 0.5D);
                    wb.setWarningDistance(ModConfig.notificationRange);
                }
                wb.setTransition(wBorder);
                Main.getLogger().info("Successfully set world border for dimension " + dimID + " to " + worldServer.getWorldBorder().getDiameter()/2 + " Blocks.");
            }
        }
    }

    public static final Set<UUID> notifiedPlayers = Sets.newConcurrentHashSet();

    @SubscribeEvent
	public static void notifyPlayers(TickEvent.PlayerTickEvent event) {
		EntityPlayer player = event.player;
		if(player != null && event.side == Side.SERVER && ModConfig.enableNotification && ModConfig.dimensions.contains(player.dimension)) {
            int border = ModConfig.radius - ModConfig.notificationRange;
		    UUID uuid = player.getUniqueID();
            if(Math.abs(getOffsetX(player)) > border || Math.abs(getOffsetZ(player)) > border) {
                if(!notifiedPlayers.contains(uuid)) {
                    player.sendStatusMessage(new TextComponentString("You're close to the world border, expect teleporting").setStyle(new Style().setColor(TextFormatting.DARK_GRAY)), true);
                    notifiedPlayers.add(uuid);
                }
            }
            else if(notifiedPlayers.contains(uuid)) notifiedPlayers.remove(uuid);
        }
	}

	private static double getOffsetX(Entity entity) {
        BlockPos spawn = getSpawn(entity.dimension);
        double result = entity.posX - spawn.getX();
        return result + Math.copySign(0.5D, result);
    }

    private static double getOffsetZ(Entity entity) {
        BlockPos spawn = getSpawn(entity.dimension);
        double result = entity.posZ - spawn.getZ();
        return result + Math.copySign(0.5D, result);
    }

	@SubscribeEvent
	public static void teleportEntities(LivingUpdateEvent event) {
		Entity entity = event.getEntity();
		World world = entity.getEntityWorld();
		if(world.isRemote || !ModConfig.dimensions.contains(entity.dimension)) return;
        int border = ModConfig.radius;
        int notRange = ModConfig.notificationRange;
        double xOffset = getOffsetX(entity) - 0.5D;
        double zOffset = getOffsetZ(entity) - 0.5D;
        BlockPos newPos = null;
        if(Math.abs(xOffset) > border) {
            double x = entity.posX - xOffset * 2 + Math.copySign(notRange + 3.0D, xOffset);
            newPos = new BlockPos(x, entity.posY, entity.posZ);
        }
        else if(Math.abs(zOffset) > border) {
            double z = entity.posZ - zOffset * 2 + Math.copySign(notRange + 3.0D, zOffset);
            newPos = new BlockPos(entity.posX, entity.posY, z);
        }
		if(newPos != null) {
            if(!world.isAirBlock(newPos) || !world.isAirBlock(newPos.up())) newPos = world.getTopSolidOrLiquidBlock(newPos).up();

            double mX = entity.motionX;
            double mY = entity.motionY;
            double mZ = entity.motionZ;
            //FIXME calculate player motion! (packets!)
            entity.setPositionAndUpdate(newPos.getX(), newPos.getY(), newPos.getZ());
            entity.setVelocity(mX, mY, mZ);
            for (Entity passenger : entity.getPassengers()) {
                world.updateEntity(passenger);
                if(passenger instanceof EntityPlayer) {
                    ((EntityPlayer) passenger).sendStatusMessage(new TextComponentString("Please get off your mount to update your position."), true); //TODO force dismount?
                }
            }
        }
	}
}
