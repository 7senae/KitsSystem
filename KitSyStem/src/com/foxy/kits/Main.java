package com.foxy.kits;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin {
  Main plugin;
  
  Setting Setting;
  
  public void onEnable() {
    this.plugin = this;
    this.Setting = new Setting(this.plugin);
    getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "KitsSystem Enabled!");
    PluginManager pluginManager = getServer().getPluginManager();
    pluginManager.registerEvents(new Events(this.plugin), (Plugin)this.plugin);
  }
  
  String packageVersion = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
  
  public Class<?> getNMSClass(String nmsClassName, String Prefix) {
    try {
      return Class.forName(String.valueOf(Prefix) + "." + this.packageVersion + "." + nmsClassName);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    } 
  }
}
