package net.samongi.CraftingMenu.Recipe;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.samongi.CraftingMenu.CraftingMenu;

public final class RecipeManager
{
  static private RecipeManager manager;
  public static RecipeManager getManager(){return RecipeManager.manager;}
  
  static private void log(String message){CraftingMenu.log("[RecipeManager] " + message);}
  static private void logDebug(String message){CraftingMenu.debugLog("[RecipeManager] " + message);} 
  
  // Stores all recipes by their name.  Recipes with the same name
  //   If a recipe already exists, it will not be overwritten. An error
  //   message will be spewed out.
  private final Map<String, Recipe> recipes = new HashMap<>();
  // Stores all the recipes under their tags. Tags can subcategorize recipes
  //   which will be useful for having professions and what not.
  private final Map<String, Set<Recipe>> recipe_tags = new HashMap<>();
  
  public RecipeManager()
  {
    RecipeManager.manager = this;
  }
  
  /**Adds a recipe to the recipe manager. If the recipe already exists, this will
   *   return false and an error log message will be shown in console. This will
   *   not overwrite an existing recipe.
   * @param recipe The recipe to be added
   * @return True if the recipe was added, otherwise false.
   */
  public boolean addRecipe(Recipe recipe)
  {
    if(!this.recipes.containsKey(recipe.getName()))
    {
      RecipeManager.log("Error registering new recipe:");
      RecipeManager.log("  Recipe with name '" + recipe.getName() + "' already exists.");
      RecipeManager.log("  This means there is a recipe using the same name as this one.");
      return false;
    }
    RecipeManager.logDebug("Registered recipe with name '" + recipe.getName() + "'");
    this.recipes.put(recipe.getName(), recipe);
    
    Set<String> tags = recipe.getSortingTags();
    for(String t : tags)
    {
      if(!recipe_tags.containsKey(t)) recipe_tags.put(t, new HashSet<Recipe>());
      recipe_tags.get(t).add(recipe);
    }
    return true;
  }
  /**Checks to see if the recipe exists.
   * @param recipe The recipe to check
   * @return True if the recipe exists, otherwise false.
   */
  public boolean containsRecipe(String recipe){return this.recipes.containsKey(recipe);}
  /**Checks to see if the recipe exists.
   * @param recipe The recipe to check
   * @return True if the recipe exists, otherwise false.
   */
  public boolean containsRecipe(Recipe recipe){return this.containsRecipe(recipe.getName());}
  
  /**Returns the recipe by name
   * @param name The name of the recipe
   * @return The recipe object.
   */
  public Recipe getRecipe(String name){return this.recipes.get(name);}
  /**Returns all registered recipes.
   * @return All the currently registered recipes
   */
  public Set<Recipe> getRecipes(){return new HashSet<Recipe>(recipes.values());}
  /**Gets recipes by tag.
   * @param tag The tag to find recipes by
   * @return A set of recipes, otherwise it will return null.
   */
  public Set<Recipe> getRecipes(String tag){return this.recipe_tags.get(tag);}
  
}
