package at.nbsgames.explobattle.utility;

import at.nbsgames.explobattle.Main;
import at.nbsgames.explobattle.enums.EnumConfigStrings;
import at.nbsgames.explobattle.events.RightClickEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.UUID;

public class ExploBattlePlayer{

    private Player player;
    private ItemStack[] inventory;
    private ItemStack[] armor;
    private ItemStack itemInOffHand;
    private int playerLevel;
    private float playerExp;

    private GameMode mode;

    public ExploBattlePlayer(Player player){
        this.player = player;
    }

    public Player getBukkitPlayer() {
        return player;
    }
    public void saveAndClearInventory(){
        if(inventory != null) return;

        inventory = player.getInventory().getContents();
        armor = player.getInventory().getArmorContents();
        playerLevel = player.getLevel();
        playerExp = player.getExp();

        player.getInventory().clear();
        player.setLevel(0);
        player.setExp(0F);

        saveGameMode();
    }
    public void saveGameMode(){
        if(mode == null) mode = player.getGameMode();
    }
    public void restoreOldGameMode(){
        if(mode != null) player.setGameMode(mode);
        mode = null;
    }

    public void restoreOldInv(){
        if(inventory == null) return;
        player.getInventory().setContents(inventory);
        player.getInventory().setArmorContents(armor);
        player.setLevel(playerLevel);
        player.setExp(playerExp);

        inventory = null;
        armor = null;
        playerLevel = 0;
        playerExp = 0F;

        restoreOldGameMode();
    }

    /**
     * Equips the player with all the items that they need for an explobattle match.
     */
    public void equipPlayer(){
        player.getInventory().setItem(0, makeSword(Main.makeItemWithName(Material.NETHERITE_SWORD, EnumConfigStrings.ITEMS_SWORD_NAME.translate())));
        player.getInventory().setItem(1, makeBow(Main.makeItemWithName(Material.BOW, EnumConfigStrings.ITEMS_BOW_NAME.translate())));
        player.getInventory().setItem(2, new ItemStack(Material.COOKED_BEEF).asQuantity(64));
        player.getInventory().setItem(10, new ItemStack(Material.ARROW).asQuantity(64));

        player.getInventory().setItem(3, Main.makeItemWithName(Material.NETHERITE_HOE, EnumConfigStrings.ITEMS_GUN_NAME.translate()));
        player.getInventory().setItem(4, Main.makeItemWithName(Material.TNT, EnumConfigStrings.ITEMS_GRENADE_NAME.translate()));
        player.getInventory().setItem(5, Main.makeItemWithName(Material.SNOWBALL, EnumConfigStrings.ITEMS_ULTRA_GRENADE_NAME.translate()));
        player.getInventory().setItem(6, Main.makeItemWithName(Material.NETHERITE_AXE, EnumConfigStrings.ITEMS_BAZOOKA_NAME.translate()));

        player.getInventory().setHelmet(makeArmour(new ItemStack(Material.DIAMOND_HELMET)));
        player.getInventory().setChestplate(makeArmour(new ItemStack(Material.DIAMOND_CHESTPLATE)));
        player.getInventory().setLeggings(makeArmour(new ItemStack(Material.DIAMOND_LEGGINGS)));
        player.getInventory().setBoots(makeArmour(new ItemStack(Material.DIAMOND_BOOTS)));
    }

    private ItemStack makeSword(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        meta.setUnbreakable(true);
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), "speed", 11D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "speed", 28D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.HAND));
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        stack.setItemMeta(meta);
        return stack;
    }
    private ItemStack makeBow(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(Enchantment.ARROW_DAMAGE, 24, true);
        meta.addEnchant(Enchantment.ARROW_INFINITE, 1, true);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        stack.setItemMeta(meta);
        return stack;
    }

    private ItemStack makeArmour(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();
        meta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 3, true);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        stack.setItemMeta(meta);
        return stack;
    }

}
