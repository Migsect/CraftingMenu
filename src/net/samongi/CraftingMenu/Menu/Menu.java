package net.samongi.CraftingMenu.Menu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

import net.samongi.CraftingMenu.CraftingMenu;
import net.samongi.CraftingMenu.Player.PlayerManager;
import net.samongi.CraftingMenu.Player.PlayerProfile;
import net.samongi.CraftingMenu.Recipe.Recipe;
import net.samongi.CraftingMenu.Recipe.RecipeManager;

/** Menus consist of a list of recipes to display to a player
 *  However the menu also handls the hiding of recipes.
 *  Menus will generate the inventory gui neccessary for the menu and then
 *  display it to the player.
 *  
 *  Menus can have multiple pages to list recipes as well as have 
 *  submenus specified within them.
 */
public class Menu
{
  static private void log(String message){CraftingMenu.log("[Menu] " + message);}
  static private void logDebug(String message){CraftingMenu.debugLog("[Menu] " + message);}
  
  private final MenuManager manager;
  private final String menu_name;
  private final Map<Integer, String> sub_menus = new HashMap<>();
  private final Set<String> recipes = new HashSet<>();
  
  public Menu(MenuManager manager, ConfigurationSection section)
  {
    this.manager = manager;
    
    Menu.logDebug("Parsing Menu with path: " + section.getCurrentPath());
    this.menu_name = section.getString("name");
    if(menu_name == null)
    {
      Menu.log("Menu Name was not set for: " + section.getCurrentPath());
      Menu.log("  This menu will not be registered until it is given a name.");
    }
    Menu.logDebug("Found menu name to be: " + this.menu_name);
    
    List<String> recipes = section.getStringList("recipes");
    if(recipes != null) this.recipes.addAll(recipes);
    Menu.logDebug("Found recipes to be: ");
    if(CraftingMenu.debug()) for(String s : recipes) Menu.logDebug(" - " + s);
    
    ConfigurationSection menu_section = section.getConfigurationSection("sub-menus");
    if(menu_section != null)
    {
      // Now we get all the keys in this section.
      Set<String> keys = menu_section.getKeys(false);
      for(String k : keys)
      {
        int i = -1;
        try{Integer.parseInt(k);}catch(NumberFormatException e){;}
        if(i < 0) continue;
        
      }
    }
    
    
  }
  
  public String getName(){return this.getName();}
  
  /**Returns the number of recipes that will be shown to the
   *   specified player. Even if the recipe is not known, some recipes will be shown as they are not hidden.
   * @param player The UUID of the player to check with.
   * @return The number of recipes the player will be shown in this menu.  -1 if the UUID has no profile.
   */
  public int getShownRecipes(UUID player)
  {
    int count = 0;
    PlayerProfile profile = PlayerManager.getManager().getProfile(player);
    if(profile == null) return -1;
    for(String s : recipes)
    {
      Recipe r = RecipeManager.getManager().getRecipe(s);
      if(r == null) continue;
      if(profile.hasRecipe(r) || !r.isHidden()) count++;
    }
    return count;
  }
  /**This will go through all submenus to see if they have recipes to show to the player
   *   The number of menus shown to the player will always be 1 as it includes the current menu.
   * @param player The player to check against
   * @return The number of all sub-menus that player will be able to reach.
   */
  public int getShownMenus(UUID player)
  {
    int count = 0;
    PlayerProfile profile = PlayerManager.getManager().getProfile(player);
    if(profile == null) return -1;
    for(String s : sub_menus.values())
    {
      Menu m = this.manager.g
      count += m.getShownMenus(player); 
    }
    if(this.getShownRecipes(player) > 0) count++;
    return count;
  }
}
