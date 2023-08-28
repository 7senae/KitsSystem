package com.foxy.kits;



import org.bukkit.inventory.ItemStack;

public class KitData {
  private String KitName;
  
  private String Permission;
  
  private ItemStack[] Contents;
  
  private ItemStack[] ArmorContents;
  
  public KitData(String KitName, String Permission, ItemStack[] Contents, ItemStack[] ArmorContents) {
    this.KitName = KitName;
    this.Permission = Permission;
    this.Contents = Contents;
    this.ArmorContents = ArmorContents;
  }
  
  public KitData(String KitName, ItemStack[] Contents, ItemStack[] ArmorContents) {
    this.KitName = KitName;
    this.Permission = null;
    this.Contents = Contents;
    this.ArmorContents = ArmorContents;
  }
  
  public ItemStack[] getContents() {
    return this.Contents;
  }
  
  public ItemStack[] getArmorContents() {
    return this.ArmorContents;
  }
  
  public String getName() {
    return this.KitName;
  }
  
  public String getPermission() {
    return this.Permission;
  }
  
  public boolean hasPermission() {
    if (this.Permission == null || this.Permission.trim().isEmpty())
      return false; 
    return true;
  }
}
