package mx.daro.himnario.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mx.daro.himnario.R;
import mx.daro.himnario.util.HimnarioHelper;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class HimnoDescargaAdapter extends ArrayAdapter<JSONObject> {
	
	private final Context context;
	private final List<JSONObject> values;
	private String tipo;
	private String version;
	private OnClickListener callback;
	private static Map<Integer, Boolean> checkeds = new HashMap<Integer, Boolean>();
	
	public HimnoDescargaAdapter(Context context, String version, String tipo, List<JSONObject> listaTitulos, OnClickListener callback) {
		super(context, R.layout.row_himno_descarga, listaTitulos);
		this.context = context;
		this.values = listaTitulos;
		this.version = version;
		this.tipo = tipo;
		this.callback = callback;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.row_himno_descarga, parent, false);
		rowView.setOnClickListener (callback);
		CheckBox numeroHimnoCheck = (CheckBox) rowView.findViewById(R.id.checkBoxNumeroDescarga);
		numeroHimnoCheck.setOnClickListener(callback);
		TextView tituloHimno = (TextView) rowView.findViewById(R.id.tituloHimnoDescarga);
		ImageView imagenDescarga = (ImageView) rowView.findViewById(R.id.imagenDescargado);
		try {
			Integer numero = values.get(position).getInt("n");
			numeroHimnoCheck.setText(numero+"");
			Boolean checked = checkeds.get(numero);
			if(checked!=null && checked) numeroHimnoCheck.setChecked(true);
			tituloHimno.setText(values.get(position).getString("t"));
			if(!HimnarioHelper.existeHimno(tipo, version, values.get(position).getInt("n")))
				imagenDescarga.setImageDrawable(getContext().getResources().getDrawable(R.drawable.descarga_palido));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return rowView;
	}
	
	public void saveChecked(Integer numero, Boolean value){
		checkeds.put(numero, value);
	}
	
	public Map<Integer, Boolean> getCheckeds(){
		return checkeds;
	}
	
	public static void clearChecked(){
		checkeds = new HashMap<Integer, Boolean>();
	}
}
