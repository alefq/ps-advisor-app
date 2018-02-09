package org.fundacionparaguaya.advisorapp.fragments;

import org.fundacionparaguaya.advisorapp.R;

/**
 * Fragment for settings tab
 */

public class SettingsTabFrag extends AbstractTabbedFrag
{
    @Override
    protected AbstractStackedFrag makeInitialFragment() {
        return UnderConstructionFragment.build(getResources().getString(R.string.dashboardtabbar_settingstitle));
    }
}
