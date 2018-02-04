package org.fundacionparaguaya.advisorapp.activities;

import android.animation.ObjectAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import android.support.v4.app.Fragment;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.instabug.library.Instabug;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;
import org.fundacionparaguaya.advisorapp.AdvisorApplication;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.fragments.AbstractSurveyFragment;
import org.fundacionparaguaya.advisorapp.fragments.SurveyIndicatorsFragment;
import org.fundacionparaguaya.advisorapp.fragments.SurveyQuestionsFrag;
import org.fundacionparaguaya.advisorapp.fragments.SurveyIntroFragment;
import org.fundacionparaguaya.advisorapp.fragments.SurveySummaryFragment;
import org.fundacionparaguaya.advisorapp.fragments.SurveySummaryIndicatorsFragment;
import org.fundacionparaguaya.advisorapp.models.Family;
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

        //only supported after Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.survey_darkyellow, this.getTheme()));
        }

        mExitButton.setOnClickListener((event)->
        {
            new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.surveyactivity_exit_confirmation))
                    .setContentText(getString(R.string.surveyactivity_exit_explanation))
                    .setCancelText(getString(R.string.all_cancel))
                    .setConfirmText(getString(R.string.surveyactivity_discard_snapshot))
                    .showCancelButton(true)
                    .setConfirmClickListener((dialog)->
                    {
                        this.finish();
                    })
                    .setCancelClickListener(SweetAlertDialog::cancel)
                    .show();
        });


        setFragmentContainer(R.id.survey_activity_fragment_container);
        initViewModel();
    }


    public void initViewModel()
    {
        //familyId can never equal -1 if retrieved from the database, so it is used as the default value
        int familyId = getIntent().getIntExtra(FAMILY_ID_KEY, -1);

        if(familyId == -1)
        {
            throw new IllegalArgumentException(this.getLocalClassName() + ": Found family id of -1. Family id is either not set" +
                    "or has been set innappropriately. To launch this activity with the family id properly set, use the " +
                    "build(int) function");
        }

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
            mTvQuestionsLeft.setText(surveyProgress.getDescription());
        });

        mSurveyViewModel.getSurveyState().observe(this, surveyState -> {
            Class<? extends AbstractSurveyFragment> nextFragment = null;

            switch (surveyState)
            {
                case INTRO:
                    nextFragment = SurveyIntroFragment.class;
                    break;

                case BACKGROUND_QUESTIONS:
                    nextFragment = SurveyQuestionsFrag.class;
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

                case COMPLETE:
                    this.finish();
            }

            if(nextFragment!=null) switchToSurveyFrag(nextFragment);
        });
    }

    void switchToSurveyFrag(Class<? extends AbstractSurveyFragment> fragmentClass)
    {
        super.switchToFrag(fragmentClass);

        AbstractSurveyFragment fragment = (AbstractSurveyFragment)getFragment(fragmentClass);
        mHeader.setBackgroundColor(getResources().getColor(fragment.getHeaderColor(), this.getTheme()));
        mFooter.setBackgroundColor(getResources().getColor(fragment.getFooterColor(), this.getTheme()));

        if(!fragment.isShowFooter())
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


    //Returns and intent to open this activity, with an extra for the family's Id.
    public static Intent build(Context c, Family family)
    {
        Intent intent = new Intent(c, SurveyActivity.class);
        intent.putExtra(FAMILY_ID_KEY, family.getId());

        return intent;
    }
}
