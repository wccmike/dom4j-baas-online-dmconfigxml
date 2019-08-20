package dom4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

public class App {
	@Test
	public void date20190524() throws Exception{
		System.out.println("start");
		// long st = System.currentTimeMillis();
//		String dir = "C:/Users/q/Desktop";
		String dir = "D:\\1";
		Searcher searcher = new Searcher(dir, "dmconfig.xml", false);
		searcher.scan();
		File[] files = searcher.outputFiles();
		XmlWccsParser parser = new XmlWccsParser();
		parser.dependencyInject();
		String[] s1 = { "propdef", "properties", "propdefentry" };
		String[] s2 = { "propdef", "properties", "propdefentry", "defaultValue" };
		String[] s3 = { "propdef", "properties", "propdefentry", "propnames", "zh_CN" };
		parser.addStructs(s1);
		parser.addStructs(s2);
		parser.addStructs(s3);
		parser.addAttr(0, "name");
		parser.addAttr(1, null);
		parser.addAttr(2, null);
		parser.customizeOutFormat("#$3\n$1=$2");
		parser.parseNOut(files);
		// long et = System.currentTimeMillis();
		// System.out.println("app run using time: " + (et - st) + " ms");
		System.out.println("\r\n###### properties #####");
//		Searcher searcherP = new Searcher(dir, "peconfigdev.properties", false);
//		searcherP.scan();
//		File[] filesP = searcherP.outputFiles();
		
		ExtensionFileFinder extensionFileFinder = new ExtensionFileFinder(dir, "properties");
		List<File> scanNOutput = extensionFileFinder.scanNOutput();
		List<File>  seclist=scanNOutput.stream().filter(f->!f.getParent().endsWith("msg")).collect(Collectors.toList());
		File[] filesP = new File[seclist.size()];
		for (int i = 0; i < filesP.length; i++) {
			filesP[i]=seclist.get(i);
		}
		
		
		if (null != filesP && filesP.length > 0) {
			for (int i = 0; i < filesP.length; i++) {
				String p = filesP[i].getAbsolutePath();
				if(p.lastIndexOf("\\src")==-1){
					System.out.println("error");
				}
				p = p.substring(0, p.lastIndexOf("\\src"));
				String pn = p.substring(p.lastIndexOf("\\") + 1);
				System.out.println("##" + pn);
				try (BufferedReader br = new BufferedReader(new FileReader(filesP[i]))) {
					String l = null;
					while (null != (l = br.readLine())) {
						System.out.println(l);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println();
			}
		}
		System.out.println("\r\napp run no error");
	}

	@Test
	public void main2() throws DocumentException {
		// TODO Auto-generated method stub
		// 创建saxReader对象
		SAXReader reader = new SAXReader();
		// 通过read方法读取一个文件 转换成Document对象
		Document document = reader.read(new File("src/sms.xml"));
		// 获取根节点元素对象，即<dmconfig>
		Element rootNode = document.getRootElement();
		// 遍历所有的元素节点
		// 获取propdef元素对象
		List<Element> propdefNodes = rootNode.elements("propdef");
		for (Element propdefNode : propdefNodes) {
			// 获取propdef元素节点中，子节点为properties的元素节点
			List<Element> propertiesNodes = propdefNode.elements("properties");
			for (Element propertiesNode : propertiesNodes) {
				List<Element> propdefentryNodes = propertiesNode.elements("propdefentry");
				for (Element propdefentryNode : propdefentryNodes) {
					String strName = propdefentryNode.attributeValue("name");
					Element defaultValueNode = propdefentryNode.element("defaultValue");
					String strValue = defaultValueNode.getText();
					System.out.println(strName + "=" + strValue);
				}
			}

		}

	}

	@Test
	public void traverseAllFile() throws IOException {
		Pattern p = Pattern.compile(".*\\.jar.*");
		Matcher m = p.matcher("finished.jar");
		System.out.println(m.matches());
	}

	@Test
	public void mm() {
		long st = System.currentTimeMillis();
		Searcher searcher = new Searcher("D:\\workpla\\code", ".jar", true);
		searcher.scan();
		File[] files = searcher.outputFiles();
		XmlWccsParser parser = new XmlWccsParser();
		parser.dependencyInject();
		String[] s1 = { "propdef", "properties", "propdefentry" };
		String[] s2 = { "propdef", "properties", "propdefentry", "defaultValue" };
		parser.addStructs(s1);
		parser.addStructs(s2);
		parser.addAttr(0, "name");
		parser.addAttr(1, null);
		parser.customizeOutFormat("$1=$2");
		parser.parseNOut(files);
		long et = System.currentTimeMillis();
		System.out.println("app run using time: " + (et - st) + " ms");
	}

	@Test
	public void find() {
		Searcher searcher = new Searcher("D:\\workpla\\code", ".jar", true);
		searcher.scan();
		File[] files = searcher.outputFiles();
		for (int i = 0; i < files.length; i++) {
			try {
				System.out.println(files[i].getCanonicalPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void printAllFilename(String path) {
		File srcDic = new File(path);
		File[] files = srcDic.listFiles();
		for (File file : files) {
			String filename = file.getName();
			if (file.isDirectory()) {
				String path2 = file.getPath();
				printAllFilename(path2);
			} else if ("dmconfig.xml".equals(filename)) {

				String canonicalPath;
				try {
					canonicalPath = file.getCanonicalPath();
					System.out.println(canonicalPath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

	}

}
