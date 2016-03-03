package mx.daro.himnario.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;

public class HimnarioHelper {

	private static final Map<String, JSONObject> cache = new HashMap<String, JSONObject>();
	
	private static Context context;
	
	public HimnarioHelper(){}
	
	public HimnarioHelper(Context context){
		HimnarioHelper.context = context;
	}
	
	private static String getFinalPath(String tipo, String version, Integer numero){
		return "/himnario/"+version+"/"+tipo+"/"+numero+".mp3";
	}

	public static boolean existeHimno(String tipo, String version, Integer numero){
		String path = StorageUtils.getPathStorage(context)+getFinalPath(tipo, version, numero);
		File f = new File(path);
		return f.exists();
	}

	public static void verificaDirectiorioAnterior(){
		SharedPreferences settings = context.getSharedPreferences(Constants.NOMBRE_PREFERENCIAS, 0);
		Boolean dirAnteriorMovido = settings.getBoolean(Constants.DIR_ANTERIOR_MOVIDO, false);
		System.out.println("Verificando directorio anterior.");
		if(!dirAnteriorMovido){
			String pathAnterior = StorageUtils.getPathPrimaryAnterior()+"/himnario";
			String pathNuevo = StorageUtils.getPathPrimary()+"/himnario";
			File dAnterior = new File(pathAnterior);
			File dNuevo = new File(pathNuevo);
			if(dAnterior.exists() && !dNuevo.exists()){
				System.out.println("Directorio anterior existe, se movera.");
				dAnterior.renameTo(dNuevo);
				System.out.println("Directorio anterior movido.");
			}
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(Constants.DIR_ANTERIOR_MOVIDO,true);
			editor.commit();
		}
	}
	
	public static void eliminaHimno(String tipo, String version, Integer numero){
		String path = StorageUtils.getPathStorage(context)+getFinalPath(tipo, version, numero);
		File f = new File(path);
		if(f.exists()){
			f.delete();
		}
	}
	
	public static String getPathHimno(String tipo, String version, Integer numero){
		return StorageUtils.getPathStorage(context)+getFinalPath(tipo, version, numero);
	}

    private static JSONObject getHimnarioBusqueda(Context context, String version){
    	JSONObject himnario = cache.get(version);
		if(himnario==null){
			himnario = HimnarioReader.getHimnarioBusqueda(context, version);
			cache.put(version, himnario);
		}
		return himnario;
    }
    
    private static JSONObject getHimnarioTitulos(Context context, String version){
    	String key = "t"+version;
    	JSONObject titulos = cache.get(key);
    	if(titulos==null){
			titulos = HimnarioReader.getListaTitulos(context, version);
			cache.put(key, titulos);
		}
		return titulos;
    }
    
    public static ArrayList<JSONObject> getListaTitulos(Context context, String version){
    	ArrayList<JSONObject> lista = new ArrayList<JSONObject>();
    	JSONObject titulos = getHimnarioTitulos(context, version);
		for(int i=1;i<=Constants.maximos.get(version);i++){
			try {
				lista.add(titulos.getJSONObject(version+"_"+i));
			} catch (JSONException e) {}
	    }
		return lista;
    }
    
    public static JSONObject getHimnoTitulo(Context context, String version, Integer numero){
		try {
			if(Constants.maximos.get(version)>=numero){
				return getHimnarioTitulos(context, version).getJSONObject(version+"_"+numero);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
    }
    
    public static JSONObject getHimnoJSON(Context context, String version, Integer numero){
    	String key = version+"_"+numero;
    	JSONObject himno = cache.get(key);
		if(himno==null){
			himno = HimnarioReader.getHimnoFromFile(context, version, numero);
			if(himno!=null){
				try {
					himno.put("n", numero);
				} catch (JSONException e) {}
			}
			cache.put(key, himno);
		}
		return himno;
    }
    
	public static ArrayList<JSONObject> getListaHimnosFiltrados(Context context, String version, String filtro){
		JSONObject himnario = getHimnarioBusqueda(context, version);
		ArrayList<JSONObject> datos = new ArrayList<JSONObject>();
		try{
			if(filtro==null || filtro.equals("")){
				for(int i=1;i<=Constants.maximos.get(version);i++){
					datos.add(getHimnoJSON(context, version, i));
			    }
			}else{
				String filtroBuscable = Utils.preparaTextoParaBusqueda(filtro);
				Integer numeroFiltro = null;
				try{
					numeroFiltro = Integer.valueOf(filtro);
				}catch(Exception e){}

				for(int i=1;i<=Constants.maximos.get(version);i++){
					if(numeroFiltro!=null){
						String numero = i+"";
						if(numero.contains(filtro)){
							datos.add(getHimnoJSON(context, version, i));
						}
					}else{
						JSONObject himnoBuscable = himnario.getJSONObject(version+"_"+i);
						String titulo = himnoBuscable.getString("t");
						if(titulo.contains(filtroBuscable)){
							datos.add(getHimnoJSON(context, version, i));
						}else{
							String texto = himnoBuscable.getString("x");
							if(texto.contains(filtroBuscable)){
								datos.add(getHimnoJSON(context, version, i));
							}
						}
					}
			    }
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return datos;
	}
}
