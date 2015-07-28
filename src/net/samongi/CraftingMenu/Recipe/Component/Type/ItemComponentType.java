package net.samongi.CraftingMenu.Recipe.Component.Type;

import net.samongi.CraftingMenu.Recipe.Component.Component;

public class ItemComponentType implements ComponentType
{
  private final String name = "ITEM";

	@Override
	/**This will return the itemstack component based on the string.
	 * Items will make use of Universal ItemStack storage by the server.
	 * This functionality is provided by SamongiLib
	 */
	public Component getComponent(String str)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName(){return this.name;}
}
