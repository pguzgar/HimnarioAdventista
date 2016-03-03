package mx.daro.himnario.activity.himno;

import mx.daro.himnario.FavoritosActivity;
import mx.daro.himnario.util.Constants;
import mx.daro.himnario.util.FavoritosUtil;
import mx.daro.himnario.util.MensajeUtil;
import android.content.Intent;

import com.actionbarsherlock.view.MenuItem;

public class HimnoActivityMenuOptions {

	public static Boolean procesaOpcionSeleccionada(final HimnoActivity instance, MenuItem item){
		final HimnoActivityReproductor reproductor = new HimnoActivityReproductor();
		final HimnoActivityFavoritos favoritos = new HimnoActivityFavoritos();
		
		if(item.getTitle().equals(Constants.TXT_OPCIONES)){
			instance.toggle();
			return true;
		}
		if(item.getTitle().equals(Constants.HIMNARIO_OPCION_SIGUIENTE)){
			instance.runOnUiThread(new Runnable() {
		        public void run() {
		        	Integer siguiente = HimnoActivity.himnoSeleccionado+1;
		        	if(Constants.maximos.get(HimnoActivity.versionSeleccionada)<siguiente){
		        		siguiente = Constants.maximos.get(HimnoActivity.versionSeleccionada);
		        	}
		        	reproductor.pararReproduccion(instance, null);
		        	instance.muestraHimno(HimnoActivity.versionSeleccionada, siguiente);
		        }
		    });
			return true;
		}
		if(item.getTitle().equals(Constants.HIMNARIO_OPCION_ANTERIOR)){
			instance.runOnUiThread(new Runnable() {
		        public void run() {
		        	Integer anterior = HimnoActivity.himnoSeleccionado-1;
		        	if(anterior<1){
		        		anterior=1;
		        	}
		        	reproductor.pararReproduccion(instance, null);
		        	instance.muestraHimno(HimnoActivity.versionSeleccionada, anterior);
		        }
		    });
			return true;
		}
		if(item.getTitle().equals(Constants.HIMNARIO_OPCION_AGREGAR_FAVORITOS)){
			instance.runOnUiThread(new Runnable() {
		        public void run() {
		        	favoritos.agregaFavorito(instance, HimnoActivity.versionSeleccionada, HimnoActivity.himnoSeleccionado);
		        	instance.setFavoriteIcon();
		        }
		    });
			return true;
		}
		if(item.getTitle().equals(Constants.HIMNARIO_OPCION_LISTA_FAVORITOS)){
			if(FavoritosUtil.getFavoritos(instance, HimnoActivity.versionSeleccionada).length()==0){
				MensajeUtil.mensaje(instance, "Favoritos", "Tu lista de favoritos est\u00E1 vac\u00EDa");
			}else{
				reproductor.pararReproduccion(instance, null);
				Intent intent = new Intent(instance, FavoritosActivity.class);
				intent.putExtra(Constants.VERSION_SELECCIONADA, HimnoActivity.versionSeleccionada);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				instance.startActivity(intent);
			}
			return true;
		}
	    switch (item.getItemId()) {
	        case android.R.id.home:
	        	instance.regresarAlMenu();
		        return true;
	    }
	    return false;
	}
}
