package mx.daro.himnario;

import java.util.ArrayList;
import java.util.List;

import mx.daro.himnario.activity.himno.HimnoActivity;
import mx.daro.himnario.adapter.HimnoFavoritoAdapter;
import mx.daro.himnario.util.Constants;
import mx.daro.himnario.util.FavoritosUtil;
import mx.daro.himnario.util.MensajeUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class FavoritosActivity extends SherlockActivity {

	private static String version;

	private HimnoFavoritoAdapter adapter;
	private FavoritosActivity myActivity;
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		myActivity = this;
		setContentView(R.layout.activity_favoritos_lista);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		Intent intent = getIntent();
		version = intent.getStringExtra(Constants.VERSION_SELECCIONADA);
		
		setTitle("Favoritos");

		List<JSONObject> listaFavoritos = new ArrayList<JSONObject>();
		ListView listaView = (ListView) findViewById(R.id.listaFavoritos);
		JSONArray lista = FavoritosUtil.getFavoritos(this, version);
		
		for(int i=0; i<lista.length(); i++){
			try {
				listaFavoritos.add(lista.getJSONObject(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		adapter = new HimnoFavoritoAdapter(this, version, listaFavoritos, new OnClickListener() {
			@Override
			public void onClick(View v) {
				TextView numero = (TextView)v.findViewById(R.id.numeroHimnoFavorito);
				muestaPantallaHimno(Integer.valueOf(numero.getText().toString()));
			}
		});
		listaView.setAdapter(adapter);
	}
	
	public void eliminarFavorito(final View v){
		muestraMensajeEliminarFavorito(v.getTag().toString().split(",")[1], new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which == DialogInterface.BUTTON_POSITIVE){
					FavoritosUtil.eliminaFavoritos(myActivity, v.getTag().toString().split(",")[0], Integer.valueOf(v.getTag().toString().split(",")[1]));
					
					JSONArray lista = FavoritosUtil.getFavoritos(myActivity, version);
					adapter.clear();
					for(int i=0; i<lista.length(); i++){
						try {
							adapter.add(lista.getJSONObject(i));
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
					adapter.notifyDataSetChanged();
				}
			}
		});
		
	}
	
	public void subirFavorito(final View v){
		FavoritosUtil.subirFavoritos(myActivity, 
				v.getTag().toString().split(",")[0], 
				Integer.valueOf(v.getTag().toString().split(",")[1]), 
				Integer.valueOf(v.getTag().toString().split(",")[2]));
		
		JSONArray lista = FavoritosUtil.getFavoritos(myActivity, version);
		adapter.clear();
		for(int i=0; i<lista.length(); i++){
			try {
				adapter.add(lista.getJSONObject(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	public void bajarFavorito(final View v){
		FavoritosUtil.bajarFavoritos(myActivity, 
				v.getTag().toString().split(",")[0], 
				Integer.valueOf(v.getTag().toString().split(",")[1]), 
				Integer.valueOf(v.getTag().toString().split(",")[2]));
		
		JSONArray lista = FavoritosUtil.getFavoritos(myActivity, version);
		adapter.clear();
		for(int i=0; i<lista.length(); i++){
			try {
				adapter.add(lista.getJSONObject(i));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		adapter.notifyDataSetChanged();
	}
	
	private void muestaPantallaHimno(Integer numero){
		Intent intent = new Intent(getApplicationContext(), HimnoActivity.class);
		intent.putExtra(Constants.HIMNO_SELECCIONADO, numero);
		startActivity(intent);
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

	private void muestraMensajeEliminarFavorito(String numero, DialogInterface.OnClickListener listener){
		MensajeUtil.preguntaBorrar(this, "Favoritos", 
				"\u00BFEst\u00E1s seguro que deseas borrar el himno "+numero+" de la lista?",listener);
	}

}
