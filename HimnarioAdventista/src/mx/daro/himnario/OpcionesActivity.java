package mx.daro.himnario;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class OpcionesActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opciones);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.opciones, menu);
		return true;
	}
}
