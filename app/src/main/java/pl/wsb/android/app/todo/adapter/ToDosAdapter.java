package pl.wsb.android.app.todo.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.wsb.android.app.todo.model.ToDo;
import pl.wsb.android.app.todo.R;

public class ToDosAdapter extends RecyclerView.Adapter<ToDosAdapter.ViewHolder> {
    private List<ToDo> data;
    private LayoutInflater inflater;

    public ToDosAdapter(
            Context context,
            List<ToDo> data
    ) {
        this.inflater = LayoutInflater.from(context);
        this.data = data;
        Log.i("WIELKOSC: ",  Integer.toString(this.data.size()));
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = this.inflater.inflate(R.layout.item_todo_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToDosAdapter.ViewHolder holder, int position) {
        ToDo item = this.getItem(position);
        if(item == null)
            return;

        holder.cbTodoDone.setText(item.getTitle());
        holder.cbTodoDone.setChecked(item.getCompleted());

    }

    @Override
    public int getItemCount() {
        return this.data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbTodoDone;
        ViewHolder(View itemView) {
            super(itemView);
            this.cbTodoDone = itemView.findViewById(R.id.cbTodosListDone);
        }
    }
    public ToDo getItem(int position) {
        return data.get(position);
    }


}
