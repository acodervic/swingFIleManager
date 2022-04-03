package github.acodervic.filemanager.treetable;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;

import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.swing.tree.filter.TipJPanel;
import net.miginfocom.swing.MigLayout;

public class TreeNameColumnPanel extends TipJPanel implements GuiUtil {
    RESWallper res;
    JLabel baseIconLable;
    Icon baseIcon = Icons.whiteFile;
    JLabel resIconLable;
    JLabel nameLable;
    JLabel extLable;
    boolean expanded = false;

    JTreeTable jTreeTable;



    public void changeExpanded(boolean expanded) {
        if (expanded != this.expanded) {
            this.expanded = expanded;
            if (this.expanded) {
                baseIcon = Icons.folderOpen;
            } else {
                baseIcon = Icons.folder;
            }
            baseIconLable.setIcon(baseIcon);
        }
    }

    

    public TreeNameColumnPanel(RESWallper res) {
        super();
        this.res = res;
        setBorder(null);
        setLayout(new MigLayout("gap -1px"));
        setOpaque(false);// 必须要设置这个,不然panel的背景会显示出来一个黑块
        reloadPanel();

    }


        /**
     * @return the jTreeTable
     */
    public JTreeTable getjTreeTable() {
        return jTreeTable;
    }

    /**
     * @param jTreeTable the jTreeTable to set
     */
    public void setjTreeTable(JTreeTable jTreeTable) {
        this.jTreeTable = jTreeTable;
    }
    
    /**
     * @return the baseIconLable
     */
    public synchronized JLabel getBaseIconLable() {
        if (isNull(baseIconLable)) {
            baseIconLable = new JLabel();
        }
        return baseIconLable;
    }

    /**
     * @param baseIconLable the baseIconLable to set
     */
    public void setBaseIconLable(JLabel baseIconLable) {
        this.baseIconLable = baseIconLable;
    }

    /**
     * @return the baseIcon
     */
    public Icon getBaseIcon() {
        return baseIcon;
    }

    /**
     * @param baseIcon the baseIcon to set
     */
    public void setBaseIcon(Icon baseIcon) {
        this.baseIcon = baseIcon;
    }

    /**
     * @return the resIconLable
     */
    public synchronized JLabel getResIconLable() {
        if (isNull(resIconLable)) {
            resIconLable=new JLabel();
        }
        return resIconLable;
    }

    /**
     * @param resIconLable the resIconLable to set
     */
    public void setResIconLable(JLabel resIconLable) {
        this.resIconLable = resIconLable;
    }

    /**
     * @return the nameLable
     */
    public synchronized JLabel getNameLable() {
        if (isNull(nameLable)) {
            nameLable=new JLabel();
            
            Font font = nameLable.getFont();
            //nameLable.setFont(new  Font(font.getFamily(), font.getStyle(),  14));;

        }
        return nameLable;
    }

    /**
     * @param nameLable the nameLable to set
     */
    public void setNameLable(JLabel nameLable) {
        this.nameLable = nameLable;
    }

    /**
     * @return the extLable
     */
    public synchronized JLabel getExtLable() {
        if (isNull(extLable)) {
            extLable=new JLabel();
        }
        return extLable;
    }

    /**
     * @param extLable the extLable to set
     */
    public void setExtLable(JLabel extLable) {
        this.extLable = extLable;
    }

    /**
     * @return the expanded
     */
    public boolean isExpanded() {
        return expanded;
    }

    /**
     * @param expanded the expanded to set
     */
    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    static MatteBorder recentReadedBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GREEN);
    static  MatteBorder recentUpdateBorder = BorderFactory.createMatteBorder(0, 0, 1, 0, Color.red);

    static {

    }
    public void reloadPanel() {
        remove(getBaseIconLable());
        remove(getResIconLable());
        remove(getExtLable());
        remove(getNameLable());
        // 渲染默认图标
              if (res.isDir()) {
                changeExpanded(this.expanded);
                Icon baseIcon = Icons.folder;
                getBaseIconLable().setIcon(baseIcon);
                getBaseIconLable().setName("baseIconLable");
                add(getBaseIconLable());
            }
    
            ImageIcon icon = res.getIcon();
            if (notNull(icon)) {
                if (!res.getIconShowDir() && res.isDir()) {
                    if (icon==Icons.whiteFile) {
                        
                    }else{
                        getBaseIconLable().setIcon(icon);
                    }
                } else {
                    getResIconLable().setIcon(icon);
                    getResIconLable().setName("resIconLable");
                    add(getResIconLable());
                }
            }
            String name = res.getName();
            if (res.isFile()) {                
                name = res.getBaseName();
            }
            // panel.setToolTip(name);
            getNameLable().setText(name);
            getNameLable().setBorder(null);
            getNameLable().setName("nameLable");
            add(getNameLable(),"gapbottom -30px");
    
            // 如果是文件则尝试给扩展名称加颜色
            if (res.isFile() && str(res.getFileExtName()).notEmpty()) {
                getNameLable().setText(res.getBaseName());
                getExtLable().setText("." + res.getFileExtName());
                getExtLable().setBorder(null);
                getExtLable().setName("extLable");
                getExtLable().setForeground(Color.WHITE);
                if (res.isHidden()) {
                    // 如果是隐藏文件/夹则降低文本颜色值
                    getExtLable().setForeground(new Color(205, 205, 0));
    
                }
                add(getExtLable());
            }
            if (res.isHidden()) {
                // 如果是隐藏文件/夹则降低文本颜色值
                getNameLable().setForeground(new Color(190, 190, 190));
            }
    
            if (res.isSymbolicLink()) {
    
            }
            if (res.isFile() && res.isExecutable()) {
                getNameLable().setForeground(Color.green);
                if (res.isHidden()) {
                    // 如果是隐藏文件/夹则降低文本颜色值
                    getNameLable().setForeground(new Color(60, 179, 113));
                }
            }

                if (notNull(res.getLastModified())) {
                if ((System.currentTimeMillis()-res.getLastModified().getTime())<(1000*60*15)) {//15分钟内
                    setBorder(recentUpdateBorder);
               }
            }

           repaint();
    }

}
