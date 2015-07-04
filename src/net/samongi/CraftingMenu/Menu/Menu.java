package net.samongi.CraftingMenu.Menu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import net.samongi.CraftingMenu.CraftingMenu;
import net.samongi.CraftingMenu.Player.PlayerManager;
import net.samongi.CraftingMenu.Player.PlayerProfile;
import net.samongi.CraftingMenu.Recipe.Recipe;
import net.samongi.CraftingMenu.Recipe.RecipeManager;
import net.samongi.SamongiLib.Menu.InventoryMenu;

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
  static private void debugLog(String message){CraftingMenu.debugLog("[Menu] " + message);}
  
  private final MenuManager manager;
  private final String menu_name;
  private final Map<Integer, String> sub_menus = new HashMap<>();
  private final Set<String> recipes = new HashSet<>();
  
  private final String display_material;
  
  // Filled with a material.
  private final Map<String, Set<Integer>> filled_slots = new HashMap<>();
  
  private final String click_material;
  private final String click_material_type;
  
  private final String block_material;
  private final String block_material_type;
  
  private final int rows = 6;
  
  public Menu(MenuManager manager, ConfigurationSection section)
  {
    this.manager = manager;
    
    Menu.debugLog("Parsing Menu with path: " + section.getCurrentPath());
    // Getting the menu name
    this.menu_name = section.getString("name");
    if(menu_name == null)
    {
      Menu.log("Menu Name was not set for: " + section.getCurrentPath());
      Menu.log("  This menu will not be registered until it is given a name.");
    }
    Menu.debugLog("Found menu name to be: " + this.menu_name);
    
    // Getting all the recipes in this menu
    List<String> recipes = section.getStringList("recipes");
    if(recipes != null) this.recipes.addAll(recipes);
    Menu.debugLog("Found recipes to be: ");
    if(CraftingMenu.debug()) for(String s : recipes) Menu.debugLog(" - " + s);
    
    // Getting all the submenus
    ConfigurationSection sub_menu_section = section.getConfigurationSection("sub-menus");
    if(sub_menu_section != null)
    {
      // Now we get all the keys in this section.
      Set<String> keys = sub_menu_section.getKeys(false);
      for(String k : keys)
      {
        int i = -1;
        try{Integer.parseInt(k);}catch(NumberFormatException e){;}
        if(i < 0) continue;
        sub_menus.put(i, sub_menu_section.getString(k));
      }
    }
    
    ConfigurationSection filled_slots = section.getConfigurationSection("filled-slots");
    if(filled_slots != null)
    {
      Set<String> keys = filled_slots.getKeys(false);
      for(String k : keys)
      {
        List<Integer> slots = filled_slots.getIntegerList(k);
        if(slots == null) continue;
        Set<Integer> slots_set = new HashSet<>(slots);
        this.filled_slots.put(k, slots_set);
      }
    }
    
    // Getting where the item can be used.
    this.click_material = section.getString("click-material", null);
    this.click_material_type = section.getString("click-material-type", null);
    this.block_material = section.getString("block-material", null);
    this.block_material_type = section.getString("block-material-type", null);

    Menu.debugLog("Found Click Material to be: " + this.click_material);
    Menu.debugLog("Found Click Material Type to be: " + this.click_material_type);
    Menu.debugLog("Found Block Material to be: " + this.block_material);
    Menu.debugLog("Found Block Material Type to be: " + this.block_material_type);
    
  }
  
  public String getName(){return this.menu_name;}
  
  public boolean hasClickMaterial(){return this.click_material != null;}
  public boolean hasClickMaterialType(){return this.click_material_type != null;}
  public String getClickMaterial(){return this.click_material;}
  public String getClickMaterialType(){return this.click_material_type;}
  
  public boolean hasBlockMaterial(){return this.block_material != null;}
  public boolean hasBlockMaterialType(){return this.click_material_type != null;}
  public String getClickBlock(){return this.block_material;}
  public String getClickBlockType(){return this.block_material_type;}
  
  public InventoryMenu getMenu(Player player)
  {
    InventoryMenu menu = new InventoryMenu(player, this.rows, this.getName());
    
    Set<Recipe> recipes = this.getShownRecipes(player.getUniqueId());
    Set<String> menus = this.getShownMenus(player.getUniqueId());
    Set<Integer> filled_slots = this.getAllFilledSlots();
    
    return menu;
  }
  
  public Set<Recipe> getShownRecipes(UUID player)
  {
    Set<Recipe> seen_recipes = new HashSet<>();
    PlayerProfile profile = PlayerManager.getManager().getProfile(player);
    if(profile == null) return null;
    for(String s : recipes)
    {
      Recipe r = RecipeManager.getManager().getRecipe(s);
      if(r == null) continue;
      if(profile.hasRecipe(r) || !r.isHidden()) seen_recipes.add(r);
    }
    return seen_recipes;
  }
  
  /**Returns the number of recipes that will be shown to the
   *   specified player. Even if the recipe is not known, some recipes will be shown as they are not hidden.
   * @param player The UUID of the player to check with.
   * @return The number of recipes the player will be shown in this menu.  -1 if the UUID has no profile.
   */
  public int getShownRecipesNumber(UUID player)
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
  
  public Set<String> getShownMenus(UUID player)
  {
    Set<String> seen_recipes = new HashSet<>();
    PlayerProfile profile = PlayerManager.getManager().getProfile(player);
    if(profile == null) return null;
    for(String s : recipes)
    {
      Recipe r = RecipeManager.getManager().getRecipe(s);
      if(r == null) continue;
      if(profile.hasRecipe(r) || !r.isHidden()) seen_recipes.add(s);
    }
    return seen_recipes;
  }
  /**This will go through all submenus to see if they have recipes to show to the player
   *   The number of menus shown to the player will always be 1 as it includes the current menu.
   * @param player The player to check against
   * @return The number of all sub-menus that player will be able to reach.
   */
  public int getShownMenusNumber(UUID player)
  {
    int count = 0;
    PlayerProfile profile = PlayerManager.getManager().getProfile(player);
    if(profile == null) return -1;
    for(String s : sub_menus.values())
    {
      Menu m = this.manager.getMenu(s);
      if(m == null) continue;
      count += m.getShownMenusNumber(player); 
    }
    if(this.getShownMenusNumber(player) > 0) count++;
    return count;
  }
  
  public Set<Integer> getAllFilledSlots()
  {
    Set<Integer> slots = new HashSet<>();
    for(String k : this.filled_slots.keySet())
    {
      slots.addAll(this.filled_slots.get(k));
    }
    return slots;
  }
}
