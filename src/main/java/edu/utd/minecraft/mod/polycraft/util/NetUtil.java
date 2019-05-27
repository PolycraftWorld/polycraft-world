package edu.utd.minecraft.mod.polycraft.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import edu.utd.minecraft.mod.polycraft.privateproperty.ServerEnforcer;
import net.minecraft.client.Minecraft;

public class NetUtil {
	
	private static final AtomicInteger threadDownloadCounter = new AtomicInteger(0);
	
	public ThreadedNetUtil getNewThreadUtil() {
		return this.new ThreadedNetUtil();
	}
	
	
	public class ThreadedNetUtil {
	
	private Thread PPThread;
	private Thread FriendsThread;
	
		public void getPrivateProperties(final String url, final boolean isMasterWorldURL) throws IOException {
			this.PPThread = new Thread("PrivateProperty Downloader #" + threadDownloadCounter.incrementAndGet())
	        {
	            private static final String __OBFID = "CL_00001050";
	            public void run()
	            {
	                HttpURLConnection httpurlconnection = null;
	                BufferedReader in = null;
	               // ThreadDownloadImageData.logger.debug("Downloading http texture from {} to {}", new Object[] {ThreadDownloadImageData.this.imageUrl, ThreadDownloadImageData.this.field_152434_e});
	
	                try
	                {
	                    httpurlconnection = (HttpURLConnection)(new URL(url)).openConnection();
	                    httpurlconnection.setDoInput(true);
	                    httpurlconnection.setDoOutput(false);
	                    httpurlconnection.connect();
	
	                    if (httpurlconnection.getResponseCode() / 100 == 2)
	                    {
	                        //Successful Response!
	                    	
	                    	in = new BufferedReader(
	                				new InputStreamReader(
	                						httpurlconnection.getInputStream()));
	
	                		StringBuilder response = new StringBuilder();
	                		String inputLine;
	
	                		while ((inputLine = in.readLine()) != null)
	                			response.append(inputLine);
	                		
	                		in.close();
	                		
	                		ServerEnforcer.INSTANCE.sendPrivateProperties(response.toString(), isMasterWorldURL);
	                        System.out.println("Success!");
	                        return;
	                    }
	                }
	                catch (Exception exception)
	                {
	                    //ThreadDownloadImageData.logger.error("Couldn\'t download http texture", exception);
	                    return;
	                }
	                finally
	                {
	                	
	                    if (httpurlconnection != null)
	                    {
	                        httpurlconnection.disconnect();
	                    }
	                }
	            }
	        };
	        this.PPThread.setDaemon(true);
	        this.PPThread.start();
		}
	
	}
	
	public static String getText(String url) throws IOException {
		URL website = new URL(url);
		URLConnection connection = website.openConnection();
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
						connection.getInputStream()));

		StringBuilder response = new StringBuilder();
		String inputLine;

		while ((inputLine = in.readLine()) != null)
			response.append(inputLine);

		in.close();

		return response.toString();
	}

	public static String postInventory(final String url, final String message) throws IOException {
		final HttpClient httpclient = HttpClients.createDefault();
		final HttpPost httpPost = new HttpPost(url);
		if (message != null) {

			httpPost.setEntity(new ByteArrayEntity(message.getBytes("UTF-8")));
		}
		final HttpResponse response = httpclient.execute(httpPost);
		if (response.getStatusLine().toString().contains("200 OK"))
		{
			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				final InputStream instream = entity.getContent();
				try {
					return IOUtils.toString(instream);
				} finally {
					instream.close();
				}
			}
		}
		return null;
	}

	public static String post(final String url, final Map<String, String> params) throws IOException {
		final HttpClient httpclient = HttpClients.createDefault();
		final HttpPost httpPost = new HttpPost(url);
		if (params != null) {
			final List<NameValuePair> paramPairs = new ArrayList<NameValuePair>(2);
			for (final Entry<String, String> param : params.entrySet()) {
				paramPairs.add(new BasicNameValuePair(param.getKey(), param.getValue()));
			}

			httpPost.setEntity(new UrlEncodedFormEntity(paramPairs, "UTF-8"));
		}
		final HttpResponse response = httpclient.execute(httpPost);
		final HttpEntity entity = response.getEntity();
		if (entity != null) {
			final InputStream instream = entity.getContent();
			try {
				return IOUtils.toString(instream);
			} finally {
				instream.close();
			}
		}
		return null;
	}
}