package com.ruitzei.utilitarios;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.ruitzei.colonApp.R;

public class AdapterAgenda extends ArrayAdapter<Object> implements Filterable{
	Context contexto;
	private List<ItemAgenda> noticias;
	private ImageLoader mImageLoader;
	private List<ItemAgenda> noticiasFiltradas;	
	private Hashtable<Character, Integer> tablaDisponibilidad;
	private static final String URL_BALLET = "http://3.bp.blogspot.com/-R_7qdxVpSIg/UTtjWiBNO-I/AAAAAAAABCE/MTX-FUb4LTY/s1600/ballet-el-lago-de-los-cisnes%5B1%5D.jpg";
	private static final String URL_OPERA = "https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcRyiXI4PEL4lr725Bldtawz9VJLVU1b7ayzgqFktV8dfLHlG8uRhh2JMA";
	private static final String URL_FILARMONICA = "http://www.teatrocolon.org.ar/images/colon_contemporaneo/vendedora_de_fosforos_list.jpg";
	private static final String URL_DEFAULT = "http://du4zwgdg3nwxa.cloudfront.net/logotipos/t/te/teatro-colon-buenos-aires-logo-1331322641.jpg";
	
	public AdapterAgenda (Context contexto, List<ItemAgenda> noticias){
		super(contexto, R.layout.item_agenda);
		this.contexto = contexto;
		this.noticias  = noticias;
		this.noticiasFiltradas = this.noticias;
		inicializarTabla();
	}
	

	private void inicializarTabla() {
		tablaDisponibilidad = new Hashtable<Character, Integer>();
		tablaDisponibilidad.put('E', R.drawable.availability_excellent);
		tablaDisponibilidad.put('G', R.drawable.availability_good);
		tablaDisponibilidad.put('L', R.drawable.availability_limited);
		tablaDisponibilidad.put('S', R.drawable.availability_sold_out);
	}


	@Override
	public int getCount(){
		return noticiasFiltradas.size();
	}
	
	@Override
	public ItemAgenda getItem(int position){
		return noticiasFiltradas.get(position);
	}
	
	private static class PlaceHolder{
		TextView tipo;
		TextView nombre;
		TextView fecha;
		NetworkImageView foto;
		ImageView disponibilidad;
	
		
		public static PlaceHolder generate (View convertView){
			PlaceHolder placeHolder = new PlaceHolder();
			placeHolder.nombre = (TextView)convertView.findViewById(R.id.item_nombre);
			placeHolder.tipo = (TextView)convertView.findViewById(R.id.item_tipo);
			placeHolder.fecha = (TextView)convertView.findViewById(R.id.item_fecha);
			placeHolder.foto=(NetworkImageView)convertView.findViewById(R.id.item_foto);
			placeHolder.disponibilidad = (ImageView)convertView.findViewById(R.id.disponibilidad);
			
			return placeHolder;			
		}		
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		PlaceHolder placeHolder;
		if (convertView == null){
			convertView = View.inflate(contexto,R.layout.item_agenda ,null);
			placeHolder = PlaceHolder.generate(convertView);
			convertView.setTag(placeHolder);
		} else {
			placeHolder = (PlaceHolder)convertView.getTag();
		}
		
		//Obteniendo instancia del volley.
		mImageLoader = VolleySingleton.getInstance().getImageLoader();
		
		//placeHolder.foto.setImageUrl("http://3.bp.blogspot.com/-R_7qdxVpSIg/UTtjWiBNO-I/AAAAAAAABCE/MTX-FUb4LTY/s1600/ballet-el-lago-de-los-cisnes%5B1%5D.jpg",mImageLoader);
		//placeHolder.foto.setImageUrl("https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcRyiXI4PEL4lr725Bldtawz9VJLVU1b7ayzgqFktV8dfLHlG8uRhh2JMA",mImageLoader);
		placeHolder.tipo.setText(noticiasFiltradas.get(position).getTipo());
		placeHolder.nombre.setText(noticiasFiltradas.get(position).getNombre());
		placeHolder.fecha.setText(noticiasFiltradas.get(position).getFecha());
		placeHolder.disponibilidad.setImageResource(tablaDisponibilidad.get(noticiasFiltradas.get(position).getDisponibilidad()));
		
		String tipo = noticiasFiltradas.get(position).getNombre().toLowerCase();
		setFoto(placeHolder, noticiasFiltradas.get(position).getLogoId(), tipo);
		
		//Fondo Gris o negro segun corresponda.
		if (position % 2 == 0){
			convertView.setBackgroundResource(R.drawable.selector_lista);
		}else{
			convertView.setBackgroundResource(R.drawable.selector_lista_secundario);
		}		
		
		return convertView;
	}
	
	
	private void setFoto(PlaceHolder placeHolder, String link, String tipo) {
		if (link.length() == 0){
			if (tipo.contains("ballet")){
				placeHolder.foto.setImageUrl(URL_BALLET,mImageLoader);
			} else if (tipo.contains("opera")){
				placeHolder.foto.setImageUrl(URL_OPERA ,mImageLoader);
			} else if (tipo.contains("filarmonica")){
				placeHolder.foto.setImageUrl( URL_FILARMONICA ,mImageLoader);
			}else{
				//Cargo un link inválido ?
				placeHolder.foto.setImageUrl(URL_DEFAULT ,mImageLoader);
			}			
		}else {
			placeHolder.foto.setImageUrl(link, mImageLoader);
		}
		
		placeHolder.foto.setErrorImageResId(R.drawable.default_logo);
		placeHolder.foto.setDefaultImageResId(R.drawable .default_logo);	
	}


	/**
	 * Cada vez que se le pasa un filtro, se recorre toda la lista de noticias
	 * buscando aquellas que cumplan con la condicion de filtro (contienen tal string)
	 * Las que cumplen con la condicion son ingresadas en el nuevo array de noticias
	 * que despues es el invocado por el getView del Adapter.
	 * En caso de no ser llamado no pasa nada porque en el constructor del Adapter,
	 * el valor inicial de las NoticiasFiltradas equivale al de las Noticias.
	 */	

	@Override
	public Filter getFilter(){
		Filter myFilter = new Filter(){
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {		
				if (constraint != null){
					List<ItemAgenda> todasLasNoticias = noticias;
					List<ItemAgenda> filtradas = new ArrayList<ItemAgenda>();
					String palabraFiltro = constraint.toString().toLowerCase();
					String nombreActual;
					String tipoActual;
					for (int i = 0 ;i < todasLasNoticias.size(); i++){
						nombreActual = todasLasNoticias.get(i).getNombre().toLowerCase();
						tipoActual = todasLasNoticias.get(i).getTipo().toLowerCase();
						if( nombreActual.contains(palabraFiltro) || tipoActual.contains(palabraFiltro)){
							filtradas.add(todasLasNoticias.get(i));
						}
					}
					FilterResults results = new FilterResults();
					results.values = filtradas;
					results.count = filtradas.size();
					return results;
				}else{
					return new FilterResults();
				}						
			}

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {								
				if (results.count > 0){
					noticiasFiltradas = (ArrayList<ItemAgenda>)results.values;
					notifyDataSetChanged();				
				}else{
					noticiasFiltradas = new ArrayList<ItemAgenda>();
					notifyDataSetInvalidated();
				}
				
			}			
		};
		return myFilter;		
	}
}