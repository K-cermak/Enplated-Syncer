package com.karlosoft;

import java.io.File;
import java.io.IOException;

import java.net.*;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import com.karlosoft.gui.Popup;



public class Requests {
    //send zip file to url with token and secret
    public static void sendZip(String url, String token, String secret, String zipFile) throws MalformedURLException, IOException {
        url = url + "?deploy=true&token=" + token + "&secret=" + secret;

        //replace spaces with %20
        url = url.replace(" ", "%20");

        //file
		File file = new File(zipFile);

		CloseableHttpClient httpclient = HttpClientBuilder.create().build();         
		HttpPost httpPost = new HttpPost(url);

		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			builder.addPart("file", new FileBody(file));

		HttpEntity entity = builder.build();

		httpPost.setEntity(entity);

		CloseableHttpResponse response = httpclient.execute(httpPost);
		int responseCode = ((org.apache.http.HttpResponse) response).getStatusLine().getStatusCode();

		//response
		HttpEntity responseEntity = response.getEntity();
		String responseContent = EntityUtils.toString(responseEntity);

		if (responseCode == 200 && responseContent.contains("ok-done")) {
			Popup.showMessage(0, "Succesfully deployed", "Deployment succesfully completed.");
		} else {
			Popup.showMessage(1, "Error", "Deployment failed.\n\nError code: " + responseContent);
		}

		httpclient.close();
    }
}