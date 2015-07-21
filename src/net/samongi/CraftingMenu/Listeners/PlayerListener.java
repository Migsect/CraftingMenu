package net.samongi.CraftingMenu.Listeners;

import net.samongi.CraftingMenu.Player.PlayerManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener
{
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    PlayerManager.getManager().onPlayerJoin(event);
  }
  @EventHandler
  public void onPlayerQuit(PlayerQuitEvent event)
  {
    PlayerManager.getManager().onPlayerQuit(event);
  }
}
