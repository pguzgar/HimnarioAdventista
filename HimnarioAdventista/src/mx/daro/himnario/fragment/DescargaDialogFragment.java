package mx.daro.himnario.fragment;

import mx.daro.himnario.listener.GeneralListener;
import mx.daro.himnario.listener.OnAttachListener;
import mx.daro.himnario.listener.OnDownloadCanceledListener;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class DescargaDialogFragment extends DialogFragment{
	
	private OnDownloadCanceledListener listenerCancel;
	private GeneralListener onSavedListener;
	private GeneralListener onResumeListener;
	private GeneralListener onDestroyListener;
	private GeneralListener onPauseListener;
	private GeneralListener onDetachListener;
	private GeneralListener onCreateListener;
	private OnAttachListener onAttachListener;
	private static Integer numero;
	
	public static DescargaDialogFragment newInstance(Integer numero) {
		DescargaDialogFragment.numero = numero;
		DescargaDialogFragment frag = new DescargaDialogFragment();
		return frag;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		setRetainInstance(true);
		
		final ProgressDialog dialog = new ProgressDialog(getActivity());
		dialog.setMessage("Descargando - Himno # "+numero);
		dialog.setIndeterminate(false);
		dialog.setMax(100);
		dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);

		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK){
					if(listenerCancel!=null)listenerCancel.onCanceled();
					return true;
				}
				if(keyCode == KeyEvent.KEYCODE_SEARCH){
					return true;
				}
				return false;
			}
		});
		if(onCreateListener!=null){
			onCreateListener.execute();
		}
		return dialog;
	}
	
	@Override
	public void onPause(){
	    super.onPause();
		if(onPauseListener!=null)onPauseListener.execute();
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(onSavedListener!=null)onSavedListener.execute();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if(onResumeListener!=null)onResumeListener.execute();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(onDestroyListener!=null)onDestroyListener.execute();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(onAttachListener!=null){
			onAttachListener.onAttach((SherlockFragmentActivity)activity);
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		if(onDetachListener!=null){
			onDetachListener.execute();
		}
	}

	public OnDownloadCanceledListener getListenerCancel() {
		return listenerCancel;
	}

	public void setListenerCancel(OnDownloadCanceledListener listenerCancel) {
		this.listenerCancel = listenerCancel;
	}

	public GeneralListener getOnSavedListener() {
		return onSavedListener;
	}

	public void setOnSavedListener(GeneralListener onSavedListener) {
		this.onSavedListener = onSavedListener;
	}

	public GeneralListener getOnResumeListener() {
		return onResumeListener;
	}

	public void setOnResumeListener(GeneralListener onResumeListener) {
		this.onResumeListener = onResumeListener;
	}

	public GeneralListener getOnDestroyListener() {
		return onDestroyListener;
	}

	public void setOnDestroyListener(GeneralListener onDestroyListener) {
		this.onDestroyListener = onDestroyListener;
	}

	public GeneralListener getOnPauseListener() {
		return onPauseListener;
	}

	public void setOnPauseListener(GeneralListener onPauseListener) {
		this.onPauseListener = onPauseListener;
	}

	public GeneralListener getOnDetach() {
		return onDetachListener;
	}

	public void setOnDetach(GeneralListener onDetach) {
		this.onDetachListener = onDetach;
	}

	public OnAttachListener getOnAttachListener() {
		return onAttachListener;
	}

	public void setOnAttachListener(OnAttachListener onAttachListener) {
		this.onAttachListener = onAttachListener;
	}

	public GeneralListener getOnCreateListener() {
		return onCreateListener;
	}

	public void setOnCreateListener(GeneralListener onCreateListener) {
		this.onCreateListener = onCreateListener;
	}
}
