package org.fundacionparaguaya.advisorapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.NavigationListener;
import org.fundacionparaguaya.advisorapp.fragments.callbacks.DisplayBackNavListener;

/**
 * This is a fragment that lives inside of a tab
 */

public abstract class AbstractTabbedFrag extends Fragment implements NavigationListener
{
    //for logging
    private static final String TAG = "AbstractTabbedFrag";

    private DisplayBackNavListener mDisplayBackNavListener;

    //view id the fragment will be placed in
    private int mContainerId;

    //was back nav required the last time we navigated
    boolean mWasBackNavRequired;

    static String HAS_BEEN_INIT_KEY = "HAS_BEEN_INITIALIZED";
    boolean mHasBeenInitialized = false;

    private String mTabTitle = "";

    public AbstractTabbedFrag()
    {

    }

    public void setTabTitle(String title)
    {
        mTabTitle = title;
    }

    public String getTabTitle()
    {
        return mTabTitle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getChildFragmentManager().addOnBackStackChangedListener(() ->
        {
            //if there was a change in whether or not we need back nav, notify our listeners
            if(mWasBackNavRequired!=isBackNavRequired())
            {
                //update our local variable to reflect the change
                mWasBackNavRequired = isBackNavRequired();

                if(mWasBackNavRequired) //if required
                {
                    mDisplayBackNavListener.onShowBackNav();
                }
                else //if not required
                {
                    mDisplayBackNavListener.onHideBackNav();
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboardtabbar, container, false);

        mContainerId = R.id.contraintlayout_dashboard_fragmentcontainer;
        return view;
    }

    /**
     * Sets the initial stacked frag for this tabbed frag
     * @param frag Fragment to set
     */
    public void setInitialFragment(AbstractStackedFrag frag) {
       makeFragmentTransaction(frag).commit();
    }

    /**
     * Navigate to a new fragment
     *
     * @param frag Fragment to navigate to
     */
    public void onNavigateNext(AbstractStackedFrag frag) {
        makeFragmentTransaction(frag).addToBackStack(null).commit();
    }

    /**
     * Muscle behind setInitialFragment and onNavigateNext
     * @param frag
     */
    private FragmentTransaction makeFragmentTransaction(AbstractStackedFrag frag) {

        FragmentTransaction ft = getChildFragmentManager().beginTransaction();

        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                R.anim.enter_from_left, R.anim.exit_to_right);
        
        ft.replace(mContainerId, frag);

        return ft;
    }

    /**
     * Navigate backwards in the stack
     */
    public void onNavigateBack() {
        getChildFragmentManager().popBackStack();
    }


    /**
     *
     * @return whether this fragment needs a button for back navigation
     */
    public boolean isBackNavRequired()
    {
        Log.d(TAG, "Back Stack Entry Count: " + getChildFragmentManager().getBackStackEntryCount());
        return getChildFragmentManager().getBackStackEntryCount() > 0;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(getChildFragmentManager().getFragments().size()==0)
        {
            this.setInitialFragment(this.makeInitialFragment());
            mHasBeenInitialized = true;
        }
    }

    protected abstract AbstractStackedFrag makeInitialFragment();

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);

        try
        {
            //if this is a nested fragment
            if(getParentFragment() != null)
            {
                mDisplayBackNavListener = (DisplayBackNavListener) getParentFragment();
            }
            else
            {
                //nested inside of an activity
                mDisplayBackNavListener = (DisplayBackNavListener) context;
            }
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException("Parent activity or fragment must implement ShowBackNavCallback");
        }
    }

    /**Saves whether or not this tab has already been initialized (and had first fragment set)
     *
     * @param outState
     */
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putBoolean(HAS_BEEN_INIT_KEY, mHasBeenInitialized);

        super.onSaveInstanceState(outState);
    }
}
