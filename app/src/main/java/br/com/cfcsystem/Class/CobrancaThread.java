package br.com.cfcsystem.Class;

import android.os.AsyncTask;

/**
 * Created by user on 02/12/2014.
 */
public class CobrancaThread {

    private Boolean finalizaThread;
    private AtualizaLista atualizaLista = null;
    private CobrancaAdapter cobrancaAdapter = null;

    public CobrancaThread(CobrancaAdapter cobrancaAdapter){
        finalizaThread = false;

        this.cobrancaAdapter = cobrancaAdapter;

        atualizaLista = new AtualizaLista();
        atualizaLista.execute();
    }



    private class AtualizaLista extends AsyncTask<String, Integer, Integer> {

        protected void onPostExecute(Integer result){
            System.out.println("Saiu");
        }

        protected void onPreExecute() {
            System.out.println("Entrou");
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            System.out.println("Teste");
            cobrancaAdapter.notifyDataSetChanged();
        }

        @Override
        protected Integer doInBackground(String... params) {
            int i = 0;
            while (! getFinalizaThread()){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(i++);
                publishProgress(i);

            }

            return null;
        }
    }

    public Boolean getFinalizaThread() {
        return finalizaThread;
    }

    public void setFinalizaThread(Boolean finalizaThread) {
        this.finalizaThread = finalizaThread;

        atualizaLista.cancel(true);

    }
}
