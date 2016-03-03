package mx.daro.himnario.service;

import mx.daro.himnario.listener.OnPlayerCompletedListener;
import mx.daro.himnario.listener.OnPlayerErrorListener;
import mx.daro.himnario.listener.OnPlayerPreparedListener;
import mx.daro.himnario.listener.OnPlayerProgressDownloadListener;
import mx.daro.himnario.listener.OnReproductorServicioCerradoListener;
import mx.daro.himnario.listener.OnReproductorServicioCreadoListener;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;

public class ReproductorService extends Service {
	
	public static final int TIPO_LOCAL=1;
	public static final int TIPO_STREAM=2;

	private static int tipo;
	private static MediaPlayer mPlayer;
	private static OnPlayerPreparedListener listenerPrepared;
	private static OnPlayerProgressDownloadListener listenerProgressDownload;
	private static OnReproductorServicioCreadoListener listenerServicioCreado;
	private static OnReproductorServicioCerradoListener listenerServicioCerrado;
	private static OnPlayerCompletedListener listenerCompleted;
	private static OnPlayerErrorListener listenerError;
	
	private static boolean preparado;
	
	
    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mPlayer.setOnCompletionListener(mOnCompletionListener);
        mPlayer.setOnErrorListener(mOnErrorListener);
        mPlayer.setOnInfoListener(mOnInfoListener);
        mPlayer.setOnPreparedListener(mOnPreparedListener);
        mPlayer.setOnSeekCompleteListener(mOnSeekCompleteListener);
        mPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        if(listenerServicioCreado!=null){
        	listenerServicioCreado.onCreated();
        }
    }
    
    public static void play(String url){
    	try{
	    	if (mPlayer != null) {
	    		preparado=false;
	            mPlayer.reset();
	            mPlayer.setDataSource(url);
	            mPlayer.prepareAsync();
	        }
    	}catch(Exception e){}
    }
    
    public static void play(){
    	if (mPlayer != null && preparado) {
    		try{
    			mPlayer.start();
    		}catch(Exception e){}
        }
    }
    
    public static void pause(){
    	if (mPlayer != null) {
    		try{
    			if (mPlayer.isPlaying()) {
                    mPlayer.pause();
                }
    		}catch(Exception e){}
        }
    }
    
    public static void resume(){
    	if (mPlayer != null) {
    		try{
    			mPlayer.start();
    		}catch(Exception e){}
        }
    }
    
    public static boolean isPlaying(){
    	if (mPlayer != null) {
    		try{
    			return mPlayer.isPlaying();
    		}catch(Exception e){}
        }
    	return false;
    }
    
    public static void stop(){
    	if (mPlayer != null) {
    		try{
    			if (mPlayer.isPlaying()) {
                    mPlayer.stop();
                }
            	mPlayer.release();
    		}catch(Exception e){}
        }
    	preparado=false;
    }
    
    public static void softStop(){
    	if (mPlayer != null) {
    		try{
	            if (mPlayer.isPlaying()) {
	                mPlayer.stop();
	            }
    		}catch(Exception e){}
        }
    }
    
    public static int getProgress(){
    	if(mPlayer!=null){
    		try{
    			return mPlayer.getCurrentPosition()/1000;
    		}catch(Exception e){}
    	}
    	return 0;
    }
    
    public static int getDuration(){
    	if(mPlayer!=null){
    		try{
    			return mPlayer.getDuration()/1000;
    		}catch(Exception e){}
    	}
    	return 0;
    }
    
    public static void seek(int segundos){
    	if (mPlayer!=null) {
    		try{
    			mPlayer.seekTo(segundos*1000);
    		}catch(Exception e){}
    	}
    }
    
    @Override
    public void onDestroy() {
    	stop();
        super.onDestroy();
        if(listenerServicioCerrado!=null)listenerServicioCerrado.onClosed();
    }
    
    public static OnPlayerPreparedListener getListenerPrepared() {
		return listenerPrepared;
	}

	public static void setListenerPrepared(OnPlayerPreparedListener listener) {
		listenerPrepared = listener;
	}

	public static OnPlayerProgressDownloadListener getListenerProgressDownload() {
		return listenerProgressDownload;
	}

	public static void setListenerProgressDownload(OnPlayerProgressDownloadListener listenerProgress) {
		ReproductorService.listenerProgressDownload = listenerProgress;
	}

	public static OnReproductorServicioCreadoListener getListenerServicioCreado() {
		return listenerServicioCreado;
	}

	public static void setListenerServicioCreado(OnReproductorServicioCreadoListener listenerServicioCreado) {
		ReproductorService.listenerServicioCreado = listenerServicioCreado;
	}

	public static OnPlayerCompletedListener getListenerCompleted() {
		return listenerCompleted;
	}

	public static void setListenerCompleted(OnPlayerCompletedListener listenerCompleted) {
		ReproductorService.listenerCompleted = listenerCompleted;
	}

	/**
     * Once the song is ready to play, we can do some operations here, in this case send some updates to the UI.
     */
    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
        	preparado = true;
        	if(listenerPrepared!=null)listenerPrepared.onPrepared();
        }
    };

    /**
     * This method is handled when there is an error in the mediaPlayer
     */
    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        public boolean onError(MediaPlayer mp, int what, int extra) {
        	if(listenerError!=null) listenerError.onError(what, extra);
            return false;
        }
    };

    /**
     * We set the percentage of the buffering being taken.
     */
    private MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
        	if(listenerProgressDownload!=null) listenerProgressDownload.onProgressDownload(percent);
        }
    };

    /**
     * Once a song has finished playing, go to the next song
     */
    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer arg0) {
        	if(listenerCompleted!=null)listenerCompleted.onCompleted();
        }
    };

    /**
     * These functions declarations, in the meantime, are just needed to avoid warnings.
     */
    private MediaPlayer.OnInfoListener mOnInfoListener = new MediaPlayer.OnInfoListener() {
        public boolean onInfo(MediaPlayer arg0, int arg1, int arg2) {
            return false;
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        public void onVideoSizeChanged(MediaPlayer arg0, int arg1, int arg2) {
        }
    };
    
    private MediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new MediaPlayer.OnSeekCompleteListener() {
		@Override
		public void onSeekComplete(MediaPlayer mp) {
		}
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

	public static OnPlayerErrorListener getListenerError() {
		return listenerError;
	}

	public static void setListenerError(OnPlayerErrorListener listenerError) {
		ReproductorService.listenerError = listenerError;
	}

	public static OnReproductorServicioCerradoListener getListenerServicioCerrado() {
		return listenerServicioCerrado;
	}

	public static void setListenerServicioCerrado(
			OnReproductorServicioCerradoListener listenerServicioCerrado) {
		ReproductorService.listenerServicioCerrado = listenerServicioCerrado;
	}

	public static int getTipo() {
		return tipo;
	}

	public static void setTipo(int tipo) {
		ReproductorService.tipo = tipo;
	}
	

}
