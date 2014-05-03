package org.lance.itu.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

/**
 * ����.NETƽ̨��webservice ֻ��Ҫwsdl��ַ�� �Ӷ���֪�����ռ� soapActionһ���������ռ�+"/"+��������һ����Ҫwsdl��ַ
 * 
 * @author lance
 * 
 */
public class NetService {
	private static final String TAG = "NetService";

	public static String getJsonInfo(String url) {
		try {
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url);
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();
			InputStream is = entity.getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));
			String result = "";
			String temp = null;
			while ((temp = reader.readLine()) != null) {
				result += temp;
			}
			return result;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

}