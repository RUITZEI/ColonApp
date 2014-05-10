package com.ruitzei.z_zteatro;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;

import org.xmlpull.v1.XmlPullParserException;

import android.R.anim;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.ruitzei.utilitarios.AdaptadorAgenda;
import com.ruitzei.utilitarios.AdaptadorAgenda2;
import com.ruitzei.utilitarios.ParserColon;
import com.ruitzei.utilitarios.RssParser;

public class FragmentAgenda extends ListFragment implements OnNavigationListener {	
	//Necesito la referencia a la vista para poder llamar a los botones que tenga.
	private View view;
	
	private AdaptadorAgenda2 adapterNoticias;
	private MainActivity actividadPrincipal;
	private ListView lista;
	private int ultimoItemClickeado = 0;
	
	private static final String URL = "http://edant.ole.com.ar/diario/ult_momento.xml";
	private static final String OLE = "http://ole.feedsportal.com/c/33068/f/577712/index.rss";
	private static final String RSS_COLON = "http://juan-i.com.ar/manu/TSperformanceCO.xml";
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_agenda, container, false);
		this.view=view;
		this.actividadPrincipal = ((MainActivity)getActivity());
		this.lista = (ListView)view.findViewById(android.R.id.list);
		//this.setRetainInstance(true);
		
		
		//actividadPrincipal.getSupportActionBar().setIcon(android.R.color.transparent);
		//actividadPrincipal.getSupportActionBar().setDisplayUseLogoEnabled(false);
		//actividadPrincipal.getSupportActionBar().setDisplayShowHomeEnabled(false);
		//actividadPrincipal.getSupportActionBar().setCustomView(R.layout.actionbar_custom);
		//actividadPrincipal.getSupportActionBar().setDisplayShowCustomEnabled(true);
		
		//actividadPrincipal.getSupportActionBar().setIcon(R.drawable.ic_launcher);
		
		if (actividadPrincipal.existenNoticias()){
			cargarSpinner();
        	mostrarLista();
        }else{
        	new DescargarYMostrar().execute(RSS_COLON);
        }
		
		setHasOptionsMenu(true);
		//cargarSpinner();
		agregarListenerLista();
		
		return view;
	}
	

	/**
	 * Carga los String del archivo Values\Items_spinner.xml para agregarlos en el menu de la parte superior. 
	 */	
	private void cargarSpinner() {
		/**
		//ArrayAdapter<CharSequence> adapterSpiiner = ArrayAdapter.createFromResource(actividadPrincipal.getSupportActionBar().getThemedContext(), R.array.items_spinner, R.layout.item_spinner);
		//ArrayAdapter<CharSequence> adapterSpiiner = ArrayAdapter.createFromResource(getActivity(), R.array.items_spinner, R.layout.item_spinner);
		
		((MainActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
		((MainActivity)getActivity()).getSupportActionBar().setListNavigationCallbacks(adapterSpiiner, this);
		//adapterSpiiner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		((MainActivity)getActivity()).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);*/
			ArrayAdapter<CharSequence> adapterSpinner = ((MainActivity)getActivity()).getAdapterSpinner();
			actividadPrincipal.getSupportActionBar().setDisplayShowTitleEnabled(false);
			actividadPrincipal.getSupportActionBar().setListNavigationCallbacks(adapterSpinner, this);
			actividadPrincipal.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			
			actividadPrincipal.getSupportActionBar().setSelectedNavigationItem(ultimoItemClickeado);
	}


	public void mostrarLista(){		
		//ListView lista = (ListView)this.view.findViewById(android.R.id.list);
		adapterNoticias = new AdaptadorAgenda2(getActivity().getApplicationContext(),actividadPrincipal.getNoticias());		
	    lista.setAdapter(adapterNoticias);
	    
	    //Me aseguro que siempre empieze mostrando todo sin filtrar.
	    //actividadPrincipal.getSupportActionBar().setSelectedNavigationItem(0);
	   
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
        //Toast.makeText(this , "Se bajo el asunto", Toast.LENGTH_SHORT).show();
        System.out.println("se bajo el asunto");
        return stream;
    }
	
    public void agregarListenerLista() {
        //ListView lista = (ListView)this.view.findViewById(android.R.id.list);

        lista.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {			
				Bundle args = new Bundle();
				args.putString("link", actividadPrincipal.getNoticias().get(position).getLinkAdicional());
				Fragment fragment = new FragmentWeb();
				fragment.setArguments(args);
				
				System.out.println("Se clickeo el elemento Nº "+ position );
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
		//Toast.makeText(getActivity(), "ASD", Toast.LENGTH_LONG).show();
		menu.findItem(R.id.refresh_icon).setVisible(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.refresh_icon:
				Toast.makeText(getActivity(), "Refresh cliked",Toast.LENGTH_LONG).show();
				new DescargarYMostrar().execute(RSS_COLON);
	      default:
	         return super.onOptionsItemSelected(item);
	   }
	}


	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		ultimoItemClickeado = itemPosition;
		switch (itemPosition) {
		case 1:
			adapterNoticias.getFilter().filter(null);
			break;
		case 2:
			adapterNoticias.getFilter().filter("Si");
			break;
		case 3:
			adapterNoticias.getFilter().filter("No");
			break;

		default:
			break;
		}
		
		//ListView lista = (ListView)this.view.findViewById(android.R.id.list);
		//lista.setSelectionFromTop(0, 0);
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
		Toast.makeText(getActivity(), "Pausado", Toast.LENGTH_LONG).show();
		actividadPrincipal.getSupportActionBar().setDisplayShowTitleEnabled(true);
		actividadPrincipal.getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}
	
	/**
	 * Aca no pregunto si existen noticias antes de cargar al spinner.
	 */
	@Override
	public void onResume(){
		super.onResume();
		Toast.makeText(getActivity(), "Resumido", Toast.LENGTH_LONG).show();
		agregarListenerLista();
	}
}
