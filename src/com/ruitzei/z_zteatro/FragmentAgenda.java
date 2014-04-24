package com.ruitzei.z_zteatro;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.ruitzei.utilitarios.AdaptadorAgenda;
import com.ruitzei.utilitarios.NoticiaOle;
import com.ruitzei.utilitarios.RssParser;

public class FragmentAgenda extends ListFragment {
	private Button btnCargar;
	
	//Necesito la referencia a la vista para poder llamar a los botones que tenga.
	private View view;
	
	private List<NoticiaOle> noticias;
	private AdaptadorAgenda adapterNoticias;
	
	private static final String URL = "http://edant.ole.com.ar/diario/ult_momento.xml";
	private static final String OLE = "http://ole.feedsportal.com/c/33068/f/577712/index.rss";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.fragment_agenda, container, false);
		this.view=view;
		
		btnCargar = (Button)view.findViewById(R.id.btnCargar);
		
		btnCargar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				new DownloadXmlTask().execute(OLE);
	            System.out.println("HILO PRINCIPAL");
	            System.out.println("HILO PRINCIPAL");
	            System.out.println("HILO PRINCIPAL");
	            System.out.println("HILO PRINCIPAL");	            	
			}
		});
		
		return view;
	}


	public void mostrarLista(){
        ListView lista = (ListView)this.view.findViewById(android.R.id.list);
        adapterNoticias = new AdaptadorAgenda(getActivity().getApplicationContext(), noticias);
        lista.setAdapter(adapterNoticias);
		//Toast.makeText(this, "Muestro", Toast.LENGTH_LONG).show();
        
    	agregarListenerLista(); 
	}

	
    // Descarga del archivo en un hilo separado para no tildar la interfaz de usuario.
    private class DownloadXmlTask extends AsyncTask<String, Void, String> {

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
        	System.out.println(noticias.get(0).getDescripcion());
        	System.out.println(noticias.size());
        	System.out.println(noticias.get(0).getFecha());
        	
        	mostrarLista();        	
        }
    }
    
    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException, ParseException {
        InputStream stream = null;
        //RssClarinParser clarinParser = new RssClarinParser();
        RssParser oleParser = new RssParser();
        noticias = null;
        try {
            stream = downloadUrl(urlString);
            System.out.println("antes del parse");
            
            //Le paso al PARSER el archivo XML para que haga lo suyo.
            noticias = oleParser.parse(stream);
            System.out.println("despues del parse");
            
        } finally {
            if (stream != null) {
                stream.close();                
            }
        }
        
        return "";
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

	private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
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
}
