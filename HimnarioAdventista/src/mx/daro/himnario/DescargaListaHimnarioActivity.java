package mx.daro.himnario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import mx.daro.himnario.adapter.HimnoDescargaAdapter;
import mx.daro.himnario.listener.GeneralListener;
import mx.daro.himnario.listener.GeneralListenerWithParameter;
import mx.daro.himnario.listener.OnDownloadCompleteListener;
import mx.daro.himnario.util.ConexionUtil;
import mx.daro.himnario.util.Constants;
import mx.daro.himnario.util.DescargaHelper;
import mx.daro.himnario.util.HimnarioHelper;
import mx.daro.himnario.util.MensajeUtil;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;

public class DescargaListaHimnarioActivity extends SherlockActivity {

	private static String version;
	private static String tipo;
	private static String tipoAlias;
	private static String versionAlias;
	
	private static String error;
	private static boolean pausa;
	
	private AlertDialog alerta;
	
	private HimnoDescargaAdapter adapter;
	private List<Integer> aDesgargar;
	
	@SuppressLint("DefaultLocale")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_descarga_lista);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		Intent intent = getIntent();
		version = intent.getStringExtra(Constants.VERSION_DESCARGA);
		tipo = intent.getStringExtra(Constants.TIPO_DESCARCA);
		if(Constants.VERSION_1962.equals(version)){
			versionAlias="Anterior";
		}else{
			versionAlias="Nuevo";
		}
		if(Constants.TIPO_REPRODUCCION_CANTADO.equals(tipo)){
			tipoAlias="cantos";
		}else{
			tipoAlias="pistas";
		}
		setTitle(versionAlias+" - "+tipoAlias.substring(0,1).toUpperCase()+tipoAlias.substring(1));
		
		ListView lista = (ListView) findViewById(R.id.listaHimnosDescarga);
		ArrayList<JSONObject> listaTitulos = HimnarioHelper.getListaTitulos(this, version);
		adapter = new HimnoDescargaAdapter(this, version, tipo, listaTitulos, new OnClickListener() {
			@Override
			public void onClick(View v) {
				CheckBox check = null;
				if(v.getId()==R.id.checkBoxNumeroDescarga){
					check = (CheckBox)v;
					save(Integer.valueOf(check.getText().toString()),check.isChecked());
				}else{
					check = (CheckBox)v.findViewById(R.id.checkBoxNumeroDescarga);
					check.setChecked(!check.isChecked());
					save(Integer.valueOf(check.getText().toString()),check.isChecked());
				}
			}
		});
		lista.setAdapter(adapter);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getTitle().equals(Constants.DESCARGA_OPCION_DESCARGAR_SELECCIONADOS)){
			descargaSeleccionadosVerificarConexion();
			return true;
		}else if(item.getTitle().equals(Constants.DESCARGA_OPCION_DESCARGAR_TODO)){
			descargaTodosVerificarConexion();
			return true;
		}else if(item.getTitle().equals(Constants.DESCARGA_OPCION_BORRAR_SELECCIONADOS)){
			borrarSeleccionados();
			return true;
		}else if(item.getTitle().equals(Constants.DESCARGA_OPCION_BORRAR_TODO)){
			borrarTodo();
			return true;
		}else if(item.getItemId() == android.R.id.home){
			finish();
			return true;
		}
	    return super.onOptionsItemSelected(item);  
	}
	
	private void borrarSeleccionados(){
		final List<Integer> aBorrar = new ArrayList<Integer>();
		for(Integer key:adapter.getCheckeds().keySet()){
			Boolean s=adapter.getCheckeds().get(key);
			if(s!=null && s){
				aBorrar.add(key);
			}
		}
		if(!aBorrar.isEmpty()){
			muestraMensajeBorrarSeleccionados(new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(which == DialogInterface.BUTTON_POSITIVE){
						for(Integer i: aBorrar){
							HimnarioHelper.eliminaHimno(tipo, version, i);
					    }
						finish();
						startActivity(getIntent());
					}
				}
			});
		}
	}
	
	private void borrarTodo(){
		muestraMensajeBorrarTodo(new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which == DialogInterface.BUTTON_POSITIVE){
					for(int i=1;i<=Constants.maximos.get(version);i++){
						HimnarioHelper.eliminaHimno(tipo, version, i);
				    }
					finish();
					startActivity(getIntent());
				}
			}
		});
	}
	
	private void descargaTodosVerificarConexion(){
		if(!new ConexionUtil(this).isConnectedToInternet()){
			muestaMensajeNoConexion();
		}else{
			Boolean conexionCelular = new ConexionUtil(this).isConnectedMobile();
			if(conexionCelular){
				muestraMensajeConectadoPorCelular(new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which == DialogInterface.BUTTON_POSITIVE){
							descargaTodosVerificarEspacio();
						}
					}
				});
			}else{
				descargaTodosVerificarEspacio();
			}
		}
	}
	
	private void descargaTodosVerificarEspacio(){
		String total = Constants.espacioRequerido.get(version+tipo);
		String adj = (Constants.TIPO_REPRODUCCION_CANTADO.equals(tipo))?"todos los":"todas las";
		alerta = MensajeUtil.pretuntaDescargar(this, "Para descargar "+adj+" "+tipoAlias+" del Himnario "+versionAlias+" necesitas "+total+" de espacio libre en tu memoria " +
				"y tu conexi\u00F3n estar\u00E1 ocupada. Deber\u00E1s esperar varios minutos para finalizar. " +
			"\u00BFEst\u00E1s seguro que deseas descargarlos?",
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if(which == DialogInterface.BUTTON_POSITIVE){
						descargaTodos();
					}
				}
			}
		);		
	}
	
	private void descargaSeleccionadosVerificarConexion(){
		if(!new ConexionUtil(this).isConnectedToInternet()){
			muestaMensajeNoConexion();
		}else{
			Boolean conexionCelular = new ConexionUtil(this).isConnectedMobile();
			if(conexionCelular){
				muestraMensajeConectadoPorCelular(new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which == DialogInterface.BUTTON_POSITIVE){
							preparaDescargaSeleccionados();
						}
					}
				});
			}else{
				preparaDescargaSeleccionados();
			}
		}
	}
	
	private void descargaTodos(){
		aDesgargar = new LinkedList<Integer>();
		for(int i=1;i<=Constants.maximos.get(version);i++){
			if(!HimnarioHelper.existeHimno(tipo, version, i)){
				aDesgargar.add(i);
			}
	    }
		if(!aDesgargar.isEmpty()){
			Integer numero = aDesgargar.get(0);
			aDesgargar.remove(numero);
			descarga(numero);
		}
	}
	
	private void preparaDescargaSeleccionados(){
		aDesgargar = new ArrayList<Integer>();
		for(Integer key:adapter.getCheckeds().keySet()){
			Boolean s=adapter.getCheckeds().get(key);
			if(s!=null && s){
				aDesgargar.add(key);
			}
		}
		if(!aDesgargar.isEmpty()){
			if(aDesgargar.size()>5){
				muestraMensajeMuchosSeleccionados(aDesgargar.size(), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which == DialogInterface.BUTTON_POSITIVE){
							descargaSeleccionados();
						}
					}
				});
			}else{
				descargaSeleccionados();
			}
			
		}else{
			muestaMensajeNoSeleccion();
		}
	}
	
	private void descargaSeleccionados(){
		if(aDesgargar!=null && !aDesgargar.isEmpty()){
			Collections.sort(aDesgargar);
			Integer numero = aDesgargar.get(0);
			aDesgargar.remove(numero);
			descarga(numero);
		}
	}
	
	private void descarga(final Integer numero){
		System.out.println("PGG - Descargando: "+numero);
		final DescargaHelper helper = new DescargaHelper(this,tipo,version,numero);
		helper.setDownloadCompleteListener(new OnDownloadCompleteListener() {
			@Override
			public void onComplete() {
				System.out.println("PGG - Descarga completa.");
				if(aDesgargar != null && !aDesgargar.isEmpty()){
					final Integer numeroNuevo = aDesgargar.get(0);
					aDesgargar.remove(numeroNuevo);
					Runnable runnable = new Runnable() {public void run () {
						descarga(numeroNuevo);
					}};
					new Handler().postDelayed(runnable, 100);
				}else{
					HimnoDescargaAdapter.clearChecked();
					finish();
					startActivity(getIntent());
				}
			}
		});
		helper.setOnCancel(new GeneralListener() {
			@Override
			public void execute() {
				System.out.println("PGG - Descarga cancelada.");
				aDesgargar=new ArrayList<Integer>();
				HimnoDescargaAdapter.clearChecked();
				finish();
				startActivity(getIntent());
			}
		});
		helper.setOnError(new GeneralListenerWithParameter() {
			@Override
			public void execute(String message) {
				System.out.println("PGG - Error en la descarga.");
				error = message;
				aDesgargar=new ArrayList<Integer>();
				showError();
			}
		});
		helper.descarga();
	}
	
	private void showError(){
		if(!pausa){
			alerta = MensajeUtil.mensajeConCallback(this, "Error", error,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which == DialogInterface.BUTTON_POSITIVE){
							error=null;
							HimnoDescargaAdapter.clearChecked();
							finish();
							startActivity(getIntent());
						}
					}
				});
		}
	}
	
	private void muestaMensajeNoSeleccion(){
		alerta = MensajeUtil.mensaje(this, "Descargas", "Por favor selecciona los himnos que deseas descargar.");
	}
	
	private void muestaMensajeNoConexion(){
		alerta = MensajeUtil.mensaje(this, "Descargas", "Para descargar los himnos debes conectarte a Internet.");
	}
	
	@SuppressLint("DefaultLocale")
	private void muestraMensajeConectadoPorCelular(DialogInterface.OnClickListener listener){
		String operador = new ConexionUtil(this).getNombreOperador();
		alerta = MensajeUtil.preguntaContinuar(this, "Descargas", "Est\u00E1s conectado a la red m\u00F3vil de "+operador.toUpperCase(Locale.US)+". " +
				"Si no tienes un plan ilimitado, puede haber cargos extras por los Megabytes descargados. " +
				"\u00BFDeseas continuar?",listener);
	}
	
	private void muestraMensajeMuchosSeleccionados(Integer total, DialogInterface.OnClickListener listener){
		alerta = MensajeUtil.preguntaContinuar(this, "Descargas", "Has seleccionado "+total+" himnos para descargar. " +
				"Eso puede tardar varios minutos y tu conexi\u00F3n estar\u00E1 ocupada. " +
				"\u00BFDeseas continuar?",listener);
	}
	
	private void muestraMensajeBorrarTodo(DialogInterface.OnClickListener listener){
		alerta = MensajeUtil.preguntaBorrar(this, "Descargas", 
				"\u00BFEst\u00E1s seguro que deseas borrar todas las descargas de "+tipoAlias+" del Himnario "+versionAlias+"?",listener);
	}

	private void muestraMensajeBorrarSeleccionados(DialogInterface.OnClickListener listener){
		alerta = MensajeUtil.preguntaBorrar(this, "Descargas", 
				"\u00BFEst\u00E1s seguro que deseas borrar los himnos seleccionados?",listener);
	}

	
	private void save(Integer numero, Boolean value){
		adapter.saveChecked(numero, value);
	}
	
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        SubMenu sub2 = menu.addSubMenu(Constants.DESCARGA_OPCION_DESCARGA).setIcon(R.drawable.descarga2);
        sub2.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        SubMenu sub3 = menu.addSubMenu(Constants.DESCARGA_OPCION_BORRAR).setIcon(R.drawable.eliminar);
        sub3.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        sub2.add(Constants.DESCARGA_OPCION_DESCARGAR_SELECCIONADOS);
        sub2.add(Constants.DESCARGA_OPCION_DESCARGAR_TODO);
        sub3.add(Constants.DESCARGA_OPCION_BORRAR_SELECCIONADOS);
        sub3.add(Constants.DESCARGA_OPCION_BORRAR_TODO);
		return true;
	}
	
	@Override
	protected void onResume(){
	    super.onResume();
	    pausa=false;
	    new HimnarioHelper(getApplicationContext());
	    if(DescargaHelper.servicioIniciado() && DescargaHelper.descargaIniciada() && !DescargaHelper.isCompletado()){
	    	DescargaHelper.showDialog();
	    }else{
	    	if(error!=null){
	    		showError();
	    	}
	    }
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		if(DescargaHelper.servicioIniciado()){
			DescargaHelper.closeDialog();
		}
		if(alerta!=null){
			alerta.dismiss();
		}
		pausa=true;
	}
}
