package org.fundacionparaguaya.advisorapp.util;

import android.content.Context;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import org.fundacionparaguaya.advisorapp.BuildConfig;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by benhylak on 2/13/18.
 */

public class MixpanelHelper {
    private static String API_KEY = BuildConfig.MIXPANEL_API_KEY_STRING;

    private static MixpanelAPI getMixpanel(Context c)
    {
        return MixpanelAPI.getInstance(c, API_KEY);
    }
    public static class SurveyEvent
    {
        public static void startSurvey(Context c) {
            getMixpanel(c).timeEvent(SurveyEvent.class.getName());
        }

        public static void quitSurvey(Context c)
        {

        }

        public static void finishSurvey(Context c, String result, int skippedQuestions)
        {
            getMixpanel(c).track(SurveyEvent.class.getName(), buildSurveyProps(result, skippedQuestions));
        }

        private static JSONObject buildSurveyProps(String result, int skipped) {
            JSONObject props = new JSONObject();
            try {
                props.put("numSkipped", Integer.toString(skipped));
                props.put("result", result);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return props;
        }
    }

    public static class LoginEvent {
        public static void success(Context c) {
            getMixpanel(c).track(LoginEvent.class.getName(), buildLoginProps("success"));
        }

        public static void validationError(Context c) {
            getMixpanel(c).track(LoginEvent.class.getName(), buildLoginProps("validation_error"));
        }

        public static void unauthenticatedFail(Context c) {
            getMixpanel(c).track(LoginEvent.class.getName(), buildLoginProps("unauthenticated_fail"));
        }

        public static void unknownFail(Context c) {
            getMixpanel(c).track(LoginEvent.class.getName(), buildLoginProps("unknown_fail"));
        }

        private static JSONObject buildLoginProps(String result) {
            JSONObject props = new JSONObject();
            try {
                props.put("result", "fail");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return props;
        }
    }
}
