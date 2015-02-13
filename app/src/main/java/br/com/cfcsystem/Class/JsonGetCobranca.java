package br.com.cfcsystem.Class;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.cfcsystem.Controls.CobrancaController;
import br.com.cfcsystem.Entity.Cobranca;
import br.com.cfcsystem.cobmobile.DatabaseHandler;

/**
 * Created by user on 29/10/2014.
 */
public class JsonGetCobranca extends Activity {
    private CobrancaController cc = null;
    private ProgressDialog dialog = null;
    private HttpGet request = null;
    private Thread thread = null;
    private Context context = null;
    private String erro = "Nenhum";
    private Parametros parametros;
    private DatabaseHandler dbHandler;
    private SimpleDateFormat dataFormato;
    private String currentDateandTime;
    private Boolean status;
    private CobrancaAdapter cobrancaAdapter = null;


    public JsonGetCobranca(CobrancaAdapter cobrancaAdapter, Context context, String url, ProgressDialog dialog, DatabaseHandler dbHandler){
        setDialog(dialog);


        dataFormato = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        currentDateandTime = dataFormato.format(new Date());

        this.context = context;

        this.dbHandler = dbHandler;
        this.cobrancaAdapter = cobrancaAdapter;

        request = new HttpGet(url);

        cc = new CobrancaController(dbHandler);

        parametros = new Parametros();

        new GetCobranca().execute();
    }

    public ProgressDialog getStatusDialog(){

        return  dialog;
    }

    private class GetCobranca extends AsyncTask<String, String, String> {

        protected void onPostExecute(String result){

            try {
                try {

                    if (result.equalsIgnoreCase("Erro")) {
                        Toast toast = Toast.makeText(context, "Erro ao Baixar Cobranca.", Toast.LENGTH_LONG);
                        toast.show();

                    } else if (result.equalsIgnoreCase("Nenhum")) {
                        Toast toast = Toast.makeText(context, "Nenhuma Cobranca para ser Baixada", Toast.LENGTH_LONG);
                        toast.show();

                    } else {
                        new JsonSetCobranca(cobrancaAdapter, context, parametros.getUrlSetCobranca(), " where status = 'B' ", getDialog(), dbHandler);
                        Toast toast = Toast.makeText(context, "Cobranca Baixada com Sucesso.", Toast.LENGTH_LONG);
                        toast.show();

                    }
                } catch (Exception e) {
                    System.out.println("erro aqui: " + e.getMessage());
                }
            }finally {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try{

                System.out.println("JsonGetCobranca ");

                DefaultHttpClient client = new DefaultHttpClient();
                HttpResponse response = client.execute(request);

                HttpEntity httpEntity = response.getEntity();

                JSONArray jsonArray = new JSONArray(stringBuilder(httpEntity.getContent()).toString());

                if (jsonArray.length() > 0) {

                    cc.Delete("todos");

                    Cobranca cobranca = new Cobranca();
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        cobranca.setCodigo(jsonObject.getInt("CODIGO"));
                        cobranca.setCodCliente(jsonObject.getInt("COD_CLIENTE"));
                        cobranca.setCliente(jsonObject.getString("CLIENTE"));
                        cobranca.setVlAnterior(jsonObject.getDouble("VL_ANTERIOR"));
                        cobranca.setVlPeriodo(jsonObject.getDouble("VL_PERIODO"));
                        cobranca.setCidade(jsonObject.getString("CIDADE"));
                        cobranca.setUf("MS");
                        try {
                            cobranca.setOrdem(jsonObject.getInt("ORDEM"));
                        }catch (Exception e){
                        }
                        if (! jsonObject.getString("FONE1").toString().equals("null")) {
                            cobranca.setFone1(jsonObject.getString("FONE1"));
                        }else{
                            cobranca.setFone1("");
                        }
                        if (! jsonObject.getString("FONE2").toString().equals("null")) {
                            cobranca.setFone2(jsonObject.getString("FONE2"));
                        }else{
                            cobranca.setFone2("");
                        }
                        try{
                            if (jsonObject.getDouble("VL_PAGO") != 0.0) {
                                cobranca.setVlPago(jsonObject.getDouble("VL_PAGO"));
                                cobranca.setStatus("E");
                                cobranca.setDtPagamento(currentDateandTime.toString());
                            }else{
                                cobranca.setVlPago(0.0);
                                cobranca.setStatus("B");
                            }
                        }catch (Exception e){
                            cobranca.setVlPago(0.0);
                            cobranca.setStatus("B");
                        }

                        try {
                            cobranca.setVlNegociar(jsonObject.getDouble("VL_NEGOCIAR"));
                        }catch (Exception e){
                            cobranca.setVlNegociar(0.0);
                        }


                        cc.setConsulta("*", " where codigo = " + cobranca.getCodigo().toString());
                        try {
                            if (!cc.getConsulta().isEmpty()) {
                                cc.Update(cobranca);
                            }else{
                                cc.Insert(cobranca);
                            }
                        } catch (Exception e) {
                            cc.Insert(cobranca);
                        }

                    }
                    erro = "Sucesso";
                }else{
                    erro = "Nenhum";
                    onCancelled("Nenhum");
                }



            } catch (Exception e){
                erro = "Erro";
                onCancelled("Erro");
                e.printStackTrace();
            }
            return erro;
        }
    }


    public void PauseThread(){
        if (thread != null) {
            System.out.println("JsonGetCobranca stop");
            thread.interrupt();
        }
    }

    public ProgressDialog getDialog() {
        return dialog;
    }

    public void setDialog(ProgressDialog dialog) {
        this.dialog = dialog;
    }

    public StringBuilder stringBuilder(InputStream is) throws IOException {


        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
        StringBuilder sb = new StringBuilder();

        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            sb.append(line + "\n");
        }
        return sb;
    }
}
