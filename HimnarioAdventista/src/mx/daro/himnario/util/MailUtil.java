package mx.daro.himnario.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;


public class MailUtil extends AsyncTask<String, String, String> { 

	public void enviaCorreo(String mensaje){
		try{
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost("http://daro.mx/enviacorreo.php");
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("mensaje", mensaje));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			httpclient.execute(httppost);
		}catch(Exception e){
		}
	}
	
	
	@Override
	protected String doInBackground(String... arg0) {
		enviaCorreo(arg0[0]);
		return null;
	}
} 