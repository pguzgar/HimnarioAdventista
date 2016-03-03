package mx.daro.himnario.activity.himno;

import java.util.HashMap;
import java.util.Map;

import mx.daro.himnario.MainActivity;
import mx.daro.himnario.R;
import mx.daro.himnario.service.ReproductorService;
import mx.daro.himnario.util.Constants;
import mx.daro.himnario.util.DescargaHelper;
import mx.daro.himnario.util.FavoritosUtil;
import mx.daro.himnario.util.HimnarioHelper;
import mx.daro.himnario.util.MensajeUtil;
import mx.daro.himnario.util.Utils;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class HimnoActivity extends SlidingFragmentActivity {
	
	public static FragmentActivity myActivity;
	public static HimnoActivity myHimnoActivity;
	public static String versionSeleccionada;
	public static String tipo;
	public static Integer himnoSeleccionado;
	public static boolean tocando;
	public static boolean preparando;
	public static boolean pausado;
	public static boolean softStop;
	public static boolean servicioReproductorCreado;
	public static boolean mostrandoLoading;
	public static int progresoDescargado;
	public static int progressGuardado;
	private static boolean pausa;
	@SuppressLint("UseSparseArrays")
	public static Map<Integer, Integer> heightApp = new HashMap<Integer, Integer>();
	public static String menuDescargaMostradoPara;
	
	public static int widthPantalla;
	public static int heightPantalla;
	public static float pppWidth;
	public static float pppHeight;
	public static int widthMaximoOpciones;
	public static int heightMaximoBotones;
	
	public static String error;
	
	public final Handler timerReproductor = new Handler();
	public DescargaHelper helper;
	private AlertDialog alerta;
	private Menu menuActionBar;
	
	HimnoActivityBottomOptions barraBotones = new HimnoActivityBottomOptions();
	HimnoActivityPantallaConfiguracion pantallaConfig = new HimnoActivityPantallaConfiguracion();
	HimnoActivityReproductor reproductor = new HimnoActivityReproductor();
	HimnoActivityConfiguracion config = new HimnoActivityConfiguracion();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_himno);
		new HimnarioHelper(getApplicationContext());
		myActivity = this;
		myHimnoActivity = this;
		
		pantallaConfig.inicializaConfiguracionSlider(this);
		pantallaConfig.configuraTamanoSlider(this);
		config.aplicaPreferenciasDelUsuario(this);
		barraBotones.configuracionBarrasYBotones(this);
		pantallaConfig.configuracionPantallaOpciones(this);
		reproductor.configuracionReproductor(this);
	    barraBotones.configuracionAltoBotonesHimno(this);
		
		Intent intent = getIntent();
		versionSeleccionada = intent.getStringExtra(Constants.VERSION_SELECCIONADA);
		himnoSeleccionado = intent.getIntExtra(Constants.HIMNO_SELECCIONADO, -1);
		if(versionSeleccionada==null){
			versionSeleccionada = config.getVersionGuardada(this);
		}
		if(himnoSeleccionado==-1){
			himnoSeleccionado=config.getHimnoGuardado(this);
			if(himnoSeleccionado==-1) himnoSeleccionado=1;
		}
	    runOnUiThread(new Runnable() {
	        public void run() {
        		muestraHimno(versionSeleccionada, himnoSeleccionado);
	        }
	    });
	    getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		config.guardaVersionHimno(this, versionSeleccionada);
	}
	

	public void muestraHimno(String version, Integer numero){
		try{
			versionSeleccionada = version;
			himnoSeleccionado = numero;
			
			TextView tituloView = (TextView) findViewById(R.id.tituloHimno);
			EditText textoHimnoView = (EditText) findViewById(R.id.textoHimno);
			Button reportarFallaBoton = (Button) findViewById(R.id.button2);

			String versionAlias = "Anterior";
			if(version.equals(Constants.VERSION_2009)) versionAlias="Nuevo";
			
			JSONObject himno = HimnarioHelper.getHimnoJSON(this, version, numero);
			if(himno==null){
				numero = 1;
				himno = HimnarioHelper.getHimnoJSON(this, version, numero);
			}
			himnoSeleccionado = numero;
			String titulo = himno.getString("t");
			String texto = himno.getString("x")+"\n\n";
			
			tituloView.setText("Himno # "+numero+" ("+versionAlias+")\n\n"+titulo);
			textoHimnoView.setText(texto);
			String th = "Men\u00FA";
			getSupportActionBar().setTitle(th);
			config.guardaNumeroHimno(this, numero);
			
			setFavoriteIcon();
			reportarFallaBoton.setText("Reportar falla en Himno # "+numero);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void setFavoriteIcon(){
		if(menuActionBar!=null){
			FavoritosUtil.cambiaIcono(menuActionBar, FavoritosUtil.isFavorito(this,versionSeleccionada,himnoSeleccionado));
		}
	}
	
	public void showError(){
		if(!pausa){
			alerta = MensajeUtil.mensajeConCallback(this, "Error", error,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if(which == DialogInterface.BUTTON_POSITIVE){
							error=null;
						}
					}
				});
		}
	}

	public void regresarAlMenu(){
		reproductor.pararReproduccion(this, null);
		barraBotones.hideOpcionesDescarga(this);
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    final HimnoActivity thisActivity = this;
	    Runnable runnable = new Runnable() {
		    public void run () {
		    	pantallaConfig.configuraTamanoSlider(thisActivity);
		    }
		};
		new Handler().postDelayed(runnable, 1000);
	    if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	    } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
	    }
	    barraBotones.configuracionAltoBotonesHimno(this);
	    if(getSlidingMenu().isMenuShowing()) toggle();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        SubMenu anterior = menu.addSubMenu(Constants.HIMNARIO_OPCION_ANTERIOR).setIcon(R.drawable.anterior);
        anterior.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);

        SubMenu favoritos = menu.addSubMenu(Constants.HIMNARIO_OPCION_FAVORITOS).setIcon(R.drawable.favoritos_no);
        favoritos.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        favoritos.add(Constants.HIMNARIO_OPCION_AGREGAR_FAVORITOS);
        favoritos.add(Constants.HIMNARIO_OPCION_LISTA_FAVORITOS);

        SubMenu siguiente = menu.addSubMenu(Constants.HIMNARIO_OPCION_SIGUIENTE).setIcon(R.drawable.siguiente);
        siguiente.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        
        SubMenu configMenu = menu.addSubMenu(Constants.TXT_OPCIONES).setIcon(R.drawable.ic_menu_preferences);
        configMenu.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        
        menuActionBar = menu;
        setFavoriteIcon();
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return HimnoActivityMenuOptions.procesaOpcionSeleccionada(this, item);
	}
	
	@Override
	public void onBackPressed() {
		regresarAlMenu();
	}
	
	@Override
	protected void onResume(){
	    super.onResume();
	    pausa=false;
	    final HimnoActivity thisActivity = this;
	    new HimnarioHelper(getApplicationContext());
	    if(DescargaHelper.servicioIniciado() && DescargaHelper.descargaIniciada() && !DescargaHelper.isCompletado()){
	    	if(helper==null){
	    		helper = new DescargaHelper(getApplicationContext(), tipo, versionSeleccionada, himnoSeleccionado);
		    	helper.setDownloadCompleteListener(HimnoActivityDescarga.getDownloadComplete(thisActivity));
		    }
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
		pausa=true;
		if(DescargaHelper.servicioIniciado()){
			DescargaHelper.closeDialog();
		}
	}

	public Runnable actualizaAvanceReproductor = new Runnable() {
		public void run() {
			if(HimnoActivity.tocando){
				int progreso = ReproductorService.getProgress();
				HimnoActivity.progressGuardado = progreso;
				SeekBar sb = (SeekBar) findViewById(R.id.progreso);
				sb.setProgress(progreso);
				TextView t = (TextView)findViewById(R.id.segundosReproducidos);
				t.setText(Utils.formatoTiempo(progreso));
				TextView tf = (TextView)findViewById(R.id.segundosFaltantes);
				int faltantes = ReproductorService.getDuration()-progreso;
				tf.setText("-"+Utils.formatoTiempo(faltantes));
				if(ReproductorService.isPlaying())
					timerReproductor.postDelayed(this, 1000);
				else
					timerReproductor.removeCallbacks(this);
			}
		}
	};
	
	public void reportarFalla(View v){
		pantallaConfig.reportarFalla(this, v);
	}
	public void showPantallaAcercaDe(View v){
		pantallaConfig.showPantallaAcercaDe(this, v);
	}
	public void showAgradecimientos(View v){
		pantallaConfig.showPantallaAgradecimientos(this, v);
	}
	public void showAdministrarDescargas(View v){
		pantallaConfig.showAdministrarDescargas(this, v);
	}
	public void showPantallaBuscador(View v){
		barraBotones.showPantallaBuscador(this,v);
	}
	public void clickDescargar(View v){
		barraBotones.clickDescargar(this, v);
	}
	public void clickTocar(View v){
		barraBotones.clickTocar(this, v);
	}
	public void clickTocarPista(View v){
		barraBotones.clickTocarPista(this, v);
	}
	public void clickTocarCancion(View v){
		barraBotones.clickTocarCancion(this, v);
	}
	public void pausarReproduccion(View v){
		reproductor.pausarReproduccion(this, v);
	}

}
