package com.ruben.habitosversion1;
import android.content.Context;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeToEditCallback extends ItemTouchHelper.SimpleCallback {

    private ListaAdapter listaAdapter;
    private Context context;


    // Constructor que recibe el adaptador y el launcher
    public SwipeToEditCallback(ListaAdapter listaAdapter, Context context) {
        super(0, ItemTouchHelper.RIGHT);  // Solo permitimos deslizamiento a la izquierda
        this.listaAdapter = listaAdapter;
        this.context = context;
    }

    // No necesitamos mover ítems, así que retornamos false
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return true;
    }

    // Método que se llama cuando un ítem es deslizado
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        Lista item = listaAdapter.getItem(position);

        Intent intent = new Intent(context, EditActivity.class);
        intent.putExtra("item_id", item.getId());
        context.startActivity(intent);
        listaAdapter.notifyItemChanged(position);
    }

}
