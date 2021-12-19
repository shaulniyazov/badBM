package edu.touro.mco152.bm;


public class Invoker {
    private static CommandInterface command;
    public static void setCommand(CommandInterface commandInterface){
        command = commandInterface;
    }
    public static void callCommand(){
        command.execute();
    }
}
