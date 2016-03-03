package mx.daro.himnario.util;

import java.io.File;

import mx.daro.himnario.listener.GeneralListener;
import mx.daro.himnario.listener.GeneralListenerWithParameter;
import mx.daro.himnario.listener.OnDownloadCompleteListener;
import mx.daro.himnario.service.DescargaService;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Handler;
import android.view.KeyEvent;

public class DescargaHelper {
	
	private final Handler timerProgresoDescarga = new Handler();
	
	private static boolean servicioIniciado;
	private static boolean mostrado;
	private static int progreso;
	
	public static Context context;
	private static ProgressDialog progressDialog;
	
	private static String tipo;
	private static String version;
	private static Integer numero;

	private OnDownloadCompleteListener downloadCompleteListener;
	private GeneralListener onCancel;
	private GeneralListenerWithParameter onError;
	
	
	public DescargaHelper(final Context context, final String tipo, final String version, final Integer numero){
		DescargaHelper.context = context;
		DescargaHelper.tipo = tipo;
		DescargaHelper.version = version;
		DescargaHelper.numero = numero;
		
		DescargaService.setOnCreated(new GeneralListener() {
			@Override
			public void execute() {
				servicioIniciado=true;
				progressDialog=null;
				mostrado=true;
		        showDialog();
				DescargaService.descarga(context, tipo, version, numero);
				inicializaTimerProgresoDescarga();
			}
		});
        DescargaService.setOnDownloadCompleted(new GeneralListener() {
			@Override
			public void execute() {
				getProgressDialog().dismiss();
		        mostrado=false;
		        timerProgresoDescarga.removeCallbacks(actualizaAvanceProgreso);
		        context.stopService(new Intent(context, DescargaService.class));
			}
		});
        DescargaService.setOnServiceClosed(new GeneralListener() {
			@Override
			public void execute() {
				servicioIniciado=false;
		        if(downloadCompleteListener!=null && !DescargaService.isCancelado() && !DescargaService.isError()){
		        	downloadCompleteListener.onComplete();
		        	downloadCompleteListener=null;
		        }else{
		        	if(DescargaService.isCancelado()){
		        		eliminaHimno();
		        		if(onCancel!=null){
		        			onCancel.execute();
		        			onCancel=null;
		        		}
		        	}
		        }
			}
		});
        DescargaService.setOnError(new GeneralListenerWithParameter() {
			@Override
			public void execute(String mensaje) {
				eliminaHimno();
				getProgressDialog().dismiss();
		        mostrado=false;
		        timerProgresoDescarga.removeCallbacks(actualizaAvanceProgreso);
		        context.stopService(new Intent(context, DescargaService.class));
		        String msg = "Ocurri\u00F3 un error al intentar descargar el Himno # "+numero+". Por favor intenta de nuevo o verifica tu conexi\u00F3n.";
		        if(mensaje.contains("No space left")){
		        	msg = "No tienes espacio disponible. Debes liberar espacio para continuar descargando los himnos.";
		        }
				eliminaHimno();
        		if(onError!=null){
        			onError.execute(msg);
        			onError=null;
        		}
			}
		});

    }
	
	public void descarga() {
		if(isMyServiceRunning(DescargaService.class)){
			servicioIniciado = true;
			OnDownloadCompleteListener temp = downloadCompleteListener;
			downloadCompleteListener = null;
			context.stopService(new Intent(context, DescargaService.class));
			while(isMyServiceRunning(DescargaService.class)){
				System.out.println("PGG - Esperando parar el servicio...");
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			downloadCompleteListener = temp;
			System.out.println("PGG - Iniciando servicio erroneo...");
		}
		context.startService(new Intent(context, DescargaService.class));
	}
	
	private boolean isMyServiceRunning(Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}
	
	public static void showDialog(){
		getProgressDialog().show();
	}
	
	public static void closeDialog(){
		getProgressDialog().dismiss();	
		mostrado=false;
	}
	
	private static void cancelar(){
		DescargaService.cancelar();
		getProgressDialog().dismiss();
		mostrado=false;
	}
	
	private static void eliminaHimno(){
		try{
	        String pathDir = StorageUtils.getPathStorage(context)+"/himnario/"+version+"/"+tipo+"/";
	        File f = new File(pathDir+numero+".mp3");
	        if(f.exists()){
	        	f.delete();
	        }
        }catch(Exception e){
        	e.printStackTrace();
        }
	}
	
	void inicializaTimerProgresoDescarga() {
        timerProgresoDescarga.removeCallbacks(actualizaAvanceProgreso);
        timerProgresoDescarga.postDelayed(actualizaAvanceProgreso, 200);
    }
	
	private Runnable actualizaAvanceProgreso = new Runnable() {
		public void run() {
			actualizaProgreso(DescargaService.getProgreso());
			if(!DescargaService.isCompletado())
				timerProgresoDescarga.postDelayed(this, 200);
			else
				timerProgresoDescarga.removeCallbacks(this);
		}
	};
	
	public void actualizaProgreso(int progreso){
		getProgressDialog().setProgress(progreso);
	}
	
	private static ProgressDialog getProgressDialog(){
		if(progressDialog==null && context!=null){
			final ProgressDialog dialog = new ProgressDialog(context);
			dialog.setMessage("Descargando - Himno # "+numero);
			dialog.setIndeterminate(false);
			dialog.setMax(100);
			dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(false);
			progressDialog = dialog;
			
			dialog.setOnKeyListener(new OnKeyListener() {
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					if(keyCode == KeyEvent.KEYCODE_BACK){
						cancelar();
						return true;
					}
					if(keyCode == KeyEvent.KEYCODE_SEARCH){
						return true;
					}
					return false;
				}
			});
		}
		return progressDialog;
	}
	
	public static ProgressDialog getDialog(){
		return progressDialog;
	}
	
	public static boolean isMostrado(){
		return mostrado;
	}
	
	public static int getProgreso(){
		return progreso;
	}
	
	public static boolean isCompletado(){
		return DescargaService.isCompletado();
	}
	
	public static boolean isCancelado(){
		return DescargaService.isCancelado();
	}

	public static boolean descargaIniciada(){
		return DescargaService.descargaIniciada();
	}
	
	public static boolean servicioIniciado(){
		return servicioIniciado;
	}
	
	public GeneralListener getOnCancel() {
		return onCancel;
	}

	public void setOnCancel(GeneralListener onCancel) {
		this.onCancel = onCancel;
	}
	
	public OnDownloadCompleteListener getDownloadCompleteListener() {
		return downloadCompleteListener;
	}

	public void setDownloadCompleteListener(
			OnDownloadCompleteListener downloadCompleteListener) {
		this.downloadCompleteListener = downloadCompleteListener;
	}

	public GeneralListenerWithParameter getOnError() {
		return this.onError;
	}

	public void setOnError(GeneralListenerWithParameter onError) {
		this.onError = onError;
	}
}
