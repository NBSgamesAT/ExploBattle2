package at.nbsgames.explobattle.utility;

public class ConfigurableTextsHelper {

    private String configuredText;
    public ConfigurableTextsHelper(String configuredText){
        this.configuredText = configuredText;
    }

    public String getConfiguredText(){
        return this.configuredText;
    }

    public void setConfiguredText(String configuredText){
        this.configuredText = configuredText;
    }

}
