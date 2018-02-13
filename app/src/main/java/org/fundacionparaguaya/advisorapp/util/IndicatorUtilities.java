package org.fundacionparaguaya.advisorapp.util;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.view.View;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.models.Indicator;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.LifeMapPriority;

import java.util.*;

public class IndicatorUtilities {

    /**
     * Returns the responses from the map in a new array, sorted by color (from Red to Green)
     * @param theMap map of questions to responses
     * @return
     */
    public static List<IndicatorOption> getResponsesAscending(Collection<IndicatorOption> responses) {

        List<IndicatorOption> responseList = new ArrayList<>(responses);
        Collections.sort(responseList);

        return responseList;
    }

    /**
     *
     * @return index in priority list if indicator is a priority, -1 otherwise
     */
    public static int isPriority(Indicator indicator, List<LifeMapPriority> priorityList)
    {
        int index = -1;

        if(priorityList != null) {
            for (int i = 0; i < priorityList.size(); i++) {
                if (priorityList.get(i).getIndicator().equals(indicator)) {
                    index =i;
                    break;
                }
            }
        }

        return index;
    }

    /**
     * Sets a view to the color of the indicator option
     */
    public static void setViewColorFromResponse(IndicatorOption option, View v) {
        int color;

        switch (option.getLevel()) {
            case Red:
                color = R.color.indicator_card_red;
                break;

            case Yellow:
                color = R.color.indicator_card_yellow;
                break;

            case Green:
                color = R.color.indicator_card_green;
                break;

            default:
                color = -1;
                break;
        }

        if (color != -1) {
            ViewCompat.setBackgroundTintList(v, ContextCompat.getColorStateList(v.getContext(), color));
        }
    }
}
