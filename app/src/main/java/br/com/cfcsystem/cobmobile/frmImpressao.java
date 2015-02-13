package br.com.cfcsystem.cobmobile;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import br.com.cfcsystem.Class.JsonThread;


public class frmImpressao extends Activity {


    private NumberFormat numberFormat = null;

    // will show the statuses
    private TextView lblStatus;
    private TextView txtCodCliente;
    private TextView txtCliente;
    private TextView txtCodCobranca;
    private TextView txtValorPago;
    private TextView txtCidade;
    private TextView txtCobrador;
    private TextView txtSaldoDevedor;
    private String   txtVlNegociar;

    private JsonThread jsonThread;
    private Boolean ativaThread;

    private String saldo;


    // will enable user to enter any text to be printed
    private String myTextbox;


    // android built in classes for bluetooth operations
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;

    private OutputStream mmOutputStream;
    private InputStream mmInputStream;
    private Thread workerThread;

    private byte[] readBuffer;
    private int readBufferPosition;
    private int counter;
    private volatile boolean stopWorker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_impressao);

        lblStatus = (TextView) findViewById(R.id.lblStatus);
        txtCodCliente = (TextView) findViewById(R.id.txtCodCliente);
        txtCliente = (TextView) findViewById(R.id.txtCliente);
        txtCodCobranca = (TextView) findViewById(R.id.txtCodCobranca);
        txtValorPago = (TextView) findViewById(R.id.txtValorPago);
        txtCidade = (TextView) findViewById(R.id.txtCidade);
        txtCobrador = (TextView) findViewById(R.id.txtCobrador);
        txtSaldoDevedor = (TextView) findViewById(R.id.txtSaldoDevedor);

        numberFormat = NumberFormat.getCurrencyInstance(new Locale("br", "BR"));
        DatabaseHandler dbHandler = new DatabaseHandler(this);
        jsonThread = new JsonThread(dbHandler);


        Intent intent = getIntent();
        Bundle params = intent.getExtras();
        if(params!=null) {
            txtCodCobranca.setText("N. "+params.getString("id").toString());
            txtCodCliente.setText(params.getString("codCliente"));
            txtCliente.setText(params.getString("cliente"));
            txtValorPago.setText(numberFormat.format(new Double(params.getString("valorPago"))).toString());
            txtCidade.setText(params.getString("cidade"));
            saldo = numberFormat.format((new Double(params.getString("valorAnterior"))+
                    new Double(params.getString("valorPeriodo")) - new Double(params.getString("valorPago")))).toString();
            txtCobrador.setText(params.getString("cobrador"));
            txtSaldoDevedor.setText(saldo);
            txtVlNegociar = numberFormat.format(new Double(params.getString("vlNegociar"))).toString();
        }

        try {
            findBT();
            openBT();
        } catch (IOException ex) {
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.frm_impressao, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //createWebPrintJob(webview);
            try {
                sendData();
            } catch (IOException ex) {
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // This will find a bluetooth printer device
    void findBT() {

        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                lblStatus.setText("Impressora bluetooth não disponivel");
            }

            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetooth = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
                    .getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {

                    // MP300 is the name of the bluetooth printer device
                    if ((device.getName().equals("PTP-II")) || (device.getName().equals("MPT-II"))) {
                        mmDevice = device;
                        break;
                    }
                }
            }
            lblStatus.setText("Impressora bluetooth funcionando");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Tries to open a connection to the bluetooth printer device
    void openBT() throws IOException {
        try {
            // Standard SerialPortService ID
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();

            beginListenForData();

            lblStatus.setText("Impressora conectada");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // After opening a connection to bluetooth printer device,
    // we have to listen and check if a data were sent to be printed.
    void beginListenForData() {
        try {
            final Handler handler = new Handler();

            // This is the ASCII code for a newline character
            final byte delimiter = 10;

            stopWorker = false;
            readBufferPosition = 0;
            readBuffer = new byte[1024];

            workerThread = new Thread(new Runnable() {
                public void run() {
                    while (!Thread.currentThread().isInterrupted()
                            && !stopWorker) {

                        try {

                            int bytesAvailable = mmInputStream.available();
                            if (bytesAvailable > 0) {
                                byte[] packetBytes = new byte[bytesAvailable];
                                mmInputStream.read(packetBytes);
                                for (int i = 0; i < bytesAvailable; i++) {
                                    byte b = packetBytes[i];
                                    if (b == delimiter) {
                                        byte[] encodedBytes = new byte[readBufferPosition];
                                        System.arraycopy(readBuffer, 0,
                                                encodedBytes, 0,
                                                encodedBytes.length);
                                        final String data = new String(
                                                encodedBytes, "ASCII");
                                        readBufferPosition = 0;

                                        handler.post(new Runnable() {
                                            public void run() {
                                                lblStatus.setText(data);
                                            }
                                        });
                                    } else {
                                        readBuffer[readBufferPosition++] = b;
                                    }
                                }
                            }

                        } catch (IOException ex) {
                            stopWorker = true;
                        }

                    }
                }
            });

            workerThread.start();
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * This will send data to be printed by the bluetooth printer
     */
    void sendData() throws IOException {
        try {

            // the text typed by the user
            String viaCliente = "via cliente                     ";
            String idCobranca = txtCodCobranca.getText().toString();
            String vlrRecebido = "Valor recebido                  ";
            String valor = "R$ "+ txtValorPago.getText().toString().substring(3);
            String vlrSaldoDevedor = "Saldo devedor                   ";
            String saldoDevedor = "R$ "+ saldo.substring(3);
            String cidade = "                                ";
            String cobradorBranco = "                                ";
            Integer colunaCobrador = (32-txtCobrador.getText().length())/2;
            Integer coluna = (32-txtCidade.getText().length())/2;
            String comprovante = "COMPROVANTE                     ";
            String vlrNegociar = "Valor Negociar                  ";
            String negociar = "R$ "+ txtVlNegociar.substring(3);
                    //32 colunas
            String msg = "          R E C I B O          "+"\n"+
                         viaCliente.substring(0,viaCliente.length() - idCobranca.length())+ idCobranca+"\n"+
                         //"via cliente           N.  89998"+"\n"+
                         "--------------------------------"+"\n"+
                         "Cliente: "+txtCodCliente.getText().toString()+"\n"+
                         txtCliente.getText().toString()+"\n"+
                         "--------------------------------"+"\n"+
                         vlrRecebido.substring(0,vlrRecebido.length() - valor.length()-1)+valor+"\n"+
                         //"valor recebido        R$ 100,00"+"\n"+
                         vlrSaldoDevedor.substring(0,vlrSaldoDevedor.length() - saldoDevedor.length()-1)+saldoDevedor+"\n"+
                         "--------------------------------"+"\n\n"+
                         cidade.substring(0,coluna)+txtCidade.getText().toString()+"\n\n\n"+
                         //"    Dourados/MS, 28/10/2014    "+"\n\n\n"+
                         "  ---------------------------  "+"\n"+
                         cobradorBranco.substring(0,colunaCobrador)+txtCobrador.getText()+"\n\n\n"+
                         //"          cobrador             "+"\n\n\n"+
                         "- - - - - - - - - - - - - - - -"+"\n\n\n"+
                         comprovante.substring(0,viaCliente.length() - idCobranca.length())+ idCobranca+"\n"+
                         //"COMPROVANTE           N.  89998"+"\n"+
                         "--------------------------------"+"\n"+
                         "Cliente: "+txtCodCliente.getText().toString()+"\n"+
                         txtCliente.getText().toString()+"\n"+
                         "--------------------------------"+"\n"+
                         vlrRecebido.substring(0,vlrRecebido.length() - valor.length()-1)+valor+"\n"+
                         vlrNegociar.substring(0,vlrNegociar.length() - negociar.length()-1)+negociar+"\n"+
                         //"valor recebido        R$ 100,00"+"\n"+
                         //"valor negociar        R$ 100,00"+"\n"+
                         "--------------------------------"+"\n\n"+
                         cidade.substring(0,coluna)+txtCidade.getText().toString()+"\n\n\n";


            mmOutputStream.write(msg.getBytes("ASCII"));

            // tell the user data were sent
            lblStatus.setText("Impresso");

        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Close the connection to bluetooth printer.
    void closeBT() throws IOException {
        try {
            stopWorker = true;
            mmOutputStream.close();
            mmInputStream.close();
            mmSocket.close();
            lblStatus.setText("Bluetooth Closed");
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void onStop(){
        super.onStop();
        jsonThread.StartaThread(ativaThread);
        try {
            closeBT();
        } catch (IOException ex) {
        }
        System.out.println("listaVenda stop");
    }

    public void onBackPressed(){
        super.onBackPressed();
        ativaThread = false;
        try {
            closeBT();
        } catch (IOException ex) {
        }
        System.out.println("listaVenda backpressed");
    }

    protected void onResume() {
        super.onResume();
        if (ativaThread != null) {
            jsonThread.PauseThread();
        }
        ativaThread = true;
        System.out.println("listaVenda resume");
    }



}
