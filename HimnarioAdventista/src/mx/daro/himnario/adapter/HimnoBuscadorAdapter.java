package mx.daro.himnario.adapter;

import java.util.List;

import mx.daro.himnario.R;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HimnoBuscadorAdapter extends ArrayAdapter<JSONObject> {
	
	private final Context context;
	private OnClickListener callback;
	private final List<JSONObject> values;
	
	public HimnoBuscadorAdapter(Context context, List<JSONObject> objects, OnClickListener callback) {
		super(context, R.layout.row_buscador, objects);
		this.context = context;
		this.values = objects;
		this.callback = callback;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolderBuscador viewHolder = null;
		if(convertView==null){
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row_buscador, parent, false);
			viewHolder = new ViewHolderBuscador();
			viewHolder.numero = (TextView) convertView.findViewById(R.id.numeroHimno);
			viewHolder.titulo = (TextView) convertView.findViewById(R.id.tituloHimno);
			viewHolder.textoHimno = (TextView) convertView.findViewById(R.id.textoHimno);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolderBuscador) convertView.getTag();
		}
		convertView.setOnClickListener(callback);
		try {
			viewHolder.numero.setText(values.get(position).getInt("n")+"");
			viewHolder.titulo.setText(values.get(position).getString("t"));
			viewHolder.textoHimno.setText(values.get(position).getString("x"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return convertView;
	}
	
	static class ViewHolderBuscador {
		TextView titulo;
		TextView numero;
		TextView textoHimno;
	}
}
