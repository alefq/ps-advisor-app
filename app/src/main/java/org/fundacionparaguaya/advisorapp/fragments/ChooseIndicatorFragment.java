package org.fundacionparaguaya.advisorapp.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.adapters.IndicatorAdapter;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.IndicatorQuestion;
import org.fundacionparaguaya.advisorapp.viewcomponents.IndicatorCard;

/**
 *
 */

public class ChooseIndicatorFragment extends AbstractSurveyFragment implements View.OnClickListener{

    IndicatorCard mGreenCard;
    IndicatorCard mYellowCard;
    IndicatorCard mRedCard;

    IndicatorQuestion question;

    SurveyIndicatorsFragment parentFragment;

    IndicatorAdapter adapter;

    @Nullable
    IndicatorCard selectedIndicatorCard;

    public ChooseIndicatorFragment newInstance(IndicatorAdapter adapter, IndicatorQuestion question) {

        ChooseIndicatorFragment fragment = new ChooseIndicatorFragment();
        this.adapter = adapter;
        this.question = question;

        parentFragment = (SurveyIndicatorsFragment) adapter.returnParent();

        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_indicatorcardchooser, container, false);

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

        if(existingResponse!=null)
        {
            switch (existingResponse.getLevel())
            {
                case Green:
                    mGreenCard.setSelected(true);
                    break;

                case Yellow:
                    mYellowCard.setSelected(true);
                    break;

                case Red:
                    mRedCard.setSelected(true);
                    break;
            }
        }

        mGreenCard.setOnClickListener(this);
        mYellowCard.setOnClickListener(this);
        mRedCard.setOnClickListener(this);

        return rootView;
    }

    /**
     * When one of the cards is selected...
     * @param view IndicatorCard
     */
    @Override
    public void onClick(View view) {
        if(view instanceof IndicatorCard)
        {
            IndicatorCard card = (IndicatorCard)view;

            onCardSelected(card);
        }
    }

    /**
     * Sets the desired selected indicator option
     *
     * @param indicatorCard
     */
    private void onCardSelected(@Nullable IndicatorCard indicatorCard) {

        if(indicatorCard.equals(selectedIndicatorCard))
        {
            indicatorCard.setSelected(false);
            parentFragment.removeIndicatorResponse(question);
            selectedIndicatorCard = null;
        }
        else
        {
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

        if (parentFragment != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (selectedIndicatorCard != null) {
                        parentFragment.nextQuestion();
                    } else {
                        parentFragment.removeIndicatorResponse(question);
                    }
                }
            }, 500); // Millisecond 1000 = 1 sec
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
