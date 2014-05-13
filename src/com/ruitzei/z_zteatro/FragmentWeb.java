package com.ruitzei.z_zteatro;

import com.ruitzei.utilitarios.ClienteWeb;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebSettings.PluginState;
import android.widget.Toast;


/**
 * El fragment crea el nuevo navegador web del tipo ClienteWeb que deja la navegacion en
 * la app.
 * requestfocus -> poder escribir dentro de las paginas.
 */
public class FragmentWeb extends Fragment{
	
	private WebView web;
	private final String COLON = "http://www.tuentrada.com/online/mobile/";
	private final String WEB_COLON = "https://www.tuentrada.com/colon/Online/";
	private final String COMPRA_COLON = "https://www.tuentrada.com/colon/Online/seatSelect.asp?BOset::WSmap::seatmap::performance_ids=";
	private MainActivity actividadPrincipal;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_web, container, false);
		
		this.actividadPrincipal = ((MainActivity)getActivity());
		
		actividadPrincipal.getSupportActionBar().setDisplayShowTitleEnabled(true);

		web = (WebView)view.findViewById(R.id.web_view);
		web.getSettings().setJavaScriptEnabled(true);
		web.requestFocus(View.FOCUS_DOWN);
		web.setWebViewClient(new ClienteWeb());
		web.getSettings().setBuiltInZoomControls(true);
		web.loadUrl(COMPRA_COLON + this.getArguments().getString("link"));
		web.setWebChromeClient(new WebChromeClient());
		
		System.out.println(this.getArguments().getString("link"));
		
		
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
		super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.menu_web, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	   // handle item selection
	   switch (item.getItemId()) {
	      case R.id.action_back:
	    	  if(web.canGoBack()){
	    		   web.goBack();
	    	  }	         
	      default:
	         return super.onOptionsItemSelected(item);
	   }
	}
	
	@Override
	public void onResume(){
		super.onResume();	
		actividadPrincipal.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actividadPrincipal.getSupportActionBar().setDisplayShowTitleEnabled(true);
		actividadPrincipal.getSupportActionBar().setDisplayShowHomeEnabled(false);
		actividadPrincipal.getSupportActionBar().setTitle(R.string.fragment_web);
	}
}
