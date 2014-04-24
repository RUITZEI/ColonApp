package com.ruitzei.utilitarios;

import android.webkit.WebView;
import android.webkit.WebViewClient;


/**
 * Permite ver Páginas web dentro de la aplicacion sin recurrir al navegador por defecto
 * de cada celular.
 */
public class ClienteWeb extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }

}
