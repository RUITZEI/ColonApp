package com.ruitzei.utilitarios;

public class ItemAgenda {
	
	private String nombre;
	private String tipo;
	private String fecha;
	private String link;
	private String logoId;
	private char disponibilidad;
	
	public ItemAgenda(String nombre, String tipo,  String fecha, String link,
					  String logoId, char disponibilidad){
		setNombre(nombre);
		setTipo(tipo);
		setFecha(fecha);
		setLink(link);
		setLogoId(logoId);
		setDisponibilidad(disponibilidad);
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
	
	
}
