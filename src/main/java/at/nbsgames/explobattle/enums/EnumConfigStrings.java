package at.nbsgames.explobattle.enums;

import at.nbsgames.explobattle.utility.ConfigurableTextsHelper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public enum EnumConfigStrings {

    PLAYER_JOINED("text.player.joined_match", new ConfigurableTextsHelper("<green>+ (PLAYER) has joined the game!")),
    PLAYER_LEFT("text.player.left_match", new ConfigurableTextsHelper("<red>- (PLAYER) has left the game!")),
    PLAYER_DIED("text.player.died_in_match", new ConfigurableTextsHelper("<light_purple>+ (PLAYER) died!")),
    PLAYER_DISCONNECTED("text.player.disconnected", new ConfigurableTextsHelper("<light_purple>- (PLAYER) disconnected and was removed from the game!")),
    PLAYER_ALREADY_JOINED("text.player.player_already_joined", new ConfigurableTextsHelper("<light_purple>You already joined (ARENA).")),
    PLAYER_JOIN_FAILURE("text.player.generic_join_failure", new ConfigurableTextsHelper("<dark_purple>Something went wrong.")),
    PLAYER_JOIN_ARENA_FULL("text.player.arena_is_full", new ConfigurableTextsHelper("<dark_purple>The limit for that Arena has been reached!")),
    PLAYER_JOIN_MATCH_ONGOING("text.player.match_is_happening", new ConfigurableTextsHelper("<dark_purple>The battle in this arena has already started")),

    PLAYER_LEAVE_NOT_INGAME("text.player.not_in_match", new ConfigurableTextsHelper("<dark_purple>You already aren't in a match.")),
    PLAYER_LEAVE_SUCCESS("text.player.player_left_success", new ConfigurableTextsHelper("<green>You have left your current arena")),
    WAIT_GAME_STARTS_IN("text.arena.loading_into_arena_in", new ConfigurableTextsHelper("<light_purple>Waiting for more players, the game starts in <yellow>(SECONDS) <light_purple>seconds.")),
    BATTLE_START_IN("text.arena.match_starts_in", new ConfigurableTextsHelper("<blue>The battle starts in <yellow>(SECONDS)<blue>.")),
    BATTLE_STARTED("text.arena.match_started", new ConfigurableTextsHelper("<light_purple>The battle has started!")),
    BATTLE_TOO_FEW_PLAYERS("text.arena.not_enough_players", new ConfigurableTextsHelper("<red>Too few players too start the match, aborting countdown")),
    PLAYER_WON("text.player.won_match", new ConfigurableTextsHelper("<aqua>(PLAYER) <yellow>has won the game!")),


    ITEMS_SWORD_NAME("names.items.sword", new ConfigurableTextsHelper("<gold><bold>SWORD"), true),
    ITEMS_BOW_NAME("names.items.bow", new ConfigurableTextsHelper("<gold><bold>BOW"), true),
    ITEMS_GUN_NAME("names.items.gun", new ConfigurableTextsHelper("<gold><bold>GUN"), true),
    ITEMS_GRENADE_NAME("names.items.grenade", new ConfigurableTextsHelper("<gold><bold>GRENADE"), true),
    ITEMS_ULTRA_GRENADE_NAME("names.items.ultra_grenade", new ConfigurableTextsHelper("<gold><bold>ULTRA GRENADE"), true),
    ITEMS_ULTRA_GRENADE_COOLDOWN("names.items.cooldown.ultra_grenade", new ConfigurableTextsHelper("<gray><strikethrough>ULTRA GRENADE"), true),
    ITEMS_BAZOOKA_NAME("names.items.bazooka", new ConfigurableTextsHelper("<gold><bold>BAZOOKA"), true),
    ITEMS_BAZOOKA_COOLDOWN("names.items.cooldown.bazooka", new ConfigurableTextsHelper("<gray><strikethrough>BAZOOKA"), true),

    ITEMS_COBWEB_NAME("names.items.cobweb", new ConfigurableTextsHelper("<gold><bold>COBWEB"), true),
    ITEMS_COBWEB_COOLDOWN("names.items.cooldown.COBWEB", new ConfigurableTextsHelper("<gray><strikethrough>COBWEB"), true);

    String config;
    ConfigurableTextsHelper defaultText;
    boolean preTranslated;
    Component translated;

    EnumConfigStrings(String config, ConfigurableTextsHelper text){
        this.config = config;
        this.defaultText = text;
        this.preTranslated = false;
    }
    EnumConfigStrings(String config, ConfigurableTextsHelper text, boolean preTranslated){
        this.config = config;
        this.defaultText = text;
        this.preTranslated = preTranslated;
    }

    @Override
    public String toString(){
        return this.config + ": " + this.defaultText.getConfiguredText();
    }

    public String getConfig(){
        return this.config;
    }

    public void setText(String text) {
        this.defaultText.setConfiguredText(text);
        this.translated = null;
    }
    public String getText() {
        return this.defaultText.getConfiguredText();
    }

    public Component translate(){
        if(!preTranslated){
            return MiniMessage.miniMessage().deserialize(this.defaultText.getConfiguredText());
        }
        else if(translated != null){
            return translated;
        }
        else{
            translated = MiniMessage.miniMessage().deserialize(this.defaultText.getConfiguredText());
            return translated;
        }
    }
    public Component translateWithArena(String arena){
        return MiniMessage.miniMessage().deserialize(this.defaultText.getConfiguredText().replace("(ARENA)", arena));
    }
    public Component translateWithPlayer(String playerName){
        return MiniMessage.miniMessage().deserialize(this.defaultText.getConfiguredText().replace("(PLAYER)", playerName));
    }
    public Component translateWithSeconds(int seconds){
        return MiniMessage.miniMessage().deserialize(this.defaultText.getConfiguredText().replace("(SECONDS)", String.valueOf(seconds)));
    }
}
