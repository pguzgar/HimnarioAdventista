package mx.daro.himnario.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;


public class HimnarioReader {

	public static JSONObject getHimnarioBusqueda(Context context, String version){
		int id = context.getResources().getIdentifier("b"+version, "raw", context.getPackageName());
		return readJsonFromFile(context, id);
	}
	
	public static JSONObject getListaTitulos(Context context, String version){
		int id = context.getResources().getIdentifier("t"+version, "raw", context.getPackageName());
		return readJsonFromFile(context, id);
	}
	
	public static JSONObject getHimnoFromFile(Context context, String version, Integer numero) {
		try{
			int id = context.getResources().getIdentifier("h_"+version+"_"+numero, "raw", context.getPackageName());
			if(id==0){
				return null;
			}
			return readJsonFromFile(context, id);
		}catch(Exception e){};
		return null;
	}
	
	private static JSONObject readJsonFromFile(Context context, int id){
		InputStream inputStream = context.getResources().openRawResource(id);
		InputStreamReader inputreader = new InputStreamReader(inputStream);
		BufferedReader bufferedreader = new BufferedReader(inputreader);
		String line;
		StringBuilder sb = new StringBuilder();
		try {
			while ((line = bufferedreader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {}
		if(sb.length()>0){
			JSONObject jo = null;
			try {
				jo = new JSONObject(sb.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return jo;
		}
		return null;
	}

}
