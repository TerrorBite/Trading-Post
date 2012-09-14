package net.lethargiclion.tradingpost.test;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Achievement;
import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.Server;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.InventoryView.Property;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.map.MapView;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/**
 *  This class exists solely for use in unit testing.
 * @author TerrorBite
 *
 */
@org.junit.Ignore
public class TestPlayer implements Player {
	
	private String name;

	public TestPlayer() {
		this("Player");
	}
	
	public TestPlayer(String name) {
		this.name = name;
	}

	
	public void awardAchievement(Achievement arg0) {
		// TODO Auto-generated method stub

	}

	
	public boolean canSee(Player arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void chat(String arg0) {
		// TODO Auto-generated method stub

	}

	
	public InetSocketAddress getAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean getAllowFlight() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public Location getBedSpawnLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Location getCompassTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public float getExhaustion() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public float getExp() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int getFoodLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int getLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public String getPlayerListName() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public long getPlayerTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public long getPlayerTimeOffset() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public float getSaturation() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int getTotalExperience() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public void giveExp(int arg0) {
		// TODO Auto-generated method stub

	}

	
	public void hidePlayer(Player arg0) {
		// TODO Auto-generated method stub

	}

	
	public void incrementStatistic(Statistic arg0) {
		// TODO Auto-generated method stub

	}

	
	public void incrementStatistic(Statistic arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	
	public void incrementStatistic(Statistic arg0, Material arg1) {
		// TODO Auto-generated method stub

	}

	
	public void incrementStatistic(Statistic arg0, Material arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	
	public boolean isPlayerTimeRelative() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isSleepingIgnored() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isSneaking() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isSprinting() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void kickPlayer(String arg0) {
		// TODO Auto-generated method stub

	}

	
	public void loadData() {
		// TODO Auto-generated method stub

	}

	
	public boolean performCommand(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void playEffect(Location arg0, Effect arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	
	public void playNote(Location arg0, byte arg1, byte arg2) {
		// TODO Auto-generated method stub

	}

	
	public void playNote(Location arg0, Instrument arg1, Note arg2) {
		// TODO Auto-generated method stub

	}

	
	public void resetPlayerTime() {
		// TODO Auto-generated method stub

	}

	
	public void saveData() {
		// TODO Auto-generated method stub

	}

	
	public void sendBlockChange(Location arg0, Material arg1, byte arg2) {
		// TODO Auto-generated method stub

	}

	
	public void sendBlockChange(Location arg0, int arg1, byte arg2) {
		// TODO Auto-generated method stub

	}

	
	public boolean sendChunkChange(Location arg0, int arg1, int arg2, int arg3,
			byte[] arg4) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void sendMap(MapView arg0) {
		// TODO Auto-generated method stub

	}

	
	public void sendRawMessage(String arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setAllowFlight(boolean arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setBedSpawnLocation(Location arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setCompassTarget(Location arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setDisplayName(String arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setExhaustion(float arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setExp(float arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setFoodLevel(int arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setLevel(int arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setPlayerListName(String arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setPlayerTime(long arg0, boolean arg1) {
		// TODO Auto-generated method stub

	}

	
	public void setSaturation(float arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setSleepingIgnored(boolean arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setSneaking(boolean arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setSprinting(boolean arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setTotalExperience(int arg0) {
		// TODO Auto-generated method stub

	}

	
	public void showPlayer(Player arg0) {
		// TODO Auto-generated method stub

	}

	
	public void updateInventory() {
		// TODO Auto-generated method stub

	}

	
	public GameMode getGameMode() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public PlayerInventory getInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public ItemStack getItemInHand() {
		// Our test user is holding a diamond pickaxe
		return new ItemStack(278);
	}

	
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	
	public int getSleepTicks() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public boolean isSleeping() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void setGameMode(GameMode arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setItemInHand(ItemStack arg0) {
		// TODO Auto-generated method stub

	}

	
	public void damage(int arg0) {
		// TODO Auto-generated method stub

	}

	
	public void damage(int arg0, Entity arg1) {
		// TODO Auto-generated method stub

	}

	
	public double getEyeHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public double getEyeHeight(boolean arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public Location getEyeLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public int getHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public Player getKiller() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public int getLastDamage() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public List<Block> getLastTwoTargetBlocks(HashSet<Byte> arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<Block> getLineOfSight(HashSet<Byte> arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public int getMaxHealth() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int getMaximumAir() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int getMaximumNoDamageTicks() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int getNoDamageTicks() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int getRemainingAir() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public Block getTargetBlock(HashSet<Byte> arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Vehicle getVehicle() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean isInsideVehicle() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean leaveVehicle() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void setHealth(int arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setLastDamage(int arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setMaximumAir(int arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setMaximumNoDamageTicks(int arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setNoDamageTicks(int arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setRemainingAir(int arg0) {
		// TODO Auto-generated method stub

	}

	
	public Arrow shootArrow() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Egg throwEgg() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Snowball throwSnowball() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean eject() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public int getEntityId() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public float getFallDistance() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int getFireTicks() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public EntityDamageEvent getLastDamageCause() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Location getLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public int getMaxFireTicks() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public List<Entity> getNearbyEntities(double arg0, double arg1, double arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Entity getPassenger() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Server getServer() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public int getTicksLived() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public UUID getUniqueId() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Vector getVelocity() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public World getWorld() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean isDead() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void playEffect(EntityEffect arg0) {
		// TODO Auto-generated method stub

	}

	
	public void remove() {
		// TODO Auto-generated method stub

	}

	
	public void setFallDistance(float arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setFireTicks(int arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setLastDamageCause(EntityDamageEvent arg0) {
		// TODO Auto-generated method stub

	}

	
	public boolean setPassenger(Entity arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void setTicksLived(int arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setVelocity(Vector arg0) {
		// TODO Auto-generated method stub

	}

	
	public boolean teleport(Location arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean teleport(Entity arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean teleport(Location arg0, TeleportCause arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean teleport(Entity arg0, TeleportCause arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public PermissionAttachment addAttachment(Plugin arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public PermissionAttachment addAttachment(Plugin arg0, String arg1,
			boolean arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public PermissionAttachment addAttachment(Plugin arg0, String arg1,
			boolean arg2, int arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean hasPermission(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean hasPermission(Permission arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isPermissionSet(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isPermissionSet(Permission arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void recalculatePermissions() {
		// TODO Auto-generated method stub

	}

	
	public void removeAttachment(PermissionAttachment arg0) {
		// TODO Auto-generated method stub

	}

	
	public boolean isOp() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void setOp(boolean arg0) {
		// TODO Auto-generated method stub

	}

	
	public void sendMessage(String arg0) {
		// TODO Auto-generated method stub

	}

	
	public long getFirstPlayed() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public long getLastPlayed() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public Player getPlayer() {
		// TODO Auto-generated method stub
		return (Player)this;
	}

	
	public boolean hasPlayedBefore() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isBanned() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isOnline() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isWhitelisted() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void setBanned(boolean arg0) {
		// TODO Auto-generated method stub

	}

	
	public void setWhitelisted(boolean arg0) {
		// TODO Auto-generated method stub

	}

	
	public Map<String, Object> serialize() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Set<String> getListeningPluginChannels() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void sendPluginMessage(Plugin arg0, String arg1, byte[] arg2) {
		// TODO Auto-generated method stub

	}

	
	public void closeInventory() {
		// TODO Auto-generated method stub
		
	}

	
	public ItemStack getItemOnCursor() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public InventoryView getOpenInventory() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public InventoryView openEnchanting(Location arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public InventoryView openInventory(Inventory arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void openInventory(InventoryView arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public InventoryView openWorkbench(Location arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void setItemOnCursor(ItemStack arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public boolean setWindowProperty(Property arg0, int arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean addPotionEffect(PotionEffect arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean addPotionEffect(PotionEffect arg0, boolean arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean addPotionEffects(Collection<PotionEffect> arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public Collection<PotionEffect> getActivePotionEffects() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean hasPotionEffect(PotionEffectType arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public <T extends Projectile> T launchProjectile(Class<? extends T> arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void removePotionEffect(PotionEffectType arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public EntityType getType() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<MetadataValue> getMetadata(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean hasMetadata(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void removeMetadata(String arg0, Plugin arg1) {
		// TODO Auto-generated method stub
		
	}

	
	public void setMetadata(String arg0, MetadataValue arg1) {
		// TODO Auto-generated method stub
		
	}

	
	public void abandonConversation(Conversation arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void acceptConversationInput(String arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public boolean beginConversation(Conversation arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isConversing() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void sendMessage(String[] arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public <T> void playEffect(Location arg0, Effect arg1, T arg2) {
		// TODO Auto-generated method stub
		
	}

	
	public void abandonConversation(Conversation arg0,
			ConversationAbandonedEvent arg1) {
		// TODO Auto-generated method stub
		
	}

	
	public boolean isFlying() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public void setFlying(boolean arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public boolean isBlocking() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public int getExpToLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public boolean hasLineOfSight(Entity arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public float getFlySpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public float getWalkSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public void setFlySpeed(float arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	
	public void setWalkSpeed(float arg0) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

}
