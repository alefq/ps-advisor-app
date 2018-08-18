package org.fundacionparaguaya.adviserplatform.data.testing;

import android.support.annotation.NonNull;

import org.fundacionparaguaya.adviserplatform.data.model.BackgroundQuestion;
import org.fundacionparaguaya.adviserplatform.data.model.Family;
import org.fundacionparaguaya.adviserplatform.data.model.IndicatorQuestion;
import org.fundacionparaguaya.adviserplatform.data.model.LifeMapPriority;
import org.fundacionparaguaya.adviserplatform.data.model.Snapshot;
import org.fundacionparaguaya.adviserplatform.data.model.Survey;
import org.fundacionparaguaya.adviserplatform.ui.families.AllFamiliesViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class SnapshotGenerator {

    Snapshot snapshot = null;
    private static  AtomicBoolean isAlive = new AtomicBoolean();
    private static Integer COUNT = 0;

    public void generateSnapshot(AllFamiliesViewModel model) {
        List<Family> families = model.getmFamilyRepository().getFamiliesNow();

        int startValue = (int) (System.currentTimeMillis() % 100000l);
        if(!families.isEmpty()) {
            startValue += getStartValue(families.get(families.size() - 1).getName());
        }

        for (int loopValue = startValue + 1; loopValue < 101 + startValue; loopValue++) {
            List<Survey> surveys = model.getSurveyRepository().getSurveysNow();
            Survey testSurvey = surveys.get(0);

            List<BackgroundQuestion> personalQuestions = testSurvey.getPersonalQuestions();
            List<BackgroundQuestion> economicalQuestions = testSurvey.getEconomicQuestions();
            List<IndicatorQuestion> indicatorQuestions = testSurvey.getIndicatorQuestions();


            /**
             * Respuestas personales
             * Map<BackgoundQuestion, String>
             *    Mail: xxx@xxx.com
             *    Tipo de documento: DUI
             *    Genero: M
             *    Nombre:
             *    Apellido:
             *    Numero de teléfono: 1234
             *    Pais de nacimiento: PY
             *    Fecha de nacimiento: 2000-05-05
             *    Numero de documento: 123456*/


            //Obtenemos el último numero identificador de una familia y le sumamos 1
            List<String> respuestasP = Arrays.asList("DUI", String.valueOf(loopValue),
                    Integer.toString(loopValue), "familia",
                    getUniqueBirthday(), "PY", "M", "603336", "M");

            /**
             * Crear la familia a la cual se le asignará el nuevo snapshot a partir de las respuestas
             * personales*/
            snapshot = new Snapshot(testSurvey);
            fillBackgroundQuestions(personalQuestions, respuestasP);

            /**
             * Respuestas económicas
             * Ingresos:999
             * Ingreso 999
             * Durante 5 años: YES
             * Location: -1.2499999989756816E-5,1.953125000397904E-5
             * Miembros de la familia: 4
             * Viviendo: OWN-TITLE
             * MONEDA: AFN
             * Discapacidad: Intelectual
             * Movilidad propia: AUTOMOVIL
             * Actividad principal: ENSEÑANZA
             * Ingreso mensual: 99999
             * Zona: URBANA
             * Estudios: COMPLETED-PRIMARY
             * */

            List<String> respuestasE = Arrays.asList("COMPLETED-PRIMARY", "4", "URBANA",
                    "ENSEÑANZA", "AFN", "9999", "9999", "9999", "OWN_TITLE", "Intelectual",
                    "YES", "AUTOMOVIL", "-1.2499999989756816E-5,1.953125000397904E-5");

            fillBackgroundQuestions(economicalQuestions, respuestasE);
            fillIndicatorQuestions(indicatorQuestions);

            snapshot.setPriorities(fillPriorities(indicatorQuestions));

            snapshot.setInProgress(false);

            model.getSnapshotRepository().saveSnapshot(snapshot);
        }


        model.getSnapshotRepository().forceNextSync();
    }

    @NonNull
    private static String getUniqueBirthday() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        calendar.set(Calendar.YEAR, 1990);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH, 30);
        calendar.add(Calendar.DAY_OF_MONTH, COUNT*-1);
        COUNT+=1;
        return dateFormat.format(calendar.getTime());
    }

    private void fillBackgroundQuestions(List<BackgroundQuestion> questions, List<String> answersList) {
        Map<BackgroundQuestion, String> backgroundAnswers = new HashMap<>();
        for (int i = 0; i < questions.size(); i++) {
            backgroundAnswers.put(questions.get(i), answersList.get(i));
            snapshot.response(questions.get(i), answersList.get(i));
        }
    }

    private void fillIndicatorQuestions(List<IndicatorQuestion> questions) {
        /**
         * 50 indicadores, se alterna entre opción 123123*/
        List<Integer> respuestasI = Arrays.asList(
                0, 1, 2, 0, 1,
                0, 1, 2, 0, 1,
                0, 1, 2, 0, 1,
                0, 1, 2, 0, 1,
                0, 1, 2, 0, 1,
                0, 1, 2, 0, 1,
                0, 1, 2, 0, 1,
                0, 1, 2, 0, 1,
                0, 1, 2, 0, 1,
                0, 1, 2, 0, 1);

        for (int i = 0; i < questions.size(); i++) {
            snapshot.response(questions.get(i), questions.get(i).getOptions().get(respuestasI.get(i)));
        }
    }

    private List<LifeMapPriority> fillPriorities(List<IndicatorQuestion> indicator) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, 30);

        List<LifeMapPriority> priorities = new ArrayList<>();


        priorities.add(new LifeMapPriority(indicator.get(4).getIndicator(), "XXX", "XXX", c.getTime(), false));
        priorities.add(new LifeMapPriority(indicator.get(6).getIndicator(), "XXX", "XXX", c.getTime(), false));
        priorities.add(new LifeMapPriority(indicator.get(9).getIndicator(), "XXX", "XXX", c.getTime(), false));

        return priorities;
    }

    private int getStartValue(String familyName) {
        return Integer.parseInt(familyName.split(" ")[0]);
    }
}
