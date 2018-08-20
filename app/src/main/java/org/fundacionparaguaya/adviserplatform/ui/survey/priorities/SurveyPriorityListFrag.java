package org.fundacionparaguaya.adviserplatform.ui.survey.priorities;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import org.fundacionparaguaya.assistantadvisor.AdviserAssistantApplication;
import org.fundacionparaguaya.assistantadvisor.R;
import org.fundacionparaguaya.adviserplatform.injection.InjectionViewModelFactory;
import org.fundacionparaguaya.adviserplatform.ui.common.PrioritiesListAdapter;
import org.fundacionparaguaya.adviserplatform.ui.survey.SharedSurveyViewModel;

import javax.inject.Inject;

/**
 * Shows all of the indicators that a family has and their red/yellow/green status. Selecting one opens up a dialog,
 * that when filled out, adds the priority to the view model
 */

public class SurveyPriorityListFrag extends Fragment {

    private TextView mHeader;
    private LinearLayout mEmptyListView;
    private RecyclerView mPrioritiesList;

    @Inject
    protected InjectionViewModelFactory mViewModelFactory;
    protected SharedSurveyViewModel mSharedSurveyViewModel;

    PrioritiesListAdapter.SurveyPriorities mPriorityAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AdviserAssistantApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mSharedSurveyViewModel = ViewModelProviders.
                of(getActivity(), mViewModelFactory)
                .get(SharedSurveyViewModel.class);

        mPriorityAdapter = new PrioritiesListAdapter.SurveyPriorities();
        mPriorityAdapter.setCallback(getCallback());
    }

    public PrioritiesListAdapter.PriorityClickedHandler getCallback() {
        try {
            return ((PrioritiesListAdapter.PriorityClickedHandler) getParentFragment());
        } catch (ClassCastException e) {
            throw new ClassCastException("Parent fragment of LifeMap must implement PriorityClickedHandler");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_prioritylist, container, false);

        mPrioritiesList = v.findViewById(R.id.rv_prioritieslist);
        mPrioritiesList.setAdapter(mPriorityAdapter);
        mPrioritiesList.setLayoutManager(new LinearLayoutManager(getContext()));

        v.findViewById(R.id.btn_prioritylist_save).setOnClickListener((view -> onSave()));

        mHeader = v.findViewById(R.id.tv_prioriitylist_header);

        mEmptyListView = v.findViewById(R.id.view_prioritieslist_empty);

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mSharedSurveyViewModel.getSnapshot().observe(this, snapshot ->
        {
            mPriorityAdapter.setSnapshot(snapshot);

            if(mPriorityAdapter.getItemCount() == 0){
                mPrioritiesList.setVisibility(View.GONE);
                mEmptyListView.setVisibility(View.VISIBLE);
            } else {
                mPrioritiesList.setVisibility(View.VISIBLE);
                mEmptyListView.setVisibility(View.GONE);
            }
        });
    }

    public void onSave() {

        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.all_confirmation_question))
                .setContentText(getString(R.string.survey_summary_confirmation_details))
                .setCancelText(getString(R.string.all_cancel))
                .setConfirmText(getString(R.string.survey_summary_submit))
                .showCancelButton(true)
                .setCancelClickListener(SweetAlertDialog::cancel)
                .setConfirmClickListener((dialog)-> {
                    mSharedSurveyViewModel.submitSnapshotAsync(dialog);
                })
                .show();
    }
}
