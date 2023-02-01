package at.nbsgames.explobattle.command_system;

import net.kyori.adventure.text.Component;

public class FailedToObjectifyException extends Exception{
    private String customMessage;
    public FailedToObjectifyException(String customMessage){
        super(customMessage);
        this.customMessage = customMessage;
    }
    public String getCustomMessage(){
        return this.customMessage;
    }
}
