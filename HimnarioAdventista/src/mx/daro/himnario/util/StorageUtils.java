package mx.daro.himnario.util;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.StatFs;

public class StorageUtils {

    private static boolean externalStorageAvailable, externalStorageWriteable;

    private static void checkStoragePrimary() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            externalStorageAvailable = externalStorageWriteable = true;
        } else if (state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)) {
            externalStorageAvailable = true;
            externalStorageWriteable = false;
        } else {
            externalStorageAvailable = externalStorageWriteable = false;
        }
    }

    public static boolean isExternalStoragePrimaryAvailable() {
    	checkStoragePrimary();
        return externalStorageAvailable;
    }

    public static boolean isExternalStoragePrimaryWriteable() {
    	checkStoragePrimary();
        return externalStorageWriteable;
    }

    public static boolean isExternalStoragePrimaryAvailableAndWriteable() {
    	checkStoragePrimary();
        if (!externalStorageAvailable) {
            return false;
        } else if (!externalStorageWriteable) {
            return false;
        } else {
            return true;
        }
    }
    
    public static String getPathPrimary(){
    	return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    public static String getPathPrimaryAnterior(){
    	return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static boolean isSecundaryAvailable(String path) {
    	if(StorageUtils.megabytesAvailable(new File(path))>0){
    		return true;
    	}
    	return false;
    }
    
    public static String getPathStorage(Context context){
    	if(context!=null){
	    	SharedPreferences settings = context.getSharedPreferences(Constants.NOMBRE_PREFERENCIAS, 0);
			return settings.getString(Constants.PREF_PATH_DESCARGAS, getPathPrimary());
    	}
    	return getPathPrimary();
    }
    
    public static boolean isMemoriaExternaDisponible(){
    	List<String> secundarios = StorageUtils.getSecundaryStorage();
		secundarios.add(StorageUtils.getPathPrimary());
		List<String> todos = new ArrayList<String>();
		for(String p: secundarios){
			boolean disponible = false;
			if(p.equals(StorageUtils.getPathPrimary())){
				disponible = isExternalStoragePrimaryAvailableAndWriteable();
			}else{
				disponible = isSecundaryAvailable(p);
			}
			if(disponible){
				todos.add(p);
			}
		}
		if(todos.size()>0){
			return true;
		}
		return false;
    }
    
    public static List<String> getPathMemoriasExternasDisponibles(){
    	List<String> todos = new LinkedList<String>();
    	List<String> secundarios = StorageUtils.getSecundaryStorage();
    	if(isExternalStoragePrimaryAvailableAndWriteable()){
    		todos.add(getPathPrimary());
    	}
    	if(secundarios.size()>0){
    		todos.addAll(secundarios);
    	}
		return todos;
    }

    
	@SuppressWarnings("deprecation")
	public static float megabytesAvailable(File f) {
		try{
			StatFs stat = new StatFs(f.getPath());
			long bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
			return bytesAvailable / (1024.f * 1024.f);
		}catch(Exception e){}
		return -1;
	}
	
	public static String espacioDisponible(Context context) {
		String msg = "";
		if(isMemoriaExternaDisponible()){
			String path = getPathStorage(context);
			float mb = megabytesAvailable(new File(path));
			if(mb>0){
				msg = "Espacio disponible:\n"+Constants.df.format(mb)+" MB.";
			}
			if(mb==-1){
				msg = "Espacio disponible:\nDESCONOCIDO";
			}
		}else{
			msg = "NO HAY MEMORIA EXTERNA\n\nDebes agregar una para poder descargar los himnos.";
		}
		return msg;
	}
	
	public static List<String> getSecundaryStorage(){
		List<String> rv = new ArrayList<String>();
		try{
			String secondaryStorages = System.getenv("SECONDARY_STORAGE");
			if(secondaryStorages!=null && !secondaryStorages.equals("")){
				final String[] rawSecondaryStorages = secondaryStorages.split(File.pathSeparator);
				for(String s: rawSecondaryStorages){
					if(isSecundaryAvailable(s)){
						rv.add(s);
					}
				}
		        
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return rv;
	}
	
	public static void definePathStorageDefault(Context context){
		SharedPreferences settings = context.getSharedPreferences(Constants.NOMBRE_PREFERENCIAS, 0);
		String path = settings.getString(Constants.PREF_PATH_DESCARGAS, "");
		if(path==null || path.equals("")){
			List<String> lista = getPathMemoriasExternasDisponibles();
			if(lista.size()>0){
				guardaPathDescarga(context, lista.get(0));
			}else{
				guardaPathDescarga(context, getPathPrimary());
			}
		}else{
			if(!isMemoriaExternaDisponible()){
				path = getPathPrimary();
				guardaPathDescarga(context, path);
			}else{
				if(!path.equals(getPathPrimary()) && !isSecundaryAvailable(path)){
					guardaPathDescarga(context, getPathPrimary());
				}
			}
		}
	}
	
	public static void guardaPathDescarga(Context context, String path){
		SharedPreferences settings = context.getSharedPreferences(Constants.NOMBRE_PREFERENCIAS, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Constants.PREF_PATH_DESCARGAS, path);
		editor.commit();
	}
}
