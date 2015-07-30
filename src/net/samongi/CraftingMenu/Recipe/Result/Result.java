package net.samongi.CraftingMenu.Recipe.Result;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Result
{
  /**Adds the result to the player
   * 
   * @param player The player to add the result to.
   */
  public void addResult(Player player);
  
  /**Gets string that would display this result.
   * 
   * @return The display of the item in one line
   */
  public String getDisplay();
  
  /**Gets an itemstack will display the result.
   * 
   * @return An itemstack that would display the item.
   */
  public ItemStack[] getMenuItems();
}
