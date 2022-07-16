package at.nbsgames.explobattle.enums;

public enum EnumConfigStrings {

    TEXT_PLAYER_JOINED("text.game.player_joined"),
    TEXT_PLAYER_LEAVE("text.game.player_leave"),
    TEXT_PLAYER_DIED("text.game.player_died"),
    TEXT_PLAYER_DISCONNECTED("text.game.player_disconnected");

    String config;
    EnumConfigStrings(String config){
        this.config = config;
    }

    public String toString(){
        return this.config;
    }

    public String getConfigWithMapName(String map){
        return config.replace("(MAPNAME)", map);
    }
}
