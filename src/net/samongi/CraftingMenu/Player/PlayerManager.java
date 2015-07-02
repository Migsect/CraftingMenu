package net.samongi.CraftingMenu.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import net.samongi.CraftingMenu.CraftingMenu;

public class PlayerManager
{
  static private PlayerManager manager;
  public static PlayerManager getManager(){return PlayerManager.manager;}
  
  @SuppressWarnings("unused")
  static private void log(String message){CraftingMenu.log("[PlayerManager] " + message);}
  static private void logDebug(String message){CraftingMenu.debugLog("[PlayerManager] " + message);}
  
  private final File save_folder;
  private final Map<UUID, PlayerProfile> loaded_profiles = new HashMap<>();
  
  public PlayerManager(File save_location)
  {
    this.save_folder = save_location;
  }
  
  public PlayerProfile getProfile(UUID player)
  {
    if(this.loaded_profiles.containsKey(player)) return this.loaded_profiles.get(player);
    String[] file = save_folder.list();
    // We will load the profile from file if it exists, otherwise we end with null;
    for(String f : file) if(f.startsWith(player.toString())) return PlayerProfile.loadProfile(new File(save_folder, f),  player);
    return null; // We return null and not a new profile because there is a good chance the player never connected
  }
  
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    UUID player = event.getPlayer().getUniqueId();
    this.loadProfile(player);
  }
  
  public void onPlayerQuit(PlayerQuitEvent event)
  {
    UUID player = event.getPlayer().getUniqueId();
    this.unloadProfile(player);
  }
  
  public boolean isLoadedProfile(UUID player){return this.loaded_profiles.containsKey(player);}
  public void loadProfile(UUID player)
  {
    PlayerManager.logDebug("Loading Profile for UUID: " + player.toString());
    if(this.isLoadedProfile(player))
    {
      PlayerManager.logDebug("  Profile already loaded, it needs to be unloaded before it can be loaded.");
      return;
    }
    PlayerProfile profile = PlayerProfile.loadProfile(save_folder, player);
    this.loaded_profiles.put(player, profile);
    
  }
  public void unloadProfile(UUID player)
  {
    PlayerManager.logDebug("Unloading Profile for UUID: " + player.toString());
    if(!this.isLoadedProfile(player))
    {
      PlayerManager.logDebug("  Profile already not loaded, it needs to be loaded before it can be unloaded.");
      return;
    }
    this.loaded_profiles.get(player).saveProfile(this.save_folder);
    this.loaded_profiles.remove(player);
  }
}
