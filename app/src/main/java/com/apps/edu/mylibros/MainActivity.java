package com.apps.edu.mylibros;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText etAutor;
    TextView tvSalida;
    ListView lvSalida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etAutor = (EditText) findViewById(R.id.etAutor);
        tvSalida = (TextView) findViewById( R.id.tvSalida);
        lvSalida = (ListView)findViewById(R.id.lvSalida);
    }

    //método de respuesta al click del botón
    public void buscarWeb(View v) {
        //iniciar tarea en segundo plano
        ComunicacionTask com = new ComunicacionTask();
        //le pasa como parámetro la dirección
        //de la página
        System.out.println("http://minionsdesapps.esy.es/apps/libros.php " +etAutor.getText().toString());
        com.execute("http://minionsdesapps.esy.es/apps/libros.php", etAutor.getText().toString());

    }

    private class ComunicacionTask extends AsyncTask<String, Void, String> {

        //    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params) {

            String cadenaJson="";
            try{
                //monta la url con la dirección y parámetro
                //de envío
                URL url=new URL(params[0]+"?parametro="+params[1]);
                System.out.println(url);
                URLConnection con=url.openConnection();
                //recuperacion de la respuesta JSON
                String s;
                InputStream is=con.getInputStream();
                //utilizamos UTF-8 para que interprete
                //correctamente las ñ y acentos
                BufferedReader bf=new BufferedReader(
                        new InputStreamReader(is, Charset.forName("UTF-8")));
                while((s=bf.readLine())!=null){
                    cadenaJson+=s;
                }

            }
            catch(IOException ex){
                ex.printStackTrace();
            }
            return cadenaJson;
        }

        @Override
        protected void onPostExecute(String result) {

            ArrayList<String> json_list = new ArrayList<>();

            try{
                //creamos un array JSON a partir de la cadena recibida
                JSONArray jarray=new JSONArray(""+result);
                //creamos el array de String con el tamaño del array JSON

                for(int i=0;i<jarray.length();i++){
                    JSONObject job=jarray.getJSONObject(i);
                    System.out.println("FILA: "+job.toString());
                    json_list.add(job.getString("titulo") +" | " +job.getString("autor") +" | " +job.getString("paginas"));
                }

            }
            catch(JSONException ex){
                ex.printStackTrace();
            }

            ArrayAdapter<String> adapter= new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, json_list);
            lvSalida.setAdapter(adapter);
            //tvSalida.setText(""+result);
        }

    }

}
