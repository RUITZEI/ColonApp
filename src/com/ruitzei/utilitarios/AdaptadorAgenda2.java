package com.ruitzei.utilitarios;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.ruitzei.z_zteatro.R;

public class AdaptadorAgenda2 extends ArrayAdapter<Object> implements Filterable{
	Context contexto;
	private List<ItemAgenda> noticias;
	private ImageLoader mImageLoader;
	private List<ItemAgenda> noticiasFiltradas;
	
	
	public AdaptadorAgenda2 (Context contexto, List<ItemAgenda> noticias){
		super(contexto, R.layout.item_agenda_2);
		this.contexto = contexto;
		this.noticias  = noticias;
		this.noticiasFiltradas = this.noticias;
	}
	
	@Override
	public int getCount(){
		return noticiasFiltradas.size();
	}
	
	private static class PlaceHolder{
		TextView tipo;
		TextView descripcion;
		TextView fecha;
		NetworkImageView foto;
	
		
		public static PlaceHolder generate (View convertView){
			PlaceHolder placeHolder = new PlaceHolder();
			placeHolder.tipo = (TextView)convertView.findViewById(R.id.textView1);
			placeHolder.descripcion = (TextView)convertView.findViewById(R.id.textView2);
			placeHolder.fecha = (TextView)convertView.findViewById(R.id.textView3);
			placeHolder.foto=(NetworkImageView)convertView.findViewById(R.id.imageView1);
			
			return placeHolder;			
		}		
	}
	
	public View getView(int position, View convertView, ViewGroup parent){
		PlaceHolder placeHolder;
		if (convertView == null){
			convertView = View.inflate(contexto,R.layout.item_agenda_2 ,null);
			placeHolder = PlaceHolder.generate(convertView);
			convertView.setTag(placeHolder);
		} else {
			placeHolder = (PlaceHolder)convertView.getTag();
		}
		
		//Obteniendo instancia del volley.
		mImageLoader = VolleySingleton.getInstance().getImageLoader();
		
		//
		placeHolder.foto.setImageUrl("http://3.bp.blogspot.com/-R_7qdxVpSIg/UTtjWiBNO-I/AAAAAAAABCE/MTX-FUb4LTY/s1600/ballet-el-lago-de-los-cisnes%5B1%5D.jpg",mImageLoader);
		placeHolder.tipo.setText(noticiasFiltradas.get(position).getTipo());
		placeHolder.descripcion.setText(noticiasFiltradas.get(position).getDescripcion());
		placeHolder.fecha.setText(noticiasFiltradas.get(position).getFecha());

		if (position % 2 == 0){
			convertView.setBackgroundResource(R.drawable.selector_lista);
		}else{
			convertView.setBackgroundResource(R.drawable.selector_lista_secundario);
		}		
		
		return convertView;
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