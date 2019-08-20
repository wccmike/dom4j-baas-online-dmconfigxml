/**
 * 
 */
package dom4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * a reader which can read the content of one or several xml files
 * 
 * @author wangchengchao
 *
 */
public class XmlWccsParser {
	/**
	 * recurse count of function nicetry
	 * 
	 */
	private int count;
	/**
	 * element node structures which to be parsed. e.g.,[{"pmdata","prop"}]
	 * means rootnode's "pmdata"node's "prop"node
	 * 
	 */
	private List<String[]> toParseStructs;
	/**
	 * for saving output result
	 * 
	 */
	private List<List<String>> generalResults;
	/**
	 * if one data is recieved by one node's attribute value,set in this list
	 * e.g.,["name",null,null,"id"],null means get data from text instead of
	 * attribute
	 * 
	 */
	private List<String> toParseAttrNames;

	/**
	 * saxreader object is used for reading xml file
	 * 
	 */
	private SAXReader reader;
	/**
	 * a format string using mike's customized syntax. syntax rule : $number
	 * means which data in number index e.g.,$2 means the 2nd data added in
	 * addStructs function
	 * 
	 */
	private String format;
	/**
	 * according to format,take num order to save in a list
	 * 
	 */
	private List<Integer> outNumList;
	/**
	 * for filling the list named toParseAttrNames,mark last add index.
	 * 
	 */
	private int lastInsertIndex;

	/**
	 * core code using recursion.do the work of memorizing xml data which user
	 * wants
	 * 
	 * @param rootNode
	 *            current xml node element
	 * @param n
	 *            the index of data which user add in node structs list
	 */
	private void nicetry(Element rootNode, int n) {
		if (this.count < this.toParseStructs.get(n).length) {
			/* mark the depth of this recursion */
			this.count++;
			/*
			 * in case the struct of one of xml files is wrong according user's
			 * input
			 */
			if (null != rootNode.elements(this.toParseStructs.get(n)[this.count - 1])) {
				List<Element> propdefNodes = rootNode.elements(this.toParseStructs.get(n)[this.count - 1]);
				for (Element propdefNode : propdefNodes) {
					nicetry(propdefNode, n);

				}
			}
			/* mark the depth of this recursion,minus for quiting one depth */
			this.count--;

		} else if (this.count == this.toParseStructs.get(n).length) {
			/*
			 * because it is List<List>,internal list might be null which can't
			 * be gotten
			 */
			if (this.generalResults.size() < n + 1) {
				this.generalResults.add(new ArrayList<String>());
			}
			/* if current data needn't get from attribute */
			if (this.toParseAttrNames.get(n) == null) {
				this.generalResults.get(n).add(rootNode.getText());
			} else {
				this.generalResults.get(n).add(rootNode.attributeValue(this.toParseAttrNames.get(n)));
			}
		}
	}

	/**
	 * add input node structs in cfg
	 * 
	 * @param struct
	 *            from left to right is from parent to child
	 */
	public void addStructs(String[] struct) {
		this.toParseStructs.add(struct);
	}

	/**
	 * Initialize this parser
	 * 
	 */
	public void dependencyInject() {
		this.toParseStructs = new ArrayList<String[]>();
		this.generalResults = new ArrayList<List<String>>();
		this.toParseAttrNames = new ArrayList<String>();
		this.outNumList = new ArrayList<Integer>();
		this.reader = new SAXReader();
	}

	/**
	 * add input node attribute name which will be parsed in cfg
	 * 
	 * @param index
	 *            the index of the strutslist mapping this attr
	 * @param att
	 *            attribute name
	 */
	public void addAttr(int index, String att) {
		/* for filling the list named toParseAttrNames,mark last add index. */
		if (this.lastInsertIndex + 1 == index || index == 0) {

			this.toParseAttrNames.add(att);
		} else if (this.lastInsertIndex + 1 < index) {
			for (int i = 0; i < index - this.lastInsertIndex - 1; i++) {
				this.toParseAttrNames.add(null);
			}
		}
		this.lastInsertIndex = index;
	}

	/**
	 * parse one xml and print results in console
	 * 
	 * @param file
	 *            xml file
	 */
	public void parseNOut(File file) {
		/* clear output data */
		this.generalResults.clear();
		try {
			// 通过read方法读取一个文件 转换成Document对象
			Document document = this.reader.read(file);
			/* print full file path and name */
			String canonicalPath = file.getCanonicalPath();
			if (-1 == canonicalPath.indexOf("\\src") && -1 != canonicalPath.indexOf("\\target"))
				return;
			String tmpstr = canonicalPath.substring(0, canonicalPath.lastIndexOf("\\src"));
			String projectName = tmpstr.substring(tmpstr.lastIndexOf("\\") + 1);
//			System.out.println("\n" + canonicalPath);

			System.out.println("\n##" + projectName);
			// 获取根节点元素对象
			Element rootNode = document.getRootElement();
			for (int i = 0; i < this.toParseStructs.size(); i++) {
				this.nicetry(rootNode, i);
				this.count = 0;
			}
			/* in case of null result list */
			if (this.generalResults.size() > 0) {

				for (int j = 0; j < this.generalResults.get(0).size(); j++) {
					String resultLine = this.format;
					/*
					 * change format string to data result in user's costomized
					 * format
					 */
					for (int h = 0; h < this.generalResults.size(); h++) {
						int currentNum = this.outNumList.get(h);
						/* transferred meaning dealing for regex */
						String str = this.generalResults.get(currentNum).get(j);
						str=str.replaceAll("(?<!\\\\)\\\\(?!\\\\)", "\\\\\\\\");
						str=str.replace("$", "\\$");
						resultLine = resultLine.replaceFirst("\\$[0-9]+", str);
					}
					System.out.println(resultLine);
				}
			}
		} catch (DocumentException | IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * parse several xml and print results in console
	 * 
	 * @param files
	 *            an array of xml
	 */
	public void parseNOut(File[] files) {
		for (int i = 0; i < files.length; i++) {
			this.parseNOut(files[i]);
		}
	}

	/**
	 * set format string using mike's customized syntax. syntax rule : $number
	 * means which data in number index e.g.,$2 means the 2nd data added in
	 * addStructs function
	 * 
	 * @param format
	 *            mike's format string
	 */
	public void customizeOutFormat(String format) {
		this.outNumList.clear();
		this.format = format;
		Pattern pat = Pattern.compile("\\$([0-9]+)");
		Matcher mat = pat.matcher(format);
		while (mat.find()) {
			String num = mat.group(1);
			/* according to format,take num order to save in a list */
			this.outNumList.add(Integer.parseInt(num) - 1);
		}
	}

	/**
	 * clear memory
	 * 
	 */
	public void clearMem() {
		this.toParseStructs.clear();
		this.generalResults.clear();
		this.toParseAttrNames.clear();
		this.outNumList.clear();

	}
}
