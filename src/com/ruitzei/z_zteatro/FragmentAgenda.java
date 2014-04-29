package com.ruitzei.z_zteatro;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;

import org.xmlpull.v1.XmlPullParserException;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.ruitzei.utilitarios.AdaptadorAgenda;
import com.ruitzei.utilitarios.RssParser;

public class FragmentAgenda extends ListFragment {
	//private Button btnCargar;
	//btnCargar = (Button)view.findViewById(R.id.btnCargar);

	
	//Necesito la referencia a la vista para poder llamar a los botones que tenga.
	private View view;
	
	private AdaptadorAgenda adapterNoticias;
	private MainActivity actividadPrincipal;
	
	private static final String URL = "http://edant.ole.com.ar/diario/ult_momento.xml";
	private static final String OLE = "http://ole.feedsportal.com/c/33068/f/577712/index.rss";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_agenda, container, false);
		this.view=view;
		this.actividadPrincipal = ((MainActivity)getActivity());
		
		if (actividadPrincipal.existenNoticias()){
        	mostrarLista();
        }else{
        	new DescargarYMostrar().execute(OLE);
        }
		
		setHasOptionsMenu(true);
		
		return view;
	}
	

	public void mostrarLista(){		
		ListView lista = (ListView)this.view.findViewById(android.R.id.list);
		adapterNoticias = new AdaptadorAgenda(getActivity().getApplicationContext(),actividadPrincipal.getNoticias());
		
	    lista.setAdapter(adapterNoticias);	    
	   
	    adapterNoticias.getFilter().filter("torito");	     
	    agregarListenerLista();
	}

	
    // Descarga del archivo en un hilo separado para no tildar la interfaz de usuario.
    private class DescargarYMostrar extends AsyncTask<String, Void, String> {
    	private ProgressDialog Asycdialog = new ProgressDialog(getActivity());
    	
    	@Override
    	protected void onPreExecute(){            
            Asycdialog.setMessage("Cargando ...");
            Asycdialog.show();
    	}    	
    	
        @Override
        protected String doInBackground(String... urls) {
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

        @Override
        protected void onPostExecute(String result) {
        	if (result.equalsIgnoreCase("success")){
        		Toast.makeText(getActivity(), "Descarga todo piola", Toast.LENGTH_LONG).show();
            	Asycdialog.dismiss();
            	
            	mostrarLista();
        	} else {
        		Asycdialog.dismiss();
        		Toast.makeText(getActivity(),"Debes tener internet para ver la Agenda. Presiona Actualizar para volver a intentar", Toast.LENGTH_LONG).show();
        	}       	
        }
    }
    
    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException, ParseException {
        InputStream stream = null;
        RssParser oleParser = new RssParser();
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
        ListView lista = (ListView)this.view.findViewById(android.R.id.list);

        lista.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				System.out.println("Se clickeo el elemento Nº "+ position );				
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
				new DescargarYMostrar().execute(OLE);
	      default:
	         return super.onOptionsItemSelected(item);
	   }
	}
}
