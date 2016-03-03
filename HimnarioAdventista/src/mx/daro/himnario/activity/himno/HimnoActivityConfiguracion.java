package mx.daro.himnario.activity.himno;

import mx.daro.himnario.R;
import mx.daro.himnario.util.Constants;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class HimnoActivityConfiguracion {

	public void aplicaTamanoTexto(HimnoActivity instance){
		SharedPreferences settings = instance.getSharedPreferences(Constants.NOMBRE_PREFERENCIAS, 0);
		Integer tamanoTexto = settings.getInt(Constants.PREF_TAMANO_TEXTO, 20);
		TextView tituloView = (TextView) instance.findViewById(R.id.tituloHimno);
		EditText et = (EditText) instance.findViewById(R.id.textoHimno);
		tituloView.setTextSize(tamanoTexto);
		et.setTextSize(tamanoTexto);
		SeekBar sb = (SeekBar) instance.findViewById(R.id.cambiadorTamano);
		sb.setProgress(tamanoTexto-5);
		TextView t = (TextView)instance.findViewById(R.id.tamanoTextoTitle);
		t.setText("Tama\u00F1o de texto: "+(tamanoTexto));
	}
	
	@SuppressWarnings("deprecation")
	public void aplicaColorDeFondo(HimnoActivity instance){
		SharedPreferences settings = instance.getSharedPreferences(Constants.NOMBRE_PREFERENCIAS, 0);
		Integer color = settings.getInt(Constants.PREF_COLOR_FONDO, 0);
		if(color==0){
			((RadioButton)instance.findViewById(R.id.radio0)).setChecked(true);
			String colorFondo = "#F0F0F0";
			String colorTexto = "#000000";
			String colorTextoBotones ="#409FFF";
			instance.findViewById(R.id.areaHimno).setBackgroundColor(Color.parseColor(colorFondo));
			instance.findViewById(R.id.areaMenuDescarga).setBackgroundColor(Color.parseColor(colorFondo));
			instance.findViewById(R.id.areaMenuDescargaGlobo).setBackgroundDrawable(instance.getResources().getDrawable(R.drawable.rounded0));
			instance.findViewById(R.id.flecha).setBackgroundDrawable(instance.getResources().getDrawable(R.drawable.triangulo0));
			instance.findViewById(R.id.areaReproductor).setBackgroundColor(Color.parseColor("#CCCCCC"));
			((TextView)instance.findViewById(R.id.tituloHimno)).setTextColor(Color.parseColor(colorTexto));
			((TextView)instance.findViewById(R.id.textoHimno)).setTextColor(Color.parseColor(colorTexto));
			instance.findViewById(R.id.areaBotones).setBackgroundDrawable(instance.getResources().getDrawable(R.drawable.border0));
			((TextView)instance.findViewById(R.id.botonTocarCantado)).setTextColor(Color.parseColor(colorTextoBotones));
			((TextView)instance.findViewById(R.id.botonTocarPista)).setTextColor(Color.parseColor(colorTextoBotones));
			((TextView)instance.findViewById(R.id.opcionDescargarYTocar)).setTextColor(Color.parseColor(colorTextoBotones));
			((TextView)instance.findViewById(R.id.opcionTocar)).setTextColor(Color.parseColor(colorTextoBotones));
			((TextView)instance.findViewById(R.id.separador)).setTextColor(Color.parseColor(colorTextoBotones));
			((ImageView)instance.findViewById(R.id.imagenBuscar)).setImageDrawable(instance.getResources().getDrawable(R.drawable.buscarazul));
		}else if(color==1){
			((RadioButton)instance.findViewById(R.id.radio1)).setChecked(true);
			String colorFondo = "#000000";
			String colorTexto = "#FFFFFF";
			String colorTextoBotones ="#CCCCCC";
			instance.findViewById(R.id.areaHimno).setBackgroundColor(Color.parseColor(colorFondo));
			instance.findViewById(R.id.areaMenuDescarga).setBackgroundColor(Color.parseColor(colorFondo));
			instance.findViewById(R.id.areaMenuDescargaGlobo).setBackgroundDrawable(instance.getResources().getDrawable(R.drawable.rounded1));
			instance.findViewById(R.id.flecha).setBackgroundDrawable(instance.getResources().getDrawable(R.drawable.triangulo1));
			instance.findViewById(R.id.areaReproductor).setBackgroundColor(Color.parseColor("#CCCCCC"));
			((TextView)instance.findViewById(R.id.tituloHimno)).setTextColor(Color.parseColor(colorTexto));
			((TextView)instance.findViewById(R.id.textoHimno)).setTextColor(Color.parseColor(colorTexto));
			instance.findViewById(R.id.areaBotones).setBackgroundDrawable(instance.getResources().getDrawable(R.drawable.border1));
			((TextView)instance.findViewById(R.id.botonTocarCantado)).setTextColor(Color.parseColor(colorTextoBotones));
			((TextView)instance.findViewById(R.id.botonTocarPista)).setTextColor(Color.parseColor(colorTextoBotones));
			((TextView)instance.findViewById(R.id.opcionDescargarYTocar)).setTextColor(Color.parseColor(colorTextoBotones));
			((TextView)instance.findViewById(R.id.opcionTocar)).setTextColor(Color.parseColor(colorTextoBotones));
			((TextView)instance.findViewById(R.id.separador)).setTextColor(Color.parseColor(colorTextoBotones));
			((ImageView)instance.findViewById(R.id.imagenBuscar)).setImageDrawable(instance.getResources().getDrawable(R.drawable.buscargris));
		}else if(color==2){
			((RadioButton)instance.findViewById(R.id.radio2)).setChecked(true);
			String colorFondo = "#EDDADD";
			String colorTexto = "#000000";
			String colorTextoBotones ="#40030C";
			instance.findViewById(R.id.areaHimno).setBackgroundColor(Color.parseColor(colorFondo));
			instance.findViewById(R.id.areaMenuDescarga).setBackgroundColor(Color.parseColor(colorFondo));
			instance.findViewById(R.id.areaMenuDescargaGlobo).setBackgroundDrawable(instance.getResources().getDrawable(R.drawable.rounded2));
			instance.findViewById(R.id.flecha).setBackgroundDrawable(instance.getResources().getDrawable(R.drawable.triangulo2));
			instance.findViewById(R.id.areaReproductor).setBackgroundColor(Color.parseColor("#CCA7AD"));
			((TextView)instance.findViewById(R.id.tituloHimno)).setTextColor(Color.parseColor(colorTexto));
			((TextView)instance.findViewById(R.id.textoHimno)).setTextColor(Color.parseColor(colorTexto));
			instance.findViewById(R.id.areaBotones).setBackgroundDrawable(instance.getResources().getDrawable(R.drawable.border2));
			((TextView)instance.findViewById(R.id.botonTocarCantado)).setTextColor(Color.parseColor(colorTextoBotones));
			((TextView)instance.findViewById(R.id.botonTocarPista)).setTextColor(Color.parseColor(colorTextoBotones));
			((TextView)instance.findViewById(R.id.opcionDescargarYTocar)).setTextColor(Color.parseColor(colorTextoBotones));
			((TextView)instance.findViewById(R.id.opcionTocar)).setTextColor(Color.parseColor(colorTextoBotones));
			((TextView)instance.findViewById(R.id.separador)).setTextColor(Color.parseColor(colorTextoBotones));
			((ImageView)instance.findViewById(R.id.imagenBuscar)).setImageDrawable(instance.getResources().getDrawable(R.drawable.buscarrosa));
		}
	}
	
	public void aplicaPreferenciasDelUsuario(HimnoActivity instance){
		aplicaTamanoTexto(instance);
		aplicaColorDeFondo(instance);
	}

	public void setColorFondo(HimnoActivity instance, Integer index){
		SharedPreferences settings = instance.getSharedPreferences(Constants.NOMBRE_PREFERENCIAS, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(Constants.PREF_COLOR_FONDO, index);
		editor.commit();
	}

	
	public int getHimnoGuardado(HimnoActivity instance){
		SharedPreferences settings = instance.getSharedPreferences(Constants.NOMBRE_PREFERENCIAS, 0);
		return settings.getInt(Constants.PREF_HIMNO_GUARDADO, -1);
	}
	
	public void guardaNumeroHimno(HimnoActivity instance, int numero){
		SharedPreferences settings = instance.getSharedPreferences(Constants.NOMBRE_PREFERENCIAS, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(Constants.PREF_HIMNO_GUARDADO, numero);
		editor.commit();
	}
	
	public void guardaVersionHimno(HimnoActivity instance, String version){
		SharedPreferences settings = instance.getSharedPreferences(Constants.NOMBRE_PREFERENCIAS, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Constants.PREF_VERSION_GUARDADA, version);
		editor.commit();
	}
	
	public String getVersionGuardada(HimnoActivity instance){
		SharedPreferences settings = instance.getSharedPreferences(Constants.NOMBRE_PREFERENCIAS, 0);
		return settings.getString(Constants.PREF_VERSION_GUARDADA, Constants.VERSION_2009);
	}

}
