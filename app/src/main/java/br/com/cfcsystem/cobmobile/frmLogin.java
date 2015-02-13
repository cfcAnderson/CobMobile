package br.com.cfcsystem.cobmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import br.com.cfcsystem.Class.JsonThread;
import br.com.cfcsystem.Controls.LoginController;
import br.com.cfcsystem.Entity.Login;


public class frmLogin extends Activity {

    private EditText _codigo, _usuario, _senha;
    private Button btnLogar, btnSettings;
    private DatabaseHandler dbHandler = null;
    private CheckBox ckbSalvaUsuario = null;
    private LoginController qry = null;
    private JsonThread jsonThread;
    private Boolean ativaThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_login);
        //ActionBar bar = getActionBar();
        //bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));

        System.out.println("onCreate");

        dbHandler = new DatabaseHandler(this);

        jsonThread = new JsonThread(dbHandler);

        btnLogar = (Button) findViewById(R.id.btnOk);
        btnSettings = (Button) findViewById(R.id.btnSettings);
        ckbSalvaUsuario = (CheckBox) findViewById(R.id.ckbSalvaUsuario);


        _usuario = (EditText) findViewById(R.id.edtUsuario);
        _senha = (EditText) findViewById(R.id.edtSenha);

        qry = new LoginController(dbHandler);

        setaUsuarioSalvo();

        //_usuario.setInputType(0x00001001);

        _usuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable et) {
                String s = et.toString();
                if (!s.equals(s.toUpperCase())){
                    s = s.toUpperCase();
                    _usuario.setText(s);
                }
                _usuario.setSelection(_usuario.getText().length());
            }
        });

        btnLogar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    Integer codCobrador = qry.validaUsuario(_usuario.getText().toString(),_senha.getText().toString());

                    if (codCobrador > 0){
                        Intent intent = new Intent(getBaseContext(),frmCobranca.class);
                        intent.putExtra("cobrador",codCobrador);
                        ativaThread = false;
                        startActivity(intent);

                        qry.NotDelete(codCobrador.toString());

                        if (ckbSalvaUsuario.isChecked()){
                            Login login = new Login();
                            login.setCODIGO(codCobrador);
                            login.setSENHA(_senha.getText().toString());
                            login.setUSUARIO(_usuario.getText().toString());
                            login.setSalva_Usuario("S");
                            qry.Update(login);
                        }else{
                            Login login = new Login();
                            login.setCODIGO(codCobrador);
                            login.setSENHA(_senha.getText().toString());
                            login.setUSUARIO(_usuario.getText().toString());
                            login.setSalva_Usuario("N");
                            qry.Update(login);
                        }

                        iniciaVariaveis();
                    }else{
                        Toast.makeText(frmLogin.this,"Usuario nao encontrado", Toast.LENGTH_LONG).show();
                    }
            }
        });

        btnSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ativaThread = false;
                startActivity(new Intent(getBaseContext(),  frmSettings.class));
            }
        });

    }

    protected void onResume() {
        super.onResume();
        if (ativaThread != null) {
            jsonThread.PauseThread();
        }
        ativaThread = true;
        setaUsuarioSalvo();
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
    }

    private void setaUsuarioSalvo(){
        ckbSalvaUsuario.setChecked(false);
        if (qry.setConsulta("*"," where salva_usuario = 'S'")){
            if (! qry.getConsulta().get(0).getSalva_Usuario().isEmpty()){
                ckbSalvaUsuario.setChecked(true);
                _usuario.setText(qry.getConsulta().get(0).getUSUARIO().toString().toUpperCase());
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.frm_login, menu);
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

    private void iniciaVariaveis(){
        _senha.setText("");
        _usuario.setText("");
    }
}
