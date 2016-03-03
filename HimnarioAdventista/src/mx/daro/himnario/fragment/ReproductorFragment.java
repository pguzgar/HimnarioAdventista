package mx.daro.himnario.fragment;

import mx.daro.himnario.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ReproductorFragment extends Fragment {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return getLayoutInflater(savedInstanceState).inflate(R.layout.fragment_reproductor, container, false);
		
	}
	
	@Override
    public void onActivityCreated(Bundle state) {
		super.onActivityCreated(state);
	}
}
