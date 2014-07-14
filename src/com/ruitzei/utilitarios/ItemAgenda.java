package com.ruitzei.utilitarios;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ItemAgenda {
	
	private String nombre;
	private String tipo;
	private String fecha;
	private String link;
	private String logoId;
	private String fechaDeVenta;
	private char disponibilidad;
	
	public ItemAgenda(String nombre, String tipo,  String fecha, String link,
					  String logoId, String fechaDeVenta, char disponibilidad){
		setNombre(nombre);
		setTipo(tipo);
		setFecha(fecha);
		setLink(link);
		setLogoId(logoId);
		setDisponibilidad(disponibilidad);
		setFechaDeVenta(fechaDeVenta);
	}
	
	public void setFechaDeVenta(String fechaDeVenta) {
		this.fechaDeVenta = fechaDeVenta;
	}

	public void setDisponibilidad(char disponibilidad) {
		this.disponibilidad = disponibilidad;		
	}

	private void setLogoId(String logoId) {
		this.logoId = logoId;		
	}

	private void setTipo(String tipo) {
		this.tipo = tipo;		
	}

	public void setNombre(String nombre){
		this.nombre=nombre;
	}
	
	public void setFecha(String fecha){
		this.fecha = fecha;		
	}
	
	public void setLink(String link){
		this.link = link;		
	}
	
	
	public String getTipo(){
		return this.tipo;
	}
	
	public String getNombre(){
		return this.nombre;		
	}
	
	public String getFecha(){
		return this.fecha;
	}
	
	public String getLink(){
		return this.link;
	}

	public String getLogoId(){
		return this.logoId;
	}

	public char getDisponibilidad(){
		return this.disponibilidad;
	}
	
	public String getFechaDeVenta(){
		return this.fechaDeVenta;
	}
	
	public String getFechaDeVentaConvertida(){
		String fecha = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm ", new Locale("es", "ES"));
		Date fechaConvertida = new Date();	
		
		try{
			fechaConvertida = dateFormat.parse(this.fechaDeVenta);
			SimpleDateFormat postFormat = new SimpleDateFormat("dd'/'M 'a las' HH:mm'hs'", new Locale("es", "ES"));			
			fecha =  postFormat.format(fechaConvertida);

		} catch (ParseException e){
			e.printStackTrace();
		}	
		return fecha;
	}
}
