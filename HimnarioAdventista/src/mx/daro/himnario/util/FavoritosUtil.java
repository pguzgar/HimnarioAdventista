package mx.daro.himnario.util;

import mx.daro.himnario.FavoritosActivity;
import mx.daro.himnario.R;
import mx.daro.himnario.activity.himno.HimnoActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.SharedPreferences;

import com.actionbarsherlock.view.Menu;

public class FavoritosUtil {
	
	public static Boolean isFavorito(Activity instance, String version, Integer numero){
		JSONArray favoritos = FavoritosUtil.getFavoritos(instance, version);
		for(int i=0; i<favoritos.length(); i++){
			try {
				if(favoritos.getJSONObject(i).getInt("numero")==numero.intValue()){
					return true;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static JSONArray getFavoritos(Activity instance, String version){
		SharedPreferences settings = instance.getSharedPreferences(Constants.NOMBRE_PREFERENCIAS, 0);
		String favoritosStringDB = settings.getString(Constants.FAVORITOS_DB+"_"+version, "[]");
		JSONArray db = null;
		try {
			db = new JSONArray(favoritosStringDB);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return db;
	}
	
	public static void guardaFavoritos(HimnoActivity instance, String version, Integer numero){
		JSONArray favoritos = FavoritosUtil.getFavoritos(instance, version);
		JSONObject nuevoFavorito = new JSONObject();
		try{
			nuevoFavorito.put("numero", numero);
		}catch(Exception e){
			e.printStackTrace();
		}
		favoritos.put(nuevoFavorito);
		guardarObjeto(instance, version, favoritos);
	}
	
	public static void eliminaFavoritos(FavoritosActivity instance, String version, Integer numero){
		JSONArray favoritosFinal = eliminar(instance, version, numero);
		guardarObjeto(instance, version, favoritosFinal);
	}
	
	public static void subirFavoritos(FavoritosActivity instance, String version, Integer numero, Integer index){
		moverFavorito(instance, version, numero, --index);
	}
	
	public static void bajarFavoritos(FavoritosActivity instance, String version, Integer numero, Integer index){
		moverFavorito(instance, version, numero, ++index);
	}
	
	public static void moverFavorito(FavoritosActivity instance, String version, Integer numero, Integer indexFinal){
		JSONArray favoritos = eliminar(instance, version, numero);
		JSONArray favoritosFinal = new JSONArray();
		JSONObject nuevoFavorito = new JSONObject();
		try{
			nuevoFavorito.put("numero", numero);
			if(indexFinal<0) indexFinal=0;
			if(indexFinal>favoritos.length()) indexFinal=favoritos.length();
			for(int i=0; i<favoritos.length(); i++){
				if(i==indexFinal){
					favoritosFinal.put(nuevoFavorito);
				}
				favoritosFinal.put(favoritos.get(i));
			}
			if(indexFinal == favoritos.length()) favoritosFinal.put(nuevoFavorito);
			guardarObjeto(instance, version, favoritosFinal);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static JSONArray eliminar(Activity instance, String version, Integer numero){
		JSONArray favoritos = FavoritosUtil.getFavoritos(instance, version);
		try {
			JSONArray newFavoritos = new JSONArray("[]");
			for(int i=0; i<favoritos.length(); i++){
				try {
					if(favoritos.getJSONObject(i).getInt("numero")==numero.intValue()){
						continue;
					}
					newFavoritos.put(favoritos.getJSONObject(i));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			favoritos = newFavoritos;
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return favoritos;
	}

	public static void guardarObjeto(Activity instance, String version, JSONArray favoritos){
		SharedPreferences settings = instance.getSharedPreferences(Constants.NOMBRE_PREFERENCIAS, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Constants.FAVORITOS_DB+"_"+version, favoritos.toString());
		editor.commit();
	}
	
	public static void cambiaIcono(Menu menuActionBar, Boolean favorito){
		if(favorito) menuActionBar.getItem(1).setIcon(R.drawable.favoritos);
		else  menuActionBar.getItem(1).setIcon(R.drawable.favoritos_no);
	}
	
}
