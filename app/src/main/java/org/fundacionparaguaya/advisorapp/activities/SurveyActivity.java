package org.fundacionparaguaya.advisorapp.activities;

import android.animation.ObjectAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;

import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.fragments.*;
import org.fundacionparaguaya.advisorapp.models.Family;
import org.fundacionparaguaya.advisorapp.util.ScreenCalculations;
import org.fundacionparaguaya.advisorapp.viewmodels.InjectionViewModelFactory;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel;
import org.fundacionparaguaya.advisorapp.viewmodels.SharedSurveyViewModel.*;

import javax.inject.Inject;

/**
 * Activity for surveying a family's situation. Displays the fragments that record background info and allows
 * the family to select indicators
 */

public class SurveyActivity extends AbstractFragSwitcherActivity
{
    static String FAMILY_ID_KEY = "FAMILY_ID";

    TextView mTvTitle;
    TextView mTvQuestionsLeft;
    TextView mTvNextUp;

    ImageButton mExitButton;

    ProgressBar mProgressBar;

    SurveyIndicatorsFragment surveyIndicatorsFragment;

    LinearLayout mHeader;
    RelativeLayout mFooter;

    //whether or not the current tablet is 7 inches
    private boolean mIs7Inch = false;

    @Inject
    InjectionViewModelFactory mViewModelFactory;

    SharedSurveyViewModel mSurveyViewModel;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        ((AdvisorApplication) this.getApplication())
                .getApplicationComponent()
                .inject(this);

        mSurveyViewModel = ViewModelProviders
                .of(this, mViewModelFactory)
                .get(SharedSurveyViewModel.class);

        mHeader = (LinearLayout) findViewById(R.id.survey_activity_header);
        mFooter = (RelativeLayout) findViewById(R.id.survey_activity_footer);

   	    mTvTitle = findViewById(R.id.tv_surveyactivity_title);
        mTvNextUp = findViewById(R.id.tv_surveyactivity_nextup);
        mTvQuestionsLeft = findViewById(R.id.tv_surveyactivity_questionsleft);

        mProgressBar = findViewById(R.id.progressbar_surveyactivity);
        mExitButton = findViewById(R.id.btn_surveyactivity_close);

        mExitButton.setOnClickListener((event)->
        {
            //someday save here
            if(mSurveyViewModel.getSurveyState().getValue()!=SurveyState.INTRO) {
                makeExitDialog().setConfirmClickListener((dialog) ->
                {
                    this.finish();
                    dialog.dismissWithAnimation();
                }).show();
            }
            else
            {
                this.finish();
            }
        });

        if(ScreenCalculations.is7InchTablet(getApplicationContext()))
        {
            mFooter.setVisibility(View.GONE);
            mIs7Inch = true;

            /**Maybe do more here??**/
        }
        else
        {
            mIs7Inch = false;
        }

        setFragmentContainer(R.id.survey_activity_fragment_container);
        initViewModel();
    }

    SweetAlertDialog makeExitDialog()
    {
       return new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(getString(R.string.surveyactivity_exit_confirmation))
                .setContentText(getString(R.string.surveyactivity_exit_explanation))
                .setCancelText(getString(R.string.all_cancel))
                .setConfirmText(getString(R.string.surveyactivity_discard_snapshot))
                .showCancelButton(true)
                .setCancelClickListener(SweetAlertDialog::cancel);
    }

    public void initViewModel()
    {
        //familyId can never equal -1 if retrieved from the database, so it is used as the default value
        int familyId = getIntent().getIntExtra(FAMILY_ID_KEY, -1);

        mSurveyViewModel.setFamily(familyId);


        //observe changes for family, when it has a value then show intro.
        mSurveyViewModel.getCurrentFamily().observe(this, (family ->
        {
            if(mSurveyViewModel.getSurveyState().getValue().equals(SurveyState.NONE))
            {
                mSurveyViewModel.getSurveyState().setValue(SurveyState.INTRO);
            }
        }));

        mSurveyViewModel.getProgress().observe(this, surveyProgress -> {

            ObjectAnimator progressAnimator = ObjectAnimator.ofInt(mProgressBar,
                    "progress", mProgressBar.getProgress(), surveyProgress.getPercentageComplete());

            progressAnimator.setDuration(400);
            progressAnimator.start();

            mProgressBar.setProgress(surveyProgress.getPercentageComplete());
            mTvQuestionsLeft.setText(setRemaining(surveyProgress.getRemaining(), surveyProgress.getSkipped()));
        });

        mSurveyViewModel.getSurveyState().observe(this, surveyState -> {
            Class<? extends AbstractSurveyFragment> nextFragment = null;

            switch (surveyState)
            {
                case NEW_FAMILY:
                    nextFragment = SurveyNewFamilyFrag.class;
                    break;

                case INTRO:
                    nextFragment = SurveyIntroFragment.class;
                    break;

                case ECONOMIC_QUESTIONS:
                    nextFragment = SurveyEconomicQuestionsFragment.class;
                    break;

                case INDICATORS:
                    nextFragment = SurveyIndicatorsFragment.class;
                    break;
                case SUMMARY:
                    nextFragment = SurveySummaryFragment.class;
                    break;
                case REVIEWINDICATORS:
                    nextFragment = SurveySummaryIndicatorsFragment.class;
                    break;
                case LIFEMAP:
                    nextFragment = SurveyChoosePrioritiesFragment.class;
                    break;

                case COMPLETE:
                    this.finish();
                    break;
            }

            if(nextFragment!=null) switchToSurveyFrag(nextFragment);
        });
    }

    @Override
    public void onBackPressed() {
        if(mSurveyViewModel.getSurveyState().getValue()!=null) {
            switch (mSurveyViewModel.getSurveyState().getValue()) {
                case INTRO:
                case NONE:
                {
                    super.onBackPressed();
                    break;
                }

                case NEW_FAMILY:
                case ECONOMIC_QUESTIONS: {
                    makeExitDialog().
                            setConfirmClickListener((dialog) ->
                            {
                                mSurveyViewModel.setSurveyState(SurveyState.INTRO);
                                dialog.dismiss();
                            })
                            .show();
                    break;
                }

                case INDICATORS: {
                    mSurveyViewModel.setSurveyState(SurveyState.ECONOMIC_QUESTIONS);
                    break;
                }
                case SUMMARY: {
                    mSurveyViewModel.setSurveyState(SurveyState.INDICATORS);
                    break;
                }
                case REVIEWINDICATORS:
                case REVIEWBACKGROUND: {
                    mSurveyViewModel.setSurveyState(SurveyState.SUMMARY);
                    break;
                }
            }
        }
    }

    void switchToSurveyFrag(Class<? extends AbstractSurveyFragment> fragmentClass)
    {
        super.switchToFrag(fragmentClass);

        AbstractSurveyFragment fragment = (AbstractSurveyFragment)getFragment(fragmentClass);

        if(fragment.getHeaderColor()!=0) {
            mHeader.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), fragment.getHeaderColor()));
        }

        if(fragment.getFooterColor()!=0) {
            mFooter.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), fragment.getFooterColor()));
        }

        mTvTitle.setText(fragment.getTitle());

        if(mIs7Inch || !fragment.isShowFooter())
        {
            mFooter.setVisibility(View.GONE);
        }
        else
        {
            mFooter.setVisibility(View.VISIBLE);
        }

        if(!fragment.isShowHeader())
        {
            mHeader.setVisibility(View.GONE);
        }
        else {
            mHeader.setVisibility(View.VISIBLE);
        }
    }

    private String setRemaining(int remaining, int skipped){

        if (skipped == -1){
            return remaining + " " + getString(R.string.survey_questionsremaining);
        }
        return (remaining + " " + getString(R.string.survey_questionsremaining) + ", "
            + skipped + " " + getString(R.string.survey_questionsskipped));
    }

    //Returns and intent to open this activity, with an extra for the family's Id.
    public static Intent build(Context c, Family family)
    {
        Intent intent = new Intent(c, SurveyActivity.class);
        intent.putExtra(FAMILY_ID_KEY, family.getId());

        return intent;
    }
}
