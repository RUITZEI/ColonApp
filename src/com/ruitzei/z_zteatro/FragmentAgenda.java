package com.ruitzei.z_zteatro;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.xmlpull.v1.XmlPullParserException;

import android.R.anim;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.print.PrintAttributes.Resolution;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ruitzei.utilitarios.AdaptadorAgenda;
import com.ruitzei.utilitarios.AdaptadorAgenda2;
import com.ruitzei.utilitarios.ItemAgenda;
import com.ruitzei.utilitarios.ParserColon;
import com.ruitzei.utilitarios.RssParser;

public class FragmentAgenda extends ListFragment implements OnNavigationListener {	
	//Necesito la referencia a la vista para poder llamar a los botones que tenga.
	private View view;
	
	private AdaptadorAgenda2 adapterNoticias;
	private MainActivity actividadPrincipal;
	private ListView lista;
	private SearchView searchView;
	private MenuItem searchItem;
	private int ultimoItemClickeado = 0;
	private Hashtable<Integer, String> itemsSpinner;
	
	private static final String URL = "http://edant.ole.com.ar/diario/ult_momento.xml";
	private static final String OLE = "http://ole.feedsportal.com/c/33068/f/577712/index.rss";
	private static final String RSS_COLON = "https://www.tuentrada.com/colon/Online/eventsXML.asp";
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_agenda, container, false);
		this.view=view;
		this.actividadPrincipal = ((MainActivity)getActivity());
		this.lista = (ListView)view.findViewById(android.R.id.list);
		
		setHasOptionsMenu(true);
		
    	inicializarHash();
		
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
		adapterNoticias = new AdaptadorAgenda2(getActivity().getApplicationContext(),actividadPrincipal.getNoticias());		
	    lista.setAdapter(adapterNoticias);
	   
	    agregarListenerLista();
	}

	
    // Descarga del archivo en un hilo separado para no tildar la interfaz de usuario.
    private class DescargarYMostrar extends AsyncTask<String, Void, String> {
    	private ProgressDialog asycdialog = new ProgressDialog(getActivity());
    	
    	public DescargarYMostrar(){
    		asycdialog = new ProgressDialog(getActivity());
    		asycdialog.setMessage("Cargando ...");
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
        		Toast.makeText(getActivity(), "Descarga correcta", Toast.LENGTH_LONG).show();
            	mostrarLista();
            	cargarSpinner();
        	} else {
        		
        		Toast.makeText(getActivity(),"Debes tener internet para ver la Agenda. Presiona Actualizar para volver a intentar", Toast.LENGTH_LONG).show();
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
	
    public void agregarListenerLista() {    	
        lista.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Bundle args = new Bundle();
				args.putString("link", adapterNoticias.getItem(position).getLink());
				Fragment fragment = new FragmentWeb();
				fragment.setArguments(args);
				MenuItemCompat.collapseActionView(searchItem);
				
				System.out.println("Se clickeo el elemento N� "+ position );
				FragmentManager fragmentManager = actividadPrincipal.getSupportFragmentManager();
				fragmentManager.beginTransaction()
				.replace(R.id.container, fragment)
				.addToBackStack("FragBack")
				.commit();
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
				Toast.makeText(getActivity(), "Refresh cliked",Toast.LENGTH_LONG).show();
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
		
		adapterNoticias.getFilter().filter(this.itemsSpinner.get(itemPosition));
		return false;
	}
	
	@Override 
	public void onDestroy(){
		super.onDestroy();
		//actividadPrincipal.getSupportActionBar().setDisplayShowTitleEnabled(true);
		//actividadPrincipal.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}
	
	@Override
	public void onPause(){
		super.onPause();
		//Toast.makeText(getActivity(), "Pausado", Toast.LENGTH_LONG).show();
		//actividadPrincipal.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		//actividadPrincipal.getSupportActionBar().setDisplayShowTitleEnabled(true);
		
	}
	
	public void inicializarHash(){
		this.itemsSpinner = new Hashtable<Integer, String>();
		
		String[] itemSpinner = getResources().getStringArray(R.array.items_spinner);
		
		for (int i = 0; i < itemSpinner.length; i++) {
			this.itemsSpinner.put(i, itemSpinner[i]);
		}
		//En la posicion 0 tiene que estar el default: en este caso VER TODOS.
		this.itemsSpinner.put(0, "");
	}
	

	@Override
	public void onResume(){
		super.onResume();
		//Toast.makeText(getActivity(), "Resumido", Toast.LENGTH_LONG).show();
		agregarListenerLista();
		actividadPrincipal.getSupportActionBar().setTitle("Agenda");
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
			//adapterNoticias.getFilter().filter("");
			actividadPrincipal.getSupportActionBar().setSelectedNavigationItem(ultimoItemClickeado);
			adapterNoticias.getFilter().filter(itemsSpinner.get(ultimoItemClickeado));
			return true;
		}
	};
}
