package com.ruitzei.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.ruitzei.utilitarios.ItemAgenda;
import com.ruitzei.z_zteatro.R;


public class MainActivity extends ActionBarActivity implements OnBackStackChangedListener{

	private List<ItemAgenda> noticias;
	private ArrayAdapter<CharSequence> adapterSpinner;
	private static final String URL_PROGRAMA = "https://www.tuentrada.com/Articlemedia/Images/Brands/Colon/prog_colon_2014.pdf";
	private static final String URL_TEMPORADA ="";
	private static final int NOTIF_ALERTA_ID = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
					  //.add(R.id.container, new FragmentAgenda()).commit();
		}
		
		getSupportActionBar().setDisplayShowTitleEnabled(true);
				
		adapterSpinner = ArrayAdapter.createFromResource(getSupportActionBar().getThemedContext(), R.array.items_spinner, R.layout.item_spinner);
		
		getSupportFragmentManager().addOnBackStackChangedListener(this);
		shouldDisplayHomeUp();		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
		//return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		/**int id = item.getItemId();
		if (id == R.id.action_settings) {
			//Toast.makeText(this, "Aprete el Action", Toast.LENGTH_SHORT).show();
			//return true;
		}*/
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment implements OnClickListener {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			
			/*
			 * En esta parte se deben inicializar los botones propios de cada fragment.
			 * En este caso serian todos los botones del menu principal.
			 */
			Button botonCargar = (Button)rootView.findViewById(R.id.btn_agenda);
			botonCargar.setOnClickListener(this);
			
			Button botonPrograma = (Button)rootView.findViewById(R.id.btn_programa);
			botonPrograma.setOnClickListener(this);
			
			Button botonTemporada = (Button)rootView.findViewById(R.id.btn_temporada);
			botonTemporada.setOnClickListener(this);
			
			
			return rootView;
		}
		
		@Override
		public void onResume(){
			super.onResume();
			((MainActivity)getActivity()).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			((MainActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
			((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);
			((MainActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
		}
		
		@Override
		public void onClick(View v){
			switch (v.getId()) {
			case R.id.btn_agenda:
				
				Fragment fragment = new FragmentAgenda();
				
				FragmentManager fragmentManager = ((MainActivity)getActivity()).getSupportFragmentManager();
				fragmentManager.popBackStack();
				fragmentManager.beginTransaction()
				.replace(R.id.container, fragment)
				.addToBackStack("FragBack")
				.commit();
				break;
			case R.id.btn_programa:					
				if (((MainActivity)getActivity()).tieneConexionInternet() ){
					confirmarDescarga("programa", URL_PROGRAMA);
				}else{
					Toast.makeText(getActivity(), R.string.msg_nointernet_programa, Toast.LENGTH_LONG).show();
				}				
				break;
			case R.id.btn_temporada:
				if (((MainActivity)getActivity()).tieneConexionInternet() ){
					confirmarDescarga("Teatro_Colon_Temporada_2014", URL_PROGRAMA);
				}else{
					Toast.makeText(getActivity(), R.string.msg_nointernet_programa, Toast.LENGTH_LONG).show();
				}	
				break;
			default:
				break;
			}
		}
		private void confirmarDescarga(final String nombreArchivo, final String linkArchivo){
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			
			builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					((MainActivity)getActivity()).new DescargarPdf(nombreArchivo).execute(linkArchivo);
				}
			       });			
			builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			           }
			       });
			builder.setMessage(R.string.alert_download);
			
			AlertDialog dialog = builder.show();
		}
	}
	
	
	@Override
	public void onBackStackChanged() {
	    shouldDisplayHomeUp();		
	}
	
	
	/**
	 * Cada vez que se invoca un fragment, la BackStackEntry count se incrementa
	 * en 1. Si la backStack esta en 0 significa que no hay ningun Fragment encima
	 * abierto y entonces no deberia mostrar la AcionBar.
	 */
	public void shouldDisplayHomeUp(){
		boolean canback = getSupportFragmentManager().getBackStackEntryCount()>0;
		getSupportActionBar().setDisplayHomeAsUpEnabled(canback);
		//deberiaMostrarActionBar(canback);
	}
	
	@Override
	public boolean onSupportNavigateUp() {
	    //Cuando toca la flechita para arriba, vuelve al main fragment.
	    getSupportFragmentManager().popBackStack();
	    return true;
	}		
	
	/**
	 * Para que los otros fragments puedan acceder a las noticias.
	 */
	public List<ItemAgenda> getNoticias(){
		return this.noticias;		
	}
	
	
	//Si devuelve true es porque hubo un problema en la descarga.
	public boolean existenNoticias(){
		return !(this.noticias == null);
	}
	
	
	//Setear las noticias por si las consegui desde la agenda misma o de donde fuera.
	public void setNoticias (List<ItemAgenda> noticias){
		this.noticias = noticias;
	}	
	
	public ArrayAdapter<CharSequence> getAdapterSpinner(){
		return this.adapterSpinner;
	}
	
	public void deberiaMostrarActionBar(boolean deberia){
		if (deberia){
			getSupportActionBar().show();
		} else{
			getSupportActionBar().hide();
		}
	}
	
	public boolean tieneConexionInternet() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	private class DescargarPdf extends AsyncTask<String, Integer, String>{
		private String urlToDownload;
		private String fileName;
		private NotificationCompat.Builder mBuilder;
		private NotificationManager mNotificationManager;
		
		public DescargarPdf(String fileName){
			this.fileName = fileName;
		}
		
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			System.out.println("antes");
			mBuilder = new NotificationCompat.Builder(getApplication())
			.setTicker("Iniciando descarga")
			//.setSmallIcon(android.R.drawable.stat_sys_warning)
			.setSmallIcon(R.drawable.ic_launcher)
			.setLargeIcon((((BitmapDrawable)getResources()
					.getDrawable(R.drawable.ic_launcher)).getBitmap()))
			.setContentTitle("Descargando...")
			.setContentText("Descarga en proceso.")
			//.setContentInfo("4")
			.setProgress(0, 0, true);
			 
			Intent intent = new Intent();
			PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
			mBuilder.setContentIntent(pendingIntent);
			
			mNotificationManager =
				    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

				mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());
		}

		@Override
		protected String doInBackground(String... params){
			try {
				return cargarPdfDeInternet(params[0]);
			} catch (IOException e) {
				return "error";
			}
		}
		
		private String cargarPdfDeInternet(String params) throws IOException{
			FileOutputStream fos = null;
			InputStream is = null;
			try {
				String fileExtension=".pdf";
	 
				System.out.println("durante");
				URL url = new URL(params);
	            HttpURLConnection c = (HttpURLConnection) url.openConnection();
	            c.setReadTimeout(100000);
	            c.setConnectTimeout(15000);
	            c.setRequestMethod("GET");
	            c.setDoOutput(true);
	            c.connect();
	            String PATH = Environment.getExternalStorageDirectory() + "/download/";
	            File file = new File(PATH);
	            file.mkdirs();
	            File outputFile = new File(file, fileName+fileExtension);
	            fos = new FileOutputStream(outputFile);
	            is = c.getInputStream();
	            byte[] buffer = new byte[1024];
	            int len1 = 0;
	            while ((len1 = is.read(buffer)) != -1) {
	                fos.write(buffer, 0, len1);
	            }	 
	           System.out.println("--pdf downloaded--ok--" + urlToDownload);
	        } finally{
	        	if (is != null ){
		            fos.close();
		            is.close();
	        	}
	        }
			return "sucess";			
		}

		@Override
		protected void onProgressUpdate(Integer... progress){
			super.onProgressUpdate(progress);
			//mBuilder.setProgress(100, progress[0], false);
			//mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());
		}
		
		/*
		 * Cuando termina la descarga, borro la notificacion anterior y creo otra
		 * tal que al clickearla, me abra directamente el archivo.
		 */
		@Override
		protected void onPostExecute(String result){
			super.onPostExecute(result);
			mBuilder.setProgress(0, 0, false);
			mNotificationManager.cancel(NOTIF_ALERTA_ID);
			
			//mBuilder = new NotificationCompat.Builder(getApplication()); 

			if (result.equalsIgnoreCase("sucess")){
			    File file = new File(Environment.getExternalStorageDirectory() + "/download/"+fileName+".pdf");
			    MimeTypeMap map = MimeTypeMap.getSingleton();
			    String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
			    String type = map.getMimeTypeFromExtension(ext);
	
			    if (type == null)
			        type = "*/*";
			    Intent intent = new Intent(Intent.ACTION_VIEW);
			    Uri data = Uri.fromFile(file);
	
			    intent.setDataAndType(data, type);
			    
			    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);    
			    
				System.out.println("despues");
				mBuilder.setTicker("Descarga completa")
				.setContentTitle("Descarga completa")
				.setContentText("Seleccione para abrir.")
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon((((BitmapDrawable)getResources()
						.getDrawable(R.drawable.default_logo)).getBitmap()))
				.setAutoCancel(true)			
				.setContentIntent(pendingIntent);
				mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());
			} else {
				File file = new File(Environment.getExternalStorageDirectory() + "/download/"+fileName+".pdf");
				file.delete();
				mBuilder.setTicker("Error en la descarga")
				.setContentTitle("Error en la descarga")
				.setContentText("Hubo un error en la descarga, intente nuevamente")
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon((((BitmapDrawable)getResources()
						.getDrawable(R.drawable.ic_launcher)).getBitmap()))
				.setAutoCancel(true);	
				
				
				mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());
				
			}
		}
	}
}
