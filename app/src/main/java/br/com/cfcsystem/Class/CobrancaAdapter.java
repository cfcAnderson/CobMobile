package br.com.cfcsystem.Class;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import br.com.cfcsystem.Controls.CobrancaController;
import br.com.cfcsystem.Entity.Cobranca;
import br.com.cfcsystem.cobmobile.R;

/**
 * Created by user on 22/09/2014.
 */
public class CobrancaAdapter extends BaseAdapter {

    private Context context;
    private CobrancaController _cc;
    private NumberFormat numberFormat = null;
    private LayoutInflater inflater;
    private ArrayList<Cobranca> listaCobranca = null;
    private View gridView = null;


    public CobrancaAdapter (Context context, CobrancaController cc){
        this.context = context;
        _cc = cc;

        this.listaCobranca = new ArrayList<Cobranca>();
        carregaLista(_cc);



        numberFormat = NumberFormat.getCurrencyInstance(new Locale("br", "BR"));

        inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void carregaLista(CobrancaController cc){

        listaCobranca = cc.getConsulta();
    }



    @Override
    public int getCount() {

        return  _cc.contCobranca();
    }

    @Override
    public Cobranca getItem(int position) {

        return _cc.getConsulta().get(position);
    }


    @Override
    public long getItemId(int position) {

        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        gridView = inflater.inflate(R.layout.db_layout_cobranca,null);

        try {

            if (gridView != null) {
                TextView cliente = (TextView) gridView.findViewById(R.id.txtCliente);
                TextView vlrAnterior = (TextView) gridView.findViewById(R.id.txtVlAnterior);
                TextView vlrPeriodo = (TextView) gridView.findViewById(R.id.txtVlPeriodo);
                TextView vlrTotal = (TextView) gridView.findViewById(R.id.txtVlTotal);
                TextView vlrPago = (TextView) gridView.findViewById(R.id.txtVlPago);

                TextView txtFone1 = (TextView) gridView.findViewById(R.id.txtFone1);
                TextView txtFone2 = (TextView) gridView.findViewById(R.id.txtFone2);

                ImageView imgSend = (ImageView) gridView.findViewById(R.id.imgSend);
                ImageView imgPago = (ImageView) gridView.findViewById(R.id.imgPago);
                ImageView imgAdiado = (ImageView) gridView.findViewById(R.id.imgAdiado);
                ImageView imgAdiadoEnviado = (ImageView) gridView.findViewById(R.id.imgAdiadoEnviado);
                if (cliente != null && vlrAnterior != null &&
                        vlrPeriodo != null && vlrTotal != null &&
                        vlrPago != null && txtFone1 != null &&
                        txtFone2 != null) {

                    cliente.setText(listaCobranca.get(position).getCliente().toString());
                    vlrAnterior.setText(numberFormat.format(listaCobranca.get(position).getVlAnterior()).toString());
                    vlrPeriodo.setText(numberFormat.format(listaCobranca.get(position).getVlPeriodo()).toString());

                    txtFone1.setText(listaCobranca.get(position).getFone1().toString());
                    txtFone2.setText(listaCobranca.get(position).getFone2().toString());

                    Double total = Double.parseDouble(listaCobranca.get(position).getVlAnterior().toString()) +
                            Double.parseDouble(listaCobranca.get(position).getVlPeriodo().toString());
                    vlrTotal.setText(numberFormat.format(total).toString());

                    vlrPago.setText(numberFormat.format(listaCobranca.get(position).getVlPago()).toString());

                    if (listaCobranca.get(position).getVlPago() != 0.0) {
                        //cliente.setTextColor(Color.parseColor("#ffff0006"));
                        imgPago.setVisibility(View.VISIBLE);
                        imgSend.setVisibility(View.INVISIBLE);
                        imgAdiado.setVisibility(View.INVISIBLE);
                        imgAdiadoEnviado.setVisibility(View.INVISIBLE);
                        cliente.setPadding(imgPago.getMaxHeight() + 5, 0, 0, 0);
                    } else {
                        imgPago.setVisibility(View.INVISIBLE);
                        imgSend.setVisibility(View.INVISIBLE);
                        imgAdiado.setVisibility(View.INVISIBLE);
                        imgAdiadoEnviado.setVisibility(View.INVISIBLE);
                        cliente.setPadding(0, 0, 0, 0);
                    }

                    if (listaCobranca.get(position).getDtProxima() != null){
                        imgSend.setVisibility(View.INVISIBLE);
                        imgPago.setVisibility(View.INVISIBLE);
                        imgAdiado.setVisibility(View.VISIBLE);
                        imgAdiadoEnviado.setVisibility(View.INVISIBLE);
                        cliente.setPadding(imgSend.getMaxHeight() + 5, 0, 0, 0);
                    }

                    if ((listaCobranca.get(position).getStatus().equals("E")) &&
                            (listaCobranca.get(position).getDtProxima() != null)) {
                        imgSend.setVisibility(View.INVISIBLE);
                        imgPago.setVisibility(View.INVISIBLE);
                        imgAdiado.setVisibility(View.INVISIBLE);
                        imgAdiadoEnviado.setVisibility(View.VISIBLE);
                        cliente.setPadding(imgSend.getMaxHeight() + 5, 0, 0, 0);
                    }

                    if ((listaCobranca.get(position).getStatus().equals("E")) &&
                        (listaCobranca.get(position).getVlPago() != 0.0)){
                        imgSend.setVisibility(View.VISIBLE);
                        imgPago.setVisibility(View.INVISIBLE);
                        imgAdiado.setVisibility(View.INVISIBLE);
                        imgAdiadoEnviado.setVisibility(View.INVISIBLE);
                        cliente.setPadding(imgSend.getMaxHeight() + 5, 0, 0, 0);
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return gridView;
    }

}
