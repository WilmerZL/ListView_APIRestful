package com.example.listview_apirestful;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import WebServices.Asynchtask;
import WebServices.WebService;

public class MainActivity extends AppCompatActivity implements Asynchtask {

    private ArrayList<Producto> productos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Consumir el API
        Map<String, String> datos = new HashMap<>();
        WebService ws = new WebService(
                "https://fakestoreapi.com/products",
                datos, MainActivity.this, MainActivity.this);
        ws.execute("GET");
    }

    @Override
    public void processFinish(String result) throws JSONException {
        productos = new ArrayList<>();
        JSONArray JSONlista = new JSONArray(result);

        // Parsear el JSON y llenar la lista de productos
        for (int i = 0; i < JSONlista.length(); i++) {
            JSONObject producto = JSONlista.getJSONObject(i);

            String title = producto.getString("title");
            String category = producto.getString("category");
            double price = producto.getDouble("price");
            String image = producto.getString("image");
            String description = producto.optString("description", "No description available.");

            productos.add(new Producto(title, category, price, image, description));
        }

        // Configurar el ListView con el adaptador personalizado
        ListView lvProductos = findViewById(R.id.LvProductos);
        ProductoAdapter adapter = new ProductoAdapter(this, productos);
        lvProductos.setAdapter(adapter);

        // Manejar la selección de productos en el ListView
        lvProductos.setOnItemClickListener((parent, view, position, id) -> {
            Producto productoSeleccionado = productos.get(position);

            // Crear un intent para abrir DetalleProductoActivity
            Intent intent = new Intent(MainActivity.this, DetalleProductoActivity.class);
            intent.putExtra("titulo", productoSeleccionado.getTitulo());
            intent.putExtra("categoria", productoSeleccionado.getCategoria());
            intent.putExtra("precio", productoSeleccionado.getPrecio());
            intent.putExtra("imagen", productoSeleccionado.getImagen());
            startActivity(intent);
        });
    }

    // Clase interna para representar un producto
    public static class Producto {
        private final String titulo;
        private final String categoria;
        private final double precio;
        private final String imagen;
        private final String descripcion;

        public Producto(String titulo, String categoria, double precio, String imagen, String descripcion) {
            this.titulo = titulo;
            this.categoria = categoria;
            this.precio = precio;
            this.imagen = imagen;
            this.descripcion = descripcion;
        }

        public String getTitulo() {
            return titulo;
        }

        public String getCategoria() {
            return categoria;
        }

        public double getPrecio() {
            return precio;
        }

        public String getImagen() {
            return imagen;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }

    // Adaptador personalizado para el ListView
    public class ProductoAdapter extends android.widget.BaseAdapter {
        private final android.content.Context context;
        private final List<Producto> productos;

        public ProductoAdapter(android.content.Context context, List<Producto> productos) {
            this.context = context;
            this.productos = productos;
        }

        @Override
        public int getCount() {
            return productos.size();
        }

        @Override
        public Object getItem(int position) {
            return productos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public android.view.View getView(int position, android.view.View convertView, android.view.ViewGroup parent) {
            if (convertView == null) {
                convertView = android.view.LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
            }

            Producto producto = productos.get(position);

            android.widget.ImageView imgProducto = convertView.findViewById(R.id.imgProducto);
            android.widget.TextView txtTitulo = convertView.findViewById(R.id.txtTitulo);
            android.widget.TextView txtCategoria = convertView.findViewById(R.id.txtCategoria);
            android.widget.TextView txtPrecio = convertView.findViewById(R.id.txtPrecio);
            android.widget.TextView txtDescripcion = convertView.findViewById(R.id.txtDescripcion);

            txtTitulo.setText(producto.getTitulo());
            txtCategoria.setText("Category: " + producto.getCategoria());
            txtPrecio.setText("Price: $" + producto.getPrecio());
            txtDescripcion.setText(producto.getDescripcion());

            Glide.with(context)
                    .load(producto.getImagen())
                    .into(imgProducto);

            return convertView;
        }
    }
}

