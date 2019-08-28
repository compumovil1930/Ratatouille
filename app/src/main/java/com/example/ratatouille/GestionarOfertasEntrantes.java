package com.example.ratatouille;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
public class GestionarOfertasEntrantes extends AppCompatActivity {

    ListView lv;
    PopupWindow popup;
    LinearLayout layout;
    String[] nombres = {"Abby", "Luca", "Pierre","Shinnosuke", "Elisa", "Matt"};
    int[] images = {R.drawable.abby, R.drawable.luca, R.drawable.pierre, R.drawable.shinnosuke, R.drawable.elisa, R.drawable.matt};
    String[] desc = {"Comida China - 3 personas", "Comida Italiana - 1 persona", "Comida Mexicana - 2 personas", "Comida China - 4 personas", "Comida Colombiana - 5 personas", "Comida China - 1 persona"};
    String[] distance = {"1 km", "3 km", "2.5 km", "4 km", "1 km", "3.1 km"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestionar_ofertas_entrantes);
        lv = findViewById(R.id.listaOfertas);
        CustomAdapter ca = new CustomAdapter();
        lv.setAdapter(ca);
        layout = new LinearLayout(this);
        popup = new PopupWindow(this);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                popup.showAtLocation(layout, Gravity.BOTTOM, 10, 10);
                popup.update(50, 50, 300, 80);
            }
        });
    }
    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return nombres.length;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }


        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View vista = getLayoutInflater().inflate(R.layout.layoutlista, null);
            ImageView im = vista.findViewById(R.id.imagenPersonas);
            TextView tname = vista.findViewById(R.id.Nombre);
            TextView tdesc = vista.findViewById(R.id.desc);
            TextView tdis = vista.findViewById(R.id.distance);
            im.setImageResource(images[i]);
            tname.setText(nombres[i]);
            tdesc.setText(desc[i]);
            tdis.setText(distance[i]);
            return vista;
        }
    }
}
