package com.ruitzei.utilitarios;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class RssParser {
	SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss", Locale.ENGLISH);
	
		
	//No uso NameSpaces
	private String ns = null;
		
	public List<NoticiaOle> parse(InputStream in) throws XmlPullParserException, IOException, ParseException {
		try {
			System.out.println("Parseando...");
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readRss(parser);
		} finally {
			in.close();
		}

	}

		/**
		 * Aca es donde va a empezar abuscar lo que se le pide
		 * Ver dentro del XML con que tag abre (ayuda mirar al final del archivo para ver
		 * los tags principales.
		 */
		private List<NoticiaOle> readRss(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {
			System.out.println("Leyendo RSS");
			parser.require(XmlPullParser.START_TAG, ns, "rss");
			List<NoticiaOle> noticias = new ArrayList<NoticiaOle>();
			
			while ( parser.next() != XmlPullParser.END_TAG){
				if (parser.getEventType() != XmlPullParser.START_TAG){
					continue;
				}			
				String name =parser.getName();
				
				if (name.equals("channel")){
					return (readChannel(parser, noticias));
				} else {
					skip(parser);
					
				}
			}	
			
			//Nunca deberia entrar por aca porque ya se de antes los nombres de los tags.
			return noticias;
			
		}
		

		private List<NoticiaOle> readChannel(XmlPullParser parser, List<NoticiaOle> noticias) throws XmlPullParserException, IOException, ParseException {
			parser.require(XmlPullParser.START_TAG, ns, "channel");
			
			System.out.println("leyendo Channel");			
			
			while (parser.next() != XmlPullParser.END_TAG){
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				
				String name =parser.getName();				
				if (name.equals("item")){
					noticias.add(readItem(parser));
				} else {
					skip(parser);
				}						
			}
			return noticias;
		}
		
		
		/**
		 * Aca iria leyendo las cosas que me interesan para ir armando mi array
		 */
		private NoticiaOle readItem(XmlPullParser parser)throws XmlPullParserException, IOException, ParseException {
			parser.require(XmlPullParser.START_TAG, ns, "item");
			String titulo = null;
			String link = null;
			String descripcion = null;			
			Date date = null;
			
			System.out.println("Leyendo los Items");
			
			
			while (parser.next() != XmlPullParser.END_TAG){
				if (parser.getEventType() != XmlPullParser.START_TAG) {
					continue;
				}
				
				
				String name = parser.getName();
				if (name.equals("title")){
					titulo = readTitulo(parser);
				} else if (name.equals("link")){
					link = readLink(parser);
				} else if (name.equals("description")){
					descripcion = readDescripcion(parser);
				} else if (name.equals("pubDate")){
					date = readDate(parser);
				}else{
					skip(parser);
				}
					
			}	
			System.out.println("Item parseado...");

			return new NoticiaOle(titulo, link, descripcion, date);
		}
		
		
		
		private Date readDate(XmlPullParser parser) throws IOException, XmlPullParserException, ParseException{
			parser.require(XmlPullParser.START_TAG, ns, "pubDate");
			Date fecha = formatter.parse(readText(parser));
			parser.require(XmlPullParser.END_TAG, ns, "pubDate");
			
			return fecha;
		}
		private String readDescripcion(XmlPullParser parser)throws IOException, XmlPullParserException {
			parser.require(XmlPullParser.START_TAG, ns, "description");
			String description = readText(parser);
			parser.require(XmlPullParser.END_TAG, ns, "description");
			
			//Le resto 4 porque no me quiere aceptar el &
			int posicionFinDescripcion = description.indexOf("img");			
			return description.substring(0,posicionFinDescripcion -  1);
		}		

		private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
			parser.require(XmlPullParser.START_TAG, ns, "link");
			String link = readText(parser);
			parser.require(XmlPullParser.END_TAG, ns, "link");
			return link;
		}
		
		private String readTitulo(XmlPullParser parser) throws IOException, XmlPullParserException {
			parser.require(XmlPullParser.START_TAG, ns, "title");
			String titulo = readText(parser);
			parser.require(XmlPullParser.END_TAG, ns, "title");
			return titulo;
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


