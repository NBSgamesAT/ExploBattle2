package at.nbsgames.explobattle;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ExploBattlePlayer{

    private Player player;
    private ItemStack[] inventory;
    private ItemStack[] armor;
    int playerLevel;
    float playerExp;

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
    }
}
