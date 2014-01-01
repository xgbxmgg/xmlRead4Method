package com.xgbxm.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class XmlModify {
	public static void main(String[] args) {
		XmlModify modify = new XmlModify();
		modify.read("C:/Users/Administrator/Desktop/tmp/mmu01200.xml",
				"C:/Users/Administrator/Desktop/tmp/mmu.xml");
	}

	public void read(String resourcePath, String detinationPath) {

		try {
			SAXReader reader = new SAXReader();
			File xmlFile = new File(resourcePath);
			if (!xmlFile.exists()) {
				System.out.println("文件不存在！");
				return;
			}
			InputStreamReader in = new InputStreamReader(new FileInputStream(
					xmlFile), "utf-8");
			// InputStream in =
			// XmlModify.class.getClassLoader().getResourceAsStream(resourcePath);
			Document doc = reader.read(in);
			Element root = doc.getRootElement();
			this.modifyByName(root, "listOfModifiers");
			this.replace(root);
			this.write(doc, detinationPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 查找标签名为name的元素的子元素节点集合
	 * 
	 * @param element
	 *            开始元素节点
	 * @param name
	 *            标签名称
	 * @return name标签的子元素
	 */
	public Element findByName(List<Element> nodes, String name) {
		Element el = null;
		for (Element e : nodes) {
			if (e.getName().equals(name)) {
				el = e;
			}
		}
		return el;
	}

	public void modifyByName(Element element, String name) {
		List<Element> children = element.elements();
		for (Element e : children) {
			if (e.getName().equals(name)) {
				List<Element> subChildren = e.elements();
				Element subE = this.compare(subChildren);
				for (Element ee : subChildren) {
					if (ee != subE)
						e.remove(ee);
				}
				Element parentE = e.getParent();
				parentE.setAttributeValue("name",
						subE.attributeValue("species"));
				// System.out.println(subE.attributeValue("species"));
				// System.out.println(parentE.attributeValue("name"));
				parentE.remove(e);
				// System.out.println(parentE.elements().size());
			} else {
				modifyByName(e, name);
			}
		}
	}

	public void replace(Element root) {
		Element modelElement = (Element) root.elements().get(0);
		List<Element> modelChildren = modelElement.elements();
		List<Element> spNodes = findByName(modelChildren, "listOfSpecies")
				.elements();
		List<Element> reNodes = this.findByName(modelChildren,
				"listOfReactions").elements();
		for (Element sp : spNodes) {
			for (Element re : reNodes) {
				String id = sp.attributeValue("id");
				if (id.equals(re.attributeValue("name"))) {
					sp.setAttributeValue("id", id + "_ics111");
					System.out.println(sp.attributeValue("id"));
				}

			}
		}

	}

	public Element compare(List<Element> nodes) {
		Element ele = null;
		List<String> ids = new ArrayList<String>();

		// 遍历元素取出id值
		for (Element e : nodes) {
			ids.add(e.attributeValue("id"));
			// System.out.println(e.getName() + "||" + e.attributeValue("id"));
		}
		String min = this.minLengthString(ids);
		// System.out.println(min);
		for (Element e : nodes) {
			if (e.attributeValue("id").equals(min))
				ele = e;

		}
		return ele;
	}

	public String minLengthString(List<String> lists) {
		String min = lists.get(0);
		for (int i = 0; i < lists.size(); i++) {
			for (int j = i + 1; j < lists.size(); j++) {
				if (lists.get(i).length() <= lists.get(j).length()) {
					min = lists.get(i);
				} else {
					min = lists.get(j);
				}
			}
		}
		return min;
	}

	public Element findById(List<Element> nodes, String id) {
		Element result = null;
		for (Element e : nodes) {
			if (e.attributeValue("id").equals(id)) {
				result = e;
			}
		}
		return result;
	}

	public void write(Document doc, String filePath) {
		try {
			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			XMLWriter out = new XMLWriter(new OutputStreamWriter(
					new FileOutputStream(filePath), "utf-8"));
			// XMLWriter out = new XMLWriter(new FileWriter(file));
			out.write(doc);
			out.flush();
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
