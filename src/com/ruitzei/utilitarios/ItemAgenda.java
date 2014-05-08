package com.ruitzei.utilitarios;

public class ItemAgenda {
	private String tipo;
	private String nombre;
	private String descripcion;
	private String fecha;
	private String link;
	private String linkAdicional;
	
	public ItemAgenda(String tipo, String nombre, String descripcion, String fecha, String link, String linkAdicional){
		setTipo(tipo);
		setNombre(nombre);
		setDescripcion(descripcion);
		setFecha(fecha);
		setLink(link);
		setLinkAdicional(linkAdicional);		
	}
	
	private void setTipo(String tipo) {
		this.tipo = tipo;		
	}

	public void setNombre(String nombre){
		this.nombre=nombre;
	}
	
	public void setDescripcion(String descripcion){
		this.descripcion = descripcion;
	}
	
	public void setFecha(String fecha){
		this.fecha = fecha;		
	}
	
	public void setLink(String link){
		this.link = link;		
	}
	
	public void setLinkAdicional(String linkAdicional){
		this.linkAdicional = linkAdicional;		
	}
	
	public String getTipo(){
		return this.tipo;
	}
	
	public String getNombre(){
		return this.nombre;		
	}
	
	public String getDescripcion(){
		return this.descripcion;		
	}
	
	public String getFecha(){
		return this.fecha;
	}
	
	public String getLink(){
		return this.link;
	}
	
	public String getLinkAdicional(){
		return this.linkAdicional;
	}
}
