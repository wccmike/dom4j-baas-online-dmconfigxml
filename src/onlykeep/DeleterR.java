package onlykeep;

import java.io.File;

public class DeleterR {
	/**
	 * location one directory,it is full path.
	 * 
	 */
	private String dir;

	/**
	 * regex in filename which you wanna delete
	 * 
	 */
	private String regex;

	private boolean keepMatched;

	/**
	 * @param dir
	 * @param regex
	 * @param keepMatched
	 */
	public DeleterR(String dir, String regex, boolean keepMatched) {
		super();
		this.dir = dir;
		this.regex = regex;
		this.keepMatched = keepMatched;
	}

	public void delete() {
		recurseEnterDirScan(this.dir);
	}

	private void recurseEnterDirScan(String dir) {

		File dirPath = new File(dir);
		File[] files = dirPath.listFiles();
		for (File file : files) {
			/* when the current file is a directory */
			if (file.isDirectory()) {
				String path = file.getPath();
				recurseEnterDirScan(path);
			} else {
				String filename = file.getName();
				if (this.keepMatched) {
					if (!filename.matches(this.regex)) {
						file.delete();
					}
				} else {
					if (filename.matches(this.regex)) {
						file.delete();
					}
				}
			}
		}

	}
}
