package onlykeep;

import dom4j.Searcher;

public class App {

	public static void main(String[] args) {
		DeleterR deleterR = new DeleterR("D:/1/pe-gyns-wxbank", "pom\\.xml", true);
		deleterR.delete();
		System.out.println("finish");
	}

}
