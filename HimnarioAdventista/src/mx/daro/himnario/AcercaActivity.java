package mx.daro.himnario;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class AcercaActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_acerca);
		PackageInfo pInfo;
		String versionApp = "";
		try {
			pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			versionApp = pInfo.versionName;
		} catch (NameNotFoundException e) {}
		TextView versionView = (TextView) findViewById(R.id.versionAcerca);
		versionView.setText("Versi\u00F3n "+versionApp);
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
