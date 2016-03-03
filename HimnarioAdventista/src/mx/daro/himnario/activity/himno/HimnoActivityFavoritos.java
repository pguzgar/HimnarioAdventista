package mx.daro.himnario.activity.himno;

import mx.daro.himnario.util.FavoritosUtil;
import mx.daro.himnario.util.MensajeUtil;

public class HimnoActivityFavoritos {

	public void agregaFavorito(HimnoActivity instance, String version, int numero){
		if(!FavoritosUtil.isFavorito(instance, version, numero)){
			FavoritosUtil.guardaFavoritos(instance, version, numero);
			MensajeUtil.mensaje(instance, "Favoritos", "El himno se agreg\u00F3 a tu lista.");
		}
	}


}
