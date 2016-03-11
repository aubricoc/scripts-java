package scripts;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.canteratech.restclient.Request;
import com.canteratech.restclient.utils.IOUtils;

public class PlayVerifier {

	public static void main(String[] args) throws Exception {
		runVerification(args[0], args[1], args[2], args[3], args[4]);
	}

	public static void runVerification(String packageName, String version, String mailUser, String mailPass,
			String... notifyTo) throws Exception {
		System.out.print("Start verification of " + packageName + " version " + version + " ? [y/n]: ");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		String line = bufferedReader.readLine();
		while (!line.equals("y") && !line.equals("n")) {
			line = bufferedReader.readLine();
		}
		if (line.equals("n")) {
			return;
		}
		long millis = System.currentTimeMillis();
		boolean uploaded = false;
		while (!uploaded) {
			uploaded = verifyIsVersionUpload(packageName, version);
			if (!uploaded) {
				System.out.println("...");
				Thread.sleep(5 * 60 * 1000);
			}
		}
		millis = System.currentTimeMillis() - millis;
		long minutes = millis / 60000;
		System.out.println("UPLOADED!!!! After " + minutes + " minutes.");
		sendMail(mailUser, mailPass, notifyTo, packageName, version);
		System.out.println("Notified by mail!");
	}

	public static boolean verifyIsVersionUpload(String packageName, String version) throws Exception {
		String url = "https://play.google.com/store/apps/details?id=" + packageName;
		InputStream inputStream = Request.newInstance(url).getInputStream();
		String data = IOUtils.toString(inputStream);
		int indexOf = data.indexOf("itemprop=\"softwareVersion\"");
		data = data.substring(indexOf + 27);
		indexOf = data.indexOf("</div>");
		data = data.substring(0, indexOf);
		data = data.trim();
		return data.equals(version);
	}

	private static void sendMail(String username, String password, String[] to, String packageName, String version) {
		String subject = "Uploaded version " + version + " of " + packageName;
		String text = "Version " + version + " of " + packageName
				+ " is ready! https://play.google.com/store/apps/details?id=" + packageName;
		for (String t : to) {
			MailSender.sendMail(username, password, t, subject, text);
		}
	}
}
