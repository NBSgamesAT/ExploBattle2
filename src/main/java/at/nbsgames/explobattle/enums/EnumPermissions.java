package at.nbsgames.explobattle.enums;

public enum EnumPermissions {

    JOIN("explobattle.join"),
    LEAVE("explobattle.leave"),
    CREATE("explobattle.create"),
    DELETE("explobattle.delete"),
    MANAGE_PLAYER_SPAWNS("explobattle.manageplayerspawn");

    String permission;
    EnumPermissions(String permission){
        this.permission = permission;
    }

    public String toString(){
        return this.permission;
    }
}
