package org.fundacionparaguaya.advisorapp.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import android.view.MotionEvent;
import com.instabug.library.InstabugTrackingDelegate;
import org.fundacionparaguaya.advisorapp.R;
import org.fundacionparaguaya.advisorapp.fragments.LoginFragment;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String LOGIN_FRAG_TAG = "LOGIN_FRAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FragmentManager manager = getSupportFragmentManager();
        LoginFragment loginFragment = (LoginFragment) manager.findFragmentByTag(LOGIN_FRAG_TAG);

        if (loginFragment == null)
            loginFragment = new LoginFragment();

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.login_root, loginFragment, LOGIN_FRAG_TAG);
        transaction.commit();
    }

    //Enables user step capturing from instabug
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        InstabugTrackingDelegate.notifyActivityGotTouchEvent(ev, this);
        return super.dispatchTouchEvent(ev);
    }

}

