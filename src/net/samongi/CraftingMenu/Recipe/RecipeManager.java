package net.samongi.CraftingMenu.Recipe;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import net.samongi.CraftingMenu.CraftingMenu;
import net.samongi.SamongiLib.Configuration.ConfigFile;

public final class RecipeManager
{
  static private RecipeManager manager;
  public static RecipeManager getManager(){return RecipeManager.manager;}
  
  static private void log(String message){CraftingMenu.log("[RecipeManager] " + message);}
  static private void debugLog(String message){CraftingMenu.debugLog("[RecipeManager] " + message);} 
  
  private final File recipe_folder;
  
  // Stores all recipes by their name.  Recipes with the same name
  //   If a recipe already exists, it will not be overwritten. An error
  //   message will be spewed out.
  private final Map<String, Recipe> recipes = new HashMap<>();
  // Stores all the recipes under their tags. Tags can subcategorize recipes
  //   which will be useful for having professions and what not.
  private final Map<String, Set<Recipe>> recipe_tags = new HashMap<>();
  
  public RecipeManager(File recipe_folder)
  {
    RecipeManager.manager = this;
    this.recipe_folder = recipe_folder;
    this.folderParseRecipes(this.recipe_folder);
  }
  
  private void folderParseRecipes(File file){this.folderParseRecipes(file, true);}
  private void folderParseRecipes(File file, boolean recursive)
  {
    RecipeManager.debugLog("Parsing all files in directory: " + file.getAbsolutePath());
    File[] files = file.listFiles();
    for(File f : files)
    {
      if(f.isDirectory() && recursive) this.folderParseRecipes(f);
      else if(f.isFile() && (f.getAbsolutePath().endsWith(".yml") || f.getAbsolutePath().endsWith(".yaml"))) this.parseRecipes(f);
      else RecipeManager.debugLog("  Passed over file: '" + f.getAbsolutePath() + "'");
    }
  }
  private void parseRecipes(File file)
  {
    RecipeManager.debugLog("  Parsing file: " + file.getAbsolutePath());
    ConfigFile config = new ConfigFile(file);
    if(!config.getConfig().getKeys(false).contains("recipes")) 
    {
      RecipeManager.debugLog("  File did not contain any recipes, returning.");
      return;
    }
    ConfigurationSection recipes = config.getConfig().getConfigurationSection("recipes");
    Set<String> recipe_keys = recipes.getKeys(false);
    for(String k : recipe_keys)
    {
      ConfigurationSection m = recipes.getConfigurationSection(k);
      Recipe recipe = new Recipe(this, m);
      this.addRecipe(recipe);
    }
  }
  
  /**Adds a recipe to the recipe manager. If the recipe already exists, this will
   *   return false and an error log message will be shown in console. This will
   *   not overwrite an existing recipe.
   * @param recipe The recipe to be added
   * @return True if the recipe was added, otherwise false.
   */
  public boolean addRecipe(Recipe recipe)
  {
    if(this.recipes.containsKey(recipe.getName()))
    {
      RecipeManager.log("Error registering new recipe:");
      RecipeManager.log("  Recipe with name '" + recipe.getName() + "' already exists.");
      RecipeManager.log("  This means there is a recipe using the same name as this one.");
      return false;
    }
    RecipeManager.debugLog("Registered recipe with name '" + recipe.getName() + "'");
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
