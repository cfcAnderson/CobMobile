package br.com.cfcsystem.Controls;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.ArrayList;

import br.com.cfcsystem.Entity.Cobranca;
import br.com.cfcsystem.cobmobile.DatabaseHandler;

/**
 * Created by user on 16/09/2014.
 */
public class CobrancaController extends Activity implements Serializable {

    private static DatabaseHandler dbHandler;
    private SQLiteDatabase db = null;
    private Cursor _fila = null;


    public CobrancaController(DatabaseHandler dbHandler){
        this.dbHandler = dbHandler;
        //db = dbHandler.getWritableDatabase();
    }

    public void Close(){
        Close();
    }

    public void Insert(Cobranca cobranca){

        try {
            try {
                db = dbHandler.getWritableDatabase();
                ContentValues registro = new ContentValues();
                registro.put("CODIGO", cobranca.getCodigo());
                registro.put("COD_CLIENTE", cobranca.getCodCliente());
                registro.put("CLIENTE", cobranca.getCliente());
                registro.put("VL_ANTERIOR", cobranca.getVlAnterior());
                registro.put("VL_PERIODO", cobranca.getVlPeriodo());
                registro.put("VL_PAGO", cobranca.getVlPago());
                registro.put("DT_PAGAMENTO", cobranca.getDtPagamento());
                registro.put("LATITUDE", cobranca.getLatitude());
                registro.put("LONGITUDE", cobranca.getLongitude());
                registro.put("STATUS", cobranca.getStatus());
                registro.put("CIDADE",cobranca.getCidade());
                registro.put("UF",cobranca.getUf());
                registro.put("DT_PROXIMA", cobranca.getDtProxima());
                registro.put("ORDEM", cobranca.getOrdem());
                registro.put("FONE1", cobranca.getFone1());
                registro.put("FONE2", cobranca.getFone2());
                registro.put("VL_NEGOCIAR", cobranca.getVlNegociar());
                db.insert("COBRANCA", null, registro);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }finally {
            db.close();
        }

    }


    public void Update(Cobranca cobranca){
        try {
            try {
                db = dbHandler.getWritableDatabase();
                ContentValues registro = new ContentValues();
                registro.put("CODIGO", cobranca.getCodigo());
                registro.put("COD_CLIENTE", cobranca.getCodCliente());
                registro.put("CLIENTE", cobranca.getCliente());
                registro.put("VL_ANTERIOR", cobranca.getVlAnterior());
                registro.put("VL_PERIODO", cobranca.getVlPeriodo());
                registro.put("VL_PAGO", cobranca.getVlPago());
                registro.put("DT_PAGAMENTO", cobranca.getDtPagamento());
                registro.put("LATITUDE", cobranca.getLatitude());
                registro.put("LONGITUDE", cobranca.getLongitude());
                registro.put("STATUS", cobranca.getStatus());
                registro.put("CIDADE",cobranca.getCidade());
                registro.put("UF",cobranca.getUf());
                registro.put("DT_PROXIMA", cobranca.getDtProxima());
                registro.put("ORDEM", cobranca.getOrdem());
                registro.put("FONE1", cobranca.getFone1());
                registro.put("FONE2", cobranca.getFone2());
                registro.put("VL_NEGOCIAR", cobranca.getVlNegociar());
                db.update("COBRANCA", registro, "CODIGO=" + cobranca.getCodigo(), null);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }finally {
            db.close();
        }
    }

    /**
     *
     * @param codigo
     * codigo = 'todos' apaga toda a tabela or
     * codigo = codigo apagar o registro informado
     */

    public void Delete(String codigo){
        try {
            try {
                db = dbHandler.getWritableDatabase();
                ContentValues registro = new ContentValues();
                if (codigo.equals("todos")){
                    db.delete("COBRANCA", null, null);
                }else if (codigo.equals("")){
                    db.delete("COBRANCA", "CODIGO=" + codigo, null);
                }else{
                    db.delete("COBRANCA", codigo, null);
                }
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
            _fila = db.rawQuery("select " + campos + " from cobranca " + parametro + " order by ordem,cliente", null);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (_fila.moveToFirst()){
            return true;
        }else{
            return false;
        }
    }

    public ArrayList<Cobranca> getConsulta() {


        ArrayList<Cobranca> listaCobranca = new ArrayList<Cobranca>();

        if (_fila.moveToFirst()) {  //si ha devuelto 1 fila, vamos al primero (que es el unico)

            while (!_fila.isAfterLast()){
                Cobranca cobranca = new Cobranca();
                cobranca.setCodigo(_fila.getInt(0));
                cobranca.setCodCliente(_fila.getInt(1));
                cobranca.setCliente(_fila.getString(2));
                cobranca.setVlAnterior(_fila.getDouble(3));
                cobranca.setVlPeriodo(_fila.getDouble(4));
                cobranca.setVlPago(_fila.getDouble(5));
                cobranca.setDtPagamento(_fila.getString(6));
                cobranca.setLatitude(_fila.getDouble(7));
                cobranca.setLongitude(_fila.getDouble(8));
                cobranca.setStatus(_fila.getString(9));
                cobranca.setCidade(_fila.getString(10));
                cobranca.setUf(_fila.getString(11));
                cobranca.setDtProxima(_fila.getString(12));
                cobranca.setOrdem(_fila.getInt(13));
                cobranca.setFone1(_fila.getString(14));
                cobranca.setFone2(_fila.getString(15));
                try {
                    cobranca.setVlNegociar(_fila.getDouble(16));
                }catch (Exception e){

                }
                listaCobranca.add(cobranca);
                _fila.moveToNext();
            }

        } else {
            System.out.println("Não existe uma cobrança cadastrada");
            //Toast.makeText(this, "Não existe uma cobrança cadastrada", Toast.LENGTH_SHORT).show();

        }

        db.close();
        return listaCobranca;

    }

    public int contCobranca(){
        if (_fila.moveToFirst()) {
            return _fila.getCount();
        }else{
            return 0;
        }
    }

    public void UpdateJson(String cobranca, String status){
        try {
            try {
                db = dbHandler.getWritableDatabase();
                String valor;
                if ((status.equals("I"))) {
                    valor = cobranca.substring(1, cobranca.length() - 1);
                }else{
                    valor = cobranca;
                }
                ContentValues registro = new ContentValues();
                registro.put("STATUS", status);
                db.update("COBRANCA", registro, "CODIGO in (" + valor + ")", null);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }finally {
            db.close();
        }
    }


}
