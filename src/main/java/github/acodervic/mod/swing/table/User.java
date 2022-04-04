package github.acodervic.mod.swing.table;
 
import github.acodervic.mod.db.anima.Model;
import github.acodervic.mod.swing.annotation.TableRowObject;
import github.acodervic.mod.swing.annotation.TableRowObjectType;


public class User extends Model {
    @TableRowObject(required = true, type = TableRowObjectType.STRING, defaultVaule = "张三", enumValues = { "李四",
            "王五" }, lableText = "名称")
    String name;
    @TableRowObject(required = true, type = TableRowObjectType.INT, defaultVaule = "12", lableText = "年纪")
    int age;
    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }

    /**
     * @return the age
     */
    public int getAge() {
        return age;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param age the age to set
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}