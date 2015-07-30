package net.samongi.CraftingMenu.Recipe.Component;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Component
{
  /**Checks to see if the player has the component.
   * 
   * @param player The player to check for the component for.
   * @return true if the player had the component.
   */
  public boolean hasComponent(Player player);
  
  /**Removes the component from the player
   * 
   * @param player
   * @return
   */
  public void removeComponent(Player player);
  
  /**Gets string that would display this component.
   * 
   * @return The display of the item in one line
   */
  public String getDisplay();
  
  /**Gets itemstacks that will display the component.
   * 
   * @return An itemstack that would display the item.
   */
  public ItemStack[] getMenuItems();
}
