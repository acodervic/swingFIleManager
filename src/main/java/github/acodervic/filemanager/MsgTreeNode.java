package github.acodervic.filemanager;

public class MsgTreeNode {
    Integer msgId;
    Integer parentMsgId;
    String treeName;

    public MsgTreeNode(Integer msgId,Integer parentMsgId) {
        this.msgId=msgId;
        this.parentMsgId=parentMsgId;
    }

    public MsgTreeNode() {
        
    }
    /**
     * @return the msgId
     */
    public Integer getMsgId() {
        return msgId;
    }
    /**
     * @param msgId the msgId to set
     */
    public void setMsgId(Integer msgId) {
        this.msgId = msgId;
    }
    /**
     * @return the parentMsgId
     */
    public Integer getParentMsgId() {
        return parentMsgId;
    }
    /**
     * @param parentMsgId the parentMsgId to set
     */
    public void setParentMsgId(Integer parentMsgId) {
        this.parentMsgId = parentMsgId;
    }

    /**
     * @return the treeName
     */
    public String getTreeName() {
        return treeName;
    }

    /**
     * @param treeName the treeName to set
     */
    public void setTreeName(String treeName) {
        this.treeName = treeName;
    }

    
}
