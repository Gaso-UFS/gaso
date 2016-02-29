package com.ericmguimaraes.gaso.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ericmguimaraes.gaso.config.Config;
import com.ericmguimaraes.gaso.MainActivity;
import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.model.User;
import com.ericmguimaraes.gaso.persistence.UserDAO;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by ericm on 2/28/2016.
 */
public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder> {

    List<User> userList;
    private User lastRemoved;
    private Context context;
    RecyclerView recyclerView;

    public UserListAdapter(List<User> userList, RecyclerView view, Context context) {
        this.userList = userList;
        this.context = context;
        recyclerView = view;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_item_recyclerview, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            User user = userList.get(position);
            holder.name.setText(user.getName());
            holder.email.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void remove(final int posistion){
        try {
            lastRemoved = userList.get(posistion);

            Snackbar snackbar = Snackbar
                    .make(recyclerView, "Usuario " + lastRemoved.getName() + " removido com sucesso.", Snackbar.LENGTH_LONG)
                    .setAction("Desfazer", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            UserDAO dao = new UserDAO(context);
                            dao.add(lastRemoved);
                            userList.add(posistion, lastRemoved);
                            notifyItemInserted(posistion);
                        }
                    });
            snackbar.show();

            userList.remove(lastRemoved);

            UserDAO dao = new UserDAO(context);
            dao.remove(lastRemoved);

            if(userList.isEmpty())
                Config.getInstance().currentUser = null;
            else
                Config.getInstance().currentUser = userList.get(0);

            notifyDataSetChanged();
        } catch (Exception e){
            Snackbar snackbar = Snackbar
                    .make(recyclerView, "ops, tivemos um pequeno problema.", Snackbar.LENGTH_LONG);
            snackbar.show();
            Log.e("remove",e.getMessage(),e);
        }
    }

    public class ViewHolder  extends RecyclerView.ViewHolder  implements View.OnClickListener {

        @Bind(R.id.name)
        TextView name;
        @Bind(R.id.email)
        TextView email;

        View view;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int itemPosition = recyclerView.getChildAdapterPosition(v);
            User u = userList.get(itemPosition);
            Toast.makeText(context, "Usuario "+u.getName()+" selecionado com sucesso.", Toast.LENGTH_LONG).show();
            Config config = Config.getInstance();
            config.currentUser = u;
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

}
