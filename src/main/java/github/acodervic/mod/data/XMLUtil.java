package github.acodervic.mod.data;

import java.util.List;

import org.w3c.dom.Document;

import cn.hutool.core.util.XmlUtil;
import cn.hutool.json.JSONObject;
import github.acodervic.mod.data.mode.XmlNode;

/**
 * xml
 */
public class XMLUtil {

    /**
     * String 转 org.dom4j.Document
     * 
     * @param xml xml字符串
     * @return dom对象
     * @throws DocumentException
     */
    public static Document strToDocument(String xml) {
        return XmlUtil.readXML(xml);
    }

 
    /**
     * 将xml节点转换为对象,注意如节点a下面无属性但内容为a会被转换为a:a 如果节点a下面有属性:则节点的文本内容会被转换为
     * 
     * @param node 节点对象
     * @return
     */
    public static JSONObject nodeToJSONObject(XmlNode node) {
        JSONObject result = new JSONObject();
        // 当前节点的名称、文本内容和属性
        node.getAttributes().forEach((k, v) -> {
            result.accumulate(k, v.to_s());
        });

        // 递归遍历当前节点所有的子节点
        List<XmlNode> listElement = node.getChildNodes();// 所有一级子节点的list
        for (XmlNode e : listElement) {// 遍历所有一级子节点
            // 添加文本
            if (!e.hasChildNode() && e.getAttributes().size() == 0) {
                result.accumulate(e.getTagName(), e.getTextContent());
            } else if (!e.hasChildNode() && e.getAttributes().size() != 0) {// 没有子节点但是有属性
                result.accumulate(e.getTagName(), nodeToJSONObject(e));// 将该一级节点放入该节点名称的属性对应的值中
                result.getJSONObject(e.getTagName()).accumulate("nodeText", e.getTextContent());
            } else {
                result.accumulate(e.getTagName(), nodeToJSONObject(e));// 将该一级节点放入该节点名称的属性对应的值中
            }
        }

        return result;
    }

}