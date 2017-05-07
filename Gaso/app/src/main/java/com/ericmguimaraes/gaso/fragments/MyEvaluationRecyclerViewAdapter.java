package com.ericmguimaraes.gaso.fragments;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.adapters.TextAdapter;
import com.ericmguimaraes.gaso.evaluation.Milestone;
import com.ericmguimaraes.gaso.fragments.EvaluationFragment.OnListFragmentInteractionListener;
import com.ericmguimaraes.gaso.fragments.dummy.DummyContent.DummyItem;
import com.ericmguimaraes.gaso.model.FuelSource;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyEvaluationRecyclerViewAdapter extends RecyclerView.Adapter<MyEvaluationRecyclerViewAdapter.ViewHolder> {

    private final List<DummyItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private final List<Milestone> mMilestones;

    public MyEvaluationRecyclerViewAdapter(List<DummyItem> items, OnListFragmentInteractionListener listener, List<Milestone> milestones) {
        mValues = items;
        mListener = listener;
        mMilestones = milestones;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_evaluation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).id);
        holder.mContentView.setText(mValues.get(position).content);
        ArrayList<FuelSource> fuelSources = new ArrayList<FuelSource>();
        fuelSources.add(new FuelSource("1", "posto 1: 20L", 20.0));
        fuelSources.add(new FuelSource("2", "posto 2: 10L", 20.0));
        fuelSources.add(new FuelSource("3", "posto 3: 5L", 20.0));
        fuelSources.add(new FuelSource("4", "posto 4: 1L", 20.0));
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, true));
        holder.recyclerView.setAdapter(new TextAdapter(fuelSources));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public DummyItem mItem;
        public RecyclerView recyclerView;

        // sections
        public LinearLayout carSection;
        public LinearLayout abastecimentoSection;
        public LinearLayout origemSection;
        public LinearLayout consumidoPericorridoSection;
        public LinearLayout perfilConsumoSection;
        public LinearLayout avaliacaoSection;

        // campos
        public final TextView carNameText;
        public final TextView dataText;
        public final TextView abastecidoUsuario;
        public final TextView abastecidoOBD;
        public final TextView consumido;
        public final TextView percorrido;
        public final TextView muitoBaixoConsumo;
        public final TextView baixoConsumo;
        public final TextView medioConsumo;
        public final TextView altoConsumo;
        public final TextView muitoAltoConsumo;
        public final TextView avaliacaoGeral;
        public final TextView avaliacaoAtual;
        public final TextView avaliacaoDescricao;
        public final ImageView baixaAvaliacao;
        public final ImageView igualAvaliacao;
        public final ImageView altaAvaliacao;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            recyclerView = (RecyclerView) view.findViewById(R.id.stations_recycler_view);

            // sections
            carSection = (LinearLayout) view.findViewById(R.id.carSection);
            origemSection = (LinearLayout) view.findViewById(R.id.origemSection);
            abastecimentoSection = (LinearLayout) view.findViewById(R.id.abastecimentoSection);
            consumidoPericorridoSection = (LinearLayout) view.findViewById(R.id.consumidoPericorridoSection);
            perfilConsumoSection = (LinearLayout) view.findViewById(R.id.perfilConsumoSection);
            avaliacaoSection = (LinearLayout) view.findViewById(R.id.avaliacaoSection);

            // campos
            carNameText = (TextView) view.findViewById(R.id.carNameText);
            dataText = (TextView) view.findViewById(R.id.dataText);
            abastecidoUsuario = (TextView) view.findViewById(R.id.abastecidoUsuario);
            abastecidoOBD = (TextView) view.findViewById(R.id.abastecidoOBD);
            consumido = (TextView) view.findViewById(R.id.consumido);
            percorrido = (TextView) view.findViewById(R.id.percorrido);
            muitoBaixoConsumo = (TextView) view.findViewById(R.id.muitoBaixoConsumo);
            baixoConsumo = (TextView) view.findViewById(R.id.baixoConsumo);
            medioConsumo = (TextView) view.findViewById(R.id.medioConsumo);
            altoConsumo = (TextView) view.findViewById(R.id.altoConsumo);
            muitoAltoConsumo = (TextView) view.findViewById(R.id.muitoAltoConsumo);
            avaliacaoGeral = (TextView) view.findViewById(R.id.avaliacaoGeral);
            avaliacaoAtual = (TextView) view.findViewById(R.id.avaliacaoAtual);
            avaliacaoDescricao = (TextView) view.findViewById(R.id.avaliacaoDescricao);
            baixaAvaliacao = (ImageView) view.findViewById(R.id.baixaAvaliacao);
            igualAvaliacao = (ImageView) view.findViewById(R.id.igualAvaliacao);
            altaAvaliacao = (ImageView) view.findViewById(R.id.altaAvaliacao);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
