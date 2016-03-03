package mx.daro.himnario.activity.himno;

import mx.daro.himnario.listener.OnDownloadCompleteListener;
import android.os.Handler;

public class HimnoActivityDescarga {

	public static OnDownloadCompleteListener getDownloadComplete(final HimnoActivity instance){
		return new OnDownloadCompleteListener() {
			@Override
			public void onComplete() {
				Runnable runnable = new Runnable() { public void run () {
					instance.helper=null;
					new HimnoActivityBottomOptions().clickTocar(instance, null);
				}};
				new Handler().postDelayed(runnable, 100);
			}
		};
	}

}
