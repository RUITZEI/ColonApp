package com.ruitzei.utilitarios;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.ruitzei.colonApp.R;


/**
 *    Clase creada para descargarse un PDF desde la web. Crea una notificacion
 *  que muestra cuando comienza la descarga, si la misma fue interrumpida,
 *  va mostrando el progreso de la misma y muestra cuando finaliza.
 *    Una vez finalizada, al clickear sobre la notificacion se abre la misma.
 *    
 * + fileNmae: Nombre del archivo a guardarse.
 * + Context:  El contexto de la aplicación sobre el que se llama (La actividad)
 */
public class DescargarPdf extends AsyncTask<String, Integer, String>{
	private String fileName;
	private Context context;
	private NotificationCompat.Builder mBuilder;
	private NotificationManager mNotificationManager;
	private static final int NOTIF_ALERTA_ID = 1;
	
	public DescargarPdf(String fileName, Context context){
		this.fileName = fileName;
		this.context = context;
	}
	
	/**
	 * Prepara la notificacion para mostrarla en la Barra de tareas
	 */
	@Override
	protected void onPreExecute(){
		super.onPreExecute();
		mBuilder = new NotificationCompat.Builder(context.getApplicationContext())
		.setTicker(context.getResources().getString(R.string.msg_download_starting))
		.setSmallIcon(android.R.drawable.stat_sys_download)
		.setLargeIcon((((BitmapDrawable)context.getResources()
				.getDrawable(R.drawable.ic_launcher)).getBitmap()))
		.setContentTitle(context.getResources().getString(R.string.msg_downloading))
		.setContentText(context.getResources().getString(R.string.msg_download_in_progress))
		.setProgress(100, 0, false);

		Intent intent = new Intent();
		PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		mBuilder.setContentIntent(pendingIntent);
		
		mNotificationManager =
			    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());
	}
	
	
	/**
	 *  Llama a CargarPdf, pasandole el Link del archivo a ejecutarse.
	 */
	@Override
	protected String doInBackground(String... params){
		try {
			return cargarPdfDeInternet(params[0]);
		} catch (IOException e) {
			return "error";
		}
	}
	
	private String cargarPdfDeInternet(String params) throws IOException{
		FileOutputStream salida = null;
		InputStream entrada = null;
		try {
			String fileExtension=".pdf"; 
			URL url = new URL(params);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setReadTimeout(100000);
            c.setConnectTimeout(15000);
            c.connect();
           
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            file.mkdirs();    
            
            File outputFile = new File(file, fileName+fileExtension);
            salida = new FileOutputStream(outputFile);
            entrada = c.getInputStream();            
            int lenghtOfFile = c.getContentLength();
            
            byte[] buffer = new byte[1024];
            int bytesDescargados = 0;
            int total = 0;
            int porcentajeAnterior = 0;
            
            while ((bytesDescargados = entrada.read(buffer)) != -1) {
            	int porcentajeDeDescarga = (total*100)/lenghtOfFile;
            	total += bytesDescargados;
            	//LLamo al publish progress cada 3% para no tildar la UI
            	if  (((porcentajeDeDescarga % 3)== 0) && (porcentajeDeDescarga != porcentajeAnterior)){
            		publishProgress(porcentajeDeDescarga);
            		porcentajeAnterior = porcentajeDeDescarga;
            	}            	
                salida.write(buffer, 0, bytesDescargados);                
            }
		}catch (Exception e){
			Log.e("Descarga Manual","Exception lanzada",e);
			return "Error";
			
        } finally{
        	if (entrada != null ){
	            salida.close();
	            entrada.close();
        	}
        }
		return "sucess";		
	}

	/**
	 * Actualiza el porcentaje en la barra de notificaciones
	 * Debe notificar a la acutalizacion para cambiar su valor.
	 */
	@Override
	protected void onProgressUpdate(Integer... progress){
		super.onProgressUpdate(progress);
			mBuilder.setProgress(100, progress[0], false);		
			mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());
	}
	
	/**
	 * Cuando termina la descarga, borro la notificacion anterior y creo otra
	 * tal que al clickearla, me abra directamente el archivo.
	 */
	@Override
	protected void onPostExecute(String result){
		super.onPostExecute(result);
		mBuilder.setProgress(0, 0, false);
		mNotificationManager.cancel(NOTIF_ALERTA_ID); 
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/"+fileName+".pdf");
		//File file = new File(context.getExternalFilesDir(null)+"/"+fileName+".pdf");
		
		if (result.equalsIgnoreCase("sucess")){
		    MimeTypeMap map = MimeTypeMap.getSingleton();
		    String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
		    String type = map.getMimeTypeFromExtension(ext);

		    if (type == null)
		        type = "*/*";
		    Intent intent = new Intent(Intent.ACTION_VIEW);
		    Uri data = Uri.fromFile(file);

		    intent.setDataAndType(data, type);
		    
		    PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);    
		    
			mBuilder.setTicker(context.getResources().getString(R.string.msg_download_complete))
			.setContentTitle(context.getResources().getString(R.string.msg_download_complete))
			.setContentText(context.getResources().getString(R.string.msg_download_open))
			.setSmallIcon(android.R.drawable.stat_sys_download_done)
			.setLargeIcon((((BitmapDrawable)context.getResources()
					.getDrawable(R.drawable.ic_launcher)).getBitmap()))
			.setAutoCancel(true)			
			.setContentIntent(pendingIntent);
			
			mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());
		} else {  //  -> hubo un error en la descarga
			file.delete();
			mBuilder.setTicker(context.getResources().getString(R.string.msg_download_error))
			.setContentTitle(context.getResources().getString(R.string.msg_download_error))
			.setContentText(context.getResources().getString(R.string.msg_download_error_long))
			.setSmallIcon(android.R.drawable.stat_sys_warning)
			.setLargeIcon((((BitmapDrawable)context.getResources()
					.getDrawable(R.drawable.ic_launcher)).getBitmap()))						
			.setAutoCancel(true);			
			
			mNotificationManager.notify(NOTIF_ALERTA_ID, mBuilder.build());
			
		}
	}
}