package mx.daro.himnario;

import mx.daro.himnario.activity.himno.HimnoActivity;
import mx.daro.himnario.util.Constants;
import mx.daro.himnario.util.HimnarioHelper;
import mx.daro.himnario.util.StorageUtils;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockActivity;

public class MainActivity extends SherlockActivity {
	
	private String version;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new HimnarioHelper(getApplicationContext());
		StorageUtils.definePathStorageDefault(getApplicationContext());
		Constants.df.setMaximumFractionDigits(2);
		
       	HimnarioHelper.verificaDirectiorioAnterior();
	}

	public void versionSeleccionada(final View v){
		if(v.getId()==R.id.link1962){
			version = Constants.VERSION_1962;
		}else{
			version = Constants.VERSION_2009;
		}
		muestraHimnario();
	}
	
	private void muestraHimnario(){
		if(version!=null){
			Intent intent = new Intent(this, HimnoActivity.class);
			intent.putExtra(Constants.VERSION_SELECCIONADA, version);
			startActivity(intent);
		}
	}
	
	@Override
	public void onBackPressed() {
		
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

}
