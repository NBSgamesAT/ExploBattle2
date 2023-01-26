package at.nbsgames.explobattle.commands;

public class NbsArguments {


    // Let all of this be set in stone

    private String name;
    private String description;
    private NbsArgumentWorker worker;

    public NbsArguments(String name, String description, NbsArgumentWorker worker) {
        this.name = name;
        this.description = description;
        this.worker = worker;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public NbsArgumentWorker getWorker() {
        return worker;
    }
}
