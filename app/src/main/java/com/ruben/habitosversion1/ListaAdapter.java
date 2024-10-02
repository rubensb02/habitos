package com.ruben.habitosversion1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.RecyclerView;

import com.ruben.habitosversion1.room.HabitosDao;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ListaAdapter extends RecyclerView.Adapter<ListaAdapter.ListaViewHolder> {

    private ArrayList<Lista> listaList;
    private HabitosDao habitosDao;
    Context context;


    public ListaAdapter(ArrayList<Lista> listaList, HabitosDao habitosDao, Context context) {
        this.listaList = listaList;
        this.habitosDao = habitosDao;
        this.context = context;
    }

    @NotNull
    @Override
    public ListaViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list, parent, false);
        return new ListaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull ListaViewHolder holder, int position) {
        Lista list = listaList.get(position);
        holder.txtNombre.setText(list.getNombre());
        holder.txtDescripcion.setText(list.getDescripcion());
        holder.txtPrecio.setText("Precio: " + list.getPrecio());
    }



    @Override
    public int getItemCount() {
        return listaList.size();
    }

    // Método para eliminar un ítem de la lista
    public void deleteItem(int position) {
        Lista lista = listaList.get(position);
        listaList.remove(position); // Eliminar el ítem de la lista
        notifyItemRemoved(position); // Notificar al adaptador que un ítem ha sido eliminado
        Toast.makeText(context, "Digimon elimiado", Toast.LENGTH_SHORT).show();

        // Eliminar el ítem de la base de datos
        new Thread(new Runnable() {
            @Override
            public void run() {
                habitosDao.delete(lista);
            }
        }).start();
    }

    public static class ListaViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtDescripcion, txtPrecio;

        public ListaViewHolder(@NotNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombre);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcion);
            txtPrecio = itemView.findViewById(R.id.txtPrecio);
        }
    }
}


