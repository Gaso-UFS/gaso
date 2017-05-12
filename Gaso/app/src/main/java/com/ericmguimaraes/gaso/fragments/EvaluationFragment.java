package com.ericmguimaraes.gaso.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.config.Constants;
import com.ericmguimaraes.gaso.evaluation.EvaluationHelper;
import com.ericmguimaraes.gaso.evaluation.Milestone;
import com.ericmguimaraes.gaso.persistence.MilestoneDAO;
import com.ericmguimaraes.gaso.util.DynamicBoxCustom;
import com.google.firebase.database.DatabaseError;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import mehdi.sakout.dynamicbox.DynamicBox;

/**
 * A fragment representing a list of Items.
 */
public class EvaluationFragment extends Fragment {

    @Bind(R.id.list)
    RecyclerView recyclerView;

    DynamicBoxCustom box;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EvaluationFragment() {
    }

    @SuppressWarnings("unused")
    public static EvaluationFragment newInstance() {
        EvaluationFragment fragment = new EvaluationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_evaluation_list, container, false);

        ButterKnife.bind(this, view);

        box = new DynamicBoxCustom(getContext(), recyclerView);

        box.showLoadingLayout();

        MilestoneDAO dao = new MilestoneDAO();
        dao.findLastMilestone(new MilestoneDAO.OneMilestoneReceivedListener() {
            @Override
            public void onMilestoneReceived(@Nullable Milestone milestone) {
                if(milestone!=null)
                    EvaluationHelper.initEvaluation(milestone, false, new EvaluationHelper.OnEvaluationListener() {
                        @Override
                        public void onDone() {
                            loadData();
                        }
                    });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //ignore
            }
        });

        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        loadData();

        return view;
    }

    private void loadData() {
        final MilestoneDAO dao = new MilestoneDAO();
        dao.findAll(new MilestoneDAO.MilestonesListReceivedListener() {
            @Override
            public void onMilestonesReceived(@Nullable List<Milestone> milestones) {
                box.hideAll();
                if (milestones != null && milestones.size()>0) {
                    recyclerView.setAdapter(new MyEvaluationRecyclerViewAdapter(milestones));
                } else
                    showNotReadyYet();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                showErrorLayout();
            }
        });
    }

    private void showNotReadyYet() {
        box.showEmptyMessage("Parece que ainda não temos avaliações. Tenta voltar mais tarde. :)");
    }

    private void showErrorLayout() {
        box.setOtherExceptionTitle(Constants.genericError);
        box.setOtherExceptionMessage("");
        box.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                box.showLoadingLayout();
                loadData();
            }
        });
        box.showExceptionLayout();
    }

}
