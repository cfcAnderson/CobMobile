package br.com.cfcsystem.cobmobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import br.com.cfcsystem.Class.JsonCobrancaPaga;
import br.com.cfcsystem.Class.JsonGetCobranca;
import br.com.cfcsystem.Class.JsonGetUsuario;
import br.com.cfcsystem.Class.JsonSetCobranca;
import br.com.cfcsystem.Class.JsonThread;
import br.com.cfcsystem.Class.Parametros;
import br.com.cfcsystem.Controls.LoginController;


public class frmSettings extends Activity{

    private Button btnAtualiza, btnBaixaCobranca;
    private LoginController lc = null;
    private CheckBox ckbCliente, ckbProduto;
    private DatabaseHandler dbHandler = null;
    private TextView txtNovoUsuario, txtBanco;
    private InputStream is = null;
    private ProgressDialog dialog;
    //private LocationListener locationListener = null;
    private Parametros parametros;

    private JsonThread jsonThread;
    private Boolean ativaThread;

    private JsonCobrancaPaga jsonCobrancaPaga = null;
    private JsonSetCobranca jsonSetCobranca = null;
    private JsonGetCobranca jsonGetCobranca = null;
    private Context context =  null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_settings);

        dbHandler =new DatabaseHandler(this);

        lc = new LoginController(dbHandler);
        lc.setConsulta("*","");

        jsonThread = new JsonThread(dbHandler);

        parametros = new Parametros();

        btnAtualiza = (Button) findViewById(R.id.btnAtualizar);
        btnBaixaCobranca = (Button) findViewById(R.id.btnBaixaCobranca);
        ckbCliente = (CheckBox) findViewById(R.id.ckbClientes);
        ckbProduto = (CheckBox) findViewById(R.id.ckbProdutos);
        txtNovoUsuario = (TextView) findViewById(R.id.txtUsuarioNovo);
        txtBanco = (TextView) findViewById(R.id.txtBanco);

        txtBanco.setText(parametros.getNomeBanco().substring(0,parametros.getNomeBanco().length()-1));

        context = getApplicationContext();


        btnBaixaCobranca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao()) {
                        setDialog(ProgressDialog.show(frmSettings.this, "Aguarde", "Atualizando Cobranca, Por Favor Aguarde..."));

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                                frmSettings.this);

                        alertDialogBuilder.setTitle("Alerta")
                                .setMessage("Essa busca apagara todas as Cobrancas não enviadas, deseja continuar assim mesmo?");

                        alertDialogBuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                try {

                                    jsonGetCobranca = new JsonGetCobranca(null,context, parametros.getUrlGetCobranca()+lc.getConsulta().get(0).getCODIGO().toString(), getDialog(), dbHandler);
                                    //jsonGetCobranca.JsonThread();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        alertDialogBuilder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                destroiDialog();
                            }
                        });
                        alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                destroiDialog();
                            }
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();


                }else{
                    Toast.makeText(frmSettings.this, "Sem Conexao para Atualizar Cobranca", Toast.LENGTH_LONG).show();
                }
            }
        });


        txtNovoUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verificaConexao()) {
                    dialog = ProgressDialog.show(frmSettings.this, "Aguarde", "Atualizando usuario, Por Favor Aguarde...");
                    try {
                        JsonGetUsuario jsonGetUsuario = new JsonGetUsuario(getApplicationContext(),parametros.getUrlGetUsuario(), dialog, dbHandler);
                        //jsonGetUsuario.JsonThread();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast.makeText(frmSettings.this, "Sem conexao para atualizar o usuario", Toast.LENGTH_LONG).show();
                }

            }
        });

        btnAtualiza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (verificaConexao()) {
                    dialog = ProgressDialog.show(frmSettings.this, "Aguarde", "Enviando Cobranca, Por Favor Aguarde...");
                    //Comunicacao comunicacao = new Comunicacao(dbHandler);
                    //comunicacao.AtualizaCobranca();

                    try {
                        jsonCobrancaPaga = new JsonCobrancaPaga(null,getApplicationContext(),parametros.getUrlCobrancaPaga(), dialog, dbHandler);
                        //jsonCobrancaPaga.JsonThread();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }else{
                    Toast.makeText(frmSettings.this, "Sem conexao para enviar a cobranca", Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    public ProgressDialog getDialog() {
        return dialog;
    }

    public void setDialog(ProgressDialog dialog) {
        this.dialog = dialog;
    }

    public void destroiDialog(){
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.frm_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onResume() {
        super.onResume();
        if (ativaThread != null) {
            jsonThread.PauseThread();
        }
        ativaThread = true;
        System.out.println("listaVenda resume");
    }

    protected void onStop(){
        super.onStop();
        jsonThread.StartaThread(ativaThread);
        System.out.println("listaVenda stop");
    }

    public void onBackPressed(){
        super.onBackPressed();
        ativaThread = false;
        System.out.println("listaVenda backpressed");
       // destroiDialog();
    }

    public  boolean verificaConexao() {
        boolean conectado;
        ConnectivityManager conectivtyManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }


}
