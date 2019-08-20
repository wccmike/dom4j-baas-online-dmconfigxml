package dom4j;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * help user location one directory and scan for all files of one keyword
 * 
 * @author wangchengchao
 *
 */
public class Searcher {

	/**
	 * the number of files in result
	 * 
	 */
	private int resultCount;

	/**
	 * location one directory,it is full path.
	 * 
	 */
	private String dir;

	/**
	 * keyword in filename which you wanna find
	 * 
	 */
	private String keyword;

	/**
	 * if true,use keyword like mode.if false,use accurate mode.default is true.
	 * 
	 */
	private boolean fuzzySearch = true;

	/**
	 * mem space that saves files which matches in a set
	 * 
	 */
	private Set<File> resultFiles;

	/**
	 * constructor
	 * 
	 * @param dir
	 *            location one directory,it is full path
	 * @param keyword
	 *            keyword in filename which you wanna find
	 */
	public Searcher(String dir, String keyword) {
		super();
		this.dir = dir;
		this.keyword = keyword;
	}

	/**
	 * constructor
	 * 
	 * @param dir
	 *            location one directory,it is full path.
	 * @param keyword
	 *            keyword in filename which you wanna find
	 * @param fuzzySearch
	 *            if true,use keyword like mode.if false,use accurate
	 *            mode.default is true.
	 */
	public Searcher(String dir, String keyword, boolean fuzzySearch) {
		super();
		this.dir = dir;
		this.keyword = keyword;
		this.fuzzySearch = fuzzySearch;
	}

	/**
	 * constructor
	 * 
	 */
	public Searcher() {
		super();
	}

	/**
	 * modify configuration of searching
	 * 
	 * @param dir
	 *            location one directory,it is full path
	 * @param keyword
	 *            keyword in filename which you want to find
	 */
	public void modifyCfg(String dir, String keyword, boolean fuzzySearch) {
		this.dir = dir;
		this.keyword = keyword;
		this.fuzzySearch = fuzzySearch;
	}

	/**
	 * scan the path according to configurations,save matched files in an array,
	 * and memorize the result count
	 * 
	 * @return all matched files
	 */
	public void scan() {
		/* init and clear mem space */
		this.resultFiles = new HashSet<File>();
		this.resultCount = 0;
		/* if the mode is fuzzy search,need transferred meaning */
		if (fuzzySearch) {
			/* transferred meaning dealing for regex */
			String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
			for (String key : fbsArr) {
				if (this.keyword.contains(key)) {
					/* add one '\' before the special simbol */
					this.keyword = this.keyword.replace(key, "\\" + key);
				}
			}
		}
		recurseEnterDirScan(this.dir, this.fuzzySearch);
//		System.out.println(this.resultCount + " files in result");
	}

	/**
	 * recursion which serves function "scan"
	 * 
	 * @param dir
	 *            directory path
	 * @param fuzzySearch
	 *            what mode
	 */
	private void recurseEnterDirScan(String dir, boolean fuzzySearch) {
		File dirPath = new File(dir);
		File[] files = dirPath.listFiles();
		for (File file : files) {
			/* when the current file is a directory */
			if (file.isDirectory()) {
				String path = file.getPath();
				recurseEnterDirScan(path, fuzzySearch);
			} else {
				String filename = file.getName();
				/* fuzzy search mode */
				if (fuzzySearch && filename.matches(".*" + this.keyword + ".*")) {
					this.resultFiles.add(file);
					this.resultCount++;
				}
				/* accurate search mode */
				if (!fuzzySearch && filename.equals(this.keyword)) {
					this.resultFiles.add(file);
					this.resultCount++;
				}
			}
		}
	}

	/**
	 * output searching results
	 * 
	 * @return an array of files
	 */
	public File[] outputFiles() {
		/* convert hashset to array */
		int size = this.resultFiles.size();
		File[] files = new File[size];
		int num = 0;
		for (File file : this.resultFiles) {
			files[num++] = file;
		}
		return files;
	}

}
