package dom4j;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ExtensionFileFinder {
	private String dir;
	private String extension;
	private Set<File> set = new HashSet<File>();

	/**
	 * @param dir
	 * @param extension
	 *            extension without dot
	 */
	public ExtensionFileFinder(String dir, String extension) {
		this.dir = dir;
		this.extension = extension;
	}

	public List<File> scanNOutput() throws IOException {
		List<File> result = null;
		File file = new File(dir);
		if (null == file || !file.exists() || !file.isDirectory())
			throw new IOException("invalid dir");
		recurseDirTree(file);
		if (!this.set.isEmpty()) {
			result = new LinkedList<File>(this.set);
		}
		return result;
	}

	private void recurseDirTree(File f) {
		File[] fs = f.listFiles();
		for (int i = 0; i < fs.length; i++) {
			if (fs[i].isDirectory())
				recurseDirTree(fs[i]);
			else if (fs[i].getName().endsWith("." + this.extension))
				this.set.add(fs[i]);
		}
	}

}
