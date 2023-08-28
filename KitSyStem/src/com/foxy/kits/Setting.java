package com.foxy.kits;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Setting {
  Main plugin;
  
  private File PlayerDataFile;
  
  private FileConfiguration PlayerData;
  
  private File KitsFile;
  
  FileConfiguration Data;
  
  FileConfiguration Kits;
  
  public Setting(Main pl) {
    this.plugin = pl;
    this.PlayerDataFile = new File(this.plugin.getDataFolder(), "PlayerData.yml");
    if (!this.PlayerDataFile.exists())
      try {
        this.PlayerDataFile.getParentFile().mkdirs();
        copy(this.plugin.getResource("PlayerData.yml"), this.PlayerDataFile);
      } catch (Exception e) {
        e.printStackTrace();
      }  
    this.KitsFile = new File(this.plugin.getDataFolder(), "config.yml");
    if (!this.KitsFile.exists())
      try {
        this.KitsFile.getParentFile().mkdirs();
        copy(this.plugin.getResource("config.yml"), this.KitsFile);
      } catch (Exception e) {
        e.printStackTrace();
      }  
    this.Data = (FileConfiguration)new YamlConfiguration();
    this.PlayerData = (FileConfiguration)new YamlConfiguration();
    try {
      this.PlayerData.load(this.PlayerDataFile);
      this.Data.load(this.KitsFile);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InvalidConfigurationException e) {
      e.printStackTrace();
    } 
  }
  
  public KitData GetKitData(String KitName) {
    String KitPermission = null;
    if (!this.Data.contains("Kits." + KitName))
      return null; 
    if (this.Data.contains("Kits." + KitName + ".Permission"))
      KitPermission = this.Data.getString("Kits." + KitName + ".Permission"); 
    ItemStack[] Contents = new ItemStack[0];
    ItemStack[] ArmorContents = new ItemStack[4];
    Inventory inv = Bukkit.createInventory(null, 36);
    List<ItemStack> Armor = new ArrayList<>();
    for (int Slot = 0; Slot < 40; Slot++) {
      if (!this.Data.contains("Kits." + KitName + "." + Slot)) {
        if (Slot > 35)
          Armor.add(null); 
      } else {
        String ItemName = null;
        int ItemType = 0;
        int ItemCount = 1;
        int ItemDamage = 0;
        if (this.Data.contains("Kits." + KitName + "." + Slot + ".Name"))
          ItemName = ChatColor.translateAlternateColorCodes('&', 
              this.Data.getString("Kits." + KitName + "." + Slot + ".Name")); 
        if (this.Data.contains("Kits." + KitName + "." + Slot + ".Type")) {
          ItemType = this.Data.getInt("Kits." + KitName + "." + Slot + ".Type");
        } else {
          if (Slot > 35)
            Armor.add(null); 
          Slot++;
        } 
        if (this.Data.contains("Kits." + KitName + "." + Slot + ".Count"))
          ItemCount = this.Data.getInt("Kits." + KitName + "." + Slot + ".Count"); 
        if (this.Data.contains("Kits." + KitName + "." + Slot + ".\t"))
          ItemDamage = this.Data.getInt("Kits." + KitName + "." + Slot + ".Damage"); 
        if (ItemType == 0) {
          if (Slot > 35)
            Armor.add(null); 
        } else {
          Map<Enchantment, Integer> enchantments = new HashMap<>();
          if (this.Data.contains("Kits." + KitName + "." + Slot + ".Enchants")) {
            List<String> Enchants = this.Data.getStringList("Kits." + KitName + "." + Slot + ".Enchants");
            for (String enchantment : Enchants) {
              int Enchantment_ID = Integer.parseInt(enchantment.split(":")[0]);
              int Enchantment_LEVEL = Integer.parseInt(enchantment.split(":")[1]);
              Enchantment Enchant = Enchantment.getById(Enchantment_ID);
              enchantments.put(Enchant, Integer.valueOf(Enchantment_LEVEL));
              System.out.println(Enchant.getName());
            } 
          } 
          ItemStack item = new ItemStack(ItemType, ItemCount, (short)ItemDamage);
          item = SaveItemAsKit(item);
          if (!enchantments.isEmpty())
            item.addEnchantments(enchantments); 
          ItemMeta item_META = item.getItemMeta();
          item_META.setDisplayName(ItemName);
          item.setItemMeta(item_META);
          item = SaveItemAsKit(item);
          if (Slot < 36) {
            inv.setItem(Slot, item);
          } else {
            Armor.add(item);
          } 
        } 
      } 
    } 
    Contents = inv.getContents();
    ArmorContents = new ItemStack[] { Armor.get(0), Armor.get(1), Armor.get(2), Armor.get(3) };
    return new KitData(KitName, KitPermission, Contents, ArmorContents);
  }
  
  public List<String> GetActiveKits() {
    if (this.Data.contains("ActiveKits"))
      return this.Data.getStringList("ActiveKits"); 
    return null;
  }
  
  public List<String> GetActiveWorlds() {
    if (this.Data.contains("ActiveWorlds"))
      return this.Data.getStringList("ActiveWorlds"); 
    return null;
  }
  
  public boolean GetPlayerHasPlayedBefore(String UUID) {
    if (this.PlayerData.contains(UUID))
      return this.PlayerData.getBoolean(String.valueOf(UUID) + ".hasPlayedBefore"); 
    return false;
  }
  
  public void SetPlayerHasPlayedBefore(String UUID, boolean value) {
    this.PlayerData.set(String.valueOf(UUID) + ".hasPlayedBefore", Boolean.valueOf(value));
    try {
      this.PlayerData.save(this.PlayerDataFile);
    } catch (IOException e) {
      e.printStackTrace();
    } 
  }
  
  public ItemStack SaveItemAsKit(ItemStack is) {
    try {
      Object tag1, stack = this.plugin.getNMSClass("inventory.CraftItemStack", "org.bukkit.craftbukkit")
        .getMethod("asNMSCopy", new Class[] { ItemStack.class });
      stack = ((Method)stack).invoke(stack, new Object[] { is });
      Object hastag = stack.getClass().getMethod("hasTag", new Class[0]).invoke(stack, new Object[0]);
      if (!((Boolean)hastag).booleanValue()) {
        tag1 = this.plugin.getNMSClass("NBTTagCompound", "net.minecraft.server").newInstance();
      } else {
        tag1 = stack.getClass().getMethod("getTag", new Class[0]).invoke(stack, new Object[0]);
      } 
      tag1.getClass().getMethod("setBoolean", new Class[] { String.class, boolean.class }).invoke(tag1, 
          new Object[] { "KitsSystem;ItemFromKit", Boolean.valueOf(true) });
      stack.getClass()
        .getMethod("setTag", new Class[] { this.plugin.getNMSClass("NBTTagCompound", "net.minecraft.server") }).invoke(stack, new Object[] { tag1 });
      is = (ItemStack)this.plugin.getNMSClass("inventory.CraftItemStack", "org.bukkit.craftbukkit")
        .getMethod("asBukkitCopy", new Class[] { stack.getClass() }).invoke(this.plugin.getNMSClass("inventory.CraftItemStack", "org.bukkit.craftbukkit")
          .getMethod("asBukkitCopy", new Class[] { stack.getClass() }), new Object[] { stack });
      return is;
    } catch (IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException|NoSuchMethodException|SecurityException|InstantiationException e) {
      e.printStackTrace();
      return is;
    } 
  }
  
  public boolean IsItemFromAKit(ItemStack is) {
    try {
      Object stack = this.plugin.getNMSClass("inventory.CraftItemStack", "org.bukkit.craftbukkit")
        .getMethod("asNMSCopy", new Class[] { ItemStack.class });
      stack = ((Method)stack).invoke(stack, new Object[] { is });
      Object hastag = stack.getClass().getMethod("hasTag", new Class[0]).invoke(stack, new Object[0]);
      if (!((Boolean)hastag).booleanValue())
        return false; 
      Object tag1 = stack.getClass().getMethod("getTag", new Class[0]).invoke(stack, new Object[0]);
      boolean result = ((Boolean)tag1.getClass().getMethod("getBoolean", new Class[] { String.class }).invoke(tag1, new Object[] { "KitsSystem;ItemFromKit" })).booleanValue();
      return result;
    } catch (IllegalAccessException|IllegalArgumentException|java.lang.reflect.InvocationTargetException|NoSuchMethodException|SecurityException e) {
      e.printStackTrace();
      return false;
    } 
  }
  
  private void copy(InputStream in, File file) {
    try {
      OutputStream out = new FileOutputStream(file);
      byte[] buf = new byte[1024];
      int len;
      while ((len = in.read(buf)) > 0)
        out.write(buf, 0, len); 
      out.close();
      in.close();
    } catch (Exception e) {
      e.printStackTrace();
    } 
  }
}
