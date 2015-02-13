package br.com.cfcsystem.cobmobile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by user on 16/09/2014.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 11;
    private String[] _tabela = {"LOGIN","COBRANCA"};
    private String[] _tabela_old = {"LOGIN_OLD","COBRANCA_OLD"};

    //LoginController
    private static  final String DATABASE_NAME = "FATCobMobileDB";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);


        //onCreate(getWritableDatabase());
        //onUpgrade(getWritableDatabase(),1,2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        executaSQL(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        for (int i=0; i<_tabela_old.length; i++){
            try {
                db.execSQL("drop table " + _tabela_old[i]);
            }catch (Exception e){
                System.out.println("Erro drop table: "+ _tabela_old[i]+" "+e.getMessage());
            }
        }

        for (int i = 0; i <_tabela.length ; i++){
            try {
                db.execSQL("alter table " + _tabela[i] + " rename to " + _tabela_old[i]);
            }catch (Exception e){
                System.out.println("Erro alter table: "+ _tabela[i]+" "+e.getMessage());
            }
        }

        executaSQL(db);

        for (int i=0; i < _tabela.length ; i++){
            try {
                if ((_tabela[i].equals("COBRANCA")) && (oldVersion < 8)) {
                    db.execSQL("insert into " + _tabela[i] + " select *,'DOURADOS','MS',null as DT_PROXIMA,null,null,null,null from " + _tabela_old[i]);
                }else if ((_tabela[i].equals("COBRANCA"))  && (oldVersion < 10)) {
                    db.execSQL("insert into " + _tabela[i] + " select *,null from " + _tabela_old[i]);
                }else{
                    db.execSQL("insert into " + _tabela[i] + " select * from " + _tabela_old[i]);
                }
            }catch (Exception e){
                System.out.println("Erro insert into: "+ _tabela[i]+" "+e.getMessage());
            }
        }

        for (int i=0; i<_tabela_old.length; i++){
            try {
                db.execSQL("drop table " + _tabela_old[i]);
            }catch (Exception e){
                System.out.println("2 Erro drop table: "+ _tabela_old[i]+" "+e.getMessage());
            }
        }
    }

    private void executaSQL(SQLiteDatabase db) {

        //Entity: LOGIN
        try{
            db.execSQL("CREATE TABLE LOGIN ( " +
                    " CODIGO  INTEGER        PRIMARY KEY NOT NULL," +
                    " USUARIO VARCHAR( 50 )," +
                    " SENHA   VARCHAR( 50 ) ," +
                    " SALVA_USUARIO VARCHAR(1) DEFAULT 'N')");
        }catch (Exception e){
            System.out.println("Erro ao criar login: "+e.getMessage());
        }


        //Entity : COBRANCA
        try {
            db.execSQL("CREATE TABLE COBRANCA ( " +
                    "    CODIGO      INTEGER PRIMARY KEY NOT NULL," +
                    "    COD_CLIENTE INTEGER NOT NULL," +
                    "    CLIENTE     VARCHAR(50) NOT NULL," +
                    "    VL_ANTERIOR NUMERIC NOT NULL," +
                    "    VL_PERIODO    NUMERIC NOT NULL," +
                    "    VL_PAGO    NUMERIC," +
                    "    DT_PAGAMENTO DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "    LATITUDE  NUMERIC," +
                    "    LONGITUDE NUMERIC," +
                    "    STATUS     VARCHAR(1)," +//A = Aguardando Importacao I = Importado
                    "    CIDADE     VARCHAR(30)," +
                    "    UF         VARCHAR(2),"+
                    "    DT_PROXIMA DATE," +
                    "    ORDEM      INTEGER," +
                    "    FONE1      VARCHAR(13)," +
                    "    FONE2      VARCHAR(50)," +
                    "    VL_NEGOCIAR NUMERIC)");
        }catch (Exception e){
            System.out.println("Erro ao criar cobranca: "+e.getMessage());
        }
    }
}
