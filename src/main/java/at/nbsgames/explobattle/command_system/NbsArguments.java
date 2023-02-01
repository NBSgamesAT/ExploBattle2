package at.nbsgames.explobattle.command_system;

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
    public NbsArguments(String name, String description, EnumMainArgs argumentType) {
        this.name = name;
        this.description = description;
        this.worker = argumentType.getWorker();
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
