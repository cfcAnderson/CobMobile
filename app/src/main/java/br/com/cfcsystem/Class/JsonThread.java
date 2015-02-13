package br.com.cfcsystem.Class;

import android.os.Handler;

import br.com.cfcsystem.cobmobile.DatabaseHandler;


/**
 * Created by user on 23/10/2014.
 */
public class JsonThread {
    private Thread thread = null;
    private DatabaseHandler dbHandler = null;
    private Handler handler;
    private JsonGetCobranca jsonGetCobranca = null;
    private JsonSetCobranca jsonSetCobranca = null;
    private JsonCobrancaPaga jsonCobrancaPaga = null;

    public JsonThread(DatabaseHandler dbHandler){
        this.dbHandler = dbHandler;



    }

    public void StartaThread(Boolean ativaThread){
        /*

        if((ativaThread == true)) {



            try {

                jsonGetCobranca = new JsonGetCobranca("http://94.249.188.151/cobrancaweb/consulta/getcobranca",null,dbHandler);
                jsonGetCobranca.JsonThread();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                jsonSetCobranca = new JsonSetCobranca("http://94.249.188.151/cobrancaweb/consulta/updatecobranca?array="," where status <> 'A' ",null,dbHandler);
                jsonSetCobranca.JsonThread();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            try {
                jsonCobrancaPaga = new JsonCobrancaPaga("http://94.249.188.151/cobrancaweb/consulta/updateValor?array=",null,dbHandler);
                jsonCobrancaPaga.JsonThread();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }



        }
        */

    }

    public void PauseThread(){

        if (jsonGetCobranca != null){
            jsonGetCobranca.PauseThread();
        }

        if (jsonSetCobranca != null){
            jsonSetCobranca.PauseThread();
        }

        if (jsonCobrancaPaga != null){
            jsonCobrancaPaga.PauseThread();
        }
    }
}
