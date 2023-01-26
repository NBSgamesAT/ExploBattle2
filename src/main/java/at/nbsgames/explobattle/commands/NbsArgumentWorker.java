package at.nbsgames.explobattle.commands;

import java.util.List;

public abstract class NbsArgumentWorker {

    private boolean multiArgs;
    private String errorString;
    NbsArgumentWorker(String errorString) {
        this.errorString = errorString;
    }
    NbsArgumentWorker(String errorString, boolean multiArgs){
        this.multiArgs = multiArgs;
        this.errorString = errorString;
    }
    public NbsArgumentWorker() {
        this.errorString = null;
    }
    public NbsArgumentWorker(boolean multiArgs){
        this.multiArgs = multiArgs;
        this.errorString = null;
    }
    public abstract Object objectize(String input, NbsArguments argument) throws IllegalArgumentException;
    public abstract List<String> autocompletionList(String input, NbsArguments argument);

    void setErrorString(String errorString){
        this.errorString = errorString;
    }

    String getErrorString(String input) {
        return errorString.replace("{{ INPUT }}", input);
    }
}
