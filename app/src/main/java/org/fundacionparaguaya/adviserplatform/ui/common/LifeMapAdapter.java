package org.fundacionparaguaya.adviserplatform.ui.common;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.fundacionparaguaya.assistantadvisor.R;
import org.fundacionparaguaya.adviserplatform.data.model.IndicatorOption;
import org.fundacionparaguaya.adviserplatform.data.model.LifeMapPriority;
import org.fundacionparaguaya.adviserplatform.util.IndicatorUtilities;

import java.util.Collection;
import java.util.List;

/**
 * Adapter for the indicators in a life map
 */

public class LifeMapAdapter extends RecyclerView.Adapter {
    private List<IndicatorOption> mResponses = null;
    private List<LifeMapPriority> mPriorities = null;
    private LifeMapFragmentCallback mClickHandler;

    public void setIndicators(Collection<IndicatorOption> responses) {
        if (responses != null) {
            mResponses = IndicatorUtilities.getResponses(responses);
        }

        notifyDataSetChanged();
    }

    public void setClickHandler(LifeMapFragmentCallback handler) {
        mClickHandler = handler;
    }


    public void setPriorities(List<LifeMapPriority> priorities) {
        mPriorities = priorities;
        //should do diff here

        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lifemapindicator, parent, false);
        LifeMapIndicatorViewHolder holder = new LifeMapIndicatorViewHolder(view);
        view.setOnClickListener(l -> mClickHandler.onLifeMapIndicatorClicked(new LifeMapIndicatorClickedEvent(holder)));

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LifeMapIndicatorViewHolder lifeMapHolder = ((LifeMapIndicatorViewHolder) holder);

        IndicatorOption response = mResponses.get(position);

        int priorityIndex = IndicatorUtilities.isPriority(response.getIndicator(), mPriorities);

        if (priorityIndex != -1) {
            lifeMapHolder.setAsPriority(response, mPriorities.get(priorityIndex), priorityIndex);
        }
        else {
            lifeMapHolder.setResponse(response);
        }
    }


    @Override
    public int getItemCount() {
        if (mResponses == null) {
            return 0;
        }
        else {
            return mResponses.size();
        }
    }

    public static class LifeMapIndicatorClickedEvent {
        IndicatorOption mOption;
        LifeMapPriority mPriority;

        public LifeMapIndicatorClickedEvent(LifeMapIndicatorViewHolder vh) {
            mOption = vh.mResponse;
            mPriority = vh.mLifeMapPriority;
        }

        public IndicatorOption getIndicatorOption() {
            return mOption;
        }

        public LifeMapPriority getPriority() {
            return mPriority;
        }
    }


    public static class LifeMapIndicatorViewHolder extends RecyclerView.ViewHolder
    {
        private AppCompatImageView mColor;
        private TextView mTitle;
        private TextView mNumber;

        protected IndicatorOption mResponse;
        protected LifeMapPriority mLifeMapPriority = null;

        public LifeMapIndicatorViewHolder(View itemView) {
            super(itemView);

            mColor = itemView.findViewById(R.id.iv_indicatoritem_color);
            mTitle = itemView.findViewById(R.id.tv_lifemapindicator_name);
            mNumber =itemView.findViewById(R.id.tv_lifemapindicator_number);

            mNumber.setVisibility(View.INVISIBLE);
            itemView.setBackground(null);
        }
        public void setSelected()
        {
            itemView.setBackground(ContextCompat.getDrawable(itemView.getContext(),
                    R.drawable.lifemapindicator_background));
            mTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.colorPrimary));
        }

        public void setUnselected()
        {
            itemView.setBackground(null);
            mTitle.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.app_gray));
        }
        /**
         * Changes the background of this view holder to the selected state
         * and displays the priority order
         *
         * @param number The ordering of the priority (1-5)
         */
        public void setAsPriority(IndicatorOption response, LifeMapPriority priority, int number)
        {
            setResponse(response);
            setSelected();

            mLifeMapPriority = priority;
        }

        /**
         * Sets the IndicatorOption associated with the priority. Note that this function must be called BEFORE
         * settings the Priority.
         * @param response family response for the indicator associated with priority
         */
        public void setResponse(IndicatorOption response) {
            mResponse = response;

            setUnselected();
            IndicatorUtilities.setViewColorFromResponse(response, mColor);

            String title = response.getIndicator().getTitle();
            mTitle.setText(title);
        }
    }
}