package com.ruitzei.utilitarios;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.ruitzei.z_zteatro.R;

public class AdaptadorAgenda extends ArrayAdapter<Object>{
	Context contexto;
	private List<NoticiaOle> noticias;
	private ImageLoader mImageLoader;
	
	
	public AdaptadorAgenda (Context contexto, List<NoticiaOle> noticias){
		super(contexto, R.layout.item_agenda_2);
		this.contexto = contexto;
		this.noticias  = noticias;
	}
	
	@Override
	public int getCount(){
		return noticias.size();
	}
	
	private static class PlaceHolder{
		TextView titulo;
		TextView descripcion;
		TextView fecha;
		NetworkImageView foto;
	
		
		public static PlaceHolder generate (View convertView){
			PlaceHolder placeHolder = new PlaceHolder();
			placeHolder.titulo = (TextView)convertView.findViewById(R.id.textView1);
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
			//System.out.println("null");
		} else {
			placeHolder = (PlaceHolder)convertView.getTag();
		}
		
		//Obteniendo instancia del volley.
		mImageLoader = VolleySingleton.getInstance().getImageLoader();
		
		//
		placeHolder.foto.setImageUrl("http://3.bp.blogspot.com/-R_7qdxVpSIg/UTtjWiBNO-I/AAAAAAAABCE/MTX-FUb4LTY/s1600/ballet-el-lago-de-los-cisnes%5B1%5D.jpg",mImageLoader);
		placeHolder.titulo.setText(noticias.get(position).getTitulo());
		placeHolder.descripcion.setText(noticias.get(position).getDescripcion());
		placeHolder.fecha.setText(noticias.get(position).getFecha().toString().substring(0, 11));

		if (position % 2 == 0){
			convertView.setBackgroundResource(R.drawable.selector_lista);
		}else{
			convertView.setBackgroundResource(R.drawable.selector_lista_secundario);
		}
		
		
		return convertView;
	}

}