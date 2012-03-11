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
public class TestServer implements Server {

	@Override
	public boolean addRecipe(Recipe recipe) {
		return true;
	}

	@Override
	public void banIP(String address) {}

	@Override
	public int broadcast(String message, String permission) {
		return 0;
	}

	@Override
	public int broadcastMessage(String message) {
		return 0;
	}

	@Override
	public void configureDbConfig(ServerConfig config) {}

	@Override
	public MapView createMap(World world) {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	@Override
	public World createWorld(WorldCreator creator) {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	@Override
	public boolean dispatchCommand(CommandSender sender, String commandLine)
			throws CommandException {
		return false;
	}

	@Override
	public boolean getAllowEnd() {
		return true;
	}

	@Override
	public boolean getAllowFlight() {
		return false;
	}

	@Override
	public boolean getAllowNether() {
		return true;
	}

	@Override
	public Set<OfflinePlayer> getBannedPlayers() {
		return new HashSet<OfflinePlayer>();
	}

	@Override
	public String getBukkitVersion() {
		return "1.1 R4";
	}

	@Override
	public Map<String, String[]> getCommandAliases() {
		return new LinkedHashMap<String, String[]>();
	}

	@Override
	public ConsoleCommandSender getConsoleSender() {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	@Override
	public GameMode getDefaultGameMode() {
		return org.bukkit.GameMode.SURVIVAL;
	}

	@Override
	public Set<String> getIPBans() {
		return new HashSet<String>();
	}

	@Override
	public String getIp() {
		return "127.0.0.1";
	}

	@Override
	public Logger getLogger() {
		return Logger.getLogger("TestServer");
	}

	@Override
	public MapView getMap(short id) {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	@Override
	public int getMaxPlayers() {
		return 20;
	}

	@Override
	public Messenger getMessenger() {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	@Override
	public String getName() {
		return "Bukkit Test Stub";
	}

	@Override
	public OfflinePlayer getOfflinePlayer(String name) {
		return new TestPlayer(name);
	}

	@Override
	public OfflinePlayer[] getOfflinePlayers() {
		return new OfflinePlayer[0];
	}

	@Override
	public boolean getOnlineMode() {
		return true;
	}

	@Override
	public Player[] getOnlinePlayers() {
		return new Player[0];
	}

	@Override
	public Set<OfflinePlayer> getOperators() {
		return new HashSet<OfflinePlayer>();
	}

	@Override
	public Player getPlayer(String name) {
		return new TestPlayer(name);
	}

	@Override
	public Player getPlayerExact(String name) {
		return new TestPlayer(name);
	}

	@Override
	public PluginCommand getPluginCommand(String name) {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	@Override
	public PluginManager getPluginManager() {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	@Override
	public int getPort() {
		return 25565;
	}

	@Override
	public BukkitScheduler getScheduler() {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	@Override
	public String getServerId() {
		return "test";
	}

	@Override
	public String getServerName() {
		return "Test Server";
	}

	@Override
	public ServicesManager getServicesManager() {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	@Override
	public int getSpawnRadius() {
		return 16;
	}

	@Override
	public String getUpdateFolder() {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	@Override
	public File getUpdateFolderFile() {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	@Override
	public String getVersion() {
		return "1.0a";
	}

	@Override
	public int getViewDistance() {
		return 10;
	}

	@Override
	public Set<OfflinePlayer> getWhitelistedPlayers() {
		return new HashSet<OfflinePlayer>();
	}

	@Override
	public World getWorld(String name) {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	@Override
	public World getWorld(UUID uid) {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	@Override
	public File getWorldContainer() {
		throw new UnsupportedOperationException("This method is not implemented in this test stub.");
	}

	@Override
	public List<World> getWorlds() {
		return new ArrayList<World>();
	}

	@Override
	public boolean hasWhitelist() {
		return false;
	}

	/**
	 * This test stub returns a list of size one containing a new
	 * TestPlayer instance with the given name.
	 */
	@Override
	public List<Player> matchPlayer(String name) {
		List<Player> matched = new ArrayList<Player>();
		matched.add(new TestPlayer(name));
		return matched;
	}

	@Override
	public void reload() {}

	@Override
	public void reloadWhitelist() {}

	@Override
	public void savePlayers() {}

	@Override
	public void setDefaultGameMode(GameMode mode) {}

	@Override
	public void setSpawnRadius(int value) {}

	@Override
	public void setWhitelist(boolean value) {}

	@Override
	public void shutdown() {}

	@Override
	public void unbanIP(String address) {}

	@Override
	public boolean unloadWorld(String name, boolean save) {
		return false;
	}

	@Override
	public boolean unloadWorld(World world, boolean save) {
		return false;
	}

	@Override
	public boolean useExactLoginLocation() {
		return false;
	}

	@Override
	public Set<String> getListeningPluginChannels() {
		return new HashSet<String>();
	}

	@Override
	public void sendPluginMessage(Plugin arg0, String arg1, byte[] arg2) {}

	@Override
	public void clearRecipes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Inventory createInventory(InventoryHolder arg0, InventoryType arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Inventory createInventory(InventoryHolder arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Inventory createInventory(InventoryHolder arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HelpMap getHelpMap() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Recipe> getRecipesFor(ItemStack arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTicksPerAnimalSpawns() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTicksPerMonsterSpawns() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Iterator<Recipe> recipeIterator() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetRecipes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getGenerateStructures() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getWorldType() {
		// TODO Auto-generated method stub
		return null;
	}

}
