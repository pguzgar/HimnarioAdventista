package mx.daro.himnario;

import java.io.File;
import java.util.List;

import mx.daro.himnario.adapter.HimnoDescargaAdapter;
import mx.daro.himnario.util.Constants;
import mx.daro.himnario.util.HimnarioHelper;
import mx.daro.himnario.util.StorageUtils;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

public class DescargaMainActivity extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_descarga_main);
		new HimnarioHelper(getApplicationContext());
		setTitle("Administrar Descargas");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		TextView info = (TextView)findViewById(R.id.infoTarjeta);
		TextView sel = (TextView)findViewById(R.id.textoSeleccionaHimnario);
		
		
		List<String> paths = StorageUtils.getPathMemoriasExternasDisponibles();
		String texto = StorageUtils.espacioDisponible(getApplicationContext());
		if(paths.size()>0){
			if(paths.size()==1){
				findViewById(R.id.textoGuardarDescargasEn).setVisibility(View.GONE);
				findViewById(R.id.grupoPaths).setVisibility(View.GONE);
				info.setVisibility(View.VISIBLE);
				info.setTextColor(Color.parseColor("#000000"));
				info.setText(texto);
			}else{
				info.setVisibility(View.GONE);
				findViewById(R.id.textoGuardarDescargasEn).setVisibility(View.VISIBLE);
				RadioGroup grupo = (RadioGroup)findViewById(R.id.grupoPaths);
				grupo.setVisibility(View.VISIBLE);
				int index=0;
				for(String p: paths){
					RadioButton rb = new RadioButton(this);
					rb.setId(2511+index);
					rb.setTag(p);
					if(StorageUtils.getPathStorage(getApplicationContext()).equals(p)){
						rb.setChecked(true);
					}
					float mb = StorageUtils.megabytesAvailable(new File(p));
					rb.setText("Memoria "+((index++)+1)+" - Disponible: "+Constants.df.format(mb)+" MB");
					grupo.addView(rb);
				}
				grupo.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(RadioGroup group, int checkedId) {
						RadioButton rb = (RadioButton)findViewById(checkedId);
						String path = (String)rb.getTag();
						StorageUtils.guardaPathDescarga(getApplicationContext(), path);
					}
				});
			}
			sel.setVisibility(View.VISIBLE);
			ListView lista = (ListView)findViewById(R.id.listaHimnariosDescarga);
			lista.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, Constants.himnarios));
			lista.setOnItemClickListener(clicHimnario());
		}else{
			findViewById(R.id.textoGuardarDescargasEn).setVisibility(View.GONE);
			sel.setVisibility(View.GONE);
			info.setTextColor(Color.parseColor("#FF0000"));
			info.setText(texto);
		}
	}
	
	private OnItemClickListener clicHimnario(){
		return new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> lista, View v, int position, long id) {
				muestaPantallaDescargaHimnario(position);
			}
		};
	}
	
	private void muestaPantallaDescargaHimnario(Integer index){
		Intent intent = new Intent(getApplicationContext(), DescargaListaHimnarioActivity.class);
		HimnoDescargaAdapter.clearChecked();
		if(index==0){
			intent.putExtra(Constants.VERSION_DESCARGA, Constants.VERSION_2009);
			intent.putExtra(Constants.TIPO_DESCARCA, Constants.TIPO_REPRODUCCION_CANTADO);
		}else if(index==1){
			intent.putExtra(Constants.VERSION_DESCARGA, Constants.VERSION_2009);
			intent.putExtra(Constants.TIPO_DESCARCA, Constants.TIPO_REPRODUCCION_PISTA);
		}else if(index==2){
			intent.putExtra(Constants.VERSION_DESCARGA, Constants.VERSION_1962);
			intent.putExtra(Constants.TIPO_DESCARCA, Constants.TIPO_REPRODUCCION_CANTADO);
		}else if(index==3){
			intent.putExtra(Constants.VERSION_DESCARGA, Constants.VERSION_1962);
			intent.putExtra(Constants.TIPO_DESCARCA, Constants.TIPO_REPRODUCCION_PISTA);
		}
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
	
	@Override
	protected void onResume(){
	    super.onResume();
	    new HimnarioHelper(getApplicationContext());
	    TextView info = (TextView)findViewById(R.id.infoTarjeta);
	    if(info.getVisibility() == View.VISIBLE){
	    	info.setText(StorageUtils.espacioDisponible(getApplicationContext()));
	    	
	    }
	    RadioGroup grupo = (RadioGroup)findViewById(R.id.grupoPaths);
	    if(grupo.getVisibility() == View.VISIBLE){
	    	for(int i=0; i<grupo.getChildCount(); i++){
	    		if(grupo.getChildAt(i) instanceof RadioButton){
	    			RadioButton rb = (RadioButton)grupo.getChildAt(i);
	    			if(rb.getTag()!=null){
	    				String p = (String)rb.getTag();
	    				float mb = StorageUtils.megabytesAvailable(new File(p));
	    				String[] textoOriginal = rb.getText().toString().split("-");
	    				if(textoOriginal.length>1){
	    					rb.setText(textoOriginal[0]+"- Disponible: "+Constants.df.format(mb)+" MB");
	    				}
	    			}
	    		}
	    	}
	    }
	}

}
