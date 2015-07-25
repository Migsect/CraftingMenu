package net.samongi.CraftingMenu.Recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.samongi.CraftingMenu.CraftingMenu;
import net.samongi.CraftingMenu.Menu.Menu;
import net.samongi.CraftingMenu.Player.PlayerManager;
import net.samongi.CraftingMenu.Player.PlayerProfile;
import net.samongi.CraftingMenu.Recipe.Component.Component;
import net.samongi.CraftingMenu.Recipe.Result.Result;
import net.samongi.SamongiLib.Items.ItemUtil;
import net.samongi.SamongiLib.Menu.InventoryMenu;
import net.samongi.SamongiLib.Menu.ButtomAction.ButtonOpenMenu;
import net.samongi.SamongiLib.Utilities.TextUtil;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**A recipe is something 
 * 
 * @author Alex
 *
 */
public class Recipe
{
  static private void log(String message){CraftingMenu.log("[Recipe] " + message);}
  static private void debugLog(String message){CraftingMenu.debugLog("[Recipe] " + message);}
  
  // The recipe manager holds all the recipe objects in the server.
  private final RecipeManager manager;
  
  // The name of the recipe, Generally this is ignored if the results count == 1
  private final String recipe_name;
  private final String display_material;
  // The components required such that recipe can be crafted.
  private final List<Component> components = new ArrayList<>();
  // The ItemStacks that this recipe will produce if crafted.
  private final List<Result> results = new ArrayList<>();
  // The components required to learn this recipe
  private final List<Component> learn_components = new ArrayList<>();
  
  // If a player does not have this permission, they will not be able to craft
  //   even if the player has learned the recipe. Generally this also implies
  //   that the player will be blocked from learning the recipe.
  private final String permission;
  // If a recipe is learned, it will always be usage by a player
  private final boolean learned;
  // If a recipe is hidden, it will not be shown unless it is known
  private final boolean hidden; 
  
  // The recipes the player requires to learn this recipe but not use this recipe
  //   These prerequisites need to exist to have an effect.
  private final Set<String> prerequisites = new HashSet<>();
  // This is a list of recipes that will be learned if this recipe is learned.
  //   This will only cause a player to learn the pooled recipe if it lists this
  //   recipe within its learn pool, otherwise this will do nothing.
  private final Set<String> learn_pool = new HashSet<>();
  // This is a list of recipes that this recipe has a comflict with.  If a recipe
  //   is part of a learn_pool and the conflict pool, then it is removed from the
  //   learn pool.
  //   While the learn_pool requires that the other recipe has this recipe in its pool,
  //   the conflict_pool will work if there is one or more recipes with the conflict. 
  private final Set<String> conflict_pool = new HashSet<>();
  // Sorting Tags are used to categorize recipes such as for recipe kits. They offer
  //   also another means of learning recipes. If a recipe is not found by name, then
  //   tags can be used to find a recipe.  This allows learning of one recipe from an
  //   item or command to learn a good bunch of recipes.
  //   Sorting tags can also be used within menus to preset which recipes will be added
  //   instead of simply specifying each recipe in the menu.
  private final Set<String> sorting_tags = new HashSet<>();
  
  public Recipe(RecipeManager manager, ConfigurationSection section)
  {
    this.manager = manager;
    
    Recipe.debugLog("Parsing Recipe with path: " + section.getCurrentPath());
    this.recipe_name = section.getString("name");
    if(recipe_name == null)
    {
      Recipe.log("Recipe Name was not set for: " + section.getCurrentPath());
      Recipe.log("  This recipe will not be registered until it is given a name.");
    }
    Recipe.debugLog("Found recipe name to be: " + this.recipe_name );
    
    this.display_material = section.getString("display","PAPER");
    Recipe.debugLog("Found recipe display mat to be: " + this.display_material );
    
    // TODO Components being added
    Recipe.debugLog("Found Components Amount: " + this.components.size());
    
    // TODO Results being added
    Recipe.debugLog("Found Results Amount: " + this.results.size());
    
    // TODO Learn Components being added
    Recipe.debugLog("Found Learn Components Amount: " + this.learn_components.size());
    
    this.permission = section.getString("permission", "");
    Recipe.debugLog("Found recipe permission to be: " + this.permission );
    
    this.learned = section.getBoolean("learned", false);
    Recipe.debugLog("Found recipe learned to be: " + this.learned );
    
    this.hidden = section.getBoolean("hidden", false);
    Recipe.debugLog("Found recipe hidden to be: " + this.hidden );
    
    List<String> prerequisites = section.getStringList("prerequisites");
    if(prerequisites != null) this.prerequisites.addAll(prerequisites);
    Recipe.debugLog("Found recipe prerequisites to be: ");
    if(CraftingMenu.debug()) for(String s : prerequisites) Recipe.debugLog(" - " + s);
    
    List<String> learn_pool = section.getStringList("learn-pool");
    if(learn_pool != null) this.learn_pool.addAll(learn_pool);
    Recipe.debugLog("Found recipe learn pool to be: ");
    if(CraftingMenu.debug()) for(String s : learn_pool) Recipe.debugLog(" - " + s);
    
    List<String> conflict_pool = section.getStringList("conflict-pool");
    if(conflict_pool != null) this.learn_pool.addAll(conflict_pool);
    Recipe.debugLog("Found recipe conflict pool to be: ");
    if(CraftingMenu.debug()) for(String s : conflict_pool) Recipe.debugLog(" - " + s);
    
    List<String> sorting_tags = section.getStringList("sorting-tags");
    if(sorting_tags != null) this.sorting_tags.addAll(sorting_tags);
    Recipe.debugLog("Found recipe sorting tags to be: ");
    if(CraftingMenu.debug()) for(String s : sorting_tags) Recipe.debugLog(" - " + s);
    
    // Cleaning up the learn_pool of conflicts.
    this.learn_pool.removeAll(conflict_pool);
  }
  
  /**Returns the name of the recipe
   * 
   * @return The name of the recipe.
   */
  public String getName(){return this.recipe_name;}
  
  /**Returns an item depicting this recipe for the player.
   * This will display status on the player's required items.
   * 
   * @param player The player to display this recipe to.
   * @return The ItemStack representing the display.
   */
  public ItemStack getMenuItem(Player player)
  {
    ItemStack menu_item = ItemUtil.getItemStack(this.display_material);
    String display_name = ChatColor.WHITE + TextUtil.formatString(recipe_name);
    ItemMeta im = menu_item.getItemMeta();
    im.setDisplayName(display_name);
    List<String> lore = new ArrayList<>();
    if(!this.hasRecipe(player))
    {
      lore.add(ChatColor.YELLOW + "Required to Learn:");
      for(Component c : this.learn_components)
      {
        String line = ChatColor.WHITE + "- ";
        if(c.hasComponent(player)) line += ChatColor.GREEN;
        else line += ChatColor.RED;
        line += c.getDisplay();
        lore.add(line);
      }
    }
    lore.add(ChatColor.YELLOW + "Components:");
    for(Component c : this.components)
    {
      String line = ChatColor.WHITE + "- ";
      if(c.hasComponent(player)) line += ChatColor.GREEN;
      else line += ChatColor.RED;
      line += c.getDisplay();
      lore.add(line);
    }
    lore.add(ChatColor.YELLOW + "Results:");
    for(Result r : this.results)
    {
      lore.add(ChatColor.WHITE + "- " + r.getDisplay());
    }
    lore.add(ChatColor.AQUA + "Right-Click for Component Info");
    lore.add(ChatColor.AQUA + "Shift Right-Click for Result Info");
    im.setLore(lore);
    menu_item.setItemMeta(im);
    
    return menu_item;
  }
  
  /**Gets a list of all the components that this recipe requires to
   * craft the results.
   * 
   * @return A list of components
   */
  public List<Component> getComponents(){return new ArrayList<>(this.components);}
  
  /**Gets a list of all the results this recipe will create.
   * 
   * @return A list of results
   */
  public List<Result> getResults(){return new ArrayList<>(this.results);}
  
  /**Returns true if this recipe is learned by default.
   * The recipe will not be learned if it has a prereq, but once
   * the prereq is met, then it will be learned. This can be used as an
   * alternative means for a learning pool.
   * 
   * @return True if it is learned by default.
   */
  public boolean isLearned(){return this.learned;}
  
  /**Returns true if this recipe is hidden before learned.
   * 
   * @return
   */
  public boolean isHidden(){return this.hidden;}
  
  /**Returns a set of prereqs for this recipe to be used or learned.
   * 
   * @return The set of prereq recipes
   */
  public Set<String> getPrerequisites(){return this.prerequisites;}
  
  /**Returns the raw, unabridged learning pool for this recipe. This doesn't
   * remove any recipes that do not have this recipe in their learning pool as well.
   * 
   * @return The raw learning pool.
   */
  public Set<String> getLearnPool(){return this.learn_pool;}
  /**Checks to see if this recipe is in the recipe's learning pool.
   * 
   * @param recipe The other recipe
   * @return True if it is.
   */
  public boolean isInLearnPool(String recipe){return this.learn_pool.contains(recipe);}
  /**Checks to see if this recipe is in the recipe's learning pool.
   * 
   * @param recipe The other recipe
   * @return True if it is.
   */
  public boolean isInLearnPool(Recipe recipe){return this.isInLearnPool(recipe.getName());}
  /**Checks the set of recipes that should be learned with this recipe in tandem.
   * 
   * @return The set of recipes defining this recipes 'learning pool'
   */
  public Set<Recipe> getTrueLearnPool()
  {
    Set<Recipe> pool = new HashSet<>();
    for(String n : this.learn_pool)
    {
      if(!this.manager.containsRecipe(n)) continue;
      if(!this.isInLearnPool(n)) continue;
      Recipe r = this.manager.getRecipe(n);
      if(!r.isInLearnPool(this)) continue;
      pool.add(r);
    }
    return pool;
  }  
  /**Checks to see if this recipe has the sorting tag.
   * 
   * @param tag The tag to check for.
   * @return True if this recipe has the sorting tag.
   */
  public boolean hasSortingTag(String tag){return this.sorting_tags.contains(tag);}
  /**Gets the sorting tags that this recipe is sorted under.
   * 
   * @return A set of all the sorting tags
   */
  public Set<String> getSortingTags(){return this.sorting_tags;}
  
  /**Checks to see if this recipe has a conflict with this recipe
   * 
   * @param recipe The recipe to check against
   * @return True if there is a conflict
   */
  public boolean hasConflict(String recipe)
  {
    Recipe r = this.manager.getRecipe(recipe);
    if(r == null) return false;
    return this.hasConflict(r);
  }
  /**Checks to see if this recipe has a conflict with this recipe.
   * 
   * @param recipe The recipe to check against
   * @return True if there is a conflict.
   */
  public boolean hasConflict(Recipe recipe)
  {
    if(this.conflict_pool.contains(recipe.getName())) return true;
    if(recipe.conflict_pool.contains(this.getName())) return true;
    return false;
  }
  /**Returns the set of other recipes that this recipe has a conflict with.
   * 
   * @return The set of other recipes that are conflicting
   */
  public Set<String> getConflictPool(){return this.conflict_pool;}
  
  /**Will attempt to craft the recipe for the player. Removing the neccessary items
   * If the player does not have the items, the recipe will fail and return false
   * 
   * @param player The player to craft the recipe for.
   * @return True if it was successful and requirements were met.
   */
  public boolean craftRecipe(Player player)
  {
    if(!this.hasComponents(player)) return false;
    for(Component c : this.components) c.removeComponent(player);
    for(Result r : this.results) r.addResult(player);
    
    return true;
  }
  /**Will learn the recipe for the player. This will remove all the components
   * required to learn the recipe from the player.
   * 
   * @param player The player to learn the recipe
   * @return True if the learning was successul.
   */
  public boolean learnRecipe(Player player)
  {
    if(!this.hasLearnComponents(player)) return false;
    for(Component c : this.components) c.removeComponent(player);
    PlayerProfile profile = PlayerManager.getManager().getProfile(player.getUniqueId());
    profile.addRecipe(this);
    
    return true;
  }
  
  /**Will check to see if the player has the required components for this recipe
   * 
   * @param player The player to check components for.
   * @return True if the player has the required components
   */
  public boolean hasComponents(Player player)
  {
    for(Component c : this.components) if(!c.hasComponent(player)) return false;
    return true;
  }
  /**Will check to see if the player has the required components to learn this recipe.
   * 
   * @param player
   * @return True if the player has the required components
   */
  public boolean hasLearnComponents(Player player)
  {
    for(Component c : this.learn_components) if(!c.hasComponent(player)) return false;
    return true;
  }
  
  /**Will return an inventory menu that displays the results this recipe will produce
   * 
   * @param prev The previous menu that this is being generated from.
   * @return An inventory menu
   */
  public InventoryMenu getResultDetails(Player player, Menu prev)
  {
    int rows = 6;
    InventoryMenu menu = new InventoryMenu(player, rows, this.recipe_name + " Results");
    int menu_slot = 0;
    for(Result r : this.results)
    {
      ItemStack[] menu_items = r.getMenuItems();
      for(ItemStack i : menu_items)
      {
        menu.setItem(menu_slot, i);
        menu_slot++;
      }
    }
    // Creating the return button.
    ItemStack display_return = new ItemStack(Material.PAPER);
    ItemMeta im = display_return.getItemMeta();
    im.setDisplayName(ChatColor.AQUA + "Return to Crafting");
    display_return.setItemMeta(im);
    ButtonOpenMenu menu_button = new ButtonOpenMenu(prev.getInventoryMenu(player), display_return, CraftingMenu.getMainClass());
    menu_button.register(menu, 6 * 9 - 1); // putting it in the bottom right of the menui
    
    // Returning the generated menu.
    return menu;
  }
  /**Will return an inventory menu that displays the components this recipe will require
   * 
   * @param prev The previous menu that this is being generated from.
   * @return An inventory menu
   */
  public InventoryMenu getComponentDetails(Player player, Menu prev)
  {
    int rows = 6;
    InventoryMenu menu = new InventoryMenu(player, rows, this.recipe_name + " Components");
    int menu_slot = 0;
    for(Component c : this.components)
    {
      ItemStack[] menu_items = c.getMenuItems();
      for(ItemStack i : menu_items)
      {
        menu.setItem(menu_slot, i);
        menu_slot++;
      }
    }
    // Creating the return button.
    ItemStack display_return = new ItemStack(Material.PAPER);
    ItemMeta im = display_return.getItemMeta();
    im.setDisplayName(ChatColor.AQUA + "Return to Crafting");
    display_return.setItemMeta(im);
    ButtonOpenMenu menu_button = new ButtonOpenMenu(prev.getInventoryMenu(player), display_return, CraftingMenu.getMainClass());
    menu_button.register(menu, 6 * 9 - 1); // putting it in the bottom right of the menui
    
    // Returning the generated menu.
    
    return menu;
  }
  
  /**Will return true if the player has this recipe
   * 
   * @param player
   * @return True if the player has the recipe
   */
  public boolean hasRecipe(Player player){return this.hasRecipe(player.getUniqueId());}
  /**Will return true if the player has this recipe
   * 
   * @param player
   * @return True if the player has the recipe
   */
  public boolean hasRecipe(UUID player)
  {
    PlayerProfile profile = PlayerManager.getManager().getProfile(player);
    return profile.hasRecipe(this);
  }
}
