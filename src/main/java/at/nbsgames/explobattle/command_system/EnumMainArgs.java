package at.nbsgames.explobattle.command_system;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public enum EnumMainArgs {

    STRING(new NbsArgumentWorker(null) {
        @Override
        public Object objectify(String input, NbsArguments argument) throws FailedToObjectifyException{
            return input;
        }

        @Override
        public List<String> autocompletionList(String input, NbsArguments argument) {
            return null;
        }
    }),
    INT(new NbsArgumentWorker("{{ INPUT }} is not an integer") {
        @Override
        public Object objectify(String input, NbsArguments argument) throws FailedToObjectifyException {
            try{
                return Integer.parseInt(input);
            }
            catch(NumberFormatException e){
                throw new FailedToObjectifyException(this.getErrorString(input));
            }
        }

        @Override
        public List<String> autocompletionList(String input, NbsArguments argument) {
            return null;
        }
    }),
    LONG(new NbsArgumentWorker("{{ INPUT }} is not an long") {
        @Override
        public Object objectify(String input, NbsArguments argument) throws FailedToObjectifyException {
            try{
                return Long.parseLong(input);
            }
            catch(NumberFormatException e){
                throw new FailedToObjectifyException(this.getErrorString(input));
            }
        }

        @Override
        public List<String> autocompletionList(String input, NbsArguments argument) {
            return null;
        }
    }),
    DOUBLE(new NbsArgumentWorker("{{ INPUT }} is not an double") {
        @Override
        public Object objectify(String input, NbsArguments argument) throws FailedToObjectifyException {
            try{
                return Double.parseDouble(input);
            }
            catch(NumberFormatException e){
                throw new FailedToObjectifyException(this.getErrorString(input));
            }
        }

        @Override
        public List<String> autocompletionList(String input, NbsArguments argument) {
            return null;
        }
    }),
    FLOAT(new NbsArgumentWorker("{{ INPUT }} is not an float") {
        @Override
        public Object objectify(String input, NbsArguments argument) throws FailedToObjectifyException {
            try{
                return Float.parseFloat(input);
            }
            catch(NumberFormatException e){
                throw new FailedToObjectifyException(this.getErrorString(input));
            }
        }

        @Override
        public List<String> autocompletionList(String input, NbsArguments argument) {
            return null;
        }
    }),
    BOOLEAN(new NbsArgumentWorker("{{ INPUT }} does not match true/false") {
        @Override
        public Object objectify(String input, NbsArguments argument) throws FailedToObjectifyException {
            if(input.equals("true")) return true;
            if(input.equals("false")) return false;
            throw new FailedToObjectifyException(this.getErrorString(input));
        }

        @Override
        public List<String> autocompletionList(String input, NbsArguments argument) {
            return Arrays.asList("true", "false");
        }
    }),
    OPTIONAL_BOOLEAN(new NbsArgumentWorker("{{ INPUT }} does not match true/false/disable") {
        @Override
        public Object objectify(String input, NbsArguments argument) throws FailedToObjectifyException {
            if(input.equals("true")) return true;
            if(input.equals("false")) return false;
            if(input.equals("disable")) return null;
            throw new FailedToObjectifyException(this.getErrorString(input));
        }

        @Override
        public List<String> autocompletionList(String input, NbsArguments argument) {
            return Arrays.asList("true", "false", "disable");
        }
    }),
    ONLINE_MEMBER(new NbsArgumentWorker("User {{ INPUT }} could not be found") {
        @Override
        public Object objectify(String input, NbsArguments argument) throws FailedToObjectifyException {
            Player p = Bukkit.getPlayer(input);
            if(p == null) throw new FailedToObjectifyException(this.getErrorString(input));
            return p;
        }

        @Override
        public List<String> autocompletionList(String input, NbsArguments argument) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
        }
    });

    // Now, what was I doing?

    private NbsArgumentWorker arg;
    EnumMainArgs(NbsArgumentWorker arg){
        this.arg = arg;
    }

    public NbsArgumentWorker getWorker() {
        return arg;
    }

    // Look, I didn't know a better way to allow for these overridable
    public void setErrorMessage(String errorMessage){
        arg.setErrorString(errorMessage);
    }
}
