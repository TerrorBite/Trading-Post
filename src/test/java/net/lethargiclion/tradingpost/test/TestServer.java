package net.lethargiclion.tradingpost.test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.Warning.WarningState;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;

import com.avaje.ebean.config.ServerConfig;

/**
 * This class provides a dummy implementation of the Bukkit Server interface
 * for use in unit testing classes that call Bukkit.getServer().
 * The TestServer has no internal state, and its implemented methods will usually
 * return meaningless but non-null values.
 * 
 * Notably, the getPlayer(), getPlayerExact() and getOfflinePlayer() methods will
 * return a new TestPlayer instance whose player name matches the one specified
 * (i.e. it will always successfully "find" a player with that name).
 * @author TerrorBite
 *
 */
@org.junit.Ignore
public class TestServer implements Server {

	
	public boolean addRecipe(Recipe recipe) {
		return true;
	}

	
	public void banIP(String address) {}

	
	public int broadcast(String message, String permission) {
		return 0;
	}

	
	public int broadcastMessage(String message) {
		return 0;
	}

	
	public void configureDbConfig(ServerConfig config) {}

	
	public MapView createMap(World world) {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	
	public World createWorld(WorldCreator creator) {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	
	public boolean dispatchCommand(CommandSender sender, String commandLine)
			throws CommandException {
		return false;
	}

	
	public boolean getAllowEnd() {
		return true;
	}

	
	public boolean getAllowFlight() {
		return false;
	}

	
	public boolean getAllowNether() {
		return true;
	}

	
	public Set<OfflinePlayer> getBannedPlayers() {
		return new HashSet<OfflinePlayer>();
	}

	
	public String getBukkitVersion() {
		return "1.3.1 R2.0";
	}

	
	public Map<String, String[]> getCommandAliases() {
		return new LinkedHashMap<String, String[]>();
	}

	
	public ConsoleCommandSender getConsoleSender() {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	
	public GameMode getDefaultGameMode() {
		return org.bukkit.GameMode.SURVIVAL;
	}

	
	public Set<String> getIPBans() {
		return new HashSet<String>();
	}

	
	public String getIp() {
		return "127.0.0.1";
	}

	
	public Logger getLogger() {
		return Logger.getLogger("TestServer");
	}

	
	public MapView getMap(short id) {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	
	public int getMaxPlayers() {
		return 20;
	}

	
	public Messenger getMessenger() {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	
	public String getName() {
		return "Bukkit Test Stub";
	}

	
	public OfflinePlayer getOfflinePlayer(String name) {
		return new TestPlayer(name);
	}

	
	public OfflinePlayer[] getOfflinePlayers() {
		return new OfflinePlayer[0];
	}

	
	public boolean getOnlineMode() {
		return true;
	}

	
	public Player[] getOnlinePlayers() {
		return new Player[0];
	}

	
	public Set<OfflinePlayer> getOperators() {
		return new HashSet<OfflinePlayer>();
	}

	
	public Player getPlayer(String name) {
		return new TestPlayer(name);
	}

	
	public Player getPlayerExact(String name) {
		return new TestPlayer(name);
	}

	
	public PluginCommand getPluginCommand(String name) {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	
	public PluginManager getPluginManager() {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	
	public int getPort() {
		return 25565;
	}

	
	public BukkitScheduler getScheduler() {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	
	public String getServerId() {
		return "test";
	}

	
	public String getServerName() {
		return "Test Server";
	}

	
	public ServicesManager getServicesManager() {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	
	public int getSpawnRadius() {
		return 16;
	}

	
	public String getUpdateFolder() {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	
	public File getUpdateFolderFile() {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	
	public String getVersion() {
		return "1.0a";
	}

	
	public int getViewDistance() {
		return 10;
	}

	
	public Set<OfflinePlayer> getWhitelistedPlayers() {
		return new HashSet<OfflinePlayer>();
	}

	
	public World getWorld(String name) {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	
	public World getWorld(UUID uid) {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	
	public File getWorldContainer() {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	
	public List<World> getWorlds() {
		return new ArrayList<World>();
	}

	
	public boolean hasWhitelist() {
		return false;
	}

	/**
	 * This test stub returns a list of size one containing a new
	 * TestPlayer instance with the given name.
	 */
	
	public List<Player> matchPlayer(String name) {
		List<Player> matched = new ArrayList<Player>();
		matched.add(new TestPlayer(name));
		return matched;
	}

	
	public void reload() {}

	
	public void reloadWhitelist() {}

	
	public void savePlayers() {}

	
	public void setDefaultGameMode(GameMode mode) {}

	
	public void setSpawnRadius(int value) {}

	
	public void setWhitelist(boolean value) {}

	
	public void shutdown() {}

	
	public void unbanIP(String address) {}

	
	public boolean unloadWorld(String name, boolean save) {
		return false;
	}

	
	public boolean unloadWorld(World world, boolean save) {
		return false;
	}

	
	public boolean useExactLoginLocation() {
		return false;
	}

	
	public Set<String> getListeningPluginChannels() {
		return new HashSet<String>();
	}

	
	public void sendPluginMessage(Plugin arg0, String arg1, byte[] arg2) {}

	
	public void clearRecipes() {
		// TODO Auto-generated method stub
		
	}

	
	public Inventory createInventory(InventoryHolder arg0, InventoryType arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Inventory createInventory(InventoryHolder arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public Inventory createInventory(InventoryHolder arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public HelpMap getHelpMap() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public List<Recipe> getRecipesFor(ItemStack arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	
	public int getTicksPerAnimalSpawns() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int getTicksPerMonsterSpawns() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public Iterator<Recipe> recipeIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public void resetRecipes() {
		// TODO Auto-generated method stub
		
	}

	
	public boolean getGenerateStructures() {
		// TODO Auto-generated method stub
		return false;
	}

	
	public String getWorldType() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public long getConnectionThrottle() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	public int getAnimalSpawnLimit() {
		// TODO Auto-generated method stub
		return 20;
	}

	
	public int getMonsterSpawnLimit() {
		// TODO Auto-generated method stub
		return 200;
	}

	
	public int getWaterAnimalSpawnLimit() {
		// TODO Auto-generated method stub
		return 20;
	}
	
	
	public String getMotd() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public WarningState getWarningState() {
		// TODO Auto-generated method stub
		return null;
	}

	
	public boolean isPrimaryThread() {
		// TODO Auto-generated method stub
		return false;
	}

}
