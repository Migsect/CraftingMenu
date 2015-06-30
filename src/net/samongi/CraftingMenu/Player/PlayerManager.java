package net.samongi.CraftingMenu.Player;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.samongi.CraftingMenu.CraftingMenu;

public class PlayerManager
{
  static private PlayerManager manager;
  public static PlayerManager getManager(){return PlayerManager.manager;}
  
  static private void log(String message){CraftingMenu.log("[PlayerManager] " + message);}
  static private void logDebug(String message){CraftingMenu.debugLog("[PlayerManager] " + message);}
  
  private final File save_location;
  private final Map<UUID, PlayerProfile> loaded_profiles = new HashMap<>();
  
  public PlayerManager(File save_location)
  {
    this.save_location = save_location;
  }
  
  public PlayerProfile getProfile(UUID player)
  {
    if(this.loaded_profiles.containsKey(player)) return this.loaded_profiles.get(player);
    String[] file = save_location.list();
    // We will load the profile from file if it exists, otherwise we end with null;
    for(String f : file) if(f.startsWith(player.toString())) return PlayerProfile.loadProfile(new File(save_location, f),  player);
    return null; // We return null and not a new profile because there is a good chance the player never connected
  }
}
