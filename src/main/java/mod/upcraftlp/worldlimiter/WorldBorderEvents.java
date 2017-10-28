package mod.upcraftlp.worldlimiter;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.world.WorldEvent;
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
            spawns.put(dimension, FMLCommonHandler.instance().getMinecraftServerInstance().getWorld(dimension).getSpawnPoint());
        }
        return spawns.get(dimension);
    }

    @SubscribeEvent
    public static void tick(WorldEvent.Load event) {
        World world = event.getWorld();
        if(event.getWorld().isRemote) return;
        int dimID = world.provider.getDimension();
        for(int dim1ID : ModConfig.affectedDimensions) {
            if(dim1ID == dimID) {
                int border = ModConfig.radius;
                WorldBorder wb = world.getWorldBorder();
                int wBorder = 29999984;
                if(border > 0 && ModConfig.enableWorldBorder) {
                    wBorder = border * 2 + 2;
                    BlockPos spawn = world.getSpawnPoint();
                    wb.setCenter(spawn.getX() + 0.5D, spawn.getZ() + 0.5D);
                    wb.setWarningDistance(ModConfig.notificationRange);
                    spawns.put(dimID, spawn);
                }
                wb.setTransition(wBorder);
                Main.getLogger().info("Successfully set world border for dimension " + dimID + " to " + wb.getDiameter()/2 + " blocks.");
                break;
            }
        }
    }

    private static final Set<UUID> notifiedPlayers = Sets.newConcurrentHashSet();

    @SubscribeEvent
	public static void notifyPlayers(TickEvent.PlayerTickEvent event) {
		EntityPlayer player = event.player;
		if(player != null && event.side == Side.SERVER && ModConfig.enableNotification) {
		    for(int id : ModConfig.affectedDimensions) {
		        if(id == player.dimension) {
                    int border = ModConfig.radius - ModConfig.notificationRange;
                    UUID uuid = player.getUniqueID();
                    if(Math.abs(getOffsetX(player)) > border || Math.abs(getOffsetZ(player)) > border) {
                        if(!notifiedPlayers.contains(uuid)) {
                            player.sendStatusMessage(new TextComponentString("You're close to the world border, expect teleporting").setStyle(new Style().setColor(TextFormatting.DARK_GRAY)), true);
                            notifiedPlayers.add(uuid);
                        }
                    }
                    else if(notifiedPlayers.contains(uuid)) notifiedPlayers.remove(uuid);
                    return;
                }
            }
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
		if(world.isRemote ||entity.isDead) return;
        for (int id : ModConfig.affectedDimensions) {
            if (id == entity.dimension) {
                int border = ModConfig.radius;
                int notRange = ModConfig.notificationRange;
                double xOffset = getOffsetX(entity);
                double zOffset = getOffsetZ(entity);
                BlockPos newPos = null;
                if (Math.abs(xOffset) + 0.5D > border) {
                    double x = entity.posX - xOffset * 2 + Math.copySign(notRange + 3.0D, xOffset);
                    newPos = new BlockPos(x, entity.posY, entity.posZ);
                } else if (Math.abs(zOffset) + 0.5D > border) {
                    double z = entity.posZ - zOffset * 2 + Math.copySign(notRange + 3.0D, zOffset);
                    newPos = new BlockPos(entity.posX, entity.posY, z);
                }
                if (newPos != null) {
                    while (!world.isAirBlock(newPos) || !world.isAirBlock(newPos.up())) newPos = newPos.up();
                    double mX = entity.motionX;
                    double mY = entity.motionY;
                    double mZ = entity.motionZ;
                    entity.setPositionAndUpdate(newPos.getX(), newPos.getY(), newPos.getZ());
                    entity.motionX = mX;
                    entity.motionY = mY;
                    entity.motionZ = mZ;
                    if (entity instanceof EntityPlayerMP) {
                        EntityPlayerMP player = (EntityPlayerMP) entity;
                        teleportPlayer(player, world, newPos.getX(), newPos.getY(), newPos.getZ());
                    }
                    for (Entity passenger : entity.getPassengers()) {
                        if (passenger instanceof EntityPlayerMP && !passenger.isDead) {
                            teleportPlayer((EntityPlayerMP) passenger, world, passenger.posX, passenger.posY, passenger.posZ);
                        }
                        world.updateEntity(passenger);
                    }
                }
                return;
            }
        }
	}

	private static void teleportPlayer(EntityPlayerMP player, World world, double x, double y, double z) {
        if(player.isRiding()) {
            Entity mount = player.getRidingEntity();
            mount.setPositionAndUpdate(x, y, z);
            world.updateEntityWithOptionalForce(mount, true);
        }
        player.setPositionAndUpdate(x, y, z);
        player.connection.sendPacket(new SPacketEntityVelocity(player));
    }
}
