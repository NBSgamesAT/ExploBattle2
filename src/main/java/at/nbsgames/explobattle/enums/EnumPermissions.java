package at.nbsgames.explobattle.enums;

public enum EnumPermissions{

    JOIN("explobattle.join"),
    ARENA_CREATE("explobattle.arena.create"),
    ARENA_DELETE("explobattle.arena.delete"),
    ARENA_INFO("explobattle.arena.info"),
    ARENA_MANAGE_SPAWNS("explobattle.arena.manageplayerspawn"),
    ARENA_SET_SIGN("explobattle.arena.sign");

    String permission;
    EnumPermissions(String permission){
        this.permission = permission;
    }

    public String toString(){
        return this.permission;
    }
}
