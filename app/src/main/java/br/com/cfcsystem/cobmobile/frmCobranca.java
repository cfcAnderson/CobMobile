package br.com.cfcsystem.cobmobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import br.com.cfcsystem.Class.CobrancaAdapter;
import br.com.cfcsystem.Class.CobrancaThread;
import br.com.cfcsystem.Class.GPSTracker;
import br.com.cfcsystem.Class.JsonCobrancaPaga;
import br.com.cfcsystem.Class.JsonGetCobranca;
import br.com.cfcsystem.Class.JsonThread;
import br.com.cfcsystem.Class.Parametros;
import br.com.cfcsystem.Controls.CobrancaController;
import br.com.cfcsystem.Controls.LoginController;
import br.com.cfcsystem.Entity.Cobranca;


public class frmCobranca extends Activity {
    private CobrancaAdapter cobrancaAdapter = null;
    private CobrancaController cc = null;
    private LoginController lc = null;
    private DatabaseHandler dbHandler = null;
    //private DatabaseHandler dbHandlerTeste = null;
    private ListView lstCobranca = null;
    private EditText edtVlPago = null;
    private EditText edtVlNegociar = null;
    private Cobranca cobranca;
    private String cobrador;
    private JsonThread jsonThread;
    private Boolean ativaThread;
    private SimpleDateFormat dataFormato;
    private SearchView edtBuscaCobranca;
    private ArrayList<Cobranca> listaCobranca = null;
    private CobrancaThread cobrancaThread = null;
    private ProgressDialog dialogBuscaCobranca = null;

    private Parametros parametros;

    private Boolean cancela;
    private Boolean finalizaThread;

    private Thread atualizaLista = null;
    private JsonGetCobranca jsonGetCobranca = null;


    // GPSTracker class
    GPSTracker gps;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_combranca);

        final SimpleDateFormat dataFormato = new SimpleDateFormat("dd/MM/yyyy");

        gps = new GPSTracker(frmCobranca.this);


        lstCobranca = (ListView) findViewById(R.id.lstCobranca);


        dbHandler = new DatabaseHandler(this);
        cc = new CobrancaController(dbHandler);

        lc = new LoginController(dbHandler);
        lc.setConsulta("*","");

        jsonThread = new JsonThread(dbHandler);

        cc.setConsulta("*","");

        parametros = new Parametros();

        cobrancaAdapter = new CobrancaAdapter(frmCobranca.this,cc);



        //new LoadViewTask().execute();

        //dialog = ProgressDialog.show(frmCobranca.this, "Aguarde", "Carregando..., Por Favor Aguarde...");



        lstCobranca.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
                cc.setConsulta("*","");

                if ((cc.getConsulta().get(position).getVlPago() <= 0.0) && (! cc.getConsulta().get(position).getStatus().equals("E"))) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            frmCobranca.this);

                    View viewPagamento = View.inflate(frmCobranca.this, R.layout.layout_pagamento, null);

                    edtVlPago = (EditText) viewPagamento.findViewById(R.id.edtVlPago);
                    edtVlNegociar = (EditText) viewPagamento.findViewById(R.id.edtVlNegociar);

                    alertDialogBuilder.setView(viewPagamento);
                    alertDialogBuilder.setTitle("Informe o valor pago.");

                    alertDialogBuilder
                            //.setMessage(cc.getConsulta().get(position).getNome().toString())
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // activity atual


                                    if ((edtVlPago.getText().toString().equals("0")) || (edtVlPago.getText().toString().equals(""))) {
                                        Toast.makeText(frmCobranca.this, "Informe um valor pago.",
                                                Toast.LENGTH_LONG).show();
                                        dialog.cancel();
                                    } else if (Double.parseDouble(edtVlPago.getText().toString()) >
                                               cc.getConsulta().get(position).getVlAnterior()+cc.getConsulta().get(position).getVlPeriodo()){
                                        Toast.makeText(frmCobranca.this, "Valor informado maior que o total devido.",
                                                Toast.LENGTH_LONG).show();
                                        dialog.cancel();
                                    } else {
                                        try {

                                            cobranca = new Cobranca();
                                            cobranca = cc.getConsulta().get(position);
                                            cobranca.setVlPago(Double.parseDouble(edtVlPago.getText().toString()));
                                            try {
                                                cobranca.setVlNegociar(Double.parseDouble(edtVlNegociar.getText().toString()));
                                            }catch (Exception e){

                                            }
                                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                            String currentDateandTime = sdf.format(new Date());
                                            cobranca.setDtPagamento(currentDateandTime);

                                            if ((! edtVlNegociar.getText().toString().equals("0")) && (! edtVlNegociar.getText().toString().equals(""))) {
                                                cobranca.setVlNegociar(Double.parseDouble(edtVlNegociar.getText().toString()));
                                            }

                                            cobranca.setStatus("A");

                                            gps.getLocation();

                                            // Check if GPS enabled
                                            if (gps.canGetLocation()) {

                                                cobranca.setLongitude(gps.getLongitude());
                                                cobranca.setLatitude(gps.getLatitude());


                                                double latitude = gps.getLatitude();
                                                double longitude = gps.getLongitude();

                                                cc.Update(cobranca);

                                                cc.setConsulta("*", "");
                                                cobrancaAdapter.carregaLista(cc);
                                                cobrancaAdapter.notifyDataSetChanged();

                                                Intent print = new Intent(getBaseContext(), frmImpressao.class);
                                                print.putExtra("id", cobranca.getCodigo().toString());
                                                print.putExtra("valorAnterior", cobranca.getVlAnterior().toString());
                                                print.putExtra("valorPeriodo", cobranca.getVlPeriodo().toString());
                                                print.putExtra("valorPago", cobranca.getVlPago().toString());
                                                print.putExtra("cidade", cobranca.getCidade().toString() + "/" + cobranca.getUf().toString()+" "+
                                                               cc.getConsulta().get(position).getDtPagamento().toString().substring(0,10).toString());
                                                print.putExtra("cliente", cobranca.getCliente().toString());
                                                print.putExtra("codCliente", cobranca.getCodCliente().toString());
                                                print.putExtra("cobrador", lc.getConsulta().get(0).getUSUARIO().toString() );
                                                print.putExtra("vlNegociar", cobranca.getVlNegociar().toString());
                                                ativaThread = false;
                                                startActivity(print);


                                                // \n is for new line
                                                //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
                                            } else {
                                                // Can't get location.
                                                // GPS or network is not enabled.
                                                // Ask user to enable GPS/network in settings.

                                                gps.showSettingsAlert();
                                            }


                                        /*
                                        //}*/


                                        } catch (Exception e) {
                                            Toast.makeText(frmCobranca.this, "Informe um valor pago.",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }


                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // se não for precionado ele apenas termina o dialog
                                    // e fecha a janelinha

                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertDialog = alertDialogBuilder.create();

                    alertDialog.show();
                }
            }
        });


        edtBuscaCobranca = (SearchView) findViewById(R.id.edtBuscaCobranca);

        edtBuscaCobranca.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                cc.setConsulta("*"," where cliente like '%" + newText + "%'");
                cobrancaAdapter.carregaLista(cc);
                cobrancaAdapter.notifyDataSetChanged();
                return false;
            }
        });


        lstCobranca.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                cc.setConsulta("*","");
                if ((cc.getConsulta().get(position).getDtProxima() != null) && (cc.getConsulta().get(position).getVlPago() > 0.0)){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            frmCobranca.this);

                    alertDialogBuilder.setTitle("Impressão")
                            .setMessage("Imprimir outra via?");

                    alertDialogBuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent print = new Intent(getBaseContext(), frmImpressao.class);

                            print.putExtra("id", cc.getConsulta().get(position).getCodigo().toString());
                            print.putExtra("codCliente", cc.getConsulta().get(position).getCodCliente().toString());
                            print.putExtra("cliente", cc.getConsulta().get(position).getCliente().toString());
                            print.putExtra("valorAnterior", cc.getConsulta().get(position).getVlAnterior().toString());
                            print.putExtra("valorPeriodo", cc.getConsulta().get(position).getVlPeriodo().toString());
                            print.putExtra("valorPago", cc.getConsulta().get(position).getVlPago().toString());
                            print.putExtra("cidade", cc.getConsulta().get(position).getCidade().toString()+"/"+
                                    cc.getConsulta().get(position).getUf().toString()+" "+
                                    cc.getConsulta().get(position).getDtPagamento().toString().substring(0,10).toString());

                            print.putExtra("cobrador", lc.getConsulta().get(0).getUSUARIO().toString() );
                            print.putExtra("vlNegociar",cc.getConsulta().get(position).getVlNegociar().toString());
                            ativaThread = false;
                            startActivity(print);
                        }
                    });
                    alertDialogBuilder.setNegativeButton("Não", null);
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }else if (cc.getConsulta().get(position).getVlPago() > 0.0) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            frmCobranca.this);

                    alertDialogBuilder.setTitle("Atenção")
                            .setMessage("Escolha uma das opções?");

                    alertDialogBuilder.setPositiveButton("Imprimir?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent print = new Intent(getBaseContext(), frmImpressao.class);

                            print.putExtra("id", cc.getConsulta().get(position).getCodigo().toString());
                            print.putExtra("codCliente", cc.getConsulta().get(position).getCodCliente().toString());
                            print.putExtra("cliente", cc.getConsulta().get(position).getCliente().toString());
                            print.putExtra("valorAnterior", cc.getConsulta().get(position).getVlAnterior().toString());
                            print.putExtra("valorPeriodo", cc.getConsulta().get(position).getVlPeriodo().toString());
                            print.putExtra("valorPago", cc.getConsulta().get(position).getVlPago().toString());
                            print.putExtra("cidade", cc.getConsulta().get(position).getCidade().toString()+"/"+
                                    cc.getConsulta().get(position).getUf().toString()+" "+
                                    cc.getConsulta().get(position).getDtPagamento().toString().substring(0,10).toString());

                            print.putExtra("cobrador", lc.getConsulta().get(0).getUSUARIO().toString() );
                            print.putExtra("vlNegociar", cc.getConsulta().get(position).getVlNegociar().toString());
                            ativaThread = false;
                            startActivity(print);
                        }
                    });
                    alertDialogBuilder.setNeutralButton("Proxima Data?", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            CarregaData(position);
                        }
                    });
                    alertDialogBuilder.setNegativeButton("Cancelar", null);
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();


                }else if ((cc.getConsulta().get(position).getDtProxima() == null) && (! cc.getConsulta().get(position).getStatus().equals("E"))){
                    CarregaData(position);
                }
                return true;
            }
        });

        lstCobranca.setScrollingCacheEnabled(false);

        lstCobranca.setAdapter(cobrancaAdapter);


    }


    private void CarregaData(final Integer position){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        final Calendar calendar = Calendar.getInstance(sdf.getTimeZone());
        calendar.setTime(new Date());

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        calendar.add(Calendar.DAY_OF_MONTH,1);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        setCancela(false);


        final DatePickerDialog data = new DatePickerDialog(frmCobranca.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {



                Calendar c = Calendar.getInstance();
                c.setTimeZone(TimeZone.getTimeZone("br"));
                c.set(year, monthOfYear, dayOfMonth);

                if (! getCancela()){
                    //Toast.makeText(frmCobranca.this, calendar.getTime().toString(), Toast.LENGTH_LONG).show();
                    if (c.getTime().compareTo(new Date()) < 0){
                        Toast.makeText(frmCobranca.this, "Data informada menor ou igual a data do dia.", Toast.LENGTH_LONG).show();
                    }else {
                        cobranca = new Cobranca();
                        cobranca = cc.getConsulta().get(position);

                        if (cobranca.getVlPago() == 0.0) {
                            cobranca.setVlPago(null);
                            cobranca.setDtPagamento(null);
                        }
                        cobranca.setDtProxima(c.getTime().toString());
                        cobranca.setStatus("A");

                        cobranca.setLongitude(gps.getLongitude());
                        cobranca.setLatitude(gps.getLatitude());

                        cc.Update(cobranca);

                        cc.setConsulta("*", "");
                        cobrancaAdapter.carregaLista(cc);
                        cobrancaAdapter.notifyDataSetChanged();

                        ChamaEnvioCobranca();
                    }
                    setCancela(true);


                }

            }


        }, year, month, day);

        data.getDatePicker().setMinDate(calendar.getTimeInMillis());

        data.setCancelable(true);
        data.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setCancela(true);
                data.dismiss();
            }
        });



        data.show();
    }


    private Boolean getCancela() {
        return cancela;
    }

    private void setCancela(Boolean cancela) {
        this.cancela = cancela;
    }

    private void Legenda(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                frmCobranca.this);

        View legenda = View.inflate(this, R.layout.layout_legenda, null);

        alertDialogBuilder.setView(legenda);
        alertDialogBuilder.setTitle("Legendas");

        alertDialogBuilder
                .setCancelable(false)
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // se não for precionado ele apenas termina o dialog
                        // e fecha a janelinha

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    private void ChamaBuscaCobranca(){

        if (dbHandler == null) {
            //dbHandlerTeste = new DatabaseHandler(this);
            dbHandler = new DatabaseHandler(this);
        }

        dialogBuscaCobranca = ProgressDialog.show(frmCobranca.this, "Aguarde", "Atualizando Cobranca, Por Favor Aguarde...");
        jsonGetCobranca = new JsonGetCobranca(cobrancaAdapter,null, parametros.getUrlGetCobranca()+lc.getConsulta().get(0).getCODIGO().toString(),dialogBuscaCobranca,dbHandler);
    }

    private void ChamaEnvioCobranca(){
        try {
            if (dbHandler == null) {
                //dbHandlerTeste = new DatabaseHandler(this);
                dbHandler = new DatabaseHandler(this);
            }

            new JsonCobrancaPaga(cobrancaAdapter,null ,parametros.getUrlCobrancaPaga(),null,dbHandler);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onResume() {
        super.onResume();

        ChamaEnvioCobranca();

        System.out.println("frmCobranca resume");
    }

    protected void onStop(){
        super.onStop();
        System.out.println("listaVenda stop");
    }

    public void onBackPressed(){
        super.onBackPressed();
        System.out.println("listaVenda backpressed");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.frm_combranca, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            cc.setConsulta("*","");
            cobrancaAdapter.carregaLista(cc);
            cobrancaAdapter.notifyDataSetChanged();
            return true;
        } else if (id == R.id.enviarCobranca){
            if (verificaConexao()) {
                ChamaEnvioCobranca();
            }else{
                Toast.makeText(frmCobranca.this, "Sem Conexao para Atualizar Cobranca", Toast.LENGTH_LONG).show();
            }
            return true;
        } else if (id == R.id.legenda){
            Legenda();
            return  true;
        }
        else if (id == R.id.buscaCobranca){
            if (verificaConexao()) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        frmCobranca.this);

                alertDialogBuilder.setTitle("Alerta")
                        .setMessage("Essa busca apagara todas as Cobrancas não enviadas, deseja continuar assim mesmo?");

                alertDialogBuilder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ChamaBuscaCobranca();
                    }
                });
                alertDialogBuilder.setNegativeButton("Não", null);
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }else{
                Toast.makeText(frmCobranca.this, "Sem Conexao para Atualizar Cobranca", Toast.LENGTH_LONG).show();
            }

            return  true;
        }
        return super.onOptionsItemSelected(item);
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
