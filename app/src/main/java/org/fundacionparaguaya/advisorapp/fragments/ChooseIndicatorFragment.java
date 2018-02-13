package org.fundacionparaguaya.advisorapp.fragments;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.SurveyIndicatorAdapter;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.IndicatorQuestion;
import org.fundacionparaguaya.advisorapp.viewcomponents.IndicatorCard;

/**
 *
 */

public class ChooseIndicatorFragment extends AbstractSurveyFragment {

    IndicatorCard mGreenCard;
    IndicatorCard mYellowCard;
    IndicatorCard mRedCard;

    IndicatorQuestion question;

    SurveyIndicatorsFragment parentFragment;

    SurveyIndicatorAdapter adapter;

    private static int clickDelay = 500;
    private static int clickDelayInterval = 100;

    @Nullable
    IndicatorCard selectedIndicatorCard;
    private CountDownTimer nextPageTimer;

    private IndicatorCard.IndicatorSelectedHandler handler = (card) ->
    {
        if (parentFragment.isPageChanged()) {
            onCardSelected(card);
        }
    };

    public ChooseIndicatorFragment newInstance(SurveyIndicatorAdapter adapter, IndicatorQuestion question) {

        ChooseIndicatorFragment fragment = new ChooseIndicatorFragment();
        this.adapter = adapter;
        this.question = question;

        parentFragment = (SurveyIndicatorsFragment) adapter.returnParent();

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chooseindicator, container, false);

        mGreenCard = (IndicatorCard) rootView.findViewById(R.id.indicatorcard_green);
        mYellowCard = (IndicatorCard) rootView.findViewById(R.id.indicatorcard_yellow);
        mRedCard = (IndicatorCard) rootView.findViewById(R.id.indicatorcard_red);

        for (IndicatorOption option : question.getOptions()) {
            switch (option.getLevel()) {
                case Green:
                    mGreenCard.setOption(option);
                    break;
                case Yellow:
                    mYellowCard.setOption(option);
                    break;
                case Red:
                    mRedCard.setOption(option);
                    break;
            }
        }

        IndicatorOption existingResponse = parentFragment.getResponses(question);

        if (existingResponse != null) {
            switch (existingResponse.getLevel()) {
                case Green:
                    mGreenCard.setSelected(true);
                    break;

                case Yellow:
                    mYellowCard.setSelected(true);
                    break;

                case Red:
                    mRedCard.setSelected(true);
                    break;
                default:
                    break;
            }
        }

        mGreenCard.addIndicatorSelectedHandler(handler);
        mYellowCard.addIndicatorSelectedHandler(handler);
        mRedCard.addIndicatorSelectedHandler(handler);
        return rootView;
    }

    /**
     * Sets the desired selected indicator option
     *
     * @param indicatorCard
     */
    private void onCardSelected(@Nullable IndicatorCard indicatorCard) {

        if (indicatorCard.equals(selectedIndicatorCard)) {
            indicatorCard.setSelected(false);
            parentFragment.removeIndicatorResponse(question);
            selectedIndicatorCard = null;
        } else {
            mRedCard.setSelected(mRedCard.equals(indicatorCard));
            mYellowCard.setSelected(mYellowCard.equals(indicatorCard));
            mGreenCard.setSelected(mGreenCard.equals(indicatorCard));

            parentFragment.addIndicatorResponse(question, indicatorCard.getOption());
            updateParent();

            selectedIndicatorCard = indicatorCard;
        }

    }

    public boolean isCardSelected() {
        if (selectedIndicatorCard == null) {
            return false;
        }
        return true;
    }

    private void updateParent() {
        if (nextPageTimer !=null){
            nextPageTimer.cancel();
            nextPageTimer = null;
        } else {
            nextPageTimer = new CountDownTimer(clickDelay, clickDelayInterval) {
                @Override
                public void onTick(long millisUntilFinished) {
                    //For future implementation if needed
                }

                @Override
                public void onFinish() {
                    if (selectedIndicatorCard != null) {
                        parentFragment.nextQuestion();
                    } else {
                        parentFragment.removeIndicatorResponse(question);
                    }
                }
            }.start();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
