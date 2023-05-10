package at.nbsgames.explobattle.exceptions;

import net.kyori.adventure.text.Component;

import javax.annotation.Nullable;

public class ExploBattleExceptions extends Exception {

    Component com;
    public ExploBattleExceptions(String reason){
        super(reason);
    }
    public ExploBattleExceptions(Component com){
        this.com = com;
    }

    public Component getComponent(){
        if(this.com == null){
            return Component.text(this.getMessage());
        }
        return this.com;
    }

    public void setComponent(Component com){
        this.com = com;
    }

}
