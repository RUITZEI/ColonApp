package com.ruitzei.z_zteatro;

import com.ruitzei.utilitarios.ClienteWeb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;


/**
 * El fragment crea el nuevo navegador web del tipo ClienteWeb que deja la navegacion en
 * la app.
 * requestfocus -> poder escribir dentro de las paginas.
 */
public class FragmentWeb extends Fragment{
	
	private WebView web;
	private final String COLON = "http://www.tuentrada.com/online/mobile/";
	private final String FOTOS = "http://www.tuentrada.com/Online/brands/colon/tour/tour.html";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_web, container, false);
		
		web = (WebView)view.findViewById(R.id.web_view);
		web.getSettings().setJavaScriptEnabled(true);
		web.requestFocus(View.FOCUS_DOWN);
		web.setWebViewClient(new ClienteWeb());
		web.getSettings().setBuiltInZoomControls(true);
		web.loadUrl(COLON);
		web.setWebChromeClient(new WebChromeClient());
		
		
		//importante para mostrar las coasas que quiero.
		setHasOptionsMenu(true);
		
		
		return view;
	}
		
	
	
	/**
	 * Importante: Esto solo se llama si en el onCreate() de cada fragment llamo al
	 * metodo SetHasOptionsMenu(true);
	 * II: Parece que si defino que hacer con cada boton en el MainActivity se llama solo al
	 * onOptionsItemSelected del Main.
	 */
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		Toast.makeText(getActivity(), "ASD", Toast.LENGTH_LONG).show();
		menu.findItem(R.id.action_settings).setVisible(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	   // handle item selection
	   switch (item.getItemId()) {
	      case R.id.action_settings:
	    	  if(web.canGoBack()){
	    		   web.goBack();
	    	  }	         
	      default:
	         return super.onOptionsItemSelected(item);
	   }
	}
}
