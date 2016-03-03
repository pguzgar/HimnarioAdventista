package mx.daro.himnario;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class AgradecimientosActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agradecimientos);
		
		TextView contenidoAgradecimientoView = (TextView) findViewById(R.id.contenidoAgradecimiento);
		TextView listaNombresView = (TextView) findViewById(R.id.listaNombres);
		TextView contenidoAgradecimientoFinalView = (TextView) findViewById(R.id.contenidoAgradecimientoFinal);
		
		String text="A continuaci\u00F3n nombraremos a las personas que amablemente nos han enviado las correcciones en las letras de los himnos. ¡Gracias por ayudarnos!";
		text+="\n\nEstaremos actualizando la lista peri\u00F3dicamente.\n\n";
		
		String listaNombres = "Abigail Gomez\n"
				+"Alejandro Juarez\n"
				+"Ana Flor Castillo Otiniano\n"
				+"Anita Lindo Venancio\n"
				+"Ariel Garcia\n"
				+"Avi Arel\n"
				+"Belino Martinez\n"
				+"Carlos Lopez Rios\n"
				+"Carlos Spindola\n"
				+"Claudia Ayala\n"
				+"Clemente Rojas\n"
				+"Dorca Duran\n"
				+"Dulce Tzic Monzón\n"
				+"Elena Ojeda\n"
				+"Gabino Rivera\n"
				+"Héctor José Hernández Reinoza\n"
				+"Hilda García Ramírez\n"
				+"Jared Caballerotito\n"
				+"Jhon Edin Carvajal Trujillo\n"
				+"Joel David Villamizar\n"
				+"José Manuel Rodríguez Guillén\n"
				+"Josue Reyes\n"
				+"Juan Carlos Pertuz Sierra\n"
				+"Juan Segura\n"
				+"Laura Solano\n"
				+"Lucero Duran\n"
				+"Luis Allende\n"
				+"Mary San Martin\n"
				+"Mateo Rueda\n"
				+"Michael Guzman Ramos\n"
				+"Miky Miky\n"
				+"Moises Duran\n"
				+"Oscar Focil Perez\n"
				+"ovalle299\n"
				+"Paola Cordero\n"
				+"perez18000\n"
				+"rbs6902\n"
				+"Richard Delgadillo\n"
				+"Roberto Kalbermatter\n"
				+"Ruth M. Cintron Rivera\n"
				+"sagitario222009\n"
				+"Samuel Turcios\n"
				+"sergiomancebo777\n"
				+"sistemascchic\n"
				+"Solange Pairo Collao\n"
				+"V Antonio Leyva Melendez\n"
				+"Willy James\n"
				+"wilmergoitia\n"
				+"Zharick Ruedas\n"
				+ "";
		
		String textoFinal = "\nSi hemos olvidado tu nombre por favor av\u00EDsanos.\n";
		
		contenidoAgradecimientoView.setText(text);
		listaNombresView.setText(listaNombres);
		contenidoAgradecimientoFinalView.setText(textoFinal);
	}
	
	public void atras(View v){
		finish();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
        case android.R.id.home:
        	finish();
	        return true;
	    default: return super.onOptionsItemSelected(item);  
	    }
	}
}
