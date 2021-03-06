package com.ruitzei.colonApp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.ruitzei.utilitarios.ClienteWeb;
import com.ruitzei.colonApp.R;


/**
 * El fragment crea el nuevo navegador web del tipo ClienteWeb que deja la navegacion en
 * la app.
 * requestfocus -> poder escribir dentro de las paginas.
 */
public class FragmentWeb extends Fragment{
	
	private WebView web;	
	private MainActivity actividadPrincipal;
    private ProgressBar progressBar;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.fragment_web, container, false);
		
		this.actividadPrincipal = ((MainActivity)getActivity());
		
		progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
		progressBar.setMax(100);
		
		actividadPrincipal.getSupportActionBar().setDisplayShowTitleEnabled(true);

		web = (WebView)view.findViewById(R.id.web_view);
		web.getSettings().setJavaScriptEnabled(true);
		web.requestFocus(View.FOCUS_DOWN);
		web.setWebViewClient(new ClienteWeb());
		web.getSettings().setBuiltInZoomControls(true);		
		web.setWebChromeClient(new WebChromeClient());
		
		web.setWebChromeClient(new WebChromeClient(){
			public void onProgressChanged(WebView view, int progress){
				progressBar.setProgress(progress);
				progressBar.setVisibility(View.VISIBLE);
				
				if (progress == 100){
					progressBar.setVisibility(View.GONE);
				}
				
			}
		});
		
		web.loadUrl(this.getArguments().getString("link"));
		//web.loadUrl("http://google.com");
		Log.d("WebView", "Abriendo Link:" + this.getArguments().getString("link"));
		
		
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
	
	@Override 
	public void onDestroy(){
		Log.d("Web", "Se Limpio el Cache");
		web.clearCache(true);
		CookieSyncManager.createInstance(getActivity());         
		CookieManager cookieManager = CookieManager.getInstance();        
		cookieManager.removeAllCookie();
		super.onDestroy();
		
	}
}
