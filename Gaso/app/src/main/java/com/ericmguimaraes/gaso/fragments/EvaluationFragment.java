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
import com.ericmguimaraes.gaso.evaluation.Milestone;
import com.ericmguimaraes.gaso.fragments.dummy.DummyContent;
import com.ericmguimaraes.gaso.fragments.dummy.DummyContent.DummyItem;
import com.ericmguimaraes.gaso.persistence.MilestoneDAO;
import com.google.firebase.database.DatabaseError;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class EvaluationFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EvaluationFragment() {
    }

    // TODO: Customize parameter initialization
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

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            final MilestoneDAO dao = new MilestoneDAO();
            dao.findAll(new MilestoneDAO.MilestonesListReceivedListener() {
                @Override
                public void onMilestonesReceived(@Nullable List<Milestone> milestones) {
                    if (milestones != null)
                        recyclerView.setAdapter(new MyEvaluationRecyclerViewAdapter(DummyContent.ITEMS, mListener, milestones));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(DummyItem item);
    }
}
