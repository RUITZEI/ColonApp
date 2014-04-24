package com.ruitzei.utilitarios;

import java.util.Date;

public class NoticiaOle {
	
	private String titulo;
	private String link;
	private String descripcion;
	private Date fecha;
	
	public NoticiaOle(String titulo, String link, String descripcion, Date fecha){
		this.titulo=titulo;
		this.link=link;
		this.descripcion = descripcion;
		this.fecha = fecha;		
	}
	
	public void setTitulo(String titulo){
		this.titulo = titulo;
	}
	
	public void setLink (String link){
		this.link = link;
	}
	
	public void setDescripcion (String descripcion){
		this.descripcion = descripcion;
	}
	
	public void setDate (Date fecha){
		this.fecha = fecha;
	}
	
	public String getTitulo(){
		return this.titulo;
	}
	
	public String getLink(){
		return this.link;
	}
	
	public String getDescripcion (){
		return this.descripcion;
	}
	
	public Date getFecha(){
		return this.fecha;
	}
}
