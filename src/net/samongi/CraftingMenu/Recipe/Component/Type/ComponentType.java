package net.samongi.CraftingMenu.Recipe.Component.Type;

import net.samongi.CraftingMenu.Recipe.Component.Component;

/**ComponentType are used to construct components
 * Dynamically and make it easier to handle components.
 * ComponentTypes are registered with the Component manager.
 * 
 * @author Alex
 *
 */
public interface ComponentType
{
  public Component getComponent(String str);
  public String getName();
}
