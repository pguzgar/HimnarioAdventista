package mx.daro.himnario.activity.himno;

import mx.daro.himnario.BuscadorActivity;
import mx.daro.himnario.R;
import mx.daro.himnario.listener.GeneralListenerWithParameter;
import mx.daro.himnario.listener.OnReproductorServicioCerradoListener;
import mx.daro.himnario.service.ReproductorService;
import mx.daro.himnario.util.ConexionUtil;
import mx.daro.himnario.util.Constants;
import mx.daro.himnario.util.DescargaHelper;
import mx.daro.himnario.util.HimnarioHelper;
import mx.daro.himnario.util.MensajeUtil;
import mx.daro.himnario.util.StorageUtils;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HimnoActivityBottomOptions {
	
	public void clickDescargar(final HimnoActivity instance, View v){
		if(!new ConexionUtil(instance).isConnectedToInternet()){
			MensajeUtil.mensaje(instance, "Reproductor", "Para descargar "+(Constants.TIPO_REPRODUCCION_PISTA.equals(HimnoActivity.menuDescargaMostradoPara)?"las pistas":"los cantos")+" debes conectarte a Internet.");
		}else{
			instance.helper = new DescargaHelper(instance, HimnoActivity.menuDescargaMostradoPara,HimnoActivity.versionSeleccionada,HimnoActivity.himnoSeleccionado);
			instance.helper.setDownloadCompleteListener(HimnoActivityDescarga.getDownloadComplete(instance));
			instance.helper.setOnError(new GeneralListenerWithParameter() {
				@Override
				public void execute(String message) {
					HimnoActivity.error = message;
					instance.showError();
				}
			});
			instance.helper.descarga();
		}
	}

	public void clickTocar(final HimnoActivity instance, View v){
		final HimnoActivityReproductor reproductor = new HimnoActivityReproductor();
		final TextView tp = (TextView)instance.findViewById(R.id.botonTocarPista);
		final TextView tc = (TextView)instance.findViewById(R.id.botonTocarCantado);
		boolean existeHimno = HimnarioHelper.existeHimno(HimnoActivity.menuDescargaMostradoPara, HimnoActivity.versionSeleccionada, HimnoActivity.himnoSeleccionado);
		if(!existeHimno && !new ConexionUtil(instance).isConnectedToInternet()){
			MensajeUtil.mensaje(instance, "Reproductor", "Para tocar "+(Constants.TIPO_REPRODUCCION_PISTA.equals(HimnoActivity.menuDescargaMostradoPara)?"las pistas":"los cantos")+" debes conectarte a Internet.");
		}else{
			reproductor.pararReproduccion(instance, new OnReproductorServicioCerradoListener() {
				@Override
				public void onClosed() {
					if(HimnoActivity.menuDescargaMostradoPara!=null){
						if(Constants.TIPO_REPRODUCCION_PISTA.equals(HimnoActivity.menuDescargaMostradoPara) && tp.getText().equals(Constants.TXT_TOCAR_PISTA)){
							System.out.println("tocando pista");
							tp.setText(Constants.TXT_QUITAR_PISTA);
							hideOpcionesDescarga(instance);
							reproductor.reproducir(instance, Constants.TIPO_REPRODUCCION_PISTA);
						}else if(Constants.TIPO_REPRODUCCION_CANTADO.equals(HimnoActivity.menuDescargaMostradoPara) && tc.getText().equals(Constants.TXT_TOCAR_CANCION)){
							System.out.println("tocando cancion...");
							tc.setText(Constants.TXT_QUITAR_CANCION);
							hideOpcionesDescarga(instance);
							reproductor.reproducir(instance, Constants.TIPO_REPRODUCCION_CANTADO);
						}
					}
				}
			});
		}
	}
	
	public void clickTocarCancion(HimnoActivity instance, View v){
		HimnoActivityReproductor reproductor = new HimnoActivityReproductor();
		TextView tc = (TextView)instance.findViewById(R.id.botonTocarCantado);
		if(tc.getText().equals(Constants.TXT_QUITAR_CANCION)){
			reproductor.pararReproduccion(instance, null);
		}else{
			if(!StorageUtils.isMemoriaExternaDisponible() || HimnarioHelper.existeHimno(Constants.TIPO_REPRODUCCION_CANTADO, HimnoActivity.versionSeleccionada, HimnoActivity.himnoSeleccionado)){
				HimnoActivity.menuDescargaMostradoPara = Constants.TIPO_REPRODUCCION_CANTADO;
				clickTocar(instance,null);
			}else{
				reproductor.pararReproduccion(instance, null);
				if(instance.findViewById(R.id.FrgMenuDescarga).getVisibility()==View.VISIBLE && HimnoActivity.menuDescargaMostradoPara==Constants.TIPO_REPRODUCCION_CANTADO){
					hideOpcionesDescarga(instance);
				}else{
					HimnoActivity.menuDescargaMostradoPara = Constants.TIPO_REPRODUCCION_CANTADO;
					showOpcionesDescarga(instance);
				}
			}
		}
		TextView tp = (TextView)instance.findViewById(R.id.botonTocarPista);
		tp.setText(Constants.TXT_TOCAR_PISTA);
	}

	public void clickTocarPista(HimnoActivity instance, View v){
		HimnoActivityReproductor reproductor = new HimnoActivityReproductor();
		TextView tp = (TextView)instance.findViewById(R.id.botonTocarPista);
		if(tp.getText().equals(Constants.TXT_QUITAR_PISTA)){
			reproductor.pararReproduccion(instance, null);
		}else{
			if(!StorageUtils.isMemoriaExternaDisponible() || HimnarioHelper.existeHimno(Constants.TIPO_REPRODUCCION_PISTA, HimnoActivity.versionSeleccionada, HimnoActivity.himnoSeleccionado)){
				HimnoActivity.menuDescargaMostradoPara = Constants.TIPO_REPRODUCCION_PISTA;
				clickTocar(instance, null);
			}else{
				reproductor.pararReproduccion(instance, null);
				if(instance.findViewById(R.id.FrgMenuDescarga).getVisibility()==View.VISIBLE && HimnoActivity.menuDescargaMostradoPara==Constants.TIPO_REPRODUCCION_PISTA){
					hideOpcionesDescarga(instance);
				}else{
					HimnoActivity.menuDescargaMostradoPara = Constants.TIPO_REPRODUCCION_PISTA;
					showOpcionesDescarga(instance);
				}
			}
		}
		TextView tc = (TextView)instance.findViewById(R.id.botonTocarCantado);
		tc.setText(Constants.TXT_TOCAR_CANCION);
	}
	

	private void acomodaFlechaSubmenuDescarga(final HimnoActivity instance){
		acomodaSubmenuDescarga(instance);
		Runnable runnable = new Runnable() { public void run () {
			acomodaSubmenuDescarga(instance);
		}};
		new Handler().postDelayed(runnable, 200);
	}
	
	private void acomodaSubmenuDescarga(HimnoActivity instance){
		TextView t = (TextView)instance.findViewById((HimnoActivity.menuDescargaMostradoPara==Constants.TIPO_REPRODUCCION_PISTA)?R.id.botonTocarPista:R.id.botonTocarCantado);
		LinearLayout area = (LinearLayout)instance.findViewById(R.id.areaMenuDescarga);
		LinearLayout globoView = (LinearLayout)instance.findViewById(R.id.wraperGlobo);
		LinearLayout areaFlecha = (LinearLayout)instance.findViewById(R.id.areaFlecha);
		
		int widthArea = area.getWidth();
		int mitadTextoWidth = t.getWidth()/2;
		int pos[] = new int[2];
		t.getLocationInWindow(pos);
		int puntoMedioTexto = pos[0]+mitadTextoWidth;
		int globoWidth = globoView.getWidth();
		int mitadGloboWidth = (int)Math.ceil((double)globoWidth/2);
		int posGloboX = puntoMedioTexto-mitadGloboWidth;
		if(posGloboX<0) posGloboX=0;
		if(HimnoActivity.menuDescargaMostradoPara==Constants.TIPO_REPRODUCCION_PISTA){
			area.setGravity(Gravity.LEFT);
			area.setPadding(posGloboX,0,0,0);
			areaFlecha.setPadding(((posGloboX==0)?puntoMedioTexto:mitadGloboWidth)-10,0,0,0);
		}else{
			area.setGravity(Gravity.RIGHT);
			int padding = widthArea-puntoMedioTexto-mitadGloboWidth;
			if(padding<0) padding=0;
			area.setPadding(0,0,padding,0);
			areaFlecha.setPadding(((padding==0)?(globoWidth-(widthArea-puntoMedioTexto)):mitadGloboWidth)-10,0,0,0);
		}
	}
	
	public void cambiaBotonPlayAPausa(HimnoActivity instance){
		ImageView imagen = (ImageView)instance.findViewById(R.id.imagenPlay);
		imagen.setImageDrawable(instance.getResources().getDrawable(R.drawable.pausa));
	}

	public void cambiaBotonPlayAPlay(HimnoActivity instance){
		ImageView imagen = (ImageView)instance.findViewById(R.id.imagenPlay);
		imagen.setImageDrawable(instance.getResources().getDrawable(R.drawable.play));
	}
	
	public void showReproductor(HimnoActivity instance){
		View viewAreaReproductor = instance.findViewById(R.id.FrgReproductor);
		viewAreaReproductor.setVisibility(View.VISIBLE);
		viewAreaReproductor.getLayoutParams().height = HimnoActivity.heightMaximoBotones;
		viewAreaReproductor.setLayoutParams(viewAreaReproductor.getLayoutParams());
		configuraAltoBotonesHimno(instance, HimnoActivity.heightApp.get(instance.getResources().getConfiguration().orientation));
	}
	
	public void showLoadingReproductor(HimnoActivity instance){
		View viewAreaCargando = instance.findViewById(R.id.areaCargando);
		viewAreaCargando.setVisibility(View.VISIBLE);
		View viewReproductor = instance.findViewById(R.id.reproductor);
		viewReproductor.setVisibility(View.GONE);
		HimnoActivity.mostrandoLoading=true;
	}
	
	public void hideLoadingReproductor(HimnoActivity instance){
		View viewAreaCargando = instance.findViewById(R.id.areaCargando);
		viewAreaCargando.setVisibility(View.GONE);
		View viewReproductor = instance.findViewById(R.id.reproductor);
		viewReproductor.setVisibility(View.VISIBLE);
		HimnoActivity.mostrandoLoading=false;
	}

	public void hideOpcionesDescarga(HimnoActivity instance){
		HimnoActivity.menuDescargaMostradoPara = null;
		View v = instance.findViewById(R.id.FrgMenuDescarga);
		v.setVisibility(View.GONE);
		System.out.println("Escondido el menu descarga..."+v);
		configuraAltoBotonesHimno(instance, HimnoActivity.heightApp.get(instance.getResources().getConfiguration().orientation));
	}
	
	private void showOpcionesDescarga(HimnoActivity instance){
		View v = instance.findViewById(R.id.FrgMenuDescarga);
		v.setVisibility(View.VISIBLE);
		configuraAltoBotonesHimno(instance, HimnoActivity.heightApp.get(instance.getResources().getConfiguration().orientation));
	}
	
	public void showPantallaBuscador(final HimnoActivity instance, View v){
		HimnoActivityReproductor reproductor = new HimnoActivityReproductor(); 
		reproductor.pararReproduccion(instance, null);
		Intent intent = new Intent(v.getContext(), BuscadorActivity.class);
		intent.putExtra(Constants.VERSION_SELECCIONADA, HimnoActivity.versionSeleccionada);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		instance.startActivity(intent);
	}
	
	public void configuracionAltoBotonesHimno(final HimnoActivity instance){
		Integer orientacion = instance.getResources().getConfiguration().orientation;
		Integer h = HimnoActivity.heightApp.get(orientacion);
		if(h==null){
			final View v = instance.findViewById(R.id.texto);
			v.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
				public void onGlobalLayout() {
					if (Build.VERSION.SDK_INT<16) removeLayoutListenerPre16(v.getViewTreeObserver(),this);
	                else removeLayoutListenerPost16(v.getViewTreeObserver(), this);
					configuraAltoBotonesHimno(instance, null);
					
				}
			});
		}else{
			configuraAltoBotonesHimno(instance, h);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void removeLayoutListenerPre16(ViewTreeObserver observer, OnGlobalLayoutListener listener){
	    observer.removeGlobalOnLayoutListener(listener);
	}

	@TargetApi(16)
	private void removeLayoutListenerPost16(ViewTreeObserver observer, OnGlobalLayoutListener listener){
	    observer.removeOnGlobalLayoutListener(listener);
	}
	

	

	public void configuraAltoBotonesHimno(final HimnoActivity instance, Integer heightAreaApp){
		if(heightAreaApp==null || heightAreaApp==0){
			heightAreaApp = instance.findViewById(R.id.texto).getHeight();
			HimnoActivity.heightApp.put(instance.getResources().getConfiguration().orientation, heightAreaApp);
		}
		HimnoActivity.heightMaximoBotones = (int)((HimnoActivity.pppHeight/2.54)*.7);
		int barrasVisibles=1;
		int heightBarraDescarga=0;
		View viewAreaReproductor = instance.findViewById(R.id.FrgReproductor);
		if(viewAreaReproductor.getVisibility()==View.VISIBLE && (
				instance.findViewById(R.id.reproductor).getVisibility()==View.VISIBLE 
				|| instance.findViewById(R.id.areaCargando).getVisibility()==View.VISIBLE
			)){
			barrasVisibles++;
			viewAreaReproductor.getLayoutParams().height = HimnoActivity.heightMaximoBotones;
			viewAreaReproductor.setLayoutParams(viewAreaReproductor.getLayoutParams());
		}
		View viewMenuDescarga = instance.findViewById(R.id.FrgMenuDescarga);
		if(viewMenuDescarga.getVisibility()==View.VISIBLE){
			heightBarraDescarga=(int)(HimnoActivity.heightMaximoBotones*1.12);
			viewMenuDescarga.getLayoutParams().height = heightBarraDescarga;
			viewMenuDescarga.setLayoutParams(viewMenuDescarga.getLayoutParams());
			if(HimnoActivity.menuDescargaMostradoPara==Constants.TIPO_REPRODUCCION_PISTA){
				instance.findViewById(R.id.botonTocarPista).getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
					public void onGlobalLayout() {
						if (Build.VERSION.SDK_INT<16)removeLayoutListenerPre16(instance.findViewById(R.id.botonTocarPista).getViewTreeObserver(),this);
		                else removeLayoutListenerPost16(instance.findViewById(R.id.botonTocarPista).getViewTreeObserver(), this);
						acomodaFlechaSubmenuDescarga(instance);
					}
				});
			}else{
				instance.findViewById(R.id.botonTocarCantado).getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
					public void onGlobalLayout() {
						if (Build.VERSION.SDK_INT<16)removeLayoutListenerPre16(instance.findViewById(R.id.botonTocarCantado).getViewTreeObserver(),this);
		                else removeLayoutListenerPost16(instance.findViewById(R.id.botonTocarCantado).getViewTreeObserver(), this);
						acomodaFlechaSubmenuDescarga(instance);
					}
				});
			}
			
		}
		
		int heightMaximoAreaHimno = heightAreaApp-(HimnoActivity.heightMaximoBotones*barrasVisibles)-heightBarraDescarga;
		
		View viewAreaHimno = instance.findViewById(R.id.areaHimno);
		viewAreaHimno.getLayoutParams().height = heightMaximoAreaHimno;
		viewAreaHimno.setLayoutParams(viewAreaHimno.getLayoutParams());

		View imagenPlay = instance.findViewById(R.id.imagenPlay);
		imagenPlay.getLayoutParams().width = HimnoActivity.heightMaximoBotones;
		imagenPlay.setLayoutParams(imagenPlay.getLayoutParams());
		
		View viewAreaBotones = instance.findViewById(R.id.areaBotones);
		viewAreaBotones.getLayoutParams().height = HimnoActivity.heightMaximoBotones;
		viewAreaBotones.setLayoutParams(viewAreaBotones.getLayoutParams());
	}
	
	public void configuracionBarrasYBotones(final HimnoActivity instance) {
		HimnoActivityReproductor reproductor = new HimnoActivityReproductor();
		instance.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		if(!ReproductorService.isPlaying() && !HimnoActivity.mostrandoLoading && !HimnoActivity.pausado){
			instance.findViewById(R.id.FrgReproductor).setVisibility(View.GONE);
		}else{
			showReproductor(instance);
			if(HimnoActivity.mostrandoLoading){
				instance.findViewById(R.id.areaCargando).setVisibility(View.VISIBLE);
				instance.findViewById(R.id.reproductor).setVisibility(View.GONE);
			}else{
				instance.findViewById(R.id.areaCargando).setVisibility(View.GONE);
				instance.findViewById(R.id.reproductor).setVisibility(View.VISIBLE);
				if(!HimnoActivity.pausado){
					reproductor.inicializaTimerReproduccion(instance);
					cambiaBotonPlayAPausa(instance);
				}
				reproductor.actualizaAvanceReproductorUsuario(instance, HimnoActivity.progressGuardado);
			}
			if(HimnoActivity.tipo!=null){
				TextView t = (TextView)instance.findViewById(R.id.botonTocarPista);
				TextView tc = (TextView)instance.findViewById(R.id.botonTocarCantado);
				if(HimnoActivity.tipo.equals(Constants.TIPO_REPRODUCCION_CANTADO)){
					tc.setText(Constants.TXT_QUITAR_CANCION);
				}else{
					t.setText(Constants.TXT_QUITAR_PISTA);
				}
			}
		}
		if(HimnoActivity.menuDescargaMostradoPara==null){
			instance.findViewById(R.id.FrgMenuDescarga).setVisibility(View.GONE);
		}else{
			instance.findViewById(R.id.FrgMenuDescarga).setVisibility(View.VISIBLE);
		}
	}
}
