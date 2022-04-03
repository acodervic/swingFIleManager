package github.acodervic.filemanager.treetable;

import java.util.Date;

import github.acodervic.filemanager.treetable.renderer.TableDateColumnRander;
import github.acodervic.filemanager.treetable.renderer.TableRightAlignmentColumnRander;
import github.acodervic.mod.data.ByteUtil;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.str;

public class Columns   {
    static TableRightAlignmentColumnRander cellRenderer = new TableRightAlignmentColumnRander();

    public static Column Size=new Column<String>("Size", String.class,cellRenderer,res  ->{
        if(res.isDir()){
            int count=res.itemCount();
            if(count==0){
                return "Empty";
            }else if(count==-1){
                return "UnKonw";
            }else{
                return count + " Items";
            }
        }else{         
            return ByteUtil.getPrintSize(res.getByteSize());
        }
    });
    public static Column Name=new Column<String>("Name", TreeTableModel.class,res  ->{
        return res.getName();
    });
    public static Column Type=new Column<String>("Type", String.class,res  ->{
        return res.isDir()?"Directory":"File";
    });
    public static Column Comment=new Column<String>("Comment", String.class,res  ->{
        Opt<JTreeTable> table = res.getTable();
        if( table.notNull_()){
            //搜索模式在返回路径
            JTreeTable jTreeTable = table.get();
            if(jTreeTable.inSearchModel()){
                return  res.getAbsolutePath();
            }
        }
        return new str(res.getUserComment().get()).to_s();
    });
    public static Column Modified= new Column<Date>("Modified", Date.class,new TableDateColumnRander(),res  ->{
        return res.getLastModified();
    });
    public static Column Owner=new Column<String>("Owner", String.class,res  ->{
        return res.getOwner();
    });
    public static Column Permissions= new Column<String>("Permissions", String.class,res  ->{
        return res.getPermissionsDesString();
    });
    public final static Column[] ColumnNamesArray={Size,Name,Comment,Modified,Owner,Permissions};

    public  static Column getColumnByIndex(int index) {
        return ColumnNamesArray[index];
    }
 


}
