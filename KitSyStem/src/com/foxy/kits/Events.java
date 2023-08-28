package com.foxy.kits;



import java.util.ArrayList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class Events implements Listener {
  Main plugin;
  
  public Events(Main pl) {
    this.plugin = pl;
  }
  
  @EventHandler
  public void onWorldChange(PlayerChangedWorldEvent e) {
    Player p = e.getPlayer();
    if (this.plugin.Setting.GetActiveWorlds() == null)
      return; 
    if (!this.plugin.Setting.GetActiveWorlds().contains(p.getWorld().getName()))
      return; 
    if (this.plugin.Setting.GetPlayerHasPlayedBefore(p.getUniqueId().toString()))
      return; 
    this.plugin.Setting.SetPlayerHasPlayedBefore(p.getUniqueId().toString(), true);
    for (String Kit : this.plugin.Setting.GetActiveKits()) {
      KitData KD = this.plugin.Setting.GetKitData(Kit);
      if (KD == null)
        continue; 
      if (KD.getPermission() != null && !p.hasPermission(KD.getPermission()))
        continue; 
      p.getInventory().setContents(KD.getContents());
      p.getInventory().setArmorContents(KD.getArmorContents());
      return;
    } 
  }
  
  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent e) {
    Player p = e.getPlayer();
    if (this.plugin.Setting.GetActiveWorlds() == null)
      return; 
    if (!this.plugin.Setting.GetActiveWorlds().contains(p.getWorld().getName()))
      return; 
    for (String Kit : this.plugin.Setting.GetActiveKits()) {
      KitData KD = this.plugin.Setting.GetKitData(Kit);
      if (KD == null)
        continue; 
      if (KD.getPermission() != null && !p.hasPermission(KD.getPermission()))
        continue; 
      p.getInventory().setContents(KD.getContents());
      p.getInventory().setArmorContents(KD.getArmorContents());
      return;
    } 
  }
  
  @EventHandler
  public void onPlayerDeath(PlayerDeathEvent e) {
    Player p = e.getEntity();
    if (this.plugin.Setting.GetActiveWorlds() == null)
      return; 
    if (!this.plugin.Setting.GetActiveWorlds().contains(p.getWorld().getName()))
      return; 
    List<ItemStack> ItemsNotFromKit = new ArrayList<>();
    for (ItemStack item : e.getDrops()) {
      if (item == null)
        continue; 
      if (!this.plugin.Setting.IsItemFromAKit(item))
        ItemsNotFromKit.add(item); 
    } 
    e.getDrops().clear();
    e.getDrops().addAll(ItemsNotFromKit);
  }
}
