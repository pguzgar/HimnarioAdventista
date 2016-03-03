package mx.daro.himnario;

import java.util.ArrayList;

import mx.daro.himnario.activity.himno.HimnoActivity;
import mx.daro.himnario.adapter.HimnoBuscadorAdapter;
import mx.daro.himnario.util.Constants;
import mx.daro.himnario.util.HimnarioHelper;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;

public class BuscadorActivity extends SherlockActivity {
	
	private Handler handler;
	private Runnable runnable;
	
	private static String versionSeleccionada;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buscador);
		
		Intent intent = getIntent();
		versionSeleccionada = intent.getStringExtra(Constants.VERSION_SELECCIONADA);
	    ArrayList<JSONObject> datos = HimnarioHelper.getListaHimnosFiltrados(this, versionSeleccionada, null);
		ListView lista = (ListView) findViewById(R.id.listaHimnos);
		lista.setAdapter(new HimnoBuscadorAdapter(this, datos, new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView numero = (TextView)v.findViewById(R.id.numeroHimno);
				muestaPantallaHimno(Integer.valueOf(numero.getText().toString()));
			}
		}));
	}
	
	private void muestaPantallaHimno(Integer numero){
		Intent intent = new Intent(getApplicationContext(), HimnoActivity.class);
		intent.putExtra(Constants.VERSION_SELECCIONADA, versionSeleccionada);
		intent.putExtra(Constants.HIMNO_SELECCIONADO, numero);
		startActivity(intent);
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
        SearchView searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setQueryHint("N\u00FAmero o texto");
        menu.add(Menu.NONE,1,Menu.NONE,"Buscar").setIcon(R.drawable.buscarazul).setActionView(searchView).setShowAsAction(
                        MenuItem.SHOW_AS_ACTION_IF_ROOM  | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				buscar(query);
				return false;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				final String query = newText;
				if(runnable==null){
					runnable =new Runnable() {
					   @Override
					   public void run() {
					      filtrar(query);
					   }
					};
				}
				if(handler==null){
					handler = new Handler();
					handler.postDelayed(runnable, 500);
				}else{
					handler.removeCallbacks(runnable);
					runnable=new Runnable() {
					   @Override
					   public void run() {
					      filtrar(query);
					   }
					};
					handler.postDelayed(runnable, 500);
				}
				return false;
			}
		});
        searchView.setFocusable(true);
        searchView.setIconifiedByDefault(true);
        return true;
	}
	
	private void filtrar(String query){
		ListView lista = (ListView) findViewById(R.id.listaHimnos);
		ArrayList<JSONObject> datos = HimnarioHelper.getListaHimnosFiltrados(this, versionSeleccionada, query);
		lista.setAdapter(new HimnoBuscadorAdapter(this, datos, new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView numero = (TextView)v.findViewById(R.id.numeroHimno);
				muestaPantallaHimno(Integer.valueOf(numero.getText().toString()));
			}
		}));
	}

	private void buscar(String query){
		Integer numero = 0;
		try{
			numero = Integer.valueOf(query);
		}catch(Exception e){}
		if(numero>0){
			muestaPantallaHimno(numero);
		}else{
			ListView lista = (ListView) findViewById(R.id.listaHimnos);
			if(lista.getAdapter().getCount()==1){
				JSONObject himno = (JSONObject)lista.getAdapter().getItem(0);
				try {
					muestaPantallaHimno(himno.getInt("n"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		MenuItem item = menu.findItem(1);
		item.expandActionView();
		return true;
	}
	
}
