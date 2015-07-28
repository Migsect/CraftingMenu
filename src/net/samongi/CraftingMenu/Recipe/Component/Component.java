package net.samongi.CraftingMenu.Recipe.Component;

import org.bukkit.configuration.ConfigurationSection;
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
 
  /**Generates a component based on a path to an key.  The key
   * can be a configuration section or a currently held key to data.
   * This will try to use the string the path may point to, to generate a component
   * Otherwise it will try to get it based on the section.
   * 
   * @param section
   * @param path
   * @return
   */
  public static Component getComponent(ConfigurationSection section, String path)
  {
    String str = section.getString(path);
    if(str != null) return Component.getComponent(str);
    ConfigurationSection sub_section = section.getConfigurationSection(path);
    if(sub_section!= null) return Component.getComponent(sub_section);
    return null;
  }
  /**Generates a component based on a configurationSection.
   * This will return null if the section is insuffient to generate a component.
   * 
   * @param section
   * @return
   */
  static Component getComponent(ConfigurationSection section)
  {
    return null;
  }
  /**Generates a component based on a string as opposed to a configuration section.
   * 
   * @param str A string that is indictative of a component
   * @return A component, if the string does not work then this will return null
   */
  static Component getComponent(String str)
  {
    Component comp = null;
    if(comp == null) comp = MaterialComponent.getComponent(str);
    if(comp == null) comp = LevelComponent.getComponent(str);
    return comp;
  }
}
