package br.com.cfcsystem.Class;

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

import br.com.cfcsystem.Controls.LoginController;
import br.com.cfcsystem.Entity.Login;
import br.com.cfcsystem.cobmobile.DatabaseHandler;

/**
 * Created by user on 29/10/2014.
 */
public class JsonGetUsuario {
    private LoginController lc = null;
    private ProgressDialog dialog = null;
    private HttpGet request = null;
    private Thread thread = null;
    private Context context = null;

    private String erro = "Nenhum";

    public JsonGetUsuario(Context context, String url, ProgressDialog dialog, DatabaseHandler dbHandler){
        this.dialog = dialog;
        this.context = context;

        request = new HttpGet(url);

        lc = new LoginController(dbHandler);

        new GetUsuario().execute();
    }

    private class GetUsuario extends AsyncTask<String, String, String> {

        protected void onPostExecute(String result){
            if (dialog != null){
                dialog.dismiss();
            }
            if(result.equalsIgnoreCase("Erro")){
                try {
                    Toast toast = Toast.makeText(context, "Erro ao Atualizar Usuarios.", Toast.LENGTH_LONG);
                    toast.show();

                }catch (Exception e){
                    System.out.println("erro aqui: "+e.getMessage());
                }
            }else if (result.equalsIgnoreCase("Nenhum")) {
                try {
                    Toast toast = Toast.makeText(context, "Nenhuma Usuario para ser Atualizado.", Toast.LENGTH_LONG);
                    toast.show();

                }catch (Exception e){
                    System.out.println("erro aqui: "+e.getMessage());
                }
            }else{
                try {
                    Toast toast = Toast.makeText(context, "Usuarios Atualizados com Sucesso.", Toast.LENGTH_LONG);
                    toast.show();
                }catch (Exception e){
                    System.out.println("erro aqui: "+e.getMessage());
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            try {


                System.out.println("JsonGetLogin ");

                DefaultHttpClient client = new DefaultHttpClient();
                HttpResponse response = client.execute(request);

                HttpEntity httpEntity = response.getEntity();

                JSONArray jsonArray = new JSONArray(stringBuilder(httpEntity.getContent()).toString());

                if (jsonArray.length() > 0) {
                    //if (true){

                    Login login = new Login();
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        login.setCODIGO(jsonObject.getInt("CODIGO"));
                        login.setUSUARIO(jsonObject.getString("USUARIO").toUpperCase());
                        login.setSENHA(jsonObject.getString("SENHA"));
                        login.setSalva_Usuario("N");

                        lc.setConsulta("*", " where codigo = " + login.getCODIGO().toString());
                        try {
                            if (!lc.getConsulta().isEmpty()) {
                                lc.Update(login);
                            }
                        } catch (Exception e) {
                            lc.Insert(login);
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
            System.out.println("JsonGetLogin stop");
            thread.interrupt();
        }
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
