package org.fundacionparaguaya.advisorapp.adapters;

import android.net.Uri;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.models.Family;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Adapter for displaying a grid of family cards, with their picture, name, next visit and last visit.
 */

public class FamiliesAdapter extends RecyclerView.Adapter<FamiliesAdapter.FamilyViewHolder> {

    private List<? extends Family> mFamilyList;
    private ArrayList<FamilySelectedHandler> mFamilySelectedHandlers;

    public FamiliesAdapter(){
        mFamilySelectedHandlers = new ArrayList<FamilySelectedHandler>();
    }

    public void addFamilySelectedHandler(FamilySelectedHandler h){
        mFamilySelectedHandlers.add(h);
    }

    public void removeFamilySelectedHandler(FamilySelectedHandler h){
        mFamilySelectedHandlers.remove(h);
    }

    private void notifyFamilySelectedHandlers(FamilySelectedEvent e){
        for(FamilySelectedHandler handler: mFamilySelectedHandlers){
            handler.onFamilySelected(e);
        }
    }

    @Override
    public FamilyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_allfamiliescard, parent, false);
        return new FamilyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(FamilyViewHolder holder, int position) {

        final Family family = mFamilyList.get(position);

        holder.familyName.setText(family.getName());

        Uri uri = Uri.parse("https://bongmendoza.files.wordpress.com/2012/08/urban-poor-family.jpg");

        holder.imageView.setImageURI(uri);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyFamilySelectedHandlers(new FamilySelectedEvent(family));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFamilyList == null ? 0: mFamilyList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public void setFamilyList(final List<? extends Family> families){
        if(mFamilyList == null || mFamilyList.size() == 0){
            mFamilyList = families;
            notifyDataSetChanged();
        } else {
            /*DiffUtil class updates the list with the least number of update operations by comparing the old list
            * and the new list and calculating the differences between them and hence calculate the updates
            * needed in the RecyclerView*/
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mFamilyList.size();
                }

                @Override
                public int getNewListSize() {
                    return families.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mFamilyList.get(oldItemPosition)
                            .equals(families.get(newItemPosition));

                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Family newFamily = families.get(newItemPosition);
                    Family oldFamily = mFamilyList.get(oldItemPosition);
                    return newFamily == oldFamily
                            && Objects.equals(newFamily.getName(), oldFamily.getName());
                }
            });
            mFamilyList = families;
            result.dispatchUpdatesTo(this);
        }
    }


    public static class FamilyViewHolder extends RecyclerView.ViewHolder{
        CardView familyCard;
        TextView familyName;
        SimpleDraweeView imageView;

       /* static TextView nextVisitLabel;
        static TextView nextVisitDate;
        static TextView lastVisitLabel;
        static TextView lastVisitDate;*/

        FamilyViewHolder(View itemView){
            super(itemView);
            familyCard = (CardView) itemView.findViewById(R.id.card_view);
            familyName = (TextView) itemView.findViewById(R.id.family_name);
            imageView = (SimpleDraweeView)itemView.findViewById(R.id.family_image);
//            nextVisitLabel = (TextView) itemView.findViewById(R.id.next_visit);
//            nextVisitDate = (TextView) itemView.findViewById(R.id.next_visit_time);
//            lastVisitLabel = (TextView) itemView.findViewById(R.id.last_visit);
//            lastVisitDate = (TextView) itemView.findViewById(R.id.last_visit_time);
        }

    }

    /*FamilySelectedEventClass defines the event of selecting a family on the list of all families
    * tha includes a function that returns the selected family*/

    public class FamilySelectedEvent {

        private FamilySelectedHandler mFamilySelectedHandler;

        Family mFamilySelected;

        FamilySelectedEvent(Family f){
            mFamilySelected = f;
        }

        public Family getSelectedFamily(){
            return mFamilySelected;
        }
    }

    /*An event listener that will be called when the FamilySelectedEvent is triggered when one
    * family on the AllFamilies list is selected*/
    public interface FamilySelectedHandler{
        void onFamilySelected(FamilySelectedEvent e);
    }
}
