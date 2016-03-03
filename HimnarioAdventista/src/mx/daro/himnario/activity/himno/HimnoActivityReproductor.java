package mx.daro.himnario.activity.himno;

import mx.daro.himnario.R;
import mx.daro.himnario.listener.OnPlayerCompletedListener;
import mx.daro.himnario.listener.OnPlayerErrorListener;
import mx.daro.himnario.listener.OnPlayerPreparedListener;
import mx.daro.himnario.listener.OnPlayerProgressDownloadListener;
import mx.daro.himnario.listener.OnReproductorServicioCerradoListener;
import mx.daro.himnario.listener.OnReproductorServicioCreadoListener;
import mx.daro.himnario.service.ReproductorService;
import mx.daro.himnario.util.Constants;
import mx.daro.himnario.util.HimnarioHelper;
import mx.daro.himnario.util.MensajeUtil;
import mx.daro.himnario.util.Utils;
import android.content.Intent;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

public class HimnoActivityReproductor {
	
	public void reproducir(final HimnoActivity instance, final String tipo){
		HimnoActivityBottomOptions barraBotones = new HimnoActivityBottomOptions();
		HimnoActivity.tipo = tipo;
		barraBotones.showReproductor(instance);
		barraBotones.showLoadingReproductor(instance);
		instance.startService(new Intent(instance.getBaseContext(), ReproductorService.class));
		ReproductorService.setListenerServicioCreado(new OnReproductorServicioCreadoListener() {
			@Override
			public void onCreated() {
				HimnoActivity.servicioReproductorCreado=true;
				String url = "http://h.daro.mx/"+HimnoActivity.versionSeleccionada+"/"+tipo+"/"+HimnoActivity.himnoSeleccionado+".mp3";
				if(HimnarioHelper.existeHimno(tipo, HimnoActivity.versionSeleccionada, HimnoActivity.himnoSeleccionado)){
					ReproductorService.setTipo(ReproductorService.TIPO_LOCAL);
					url = HimnarioHelper.getPathHimno(tipo, HimnoActivity.versionSeleccionada, HimnoActivity.himnoSeleccionado);
				}else{
					ReproductorService.setTipo(ReproductorService.TIPO_STREAM);
				}
				HimnoActivity.preparando=true;
				ReproductorService.play(url);
				HimnoActivity.pausado=false;
				HimnoActivity.softStop=false;
			}
		});
	}
	
	public void pausarReproduccion(HimnoActivity instance, View v){
		HimnoActivityBottomOptions barraBotones = new HimnoActivityBottomOptions();
		if(HimnoActivity.pausado){
			HimnoActivity.tocando=true;
			if(HimnoActivity.softStop){
				ReproductorService.play();
				HimnoActivity.softStop=false;
				HimnoActivity.tocando=true;
			}
			else ReproductorService.resume();
			barraBotones.cambiaBotonPlayAPausa(instance);
			HimnoActivity.pausado=false;
			inicializaTimerReproduccion(instance);
		}else{
			if(HimnoActivity.softStop){
				ReproductorService.play();
				HimnoActivity.softStop=false;
				HimnoActivity.tocando=true;
				HimnoActivity.pausado=false;
				barraBotones.cambiaBotonPlayAPausa(instance);
				inicializaTimerReproduccion(instance);
			}else{
				ReproductorService.pause();
				barraBotones.cambiaBotonPlayAPlay(instance);
				HimnoActivity.pausado=true;
				HimnoActivity.tocando=false;
				instance.timerReproductor.removeCallbacks(instance.actualizaAvanceReproductor);
			}
		}
		
	}
	
	public void pararReproduccion(final HimnoActivity instance, final OnReproductorServicioCerradoListener afterClose){
		final HimnoActivityBottomOptions barraBotones = new HimnoActivityBottomOptions();
		HimnoActivity.tocando=false;
		HimnoActivity.pausado=false;
		HimnoActivity.preparando=false;
		instance.timerReproductor.removeCallbacks(instance.actualizaAvanceReproductor);
		final TextView tp = (TextView)instance.findViewById(R.id.botonTocarPista);
		final TextView tc = (TextView)instance.findViewById(R.id.botonTocarCantado);
		tp.setText(Constants.TXT_TOCAR_PISTA);
		tc.setText(Constants.TXT_TOCAR_CANCION);
		if(HimnoActivity.servicioReproductorCreado){
			ReproductorService.setListenerServicioCerrado(new OnReproductorServicioCerradoListener() {
				@Override
				public void onClosed() {
					instance.findViewById(R.id.FrgReproductor).setVisibility(View.GONE);
					barraBotones.hideLoadingReproductor(instance);
					barraBotones.configuraAltoBotonesHimno(instance, HimnoActivity.heightApp.get(instance.getResources().getConfiguration().orientation));
					barraBotones.cambiaBotonPlayAPlay(instance);
					if(afterClose!=null)afterClose.onClosed();
					HimnoActivity.servicioReproductorCreado=false;
				}
			});
			instance.stopService(new Intent(instance.getBaseContext(), ReproductorService.class));
		}else{
			if(afterClose!=null)afterClose.onClosed();
		}
	}
	
	public void configuracionReproductor(final HimnoActivity instance){
		final HimnoActivityReproductor reproductor = new HimnoActivityReproductor();
		final HimnoActivityBottomOptions barraBotones = new HimnoActivityBottomOptions();
		instance.timerReproductor.removeCallbacks(instance.actualizaAvanceReproductor);
		ReproductorService.setListenerPrepared(new OnPlayerPreparedListener() {
			@Override
			public void onPrepared() {
				if(HimnoActivity.preparando){
					HimnoActivity.tocando=true;
					HimnoActivity.preparando=false;
					SeekBar sb = (SeekBar) instance.findViewById(R.id.progreso);
					sb.setProgress(0);
					actualizaAvanceReproductorUsuario(instance, 0);
					barraBotones.cambiaBotonPlayAPausa(instance);
					barraBotones.hideLoadingReproductor(instance);
					ReproductorService.play();
					sb.setMax(ReproductorService.getDuration());
					if(ReproductorService.getTipo()==ReproductorService.TIPO_STREAM){
						HimnoActivity.progresoDescargado=0;
						sb.setSecondaryProgress(0);
					}else{
						HimnoActivity.progresoDescargado=sb.getMax();
						sb.setSecondaryProgress(HimnoActivity.progresoDescargado);
					}
					inicializaTimerReproduccion(instance);
				}
			}
		});
		ReproductorService.setListenerProgressDownload(new OnPlayerProgressDownloadListener() {
			@Override
			public void onProgressDownload(int progress) {
				if(HimnoActivity.tocando){
					SeekBar sb = (SeekBar) instance.findViewById(R.id.progreso);
					HimnoActivity.progresoDescargado=(int)((double)progress/100*ReproductorService.getDuration());
					sb.setSecondaryProgress(HimnoActivity.progresoDescargado);
				}
			}
		});
		ReproductorService.setListenerCompleted(new OnPlayerCompletedListener() {
			@Override
			public void onCompleted() {
				instance.timerReproductor.removeCallbacks(instance.actualizaAvanceReproductor);
				ReproductorService.softStop();
				barraBotones.cambiaBotonPlayAPlay(instance);
				SeekBar sb = (SeekBar) instance.findViewById(R.id.progreso);
				sb.setProgress(0);
				HimnoActivity.pausado=false;
				HimnoActivity.softStop=true;
				actualizaAvanceReproductorUsuario(instance, 0);
			}
		});
		
		SeekBar sb = (SeekBar) instance.findViewById(R.id.progreso);
		sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				if(ReproductorService.isPlaying()){
					ReproductorService.seek(seekBar.getProgress());
					inicializaTimerReproduccion(instance);
				}else{
					ReproductorService.seek(seekBar.getProgress());
				}
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				instance.timerReproductor.removeCallbacks(instance.actualizaAvanceReproductor);
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(fromUser){
					if(progress>HimnoActivity.progresoDescargado){
						progress = HimnoActivity.progresoDescargado;
						seekBar.setProgress(HimnoActivity.progresoDescargado);
					}
					HimnoActivity.progressGuardado=progress;
					actualizaAvanceReproductorUsuario(instance, progress);
				}
			}
		});
		
		ReproductorService.setListenerError(new OnPlayerErrorListener() {
			@Override
			public void onError(int what, int extra) {
				reproductor.pararReproduccion(instance, null);
				MensajeUtil.mensaje(instance, "Error", "Hubo un error al intentar reproducir. Verifica tu conexi\u00F3n a Internet e int\u00E9ntalo de nuevo.");
			}
		});
	}
	
	public void actualizaAvanceReproductorUsuario(HimnoActivity instance, int progreso){
		TextView t = (TextView)instance.findViewById(R.id.segundosReproducidos);
		t.setText(Utils.formatoTiempo(progreso));
		TextView tf = (TextView)instance.findViewById(R.id.segundosFaltantes);
		int faltantes = ReproductorService.getDuration()-progreso;
		tf.setText("-"+Utils.formatoTiempo(faltantes));
	}

	public void inicializaTimerReproduccion(HimnoActivity instance) {
		instance.timerReproductor.removeCallbacks(instance.actualizaAvanceReproductor);
		instance.timerReproductor.postDelayed(instance.actualizaAvanceReproductor, 1000);
    }
	
}
