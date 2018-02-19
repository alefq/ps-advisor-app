package org.fundacionparaguaya.advisorapp.fragments;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;

import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.models.BackgroundQuestion;
import org.fundacionparaguaya.advisorapp.models.Survey;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel;

import javax.inject.Inject;

/**
 * Fragment that displays economic questions
 */

public class SurveyEconomicQuestionsFragment extends SurveyQuestionsFrag {

    @Inject protected InjectionViewModelFactory mViewModelFactory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        ((AdvisorApplication) getActivity().getApplication())
                .getApplicationComponent()
                .inject(this);

        mSharedSurveyViewModel = ViewModelProviders.
                of(getActivity(), mViewModelFactory)
                .get(SharedSurveyViewModel.class);

        setTitle(getString(R.string.surveyquestions_economic_title));

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initQuestionList() {
        Survey survey = mSharedSurveyViewModel.getSurveyInProgress();
        mQuestions = survey.getEconomicQuestions();
        mSharedSurveyViewModel.getEconomicResponses().observe(this, mSurveyReviewAdapter::setResponses);

        super.initQuestionList();
    }

    @Override
    public void onSubmit() {
        mSharedSurveyViewModel.setSurveyState(SharedSurveyViewModel.SurveyState.INDICATORS);
    }

    @Override
    public String getResponseFor(BackgroundQuestion q) {
        return mSharedSurveyViewModel.getBackgroundResponse(q);
    }
}