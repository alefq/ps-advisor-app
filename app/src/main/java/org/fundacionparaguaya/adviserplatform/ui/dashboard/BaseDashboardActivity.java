import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.transition.TransitionManager;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import org.fundacionparaguaya.adviserplatform.data.model.Snapshot;
import org.fundacionparaguaya.adviserplatform.data.remote.AuthenticationManager;
import org.fundacionparaguaya.adviserplatform.data.repositories.SyncManager;
import org.fundacionparaguaya.adviserplatform.injection.InjectionViewModelFactory;
import org.fundacionparaguaya.adviserplatform.ui.base.AbstractTabbedFrag;
import org.fundacionparaguaya.adviserplatform.ui.base.DisplayBackNavListener;
import org.fundacionparaguaya.adviserplatform.ui.common.AbstractFragSwitcherActivity;
import org.fundacionparaguaya.adviserplatform.util.AppConstants;
import javax.inject.Inject;

public abstract class BaseDashboardActivity extends AbstractFragSwitcherActivity implements DisplayBackNavListener {

    TextView mSyncLabel;
    LinearLayout mSyncArea;
    ImageView mSyncButtonIcon;
    RelativeTimeTextView mLastSyncTextView;
    TextView mTvTabTitle;
    TextView mTvBackLabel;
    LinearLayout mBackButton;
    
    @Inject
    SyncManager mSyncManager;
    @Inject
    AuthenticationManager mAuthManager;
    @Inject
    InjectionViewModelFactory mViewModelFactory;

    @Override
    public void onBackPressed() {
        // Implementation of back navigation
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Common initialization code here...
    }

    protected void setSyncStatus(boolean b, int icon, int circle) {
        // Implementation of sync status setup...
    }

    @Override
    public void onShowBackNav() {
        // Implementation of showing back navigation...
    }

    @Override
    public void onHideBackNav() {
        // Implementation of hiding back navigation...
    }

    public void setSyncLabel(Integer id, final long value, final long total) {
        // Implementation to set sync label...
    }

    protected void snapshotsRemainingToSync() {
        // Implementation of snapshots remaining to sync...
    }

    protected void validateStorageCapacity() {
        // Implementation of storage capacity validation...
    }
}
