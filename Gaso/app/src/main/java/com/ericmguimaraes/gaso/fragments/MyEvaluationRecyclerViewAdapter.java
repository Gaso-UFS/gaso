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
import com.ericmguimaraes.gaso.evaluation.FeatureType;
import com.ericmguimaraes.gaso.evaluation.Milestone;
import com.ericmguimaraes.gaso.evaluation.evaluations.Evaluation;
import com.ericmguimaraes.gaso.model.FuelSource;
import com.ericmguimaraes.gaso.model.FuzzyConsumption;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

public class MyEvaluationRecyclerViewAdapter extends RecyclerView.Adapter<MyEvaluationRecyclerViewAdapter.ViewHolder> {

    private final List<Milestone> mValues;

    public MyEvaluationRecyclerViewAdapter(List<Milestone> milestones) {
        mValues = milestones;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_evaluation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Milestone m = mValues.get(position);
        holder.mItem = m;
        holder.carNameText.setText(m.getCarModel());
        holder.dataText.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(m.getCreationDate())));

        if(m.getEvaluations()!=null && m.getEvaluations().containsKey(FeatureType.OBD_FUEL_AMOUNT)) {
            holder.abastecidoUsuario.setText("Usuário : " + m.getExpenseAmount() + "L");
            holder.abastecidoOBD.setText("OBDII: " + m.getExpenseAmountOBDRefil() + "L");
            holder.abastecidoDescricao.setText(m.getEvaluations().get(FeatureType.OBD_FUEL_AMOUNT).getMessage());
        } else
            holder.abastecimentoSection.setVisibility(View.GONE);

        holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, true));
        if(m.getFuelSources()!=null && m.getFuelSources().size()>0)
            holder.recyclerView.setAdapter(new TextAdapter(m.getFuelSources()));
        else
            holder.origemSection.setVisibility(View.GONE);

        if(m.getDistanceRolled()!=0 && m.getCombustiveConsumed()!=0){
            holder.consumido.setText(m.getCombustiveConsumed()+"L");
            holder.percorrido.setText(m.getDistanceRolled()+"");
        } else
            holder.consumidoPericorridoSection.setVisibility(View.GONE);

        if (m.getFuzzyConsumption()!=null) {
            FuzzyConsumption f = m.getFuzzyConsumption();
            holder.muitoBaixoConsumo.setText(String.format("%.2f", f.getPercentage(f.getVerylow()))+"%");
            holder.baixoConsumo.setText(String.format("%.2f", f.getPercentage(f.getLow()))+"%");
            holder.medioConsumo.setText(String.format("%.2f", f.getPercentage(f.getAverage()))+"%");
            holder.altoConsumo.setText(String.format("%.2f", f.getPercentage(f.getHigh()))+"%");
            holder.muitoAltoConsumo.setText(String.format("%.2f", f.getPercentage(f.getVeryhigh()))+"%");
        } else
            holder.perfilConsumoSection.setVisibility(View.GONE);

        if (m.getEvaluations()!=null && m.getEvaluations().containsKey(FeatureType.FUEL_CONSUMPTION_OBD_FUEL_LEVEL_AND_OBD_DISTANCE)) {
            Evaluation e = m.getEvaluations().get(FeatureType.FUEL_CONSUMPTION_OBD_FUEL_LEVEL_AND_OBD_DISTANCE);
            if(e.getRate()>0)
                holder.altaAvaliacao.setAlpha(1f);
            else if (e.getRate()<0)
                holder.baixaAvaliacao.setAlpha(1f);
            else
                holder.igualAvaliacao.setAlpha(1f);

            holder.avaliacaoGeral.setText("Geral: "+m.getConsumptionRateCar()+"KM/L");
            holder.avaliacaoAtual.setText("Atual: "+m.getConsumptionRateMilestone()+"KM/L");
            holder.avaliacaoDescricao.setText(e.getMessage());
        } else
            holder.avaliacaoSection.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public Milestone mItem;
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
        public final TextView abastecidoDescricao;
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
            abastecidoDescricao = (TextView) view.findViewById(R.id.abastecidoDescricao);
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

    }
}
