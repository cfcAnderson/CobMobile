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
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.Locale;

import br.com.cfcsystem.Controls.CobrancaController;
import br.com.cfcsystem.Controls.LoginController;
import br.com.cfcsystem.cobmobile.DatabaseHandler;

/**
 * Created by user on 29/10/2014.
 */
public class JsonCobrancaPaga extends Activity{
    private CobrancaController cc = null;
    private LoginController lc = null;
    private String url;
    private ProgressDialog dialog;
    private Thread thread = null;
    private String erro = "Nenhum";
    private Context context;
    private String totalPago = "";
    private NumberFormat numberFormat;
    private Integer contEnvio, contTotalEnvio;
    private Double total = 0.0;
    private CobrancaPaga cobranca;
    private CobrancaAdapter cobrancaAdapter = null;



    public JsonCobrancaPaga(CobrancaAdapter cobrancaAdapter, Context context, String url,ProgressDialog dialog, DatabaseHandler dbHandler) throws UnsupportedEncodingException {
        this.context = context;
        this.dialog = dialog;
        this.cobrancaAdapter = cobrancaAdapter;

        total = 0.0;

        contEnvio = 5;

        this.url = url;

        cc = new CobrancaController(dbHandler);
        lc = new LoginController(dbHandler);

        numberFormat = NumberFormat.getCurrencyInstance(new Locale("br", "BR"));

        cobranca = new CobrancaPaga();
        cobranca.execute();

    }


    private class CobrancaPaga extends AsyncTask<String, DatabaseHandler, String>{

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
                    Toast toast = Toast.makeText(context, "Erro ao Enviar Cobranca.", Toast.LENGTH_LONG);
                    toast.show();

                } else if (result.equalsIgnoreCase("Nenhum")) {
                    Toast toast = Toast.makeText(context, "Nenhuma Cobranca para ser Enviada", Toast.LENGTH_LONG);
                    toast.show();

                } else {
                    Toast toast = Toast.makeText(context, "Cobranca Enviada com Sucesso. Total Enviado: " + totalPago, Toast.LENGTH_LONG);
                    toast.show();
                }
            }catch (Exception e){
                System.out.println("erro aqui: " + e.getMessage());
            }
        }


        @Override
        protected String doInBackground(String... params){
            System.out.println("JsonCobrancaPaga ");

            try {
                cc.setConsulta("*", " where status = 'A' ");

                lc.setConsulta("*", "");

                contTotalEnvio = cc.contCobranca();
            }catch (Exception e){
                e.printStackTrace();

                erro = "Erro";
                onCancelled("Erro");

                return "Erro";
            }
            //cc.setConsulta("*", " where VL_PAGO <> 0 and status = 'A' ");
            //cc.setConsulta("*", " where VL_PAGO <> 0 ");
            JSONArray jsonArray = new JSONArray();
            String listaCodigo = "";

            erro = "Nenhum";
            for (int j = 0; j < contTotalEnvio; j++) {
                try {
                    listaCodigo = "";
                    if (contTotalEnvio - j < 5) {
                        contEnvio = contTotalEnvio;
                        erro = "Sucesso";
                    } else {
                        contEnvio = j + 5;
                    }
                    for (int i = j; i < contEnvio; i++) {
                        JSONObject jsonObject = new JSONObject();

                        jsonObject.put("CODIGO", cc.getConsulta().get(i).getCodigo());
                        jsonObject.put("CLIENTE", cc.getConsulta().get(i).getCliente());
                        jsonObject.put("COD_CLIENTE", cc.getConsulta().get(i).getCodCliente());
                        listaCodigo = listaCodigo + cc.getConsulta().get(i).getCodigo().toString() + ",";
                        jsonObject.put("COBRADOR", lc.getConsulta().get(0).getCODIGO());
                        jsonObject.put("VL_PAGO", cc.getConsulta().get(i).getVlPago());
                        total = total + cc.getConsulta().get(i).getVlPago().doubleValue();
                        jsonObject.put("DT_PAGAMENTO", cc.getConsulta().get(i).getDtPagamento());
                        jsonObject.put("LATITUDE", cc.getConsulta().get(i).getLatitude());
                        jsonObject.put("LONGITUDE", cc.getConsulta().get(i).getLongitude());
                        jsonObject.put("VL_PERIODO", cc.getConsulta().get(i).getVlPeriodo());
                        jsonObject.put("VL_ANTERIOR", cc.getConsulta().get(i).getVlAnterior());
                        jsonObject.put("CIDADE", cc.getConsulta().get(i).getCidade());
                        jsonObject.put("UF", cc.getConsulta().get(i).getUf());
                        jsonObject.put("DT_PROXIMO", cc.getConsulta().get(i).getDtProxima());
                        jsonObject.put("VL_NEGOCIAR", cc.getConsulta().get(i).getVlNegociar());
                        jsonArray.put(jsonObject);
                    }
                    j = contEnvio - 1;
                    listaCodigo = listaCodigo.substring(0, listaCodigo.length() - 1);
                    totalPago = numberFormat.format(total);
                } catch (Exception e) {
                    erro = "Erro";
                    System.out.println(e.getMessage());
                }

                if (jsonArray.length() > 0) {
                    try {
                        HttpParams httpParams = new BasicHttpParams();
                        HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
                        HttpConnectionParams.setSoTimeout(httpParams, 10000);
                        HttpClient httpclient = new DefaultHttpClient(httpParams);
                        String json = jsonArray.toString();


                        String link = jsonArray.toString();
                        String linkFinal = URLEncoder.encode(link, "UTF-8");
                        HttpPost httpPost = new HttpPost(url + linkFinal);
                        HttpResponse response = httpclient.execute(httpPost);
                        String temp = "";

                         temp = EntityUtils.toString(response.getEntity());
                         System.out.println("temp " + temp);

                        if (!temp.equals("")) {
                            erro = "Erro";
                            onCancelled("Erro");

                            return "Erro";
                        } else {
                            onCancelled(erro);
                            cc.UpdateJson(listaCodigo, "E");
                            //return erro;
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        erro = "Erro";
                        onCancelled("Erro");

                        return "Erro";
                    }
                } else {
                    erro = "Sucesso";
                    onCancelled("Sucesso");

                    return "Sucesso";
                }
            }
            return erro;

        }
    }



    public void PauseThread(){
        if (thread != null) {
            System.out.println("JsonSetNovoCliente stop");
            thread.interrupt();
        }
    }


}
