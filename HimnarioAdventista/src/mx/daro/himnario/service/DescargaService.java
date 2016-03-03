package mx.daro.himnario.service;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import mx.daro.himnario.listener.GeneralListener;
import mx.daro.himnario.listener.GeneralListenerWithParameter;
import mx.daro.himnario.util.ConexionUtil;
import mx.daro.himnario.util.MailUtil;
import mx.daro.himnario.util.StorageUtils;
import mx.daro.himnario.util.Utils;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

public class DescargaService extends Service {
	
	private static GeneralListener onCreated;
	private static GeneralListener onStart;
	private static GeneralListener onCancel;
	private static GeneralListener onDownloadCompleted;
	private static GeneralListener onServiceClosed;
	private static GeneralListenerWithParameter onError;
	
	private static Context ctx;
	
	private static BroadcastReceiver receiver;
	private static DownloadManager dm;
	private static long enqueue;
	
	private static String tipo;
	private static String version;
	private static Integer numero;
	
	private static boolean completado;
	private static boolean cancelado;
	private static boolean descargaIniciada;
	private static boolean error;
	
	private static int progreso;
	
	private static int nErrores = 0;
	
	private static String versionApp;
	
    @Override
    public void onCreate() {
        super.onCreate();
        if(onCreated!=null){
        	onCreated.execute();
        }
        completado=false;
        cancelado=false;
        descargaIniciada=false;
        error=false;
        
        PackageInfo pInfo;
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			versionApp = pInfo.versionName;
		} catch (NameNotFoundException e) {}
    }
    
    public static void descarga(Context context, String tipo, String version, Integer numero){
    	ctx = context;
    	new DescargaTask(context).execute(tipo, version, numero+"");
    }
    
    private static void descargaHimno(Context context, final String tipo, final String version, final Integer numero) {
    	descargaIniciada=true;
        try {
        	completado=false;
			cancelado=false;
			error=false;
			progreso=0;
			DescargaService.tipo = tipo;
			DescargaService.version = version;
			DescargaService.numero = numero;
			
			receiver = new BroadcastReceiver() {
	            @Override
	            public void onReceive(Context context, Intent intent) {
            		long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
	            	if(!completado && referenceId == enqueue){
		                String action = intent.getAction();
		                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
		                    Query query = new Query();
		                    query.setFilterById(enqueue);
		                    Cursor c = null;
		                    try{
			                    c = dm.query(query);
			                    if (c!=null && c.moveToFirst()) {
			                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
			                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
			                        	finalizaDescarga();
			                        }else{
			                        	System.err.println("PGG - DOWNLOAD NOT SUCCESSFUL");
			                        }
			                    }else{
			                    	System.err.println("PGG - QUERY FINALIZADA NULL");
			                    	finalizaDescarga();
			                    }
		                    }catch (Exception e){
		                    	System.err.println("PGG - ERROR EN CURSOR: "+e.getMessage());
		                    }finally{
								if(c!=null && !c.isClosed()){
									c.close();
								}
							}
		                }
	            	}
	            }
	        };
	        context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	        
			String u = "http://h.daro.mx/"+version+"/"+tipo+"/"+numero+".mp3";
			String pathDir = StorageUtils.getPathStorage(context)+"/himnario/"+version+"/"+tipo+"/";
			File directorio = new File(pathDir);
			directorio.mkdirs();
			File oFile = new File(pathDir+numero+".mp3");
            if (oFile.exists()) {
                oFile.delete();
            }
			dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
			Request request = new Request(Uri.parse(u));
			request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
			request.setTitle("Himno # "+numero);
			request.setDestinationUri(Uri.fromFile(oFile));
			enqueue = dm.enqueue(request);
			
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (!completado) {
						if (cancelado){
							break;
						}
						Query query = new Query();
						query.setFilterById(enqueue);
						Cursor c = null;
						try{
							c = dm.query(query);
							if (c!=null && c.moveToFirst()) {
								int sizeIndex = c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
								int downloadedIndex = c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
								long size = c.getInt(sizeIndex);
								long downloaded = c.getInt(downloadedIndex);
								if (size != -1) progreso = (int)(downloaded*100/size);
								descargaIniciada=true;
							}else{
								System.err.println("PGG - QUERY PROGRESS NULL");
								break;
							}
						}catch(Exception e){
							System.err.println("PGG - ERROR EN CURSOR: "+e.getMessage());
						}finally{
							if(c!=null && !c.isClosed()){
								c.close();
							}
						}
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}).start();
        } catch (Exception e) {
        	System.out.println("PGG - ERROR: "+e.getMessage());
        	e.printStackTrace();
            if(nErrores>=3){
            	nErrores=0;
            	error=true;
    			StringWriter errors = new StringWriter();
    			try{
    				ConexionUtil conexion = new ConexionUtil(context);
    				e.printStackTrace(new PrintWriter(errors));
    				String extra="";
    				if(conexion.isConnectedMobile()){
    					extra+="Operador: "+conexion.getNombreOperador();
    				}
    				String versionAplication = "";
    				if(versionApp!=null){
    					versionAplication = versionApp;
    				}
    				final String error = "Version: " + versionAplication+
    						"\n"+Utils.getDeviceName()+
    						"\nWIFI: "+conexion.isConnectedWifi()+
    						"\nMobile: "+conexion.isConnectedMobile()+
    						"\n"+extra+
    						"\nTipo: "+tipo+
    						"\nVersion: "+version+
    						"\nNumero: "+numero+
    						"\n\n\n"+errors.toString();
    				Handler hcorreo = new Handler(Looper.getMainLooper());
    				hcorreo.post(new Runnable() {
    					@Override
    					public void run() {
    						new MailUtil().execute(error);
    					}
    				});
    			}catch(Exception ex){}
    			if(onError!=null){
    				final String err = errors.toString();
    				Handler handler = new Handler(Looper.getMainLooper());
    				handler.post(new Runnable() {
    					@Override
    					public void run() {
    						onError.execute(err);
    					}
    				});
    			}
            }else{
	            nErrores++;
	            System.out.println("PGG - Intento de descarga # "+nErrores+1);
	            descargaHimno(context, tipo, version, numero);
            }
        }
    }
    
    public static void finalizaDescarga(){
    	completado=true;
		if(onDownloadCompleted!=null){
			onDownloadCompleted.execute();
		}
		onCreated=null;
		onDownloadCompleted=null;
		descargaIniciada=false;
		if(ctx!=null && receiver!=null){
			try{
				ctx.unregisterReceiver(receiver);
			}catch(Exception e){}
			receiver = null;
		}
    }
    
	public static void cancelar(){
    	if(!completado){
    		cancelado=true;
    		dm.remove(enqueue);
    		finalizaDescarga();
    	}
    	progreso=0;
    	if(onCancel!=null){
    		onCancel.execute();
    	}
    	onCancel=null;
    }

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
    public void onStart(Intent intent, int startId) {
		if(onStart!=null){
			onStart.execute();
		}
    }
	
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(onServiceClosed!=null){
        	onServiceClosed.execute();
        }
    }
    
	private static class DescargaTask extends AsyncTask<String, Integer, String> {

		Context contexto;
		
		public DescargaTask(Context context){
			this.contexto = context;
		}
		
		@Override
		protected String doInBackground(String... params) {
			String tipo = params[0];
			String version = params[1];
			Integer numero = Integer.valueOf(params[2]);
			descargaHimno(contexto,tipo, version, numero);
			return null;
		}
	}

    public static boolean isError() {
		return error;
	}

	public static void setError(boolean error) {
		DescargaService.error = error;
	}

	public static GeneralListener getOnCancel() {
		return onCancel;
	}

	public static void setOnCancel(GeneralListener onCancel) {
		DescargaService.onCancel = onCancel;
	}

	public static String getTipo() {
		return tipo;
	}

	public static void setTipo(String tipo) {
		DescargaService.tipo = tipo;
	}

	public static String getVersion() {
		return version;
	}

	public static void setVersion(String version) {
		DescargaService.version = version;
	}

	public static Integer getNumero() {
		return numero;
	}

	public static void setNumero(Integer numero) {
		DescargaService.numero = numero;
	}

	public static int getProgreso() {
		return progreso;
	}

	public static void setProgreso(int progreso) {
		DescargaService.progreso = progreso;
	}

	public static GeneralListener getOnCreated() {
		return onCreated;
	}

	public static void setOnCreated(GeneralListener onCreated) {
		DescargaService.onCreated = onCreated;
	}

	public static boolean isCompletado() {
		return completado;
	}

	public static void setCompletado(boolean completado) {
		DescargaService.completado = completado;
	}

	public static boolean descargaIniciada() {
		return DescargaService.descargaIniciada;
	}

	public static boolean isCancelado() {
		return cancelado;
	}

	public static void setCancelado(boolean cancelado) {
		DescargaService.cancelado = cancelado;
	}

	public static GeneralListener getOnDownloadCompleted() {
		return onDownloadCompleted;
	}

	public static void setOnDownloadCompleted(GeneralListener onCompleted) {
		DescargaService.onDownloadCompleted = onCompleted;
	}

	public static GeneralListener getOnServiceClosed() {
		return onServiceClosed;
	}

	public static void setOnServiceClosed(GeneralListener onServiceClosed) {
		DescargaService.onServiceClosed = onServiceClosed;
	}

	public static GeneralListener getOnStart() {
		return onStart;
	}

	public static void setOnStart(GeneralListener onStart) {
		DescargaService.onStart = onStart;
	}

	public static GeneralListenerWithParameter getOnError() {
		return onError;
	}

	public static void setOnError(GeneralListenerWithParameter onError) {
		DescargaService.onError = onError;
	}
}
