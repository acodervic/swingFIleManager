package github.acodervic.mod.data.mode;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cn.hutool.json.JSONObject;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.XMLUtil;
import github.acodervic.mod.data.str;

public class XmlNode {
    Element xmlDom;
    HashMap<String, str> attrs;// 节点的属性

    /**
     * 构建一个xml节点
     * 
     * @param xmlDom
     */
    public XmlNode(Element xmlDom) {
        nullCheck(xmlDom);
        this.xmlDom = xmlDom;
        getAttributes();// 自动初始化属性
    }

    /**
     * 读取第一个子节点
     * 
     * @return
     */
    public XmlNode getFirstChild() {
        return new XmlNode((Element) xmlDom.getFirstChild());
    }

    /**
     * 读取最后一个子节点
     * 
     * @return
     */
    public XmlNode getLastChild() {
        return new XmlNode((Element) xmlDom.getLastChild());
    }

    /**
     * 读取标签名称
     * 
     * @return
     */
    public String getTagName() {
        return xmlDom.getTagName();
    }

    /**
     * 读取Element对象
     * 
     * @return
     */
    public Element get() {
        return this.xmlDom;
    }

    /**
     * 通过标签选择
     * 
     * @param name
     * @return
     */
    public Opt<XmlNode> getElementByTagName(String name) {
        Element node = null;
        try {
            node = (Element) xmlDom.getElementsByTagName(name).item(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Opt<XmlNode>(node != null ? new XmlNode(node) : null);
    }

    /**
     * 通过标签选择
     * 
     * @param name
     * @return
     */
    public List<XmlNode> getElementsByTagName(String name) {
        List<XmlNode> nodes = new ArrayList<XmlNode>();
        try {
            NodeList elementsByTagName = xmlDom.getElementsByTagName(name);
            for (int i = 0; i < elementsByTagName.getLength(); i++) {
                Node item = elementsByTagName.item(i);
                nodes.add(new XmlNode((Element) item));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nodes;
    }

    /**
     * 速度所有属性
     * 
     * @return
     */
    public HashMap<String, str> getAttributes() {
        if (this.attrs == null) {
            this.attrs = new HashMap<String, str>();
            NamedNodeMap attributes = xmlDom.getAttributes();
            for (int i = 0; i < attributes.getLength(); i++) {
                Node item = attributes.item(i);
                attrs.put(item.getNodeName(), new str(item.getNodeValue()));
            }
        }
        return attrs;
    }

    /**
     * 读取属性
     * 
     * @param attrName
     * @return
     */
    public Opt<str> getAttribute(String attrName) {
        nullCheck(attrName);
        return new Opt<str>(getAttributes().get(attrName));
    }

    /**
     * 读取当前节点的文本内容
     */
    public String getTextContent() {
        return this.xmlDom.getTextContent();
    }

    /**
     * 通过标签选择器找节点
     * 
     * @param nameSelecter 格式tag1->tag2->tag3
     * @return
     */
    public XmlNode getElementByTagSelect(String nameSelecter) {
        String[] tagNames = nameSelecter.split("->");
        XmlNode xml = null;
        // 一层层的找
        for (int i = 0; i < tagNames.length; i++) {
            str tagName = new str(tagNames[i]).trim();
            if (tagName.notEmpty() && xml == null) {
                Opt<XmlNode> find = getElementByTagName(tagName.to_s());
                if (find != null) {
                    xml = find.get();
                    // 继续找
                } else {
                    // 找不到
                    break;
                }
            } else if (xml != null) {
                // 在当前节点下找
                Opt<XmlNode> find = xml.getElementByTagName(tagName.to_s());
                if (find != null) {
                    xml = find.get();
                    // 继续找
                } else {
                    // 找不到
                    break;
                }
            }
        }
        return xml;
    }

    /**
     * 判断是否有子节点
     * 
     * @return
     */
    public boolean hasChildNode() {
        return this.getChildNodes().size() != 0;
    }

    /**
     * 读取子节点
     * 
     * @return
     */
    public List<XmlNode> getChildNodes() {
        List<XmlNode> nodes = new ArrayList<XmlNode>();
        NodeList childNodes = xmlDom.getChildNodes();
        if (childNodes != null) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node item = childNodes.item(i);
                if (item.hasChildNodes()) {
                    nodes.add(new XmlNode((Element) item));
                }
            }
        }
        return nodes;
    }

    @Override
    public String toString() {
        return xmlDom.getNodeName() + "  子节点" + xmlDom.getChildNodes().getLength() + "  属性:"
                + xmlDom.getAttributes().getLength() + "  内容:" + xmlDom.getTextContent();
    }

    public JSONObject toJson() {
        return XMLUtil.nodeToJSONObject(this);
    }
}
