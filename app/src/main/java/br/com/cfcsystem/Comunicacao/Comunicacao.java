package br.com.cfcsystem.Comunicacao;

import android.app.Activity;

import br.com.cfcsystem.Controls.CobrancaController;
import br.com.cfcsystem.Controls.LoginController;
import br.com.cfcsystem.Entity.Cobranca;
import br.com.cfcsystem.Entity.Login;
import br.com.cfcsystem.cobmobile.DatabaseHandler;

/**
 * Created by user on 18/09/2014.
 */
public class Comunicacao extends Activity {

    private static final int CLIENTE_TIPO = 1;
    private static final int PRODUTO_TIPO = 2;
    private LoginController qryLogin = null;
    private CobrancaController cc = null;
    private DatabaseHandler dbHandler = null;

    public Comunicacao(DatabaseHandler dbHandler){
        this.dbHandler = dbHandler;

        cc =  new CobrancaController(dbHandler);

        qryLogin = new LoginController(dbHandler);

        //CarregaUsuarioNovo();


    }

    public void Atualiza(int tipo){


        if (tipo == CLIENTE_TIPO){
            //AtualizaCliente();
        }



        if (tipo == PRODUTO_TIPO){
            //AtualizaProduto();
        }

    }

    public void AtualizaCobranca(){
       // CobrancaController qry = new CobrancaController(dbHandler);

        try {
            Cobranca cobranca = new Cobranca();
            String[] nome = {"Anderson Carvalho","Leonardo Neto","Andre Rogerio"};
            cc.Delete("todos");
            for (int i = 0; i < 3; i++){
                cobranca.setCodigo(i+1);
                cobranca.setCodCliente(i+1);
                cobranca.setCliente(nome[i]);
                cobranca.setVlAnterior(10.23*(i+1));
                cobranca.setVlPeriodo(5.34*(i+1));
                cobranca.setCidade("DOURADOS");
                cobranca.setUf("MS");
                cc.setConsulta("*", " where codigo = " + cobranca.getCodigo().toString());
                try {
                    if (!cc.getConsulta().isEmpty()) {
                        cc.Update(cobranca);
                    }
                } catch (Exception e) {
                    cc.Insert(cobranca);
                }
            }

        }catch (Exception e){

        }
    }/*

    private void AtualizaProduto(){
        ProdutoController qryProd = new ProdutoController(dbHandler);

        try {
            Produto produto = new Produto();
            for (int i = 1; i <= 3; i++) {
                produto.setCODIGO(i);
                produto.setDESCRICAO("PRODUTO "+String.valueOf(i).toString());
                produto.setVALOR(10.25*i);
                qryProd.Insert(produto);
            }
        }catch (Exception e){

        }
    }*/

    public void CobrancaDia(){

        Cobranca cobranca = new Cobranca();

        try {
            for (int i = 1; i < 4; i++){
                cobranca.setCodigo(i);
                cobranca.setCodCliente(i + 10);
                cobranca.setCliente("CLIENTE " + i);
                cobranca.setVlAnterior(10.20 * i);
                cobranca.setVlPeriodo(25.00 * i);

                cc.setConsulta("*"," where codigo = "+cobranca.getCodigo().toString());
                try{
                    cc.getConsulta().get(0).getCodigo();
                    cc.Update(cobranca);
                }catch (Exception e){
                    cc.Insert(cobranca);
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    public void CarregaUsuarioNovo(){



        Login login = new Login();
        login.setUSUARIO("TESTE");
        login.setSENHA("123456");
        qryLogin.Insert(login);

        login.setUSUARIO("TESTE2");
        login.setSENHA("123456");
        qryLogin.Insert(login);


    }

}
