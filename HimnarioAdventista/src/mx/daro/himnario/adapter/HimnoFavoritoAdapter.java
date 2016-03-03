package mx.daro.himnario.adapter;

import java.util.List;

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
import android.widget.ImageView;
import android.widget.TextView;

public class HimnoFavoritoAdapter extends ArrayAdapter<JSONObject> {
	
	private final Context context;
	private final List<JSONObject> values;
	private OnClickListener callback;
	private String version;
	
	public HimnoFavoritoAdapter(Context context, String version, List<JSONObject> listaFavoritos, 
			OnClickListener callback) {
		super(context, R.layout.row_himno_favorito, listaFavoritos);
		this.context = context;
		this.values = listaFavoritos;
		this.callback = callback;
		this.version = version;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		
		if(convertView==null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_himno_favorito, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.numero = (TextView) convertView.findViewById(R.id.numeroHimnoFavorito);
			viewHolder.titulo = (TextView) convertView.findViewById(R.id.tituloHimnoFavorito);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		convertView.setOnClickListener(callback);
		
		try {
			Integer numero = values.get(position).getInt("numero");
			JSONObject himno = HimnarioHelper.getHimnoTitulo(context, version, numero);
			if(himno!=null){
				viewHolder.numero.setText(numero+"");
				viewHolder.titulo.setText(himno.getString("t"));
				
				ImageView botonEliminar = (ImageView)convertView.findViewById(R.id.imagenEliminarFavorito);
				botonEliminar.setTag(version+","+numero);
				
				ImageView botonSubir = (ImageView)convertView.findViewById(R.id.imagenSubirFavorito);
				botonSubir.setTag(version+","+numero+","+position);
				
				ImageView botonBajar = (ImageView)convertView.findViewById(R.id.imagenBajarFavorito);
				botonBajar.setTag(version+","+numero+","+position);
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}
	
	static class ViewHolder {
		TextView titulo;
		TextView numero;
	}
}
