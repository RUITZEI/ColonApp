package com.ruitzei.app;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;

import org.xmlpull.v1.XmlPullParserException;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ruitzei.utilitarios.AdapterAgenda;
import com.ruitzei.utilitarios.ParserColon;
import com.ruitzei.z_zteatro.R;

public class FragmentAgenda extends ListFragment implements OnNavigationListener {		
	private AdapterAgenda adapterNoticias;
	private MainActivity actividadPrincipal;
	private ListView lista;
	private SearchView searchView;
	private MenuItem searchItem;
	private int ultimoItemClickeado = 0;
	private String[] itemsSpinner;
	
	private static final String RSS_COLON = "https://www.tuentrada.com/colon/Online/eventsXML.asp";
	private static final String COMPRA_COLON = "https://www.tuentrada.com/colon/Online/mobile/seatSelect.asp?BOset::WSmap::seatmap::performance_ids=";	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_agenda, container, false);
		this.actividadPrincipal = ((MainActivity)getActivity());
		this.lista = (ListView)view.findViewById(android.R.id.list);
		
		setHasOptionsMenu(true);
		
    	inicializarElementosSpinner();
		
		if (actividadPrincipal.existenNoticias()){
			cargarSpinner();
        	mostrarLista();
        }else{
        	new DescargarYMostrar().execute(RSS_COLON);
        }
		
		agregarListenerLista();
		
		return view;
	}
	

	/**
	 * Carga los String del archivo Values\Items_spinner.xml para agregarlos en el menu de la parte superior. 
	 */	
	private void cargarSpinner() {
			ArrayAdapter<CharSequence> adapterSpinner = ((MainActivity)getActivity()).getAdapterSpinner();
			actividadPrincipal.getSupportActionBar().setDisplayShowTitleEnabled(false);
			actividadPrincipal.getSupportActionBar().setListNavigationCallbacks(adapterSpinner, this);
			actividadPrincipal.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			actividadPrincipal.getSupportActionBar().setSelectedNavigationItem(ultimoItemClickeado);
	}


	public void mostrarLista(){		
		adapterNoticias = new AdapterAgenda(getActivity().getApplicationContext(),actividadPrincipal.getNoticias());		
	    lista.setAdapter(adapterNoticias);
	   
	    agregarListenerLista();
	}

	
    // Descarga del archivo en un hilo separado para no tildar la interfaz de usuario.
    private class DescargarYMostrar extends AsyncTask<String, Void, String> {
    	private ProgressDialog asycdialog = new ProgressDialog(getActivity());
    	
    	public DescargarYMostrar(){
    		asycdialog = new ProgressDialog(getActivity());
    		asycdialog.setMessage(getString(R.string.msg_loading));
    		asycdialog.setCancelable(true);
    		asycdialog.setOnCancelListener(new OnCancelListener() {				
				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(true);					
				}
			});
    	}
    	
    	@Override
    	protected void onPreExecute(){  
            asycdialog.show();
    	}    	
    	
        @Override
        protected String doInBackground(String... urls) {
        	while (!isCancelled()){
	        	try {
	                return loadXmlFromNetwork(urls[0]);
	            } catch (IOException e) {
	                return "Error de conexion";
	            } catch (XmlPullParserException e) {
	                return "Error del link!";
	            } catch (ParseException e) {
					return "Error del parse Date";
				}
        	}
        	return "asd";
        }

        @Override
        protected void onPostExecute(String result) {
        	if (result.equalsIgnoreCase("success")){
        		//Toast.makeText(getActivity(), "Descarga correcta", Toast.LENGTH_LONG).show();
            	mostrarLista();
            	cargarSpinner();
        	} else {        		
        		Toast.makeText(getActivity(),R.string.msg_nointernet_agenda, Toast.LENGTH_LONG).show();
        	}   
        	asycdialog.dismiss();        	
        }
    }
    
    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException, ParseException {
        InputStream stream = null;
        ParserColon oleParser = new ParserColon();
        try {
            stream = downloadUrl(urlString);
            System.out.println("antes del parse");            
            //Le paso al PARSER el archivo XML para que haga lo suyo.
            actividadPrincipal.setNoticias(oleParser.parse(stream));
            System.out.println("despues del parse");            
        } finally {
            if (stream != null) {
                stream.close();                
            }
        }        
        return "Success";
    }

	private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds*/ );
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        
        // Comienza la descarga.
        conn.connect();
        InputStream stream = conn.getInputStream();
        System.out.println("se bajo el asunto");
        return stream;
    }
	
	public void agregarListenerSearchView() {
		if (actividadPrincipal.existenNoticias()){
			searchView.setIconifiedByDefault(true);
			searchView.setOnQueryTextListener(queryTextListener);
			MenuItemCompat.setOnActionExpandListener(searchItem, onActionExpand);
		}
	}
	
	/**
	 * Paso como extra la ruta COMPLETA al link donde quiero acceder.
	 */
    public void agregarListenerLista() {    	
        lista.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				boolean tieneAsientosDisponibles = adapterNoticias.getItem(position).getDisponibilidad() != 'S'; 
				if (tieneAsientosDisponibles){
					Bundle args = new Bundle();
					args.putString("link", COMPRA_COLON+adapterNoticias.getItem(position).getLink());
					Fragment fragment = new FragmentWeb();
					fragment.setArguments(args);
					MenuItemCompat.collapseActionView(searchItem);
					
					System.out.println("Se clickeo el elemento Nº "+ position );
					FragmentManager fragmentManager = actividadPrincipal.getSupportFragmentManager();
					fragmentManager.beginTransaction()
					.replace(R.id.container, fragment)
					.addToBackStack("FragBack")
					.commit();
				}else{
					Toast.makeText(getActivity(),R.string.msg_no_seats_available, Toast.LENGTH_LONG).show();					
				}
			}
		});	
	}
    
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
		super.onCreateOptionsMenu(menu, inflater);
		menu.clear();
		inflater.inflate(R.menu.menu_agenda, menu);
		
		searchItem = menu.findItem(R.id.action_search);
		searchView = (SearchView)MenuItemCompat.getActionView(searchItem);
		
		//Esto arregla el bug que no ensanchaba correctamente al SearchView.
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		searchView.setLayoutParams(lp);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.refresh_icon:
				new DescargarYMostrar().execute(RSS_COLON);
				break;
			case R.id.action_search:
				agregarListenerSearchView();
				break;
	      default:
	         return super.onOptionsItemSelected(item);
	   }
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		ultimoItemClickeado = itemPosition;
		
		adapterNoticias.getFilter().filter(this.itemsSpinner[itemPosition]);
		return false;
	}
	
	/**
	 * Dejo vacio en el primer elemento porque el primero es el elemento por defecto.
	 */
	public void inicializarElementosSpinner(){
		this.itemsSpinner = getResources().getStringArray(R.array.items_spinner);		
		this.itemsSpinner[0] = "";
	}	

	@Override
	public void onResume(){
		super.onResume();
		agregarListenerLista();
		actividadPrincipal.getSupportActionBar().setTitle(R.string.btn_agenda);
		actividadPrincipal.getSupportActionBar().setDisplayShowHomeEnabled(true);
		actividadPrincipal.getSupportActionBar().setIcon(android.R.color.transparent);
		
		if (actividadPrincipal.existenNoticias()){
			cargarSpinner();
		}
	}
	
	final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
		@Override
		public boolean onQueryTextSubmit(String arg0) {
			return false;
		}
		
		@Override
		public boolean onQueryTextChange(String arg0) {
			adapterNoticias.getFilter().filter(arg0);
			return false;
		}
	};
	
	final MenuItemCompat.OnActionExpandListener onActionExpand = new MenuItemCompat.OnActionExpandListener() {		
		@Override
		public boolean onMenuItemActionExpand(MenuItem item) {
			return true;
		}
		
		@Override
		public boolean onMenuItemActionCollapse(MenuItem item) {
			actividadPrincipal.getSupportActionBar().setSelectedNavigationItem(ultimoItemClickeado);
			adapterNoticias.getFilter().filter(itemsSpinner[ultimoItemClickeado]);
			return true;
		}
	};
}
