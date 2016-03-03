package mx.daro.himnario.util;

import java.util.Locale;

import mx.daro.himnario.R;
import mx.daro.himnario.activity.himno.HimnoActivity;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint("DefaultLocale")
public class Utils {

	public static String formatoTiempo(int segundos) {
		String secondsPref = "";
		int minutes;
		int seconds;

		if (segundos < 60) {
			minutes = 0;
			seconds = segundos;
		} else {
			minutes = segundos / 60;
			seconds = segundos % 60;
		}

		if (seconds < 10) {
			secondsPref = "0";
		}
		if(seconds<0){
			seconds=0;
		}

		return minutes + ":" + secondsPref + seconds;
	}

	public static String getDeviceName() {
		String manufacturer = Build.MANUFACTURER.toUpperCase();
		String model = Build.MODEL.toUpperCase();
		if (model.startsWith(manufacturer)) {
			return model;
		} else {
			return manufacturer+ " " + model;
		}
	}

	public static Intent getIntentReportarFalla(Context context, String version, Integer numero){
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"pedro.guzman@daro.mx"});
		PackageInfo pInfo;
		String versionApp = "";
		try {
			pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			versionApp = pInfo.versionName;
		} catch (NameNotFoundException e) {}
		String tituloHimno = ((TextView)((HimnoActivity)context).findViewById(R.id.tituloHimno)).getText().toString();
		String textoHimno = ((EditText)((HimnoActivity)context).findViewById(R.id.textoHimno)).getText().toString();
		i.putExtra(Intent.EXTRA_SUBJECT, "Reporte de fallo en Himnario Adventista "+versionApp);
		String body = "Modelo: "+Utils.getDeviceName()+"\n--------------------------\n";
		body += tituloHimno+"\n\n"+textoHimno+"\n--------------------------\n\n";
		body += "Si hay alg\u00FAn problema en la letra del himno, por favor corr\u00EDgelo con el texto que hemos puesto arriba. Si quieres reportar otro problema puedes describirlo a continuaci\u00F3n:\n";
		i.putExtra(Intent.EXTRA_TEXT, body);
		return i;
	}
	
	public static String preparaTextoParaBusqueda(String input) {
	    String output = input.trim().toLowerCase(Locale.US);
	    output = output.replaceAll("\u00E1", "a");
	    output = output.replaceAll("\u00E9", "e");
	    output = output.replaceAll("\u00ED", "i");
	    output = output.replaceAll("\u00F3", "o");
	    output = output.replaceAll("\u00FA", "u");
	    output = output.replaceAll("\u00FC", "u");
	    output = output.replaceAll("\u00F1", "n");
	    output = output.replaceAll("\u00A1", " ");
	    output = output.replaceAll("!", " ");
	    output = output.replaceAll("\u00BF", " ");
	    output = output.replaceAll("\\?", " ");
	    output = output.replaceAll("\"", " ");
	    output = output.replaceAll("'", " ");
	    output = output.replaceAll(";", " ");
	    output = output.replaceAll(":", " ");
	    output = output.replaceAll(",", " ");
	    output = output.replaceAll("\\.", " ");
	    output = output.replaceAll("\r", " ");
	    output = output.replaceAll("\n", " ");
	    output = output.replaceAll("\t", " ");
	    output = output.replaceAll("[^a-z]", " ");
	    output = output.replaceAll(" +", " ");
	    return output;
	}

}
