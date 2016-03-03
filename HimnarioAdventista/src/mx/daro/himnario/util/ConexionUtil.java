package mx.daro.himnario.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class ConexionUtil {
    private Context context;

    public ConexionUtil(Context context) {
        this.context = context;
    }

    public NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm!=null)
        	return cm.getActiveNetworkInfo();
        return null;
    }

    public boolean isConnectedToInternet() {
        NetworkInfo activeNetwork = getNetworkInfo(context);
        if (activeNetwork != null) {
            if(activeNetwork.isConnected()){
                return true;
            }
        }
        return false;
    }
    
    public boolean isConnectedWifi(){
    	NetworkInfo activeNetwork = getNetworkInfo(context);
        return (activeNetwork != null && activeNetwork.isConnected() && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI);
    }
    
    public boolean isConnectedMobile(){
    	NetworkInfo activeNetwork = getNetworkInfo(context);
        return (activeNetwork != null && activeNetwork.isConnected() && activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE);
    }
    
    public String getNombreOperador(){
    	TelephonyManager manager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
    	String operador = "";
    	if(manager.getNetworkOperatorName()!=null && manager.getNetworkOperatorName()!=""){
    		operador = manager.getNetworkOperatorName();
    	}
    	return operador;
    }
    
}
