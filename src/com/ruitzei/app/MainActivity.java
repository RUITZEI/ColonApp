package com.ruitzei.app;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.ruitzei.utilitarios.DescargarPdf;
import com.ruitzei.utilitarios.ItemAgenda;
import com.ruitzei.z_zteatro.R;


public class MainActivity extends ActionBarActivity implements OnBackStackChangedListener{

	private List<ItemAgenda> noticias;
	private ArrayAdapter<CharSequence> adapterSpinner;
	private static final String URL_PROGRAMA = "https://www.tuentrada.com/Articlemedia/Images/Brands/Colon/prog_colon_2014.pdf";
	private static final String URL_TEMPORADA ="";
	
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
				confirmarDescarga("programa", URL_PROGRAMA);
				break;
				
			case R.id.btn_temporada:
				confirmarDescarga("Teatro_Colon_Temporada_2014", URL_PROGRAMA);
				break;
				
			default:
				break;
			}
		}
		
		/*
		 * Se abre el mensaje de Dialogo para que el usuario confirme la descarga.
		 * Siempre y cuando tenga acceso a Internet
		 */
		private void confirmarDescarga(final String nombreArchivo, final String linkArchivo){
			if (((MainActivity)getActivity()).tieneConexionInternet()){
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				
				builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						new DescargarPdf(nombreArchivo,getActivity()).execute(linkArchivo);
					}
				       });			
				builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				           public void onClick(DialogInterface dialog, int id) {
				        	   //No hago nada si el usuario apretó cancelar
				           }
				       });
				builder.setMessage(R.string.alert_download);				
				AlertDialog dialog = builder.show();
			}else{
				Toast.makeText(getActivity(), R.string.msg_nointernet_programa, Toast.LENGTH_LONG).show();
			}
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
	    //Cuando toca la flechita para arriba, vuelve al fragment anterior.
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
}
