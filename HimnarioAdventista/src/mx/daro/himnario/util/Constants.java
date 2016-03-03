package mx.daro.himnario.util;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Constants {
	
	public static final Map<String, Integer> maximos = new HashMap<String, Integer>(){
		private static final long serialVersionUID = 1L;
		{put(Constants.VERSION_1962,527); put(Constants.VERSION_2009,613);}
	};
	public static final Map<String, String> espacioRequerido = new HashMap<String, String>(){
		private static final long serialVersionUID = 1L;
		{
			put(Constants.VERSION_1962+Constants.TIPO_REPRODUCCION_PISTA,"1.2 GB"); 
			put(Constants.VERSION_1962+Constants.TIPO_REPRODUCCION_CANTADO,"1.3 GB"); 
			put(Constants.VERSION_2009+Constants.TIPO_REPRODUCCION_PISTA,"1.5 GB");
			put(Constants.VERSION_2009+Constants.TIPO_REPRODUCCION_CANTADO,"2.3 GB");
		}
	};

	
	public final static String VERSION_SELECCIONADA = "mx.daro.himnario.VERSION_SELECCIONADA";
	public final static String HIMNO_SELECCIONADO = "mx.daro.himnario.HIMNO_SELECCIONADO";
	
	public final static String TIPO_DESCARCA = "mx.daro.himnario.TIPO_DESCARGA";
	public final static String VERSION_DESCARGA = "mx.daro.himnario.VERSION_DESCARGA";
	public final static String[] himnarios = {"Himnario Nuevo - Cantos", "Himnario Nuevo - Pistas", "Himnario Anterior - Cantos", "Himnario Anterior - Pistas"};


	public static final float medidaLayoutOpciones = 1.5f;
	public static final float medidaMaximaOpcionesPorcentajePantalla = 0.75f;
	public static final String BROADCAST_SEEKBAR = "mx.daro.himnario.reproductorSeekBar";
	public static final String NOMBRE_PREFERENCIAS = "titanium";
	public static final String PREF_TAMANO_TEXTO="tamanoTexto";
	public static final String PREF_PATH_DESCARGAS="pathDescargas";
	public static final String PREF_COLOR_FONDO="colorFondo";
	public static final String PREF_VERSION_GUARDADA="versionGuardada";
	public static final String PREF_HIMNO_GUARDADO="himnoGuardado";
	public static final String TXT_TOCAR_PISTA="Pista";
	public static final String TXT_QUITAR_PISTA="Quitar Pista";
	public static final String TXT_TOCAR_CANCION="Canci\u00F3n";
	public static final String TXT_QUITAR_CANCION="Quitar Canci\u00F3n";
	public static final String TIPO_REPRODUCCION_PISTA="pistas";
	public static final String TIPO_REPRODUCCION_CANTADO="cantado";
	public static final String VERSION_2009="2009";
	public static final String VERSION_1962="1962";
	public static final String TXT_OPCIONES="Opciones";
	
	
	public static final String DESCARGA_OPCION_DESCARGA="Descargar";
	public static final String DESCARGA_OPCION_BORRAR="Borrar";
	public static final String DESCARGA_OPCION_DESCARGAR_SELECCIONADOS="Descargar seleccionados";
	public static final String DESCARGA_OPCION_DESCARGAR_TODO="Descargar todo";
	public static final String DESCARGA_OPCION_BORRAR_SELECCIONADOS="Borrar seleccionados";
	public static final String DESCARGA_OPCION_BORRAR_TODO="Borrar todo";
	
	public static final String HIMNARIO_OPCION_SIGUIENTE="Siguiente";
	public static final String HIMNARIO_OPCION_ANTERIOR="Anterior";
	public static final String HIMNARIO_OPCION_FAVORITOS="Favoritos";
	public static final String HIMNARIO_OPCION_AGREGAR_FAVORITOS="Agregar a Favoritos";
	public static final String HIMNARIO_OPCION_LISTA_FAVORITOS="Lista de Favoritos";
	
	public static final String FAVORITOS_DB="favoritosDB";
	
	public final static String DIR_ANTERIOR_MOVIDO = "mx.daro.himnario.DIR_ANTERIOR_MOVIDO";
	
    public static final NumberFormat df = NumberFormat.getInstance(Locale.US);

}
