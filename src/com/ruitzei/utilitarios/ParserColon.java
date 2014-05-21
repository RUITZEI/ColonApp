package com.ruitzei.utilitarios;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class ParserColon {
	
	private String ns = null;
	private static final String ATT_NOMBRE = "performance_description";
	private static final String ATT_TIPO = "performance_data2";
	private static final String ATT_FECHA = "data1";
	private static final String ATT_LINK = "performance_id";
	private static final String ATT_LOGOID = "performance_logo1";
	private static final String ATT_DISPONIBILIDAD = "availability_status";
	
	public List<ItemAgenda> parse(InputStream in) throws XmlPullParserException, IOException{
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readResults(parser);
		} finally {
			in.close();
		}
	}
	
	private List<ItemAgenda> readResults (XmlPullParser parser) throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, ns, "results");
		List<ItemAgenda> noticias = new ArrayList<ItemAgenda>();
		
		while ( parser.next() != XmlPullParser.END_TAG){
			if (parser.getEventType() != XmlPullParser.START_TAG){
				continue;
			}			
			String name =parser.getName();
			
			if (name.equals("event")){
				noticias.add (readEvent(parser));
			} else {
				skip(parser);				
			}
		}	
		
		return noticias;		
}
	
	private ItemAgenda readEvent(XmlPullParser parser)throws XmlPullParserException, IOException{
		parser.require(XmlPullParser.START_TAG, ns, "event");
		String nombre = null;
		String tipo = null;
		String fecha = null;
		String link = null;
		String logoId = null;
		char disponibilidad = ' ';		
		
		while (parser.next() != XmlPullParser.END_TAG){
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}			
			
			String name = parser.getName();
			if (name.equals(ATT_NOMBRE)){
				nombre = leerNombre(parser);
			} else if (name.equals(ATT_TIPO)){
				tipo = leerTipo(parser);
			} else if (name.equals(ATT_FECHA)){
				fecha = leerFecha(parser);
			} else if (name.equals(ATT_LINK)){
				link = leerLink(parser);
			}else if (name.equals(ATT_LOGOID)){
				logoId = leerLogoId(parser);			
			}else if (name.equals(ATT_DISPONIBILIDAD)){
				disponibilidad = leerDisponibilidad(parser);
			}else{			
				skip(parser);
			}			
		}	

		return new ItemAgenda(nombre, tipo, fecha, link, logoId, disponibilidad);
	}
	
	
	private String leerNombre(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, ATT_NOMBRE);
		String nombre = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, ATT_NOMBRE);
		return nombre;
	}
	
	private String leerTipo(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, ATT_TIPO);
		String tipo = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, ATT_TIPO);
		
		return tipo;
	}
	
	private String leerFecha(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, ATT_FECHA);
		String fecha = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, ATT_FECHA);
		return fecha;
	}
	
	private String leerLink(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, ATT_LINK);
		String link = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, ATT_LINK);
		return link;
	}
	
	private String leerLogoId(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, ATT_LOGOID);
		String logoId = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, ATT_LOGOID);
		return logoId;
	}	
	
	private char leerDisponibilidad(XmlPullParser parser) throws IOException, XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, ATT_DISPONIBILIDAD);
		char asientosLibres = readText(parser).charAt(0);
		parser.require(XmlPullParser.END_TAG, ns, ATT_DISPONIBILIDAD);
		return asientosLibres;
	}
		
	//Aca estoy levantando texto del atributo pedido.
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String resultado = "";
		if (parser.next() == XmlPullParser.TEXT){
			resultado = parser.getText();
			parser.nextTag();
		}		
		return resultado;
	}

	
	//Si no me interesa porque no tiene el tag que le pedi, lo mando al carajo.
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException{
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                    depth--;
                    break;
            case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }			
	}
}
