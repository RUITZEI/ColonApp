package com.ruitzei.utilitarios;

public class ItemAgenda {
	
	private String nombre;
	private String tipo;
	private String fecha;
	private String link;
	private String logoId;
	private double minPrecio;
	private double maxPrecio;
	private char disponibilidad;
	
	public ItemAgenda(String nombre, String tipo,  String fecha, String link,
					  String logoId, double minPrecio, double maxPrecio, char disponibilidad){
		setNombre(nombre);
		setTipo(tipo);
		setFecha(fecha);
		setLink(link);
		setLogoId(logoId);
		setMinPrecio(minPrecio);
		setMaxPrecio(maxPrecio);
		setDisponibilidad(disponibilidad);
	}
	
	private void setDisponibilidad(char disponibilidad) {
		this.disponibilidad = disponibilidad;		
	}

	private void setMaxPrecio(double maxPrecio) {
		this.maxPrecio = maxPrecio;		
	}

	private void setMinPrecio(double minPrecio) {
		this.minPrecio = minPrecio;		
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
	
	public double getMinPrecio(){
		return this.minPrecio;
	}
	
	public double getMaxPrecio(){
		return this.maxPrecio;
	}
	
	public char getDisponibilidad(){
		return this.disponibilidad;
	}
	
	
}
