package br.com.cfcsystem.Entity;

import java.io.Serializable;




/**
 * Created by user on 17/09/2014.
 */
public class Login implements Serializable {


    private Integer CODIGO;
    private String USUARIO;
    private String SENHA;
    private String SALVA_USUARIO;

    public Integer getCODIGO() {
        return CODIGO;
    }

    public void setCODIGO(Integer CODIGO) {
        this.CODIGO = CODIGO;
    }

    public String getUSUARIO() {
        return USUARIO;
    }

    public void setUSUARIO(String USUARIO) {
        this.USUARIO = USUARIO;
    }

    public String getSENHA() {
        return SENHA;
    }

    public void setSENHA(String SENHA) {
        this.SENHA = SENHA;
    }

    public String getSalva_Usuario() {
        return SALVA_USUARIO;
    }

    public void setSalva_Usuario(String SALVA_USUARIO) {
        this.SALVA_USUARIO = SALVA_USUARIO;
    }
}
