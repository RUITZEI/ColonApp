package com.ruitzei.z_zteatro;

import java.lang.reflect.Field;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.ruitzei.utilitarios.ItemAgenda;
import com.ruitzei.utilitarios.NoticiaOle;

public class MainActivity extends ActionBarActivity implements OnBackStackChangedListener{

	private List<ItemAgenda> noticias;
	private ArrayAdapter<CharSequence> adapterSpinner;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
					  //.add(R.id.container, new FragmentAgenda()).commit();
		}
		
		//Para poder usar el iconito de overFlow en todos los dispositivos.
		//quitarBotonOpciones();
		
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
		int id = item.getItemId();
		/**if (id == R.id.action_settings) {
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
			View rootView = inflater.inflate(R.layout.fragment_main_nuevo, container,
					false);
			
			/*
			 * En esta parte se deben inicializar los botones propios de cada fragment.
			 * En este caso serian todos los botones del menu principal.
			 */
			Button botonCargar = (Button)rootView.findViewById(R.id.btn_agenda);
			botonCargar.setOnClickListener(this);
			
			Button botonPrograma = (Button)rootView.findViewById(R.id.btn_programa);
			botonPrograma.setOnClickListener(this);
			return rootView;
		}
		
		@Override
		public void onResume(){
			super.onResume();
			((MainActivity)getActivity()).getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			((MainActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
			((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);
		}
		
		@Override
		public void onClick(View v){
			Fragment fragment = null;
			switch (v.getId()) {
			case R.id.btn_agenda:
				fragment = new FragmentAgenda();
				break;
			case R.id.btn_programa:
				fragment = new FragmentWeb();
				break;
			default:
				break;
			}
			
			FragmentManager fragmentManager = ((MainActivity)getActivity()).getSupportFragmentManager();
			fragmentManager.popBackStack();
			fragmentManager.beginTransaction()
			.replace(R.id.container, fragment)
			.addToBackStack("FragBack")
			.commit();
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
		//Enable Up button only  if there are entries in the back stack
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
	
	private void quitarBotonOpciones() {
	    try {
	        ViewConfiguration config = ViewConfiguration.get(this);
	        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	        if(menuKeyField != null) {
	            menuKeyField.setAccessible(true);
	            menuKeyField.setBoolean(config, false);
	        }
	    } catch (Exception ex) {
	        // Ignore
	    }		
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
}
