package com.ruitzei.utilitarios;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class ParserColon {
	
	
	public List<ItemAgenda> parse(InputStream in) throws XmlPullParserException, IOException{
		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(in, null);
		parser.nextTag();
		return readRss(parser);
	}
	
	private List<ItemAgenda> readRss (XmlPullParser parser) throws XmlPullParserException, IOException{
		List<ItemAgenda> agenda = new ArrayList<ItemAgenda>();
		int eventType = parser.getEventType();
		
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_DOCUMENT) {
                System.out.println("Start document");
            } else if(eventType == XmlPullParser.END_DOCUMENT) {
                System.out.println("End document");

            } else if(eventType == XmlPullParser.START_TAG) {
                System.out.println("Start tag "+parser.getAttributeCount());
                if (parser.getName().equalsIgnoreCase("row")){
                	agenda.add(parsearItem(parser));
                }
            } else if(eventType == XmlPullParser.END_TAG) {
                System.out.println("End Tag.");
            } else if(eventType == XmlPullParser.TEXT) {
            }
            eventType = parser.next();
	}
        return agenda;	

}
	
	
	/** Posiciones dentro del XML:
	 * 0    ->  Tipo
	 * 1    ->  Nombre
	 * 2    ->  Descripcion
	 * 4    ->  Fecha
	 * 5    ->  Link
	 * 6    ->  Link Adicional.
	 */
	private ItemAgenda parsearItem(XmlPullParser parser) throws XmlPullParserException, IOException{
		String tipo = null;
		String nombre = null;
		String descripcion = null;
		String fecha = null;
		String link = null;
		String linkAdicional = null;
		
		tipo = parsearTipo(parser.getAttributeValue(0));		
		nombre = parser.getAttributeValue(1);
		descripcion = parser.getAttributeValue(2);
		fecha = parser.getAttributeValue(4);
		link = parser.getAttributeValue(5);
		linkAdicional = parser.getAttributeValue(6);
		
		return new ItemAgenda(tipo, nombre, descripcion, fecha, link, linkAdicional);
	}

	private String parsearTipo(String tipo) {
		int posGuion = tipo.indexOf('-');
		int finGuion = tipo.lastIndexOf('-');
		
		if ((finGuion < 0) || (finGuion == posGuion)){
			finGuion = tipo.length();
		}
		
		return tipo.substring(posGuion +2, finGuion);
	}
}
