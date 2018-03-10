package org.fundacionparaguaya.advisorapp.viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.fundacionparaguaya.advisorapp.models.Family;
import org.fundacionparaguaya.advisorapp.models.Snapshot;
import org.fundacionparaguaya.advisorapp.models.Survey;
import org.fundacionparaguaya.advisorapp.repositories.FamilyRepository;
import org.fundacionparaguaya.advisorapp.repositories.SurveyRepository;

/**
 * The view model for the resume snapshot popup window.
 */

public class ResumeSnapshotPopupViewModel extends ViewModel {
    private SurveyRepository mSurveyRepository;
    private FamilyRepository mFamilyRepository;

    private MutableLiveData<Snapshot> mSnapshot;
    private MediatorLiveData<Survey> mSurvey;
    private LiveData<Survey> mSurveySource;
    private MediatorLiveData<Family> mFamily;
    private LiveData<Family> mFamilySource;


    ResumeSnapshotPopupViewModel(SurveyRepository surveyRepository,
                                        FamilyRepository familyRepository) {
        mSurveyRepository = surveyRepository;
        mFamilyRepository = familyRepository;

        mSnapshot = new MutableLiveData<>();
        mSurvey = new MediatorLiveData<>();
        mFamily = new MediatorLiveData<>();
    }

    /**
     * Sets the snapshot that will be considered that is in progress.
     */
    public void setSnapshot(@NonNull Snapshot snapshot) {
        mSnapshot.setValue(snapshot);

        LiveData<Survey> newSurveySource = mSurveyRepository.getSurvey(snapshot.getSurveyId());
        replaceSource(mSurvey, mSurveySource, newSurveySource);
        mSurveySource = newSurveySource;

        // get the family if this snapshot has one, or replace with a fake source otherwise
        Integer familyId = snapshot.getFamilyId();
        LiveData<Family> newFamilySource;
        if (familyId != null) {
            newFamilySource = mFamilyRepository.getFamily(familyId);
        } else {
            newFamilySource = mFamilyRepository.getFamily(-1);
        }
        replaceSource(mFamily, mFamilySource, newFamilySource);
        mFamilySource = newFamilySource;
    }

    public @Nullable Snapshot getSnapshot() {
        return mSnapshot.getValue();
    }

    public LiveData<Snapshot> snapshot() {
        return mSnapshot;
    }

    public LiveData<Survey> survey() {
        return mSurvey;
    }

    public LiveData<Family> family() {
        return mFamily;
    }

    private <T> void  replaceSource(@NonNull MediatorLiveData<T> mediator,
                                    LiveData<T> oldSource,
                                    LiveData<T> newSource) {
        if (oldSource != null) {
            mediator.removeSource(oldSource);
        }
        if (newSource != null) {
            mediator.addSource(newSource, mediator::setValue);
        }
    }

}
