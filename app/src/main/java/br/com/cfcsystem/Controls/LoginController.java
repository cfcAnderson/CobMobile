package br.com.cfcsystem.Controls;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import br.com.cfcsystem.Entity.Login;
import br.com.cfcsystem.cobmobile.DatabaseHandler;

/**
 * Created by user on 16/09/2014.
 */
public class LoginController extends Activity implements Serializable {

    private static DatabaseHandler dbHandler;
    private SQLiteDatabase db = null;
    private Cursor fila = null;


    public LoginController(DatabaseHandler dbHandler){
        this.dbHandler = dbHandler;
        //db = dbHandler.getWritableDatabase();
    }

    public void Close(){
        Close();
    }

    public void Insert(Login login){

        try {
            try {
                db = dbHandler.getWritableDatabase();
                ContentValues registro = new ContentValues();
                registro.put("CODIGO",login.getCODIGO());
                registro.put("USUARIO", login.getUSUARIO());
                registro.put("SENHA", login.getSENHA());
                registro.put("SALVA_USUARIO", "N");
                db.insert("LOGIN", null, registro);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }finally {
            db.close();
        }

    }

    public void Update(Login login){
        try {
            try {
                db = dbHandler.getWritableDatabase();
                ContentValues registro = new ContentValues();
                registro.put("USUARIO", login.getUSUARIO());
                registro.put("SENHA", login.getSENHA());
                registro.put("SALVA_USUARIO", login.getSalva_Usuario());
                db.update("LOGIN", registro, "CODIGO=" + login.getCODIGO(), null);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }finally {
            db.close();
        }
    }

    public void Delete(String codigo){
        try {
            try {
                db = dbHandler.getWritableDatabase();
                ContentValues registro = new ContentValues();
                if (codigo.equals("todos")){
                    db.delete("LOGIN", null, null);
                }else {
                    db.delete("LOGIN", "CODIGO=" + codigo, null);
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }finally {
            db.close();
        }
    }

    public void NotDelete(String codigo){
        try {
            try {
                db = dbHandler.getWritableDatabase();
                ContentValues registro = new ContentValues();
                db.delete("LOGIN", "CODIGO<>" + codigo, null);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }finally {
            db.close();
        }
    }

    public Boolean setConsulta(String campos, String parametro){
        //dbHandler = new DatabaseHandler(this);

        db = dbHandler.getReadableDatabase();

        //String dni = et1.getText().toString();
        try {
            fila = db.rawQuery("select " + campos + " from login " + parametro, null);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (fila.moveToFirst()){
            return true;
        }else{
            return false;
        }
    }

    public ArrayList<Login> getConsulta() {


        ArrayList<Login> listaLogin = new ArrayList<Login>();

        if (fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)

            while (!fila.isAfterLast()){
                Login login = new Login();
                login.setCODIGO(fila.getInt(0));
                login.setUSUARIO(fila.getString(1));
                login.setSENHA(fila.getString(2));
                login.setSalva_Usuario(fila.getString(3));
                listaLogin.add(login);
                fila.moveToNext();
            }

        } else {
            Toast.makeText(this, "No existe una persona con dicho dni",
                    Toast.LENGTH_SHORT).show();

        }

        db.close();
        return listaLogin;

    }

    public Integer validaUsuario(String usuario, String senha){
        try{
            db = dbHandler.getWritableDatabase();
            Cursor fila = db.rawQuery(  "select codigo from login where usuario = '"+ usuario.toUpperCase() + "' and senha = '" + senha+"'", null);

            if (fila.moveToFirst()){
                return fila.getInt(0);
            }else{
                return 0;
            }
        }finally {
            db.close();
        }
    }




}
