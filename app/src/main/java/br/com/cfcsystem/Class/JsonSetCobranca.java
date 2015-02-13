package br.com.cfcsystem.Class;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import java.io.UnsupportedEncodingException;

import br.com.cfcsystem.Controls.CobrancaController;
import br.com.cfcsystem.Entity.Cobranca;
import br.com.cfcsystem.cobmobile.DatabaseHandler;

/**
 * Created by user on 29/10/2014.
 */
public class JsonSetCobranca extends Activity {
    private CobrancaController cc = null;
    private String url;
    private ProgressDialog dialog;
    private String parametro;
    private Thread thread = null;
    private Context context = null;
    private String erro = "Nenhum";
    private CobrancaAdapter cobrancaAdapter = null;

    public JsonSetCobranca(CobrancaAdapter cobrancaAdapter, Context context, String url, String parametro,ProgressDialog dialog, DatabaseHandler dbHandler) throws UnsupportedEncodingException {
        this.dialog = dialog;
        this.context = context;

        this.cobrancaAdapter = cobrancaAdapter;

        this.url = url;

        this.parametro = parametro;

        cc = new CobrancaController(dbHandler);

        new SetCobranca().execute();
    }

    private class SetCobranca extends AsyncTask<String, String, String> {
        protected void onPostExecute(String result){
            if (dialog != null){
                dialog.dismiss();
            }

            if (cobrancaAdapter != null){
                cc.setConsulta("*","");
                cobrancaAdapter.carregaLista(cc);
                cobrancaAdapter.notifyDataSetChanged();
            }

            try {
                if (result.equalsIgnoreCase("Erro")) {
                    Toast toast = Toast.makeText(context, "Erro ao atualizar cobranca.", Toast.LENGTH_LONG);
                    toast.show();
                } else if (result.equalsIgnoreCase("Nenhum")) {
                    Toast toast = Toast.makeText(context, "Nenhuma cobranca para ser atualizada", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(context, "Cobranca atualizada com sucesso.", Toast.LENGTH_LONG);
                    toast.show();
                }
            }catch (Exception e){
                System.out.println("erro aqui: "+ e.getMessage());
            }

        }

        @Override
        protected String doInBackground(String... params) {
            System.out.println("JsonSetcOBRANCA ");

            cc.setConsulta("*", parametro);

            JSONArray jsonArray = new JSONArray();
            try {
                for (Cobranca cobranca : cc.getConsulta()) {
                    jsonArray.put(cobranca.getCodigo());
                }
            } catch (Exception e) {
                //System.out.println(e.getMessage());
                e.printStackTrace();
            }

            if (jsonArray.length() > 0) {
                try {
                    HttpParams httpParams = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
                    HttpConnectionParams.setSoTimeout(httpParams, 10000);
                    HttpClient httpclient = new DefaultHttpClient(httpParams);
                    String json = jsonArray.toString();



                    HttpPost httppost = new HttpPost(url + jsonArray.toString());
                    HttpResponse response = httpclient.execute(httppost);
                    String temp = EntityUtils.toString(response.getEntity());
                    if (! temp.equals("")){
                        erro = "temp";
                        //Toast.makeText(context, "Erro ao Atualizar Cobranca.", Toast.LENGTH_LONG).show();
                    }

                    cc.UpdateJson(jsonArray.toString(), "I");
                    erro = "Sucesso";
                } catch (Exception e){
                    //System.out.println("Erro: "+ e.getMessage());
                    erro = "Erro";
                    onCancelled("Erro");
                    e.printStackTrace();

                }
            }else{
                erro = "Nenhum";
                onCancelled("Nenhum");
            }
            return erro;
        }
    }



    public void PauseThread(){
        if (thread != null) {
            System.out.println("JsonSetCobranca stop");
            thread.interrupt();
        }
    }


}
