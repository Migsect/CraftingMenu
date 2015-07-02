package net.samongi.CraftingMenu.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import net.samongi.CraftingMenu.CraftingMenu;
import net.samongi.CraftingMenu.Recipe.Recipe;
import net.samongi.CraftingMenu.Recipe.RecipeManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerProfile implements Serializable
{
  @SuppressWarnings("unused")
  static private void log(String message){CraftingMenu.log("[PlayerProfile] " + message);}
  static private void debugLog(String message){CraftingMenu.debugLog("[PlayerProfile] " + message);}
  
  /**Will save the profile to file within the directory.
   * @param dir The directory where the file is located.
   * @param profile The profile to save.
   * @return true if it was successful, otherwise false
   */
  static public boolean saveProfile(File dir, PlayerProfile profile)
  {
    if(!dir.isDirectory()) return false;
    File file = new File(dir, profile.getPlayerUUID().toString() + ".dat");
    try
    {
      if(!file.exists())
      {
        PlayerProfile.debugLog("File does not yet exist, making file: " + file.getAbsolutePath());
        file.createNewFile();
      }
      FileOutputStream file_out = new FileOutputStream(file);
      ObjectOutputStream obj_out = new ObjectOutputStream(file_out);
      
      obj_out.writeObject(profile);

      obj_out.close();
      file_out.close();
    }
    catch(IOException e)
    {
      e.printStackTrace();
      return false;
    }
    return true;
  }
  /**Will load the profile from file. If it does not exist, it will create a new profile.
   * @param dir The directory to look for the profile in.
   * @param player_uuid The uuid of the player to look for.
   * @return The Profile, it will be new if one was not found or something when wrong.
   */
  static public PlayerProfile loadProfile(File dir, UUID player_uuid)
  {
    File file = new File(dir, player_uuid.toString() + ".dat");
    if(!file.exists() || file.isDirectory()) 
    {
      PlayerProfile.debugLog("Profile not found or is directory for file: " + file.getAbsolutePath());
      PlayerProfile.debugLog("  Returning new profile object for player: '" + player_uuid + "'");
      return new PlayerProfile(player_uuid);
    }
    PlayerProfile ret = null;
    try
    {
      FileInputStream file_in = new FileInputStream(file);
      ObjectInputStream obj_in = new ObjectInputStream(file_in);
      
      Object o = obj_in.readObject();
      ret = (PlayerProfile)o;
      
      obj_in.close();
      file_in.close();
    }
    catch(IOException e)
    {
      e.printStackTrace();
      return new PlayerProfile(player_uuid);
    }
    catch (ClassNotFoundException e)
    {
      e.printStackTrace();
      return new PlayerProfile(player_uuid);
    }
    return ret;
  }
  
  private static final long serialVersionUID = 8771725335415513619L;
  private final String player_name;
  private final UUID uuid;
  private final Set<String> learned_recipes = new HashSet<String>();
  
  public PlayerProfile(UUID player)
  {
    this.player_name = Bukkit.getPlayer(player).getName();
    this.uuid = player;
  }
  
  public Player getPlayer(){return Bukkit.getPlayer(uuid);}
  public UUID getPlayerUUID(){return this.uuid;}
  public String getPlayerName(){return this.player_name;}
  
  public boolean hasRecipe(Recipe recipe){return this.hasRecipe(recipe.getName());}
  public boolean hasRecipe(String string){return this.learned_recipes.contains(string);}
  public void removeRecipe(Recipe recipe){this.removeRecipe(recipe.getName());}
  public void removeRecipe(String string){this.learned_recipes.remove(string);}
  public int addRecipe(Recipe recipe)
  {
    // Getting all the recipes in its learn pool that exist.
    Set<Recipe> add_recipes = recipe.getTrueLearnPool();
    // Adding all the new recipes.
    add_recipes.add(recipe);
    for(Recipe r : add_recipes) this.learned_recipes.add(r.getName());
    return add_recipes.size();
  }
  public void updateRecipes(RecipeManager manager)
  {
    Set<String> remove_recipes = new HashSet<>();
    for(String s : learned_recipes)
    {
      Recipe r = manager.getRecipe(s);
      // Setting up for removal of all unneeded recipes.
      if(r == null) remove_recipes.add(s);
    }
    learned_recipes.remove(remove_recipes);
    // Adding any missing pool recipes.
    for(String s : learned_recipes)
    {
      Recipe r = manager.getRecipe(s);
      Set<Recipe> add_recipes = r.getTrueLearnPool();
      for(Recipe ar : add_recipes) learned_recipes.add(ar.getName());
    }
  }
  public boolean saveProfile(File dir){return PlayerProfile.saveProfile(dir, this);}
  
}
