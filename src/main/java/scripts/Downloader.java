package scripts;

import java.io.File;

import org.apache.commons.io.FileUtils;

import com.canteratech.restclient.Request;

public class Downloader {
	
	public static void main(String[] args) throws Exception {
		download(args[0], args[1]);
	}
	
	public static void download(String url, String file) throws Exception {
		byte[] bytes = Request.newInstance(url).getFile();
		FileUtils.writeByteArrayToFile(new File(file), bytes);
	}
}
