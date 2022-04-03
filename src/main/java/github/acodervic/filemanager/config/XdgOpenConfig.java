package github.acodervic.filemanager.config;

import java.util.function.Consumer;

import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.data.Opt;

public class XdgOpenConfig implements GuiUtil {
    public static String type_cmd="CMD";
    public static String type_process="CMD";
    String type=type_cmd;
    Opt<Consumer<RESWallper>> action=new Opt<>();

    
    /**
     * @param type
     * @param action
     */
    public XdgOpenConfig(String type, Consumer<RESWallper> action) {
        this.type = type;
        if ( notNull( action)) {
            this.action.of( action);
        }
    }


    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }


    /**
     * @return the action
     */
    public Opt<Consumer<RESWallper>> getAction() {
        return action;
    }


    /**
     * @param action the action to set
     */
    public void setAction(Opt<Consumer<RESWallper>> action) {
        this.action = action;
    }


    /**
     * @return the type
     */
    public String getType() {
        return type;
    }



    
}
