package edu.utd.minecraft.mod.polycraft.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class NetUtil {
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