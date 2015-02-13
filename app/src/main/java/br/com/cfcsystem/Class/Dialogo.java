package br.com.cfcsystem.Class;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by user on 19/11/2014.
 */
public class Dialogo extends AlertDialog {

    private Context context;


    protected Dialogo(Context context) {
        super(context);


    }

    public AlertDialog ShowDialog(String titulo, String mensagem){
        Builder alertDialogBuilder = new Builder(
                context);

        alertDialogBuilder.setTitle(titulo);
        alertDialogBuilder.setPositiveButton("OK", null);
        alertDialogBuilder.setNegativeButton("Cancelar", null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        return alertDialog;
    }

}
