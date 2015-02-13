package br.com.cfcsystem.Class;

/**
 * Created by user on 20/11/2014.
 */
public class Parametros {

    private String nomeBanco = "cobrancawebteste/";
    private String urlPadrao = "http://94.249.188.151/"+nomeBanco;

    private String urlCobrancaPaga;
    private String urlGetCobranca;
    private String urlGetUsuario;
    private String urlSetCobranca;


    public Parametros(){

        this.urlCobrancaPaga = urlPadrao+"consulta/updateValor?array=";

        this.urlGetCobranca  = urlPadrao+"consulta/getcobranca/";

        this.urlGetUsuario   = urlPadrao+"consulta/getlogin";

        this.urlSetCobranca  = urlPadrao+"consulta/updatecobranca?array=";
    }

    public String getNomeBanco() {
        return nomeBanco;
    }

    public String getUrlCobrancaPaga() {
        return urlCobrancaPaga;
    }

    public String getUrlGetCobranca() {
        return urlGetCobranca;
    }

    public String getUrlGetUsuario() {
        return urlGetUsuario;
    }

    public String getUrlSetCobranca() {
        return urlSetCobranca;
    }
}
