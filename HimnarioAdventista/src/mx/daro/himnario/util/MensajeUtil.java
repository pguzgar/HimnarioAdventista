package mx.daro.himnario.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class MensajeUtil {
	
    public static AlertDialog mensaje(Context context, String titulo, String mensaje) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensaje);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
        return alertDialog;
    }

    public static AlertDialog mensajeConCallback(Context context, String titulo, String mensaje, DialogInterface.OnClickListener callback){
    	AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensaje);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", callback);
        alertDialog.show();
        return alertDialog;
    }

    public static AlertDialog pretuntaDescargar(Context context, String mensaje, DialogInterface.OnClickListener callback) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Descargas");
        alertDialog.setMessage(mensaje);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Si, Descargar", callback);
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", callback);
        alertDialog.show();
        return alertDialog;
    }

    public static AlertDialog preguntaContinuar(Context context, String titulo, String mensaje, DialogInterface.OnClickListener callback){
    	AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensaje);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Si, Continuar", callback);
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", callback);
        alertDialog.show();
        return alertDialog;
    }
    
    public static AlertDialog preguntaBorrar(Context context, String titulo, String mensaje, DialogInterface.OnClickListener callback){
    	AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensaje);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Si, Borrar", callback);
        alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", callback);
        alertDialog.show();
        return alertDialog;
    }
}
