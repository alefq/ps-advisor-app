package org.fundacionparaguaya.advisorapp.viewmodels;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import org.fundacionparaguaya.advisorapp.data.remote.AuthenticationManager;
import org.fundacionparaguaya.advisorapp.repositories.FamilyRepository;
import org.fundacionparaguaya.advisorapp.repositories.SnapshotRepository;
import org.fundacionparaguaya.advisorapp.repositories.SurveyRepository;

/**
 * A custom view model factory which will inject view models with the correct dependencies.
 */

public class InjectionViewModelFactory implements ViewModelProvider.Factory {
    private final AuthenticationManager authManager;
    private final FamilyRepository familyRepository;
    private final SurveyRepository surveyRepository;
    private final SnapshotRepository snapshotRepository;

    public InjectionViewModelFactory(AuthenticationManager authManager,
                                     FamilyRepository familyRepository,
                                     SurveyRepository surveyRepository,
                                     SnapshotRepository snapshotRepository) {
        this.authManager = authManager;
        this.familyRepository = familyRepository;
        this.surveyRepository = surveyRepository;
        this.snapshotRepository = snapshotRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(AllFamiliesViewModel.class))
            return (T) new AllFamiliesViewModel(familyRepository);
        else if (modelClass.isAssignableFrom(LoginViewModel.class))
            return (T) new LoginViewModel(authManager);
        else if (modelClass.isAssignableFrom(FamilyInformationViewModel.class))
        {
            return (T) new FamilyInformationViewModel(familyRepository, snapshotRepository);
        }
        else if (modelClass.isAssignableFrom(SharedSurveyViewModel.class)) {
            return (T) new SharedSurveyViewModel(snapshotRepository, surveyRepository, familyRepository);
        }
        else if (modelClass.isAssignableFrom(AddFamilyViewModel.class)) {
            return (T) new AddFamilyViewModel(familyRepository);
        }
        else if (modelClass.isAssignableFrom(SettingsViewModel.class)) {
            return (T) new SettingsViewModel(authManager);
        }
        else
            throw new IllegalArgumentException("The view model was not found for " + modelClass.toString());
    }
}