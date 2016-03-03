package mx.daro.himnario.activity.himno;

import mx.daro.himnario.AcercaActivity;
import mx.daro.himnario.AgradecimientosActivity;
import mx.daro.himnario.DescargaMainActivity;
import mx.daro.himnario.R;
import mx.daro.himnario.util.Constants;
import mx.daro.himnario.util.MensajeUtil;
import mx.daro.himnario.util.Utils;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class HimnoActivityPantallaConfiguracion {

	public void inicializaConfiguracionSlider(HimnoActivity instance){
		instance.setBehindContentView(R.layout.activity_opciones);
		SlidingMenu menu = instance.getSlidingMenu();
		menu.setMode(SlidingMenu.RIGHT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.drawable.shadow);
		menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		menu.setFadeDegree(0.35f);
	}

	public void configuraTamanoSlider(HimnoActivity instance){
		DisplayMetrics metrics = new DisplayMetrics();
		instance.getWindowManager().getDefaultDisplay().getMetrics(metrics);
		HimnoActivity.widthPantalla = metrics.widthPixels;
		HimnoActivity.heightPantalla = metrics.heightPixels;
		HimnoActivity.pppWidth = metrics.xdpi;
		HimnoActivity.pppHeight = metrics.ydpi;
		
		HimnoActivity.widthMaximoOpciones = (int)(HimnoActivity.pppWidth * Constants.medidaLayoutOpciones);
		if(HimnoActivity.widthMaximoOpciones>(HimnoActivity.widthPantalla * Constants.medidaMaximaOpcionesPorcentajePantalla)){
			HimnoActivity.widthMaximoOpciones = (int)(HimnoActivity.widthPantalla * Constants.medidaMaximaOpcionesPorcentajePantalla);
		}
		TypedValue tv = new TypedValue();
		if (instance.getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)){
			int h = TypedValue.complexToDimensionPixelSize(tv.data,instance.getResources().getDisplayMetrics());
			instance.findViewById(R.id.headerOpciones).getLayoutParams().height=h;
			instance.findViewById(R.id.headerOpciones).getLayoutParams().width=(int)(h*600/50);
		}
		instance.getSlidingMenu().setShadowWidth(2);
		instance.getSlidingMenu().setBehindWidth(HimnoActivity.widthMaximoOpciones);
	}
	

	public void configuracionPantallaOpciones(final HimnoActivity instance){
		final HimnoActivityConfiguracion config = new HimnoActivityConfiguracion();
		RadioGroup rg = (RadioGroup)instance.findViewById(R.id.radioGroup1);
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId){
				case R.id.radio0: config.setColorFondo(instance, 0); break;
				case R.id.radio1: config.setColorFondo(instance, 1); break;
				case R.id.radio2: config.setColorFondo(instance, 2); break;
				}
				config.aplicaColorDeFondo(instance);
			}
		});

		SeekBar sb = (SeekBar) instance.findViewById(R.id.cambiadorTamano);
		sb.setMax(95);
		sb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				SharedPreferences settings = instance.getSharedPreferences(Constants.NOMBRE_PREFERENCIAS, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt(Constants.PREF_TAMANO_TEXTO, seekBar.getProgress()+5);
				editor.commit();
				config.aplicaTamanoTexto(instance);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) { }
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				TextView t = (TextView)instance.findViewById(R.id.tamanoTextoTitle);
				t.setText("Tama\u00F1o de texto: "+(progress+5));
			}
		});
	}
	
	public void reportarFalla(HimnoActivity instance, View v){
		Intent i = Utils.getIntentReportarFalla(instance, HimnoActivity.versionSeleccionada, HimnoActivity.himnoSeleccionado);
		try {
			instance.startActivity(Intent.createChooser(i, "Selecciona tu cliente de correo"));
		} catch (android.content.ActivityNotFoundException ex) {
			MensajeUtil.mensaje(instance, "Error", "No es posible enviar email's desde tu dispositivo, por favor configura tu cuenta de correo.");
		}
	}
	
	public void showPantallaAcercaDe(final HimnoActivity instance,View v){
		Intent intent = new Intent(v.getContext(),AcercaActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		instance.startActivity(intent);
		Runnable runnable = new Runnable() {public void run () {
			instance.toggle();
		}};
		new Handler().postDelayed(runnable, 500);
	}
	
	public void showPantallaAgradecimientos(final HimnoActivity instance,View v){
		Intent intent = new Intent(v.getContext(),AgradecimientosActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		instance.startActivity(intent);
		Runnable runnable = new Runnable() {public void run () {
			instance.toggle();
		}};
		new Handler().postDelayed(runnable, 500);
	}
	
	public void showAdministrarDescargas(final HimnoActivity instance, View v){
		HimnoActivityReproductor reproductor = new HimnoActivityReproductor();
		reproductor.pararReproduccion(instance, null);
		Intent intent = new Intent(v.getContext(),DescargaMainActivity.class);
		instance.startActivity(intent);
		Runnable runnable = new Runnable() {public void run () {
			instance.toggle();
		}};
		new Handler().postDelayed(runnable, 500);
	}
	
	

}
