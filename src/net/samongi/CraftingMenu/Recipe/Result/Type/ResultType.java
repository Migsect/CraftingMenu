package net.samongi.CraftingMenu.Recipe.Result.Type;

import net.samongi.CraftingMenu.Recipe.Result.Result;

/**ResultTypes are used to construct results
 * Dynamically and make it easier to handle results.
 * ResultTypss are registered with the Result manager.
 * 
 * @author Alex
 *
 */
public interface ResultType
{
  public Result getResult(String str);
  public String getName();
  
}
