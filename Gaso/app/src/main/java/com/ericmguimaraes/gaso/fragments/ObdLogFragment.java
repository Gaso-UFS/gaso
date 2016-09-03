package com.ericmguimaraes.gaso.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ericmguimaraes.gaso.R;
import com.ericmguimaraes.gaso.adapters.MyObdCommandJobRecyclerViewAdapter;
import com.ericmguimaraes.gaso.model.ObdLog;

/**
 * A fragment representing a list of Items.
 * <p>
 * Activities containing this fragment MUST implement the {@link OnObdLogListFragmentInteractionListener}
 * interface.
 */
public class ObdLogFragment extends Fragment {

    private OnObdLogListFragmentInteractionListener mListener;
    private MyObdCommandJobRecyclerViewAdapter adapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ObdLogFragment() {
    }

    @SuppressWarnings("unused")
    public static ObdLogFragment newInstance() {
        ObdLogFragment fragment = new ObdLogFragment();
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
        View view = inflater.inflate(R.layout.fragment_obdlog_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            adapter = new MyObdCommandJobRecyclerViewAdapter(null, mListener);
            recyclerView.setAdapter(adapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnObdLogListFragmentInteractionListener) {
            mListener = (OnObdLogListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnObdLogListFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnObdLogListFragmentInteractionListener {
        void onObdLogListFragmentInteraction(ObdLog log);
    }

    public void addOrUpdateJob(ObdLog log){
        adapter.addOrUpdateJob(log);
    }
}
