package com.web.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Properties;

public class DownloadFile {
	public String downloadFile(String fileLink, String fileFolderSave, String fileName) throws IOException {
		
		URL website;
		ReadableByteChannel rbc;
		
		String localPath = fileFolderSave + File.separator  + fileName;
		ClassLoader classLoader = getClass().getClassLoader();
		Properties prop = new Properties();
//		InputStream input = null;
//
//		input = new FileInputStream(new File(classLoader.getResource("tris.properties").getFile()));
		
		//InputStream input = classLoader.getResourceAsStream("tris.properties");

		//prop.load(input);
		prop.load(new FileInputStream("C:\\tris.properties"));
		
		Authenticator.setDefault(new MyAuthenticator(prop));
		
		website = new URL(fileLink);
		rbc = Channels.newChannel(website.openStream());
		FileOutputStream fos = new FileOutputStream(localPath);
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
		
		fos.close();
		
		return localPath;
	}
}
