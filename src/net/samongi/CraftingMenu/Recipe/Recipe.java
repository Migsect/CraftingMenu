package net.samongi.CraftingMenu.Recipe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.samongi.CraftingMenu.CraftingMenu;

import org.bukkit.configuration.ConfigurationSection;

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
  // The ItemStacks required such that recipe can be crafted.
  private final Map<String, Integer> components = new HashMap<>();
  // The ItemStacks that this recipe will produce if crafted.
  private final Map<String, Integer> results = new HashMap<>();
  
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
    
    if(section.getKeys(false).contains("components"))
    {
      ConfigurationSection components = section.getConfigurationSection("components");
      Set<String> item_keys = components.getKeys(false);
      for(String k : item_keys)
      {
        this.components.put(k, components.getInt(k));
      }
    }
    Recipe.debugLog("Found Components Amount: " + this.components.size());
    
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
  
  public String getName(){return this.recipe_name;}
  
  /**Gets the map that contains the material string for all required components
   * This map is a copy of the original components map.
   * 
   * @return A map containing material string and the corresponding amount
   */
  public Map<String, Integer> getComponents(){return new HashMap<>(this.components);}
  
  public Map<String, Integer> getResults(){return new HashMap<>(this.results);}
  
  public boolean isLearned(){return this.learned;}
  
  public boolean isHidden(){return this.hidden;}
  
  public Set<String> getPrerequisites(){return this.prerequisites;}
  
  public Set<String> getLearnPool(){return this.learn_pool;}
  public boolean isInLearnPool(String recipe){return this.learn_pool.contains(recipe);}
  public boolean isInLearnPool(Recipe recipe){return this.isInLearnPool(recipe.getName());}
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
  
  public boolean hasSortingTag(String tag){return this.sorting_tags.contains(tag);}
  public Set<String> getSortingTags(){return this.sorting_tags;}
  
  public boolean hasConflict(String recipe)
  {
    Recipe r = this.manager.getRecipe(recipe);
    if(r == null) return false;
    return this.hasConflict(r);
  }
  public boolean hasConflict(Recipe recipe)
  {
    if(this.conflict_pool.contains(recipe.getName())) return true;
    if(recipe.conflict_pool.contains(this.getName())) return true;
    return false;
  }
  public Set<String> getConflictPool(){return this.conflict_pool;}
}
