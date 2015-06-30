package net.samongi.CraftingMenu.Player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

public class UUIDMap implements Serializable
{
  private static final long serialVersionUID = -2342729068799006101L;

  private final Map<String, UUID> name_map = new HashMap<>();
  private final Map<UUID, String> uuid_map = new HashMap<>();
  
  public void addPlayer(Player player)
  {
    name_map.put(player.getName(), player.getUniqueId());
    uuid_map.put(player.getUniqueId(), player.getName());
  }
}
