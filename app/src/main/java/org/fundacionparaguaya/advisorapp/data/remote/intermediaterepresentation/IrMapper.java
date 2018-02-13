package org.fundacionparaguaya.advisorapp.data.remote.intermediaterepresentation;

import org.fundacionparaguaya.advisorapp.models.BackgroundQuestion;
import org.fundacionparaguaya.advisorapp.models.Family;
import org.fundacionparaguaya.advisorapp.models.FamilyMember;
import org.fundacionparaguaya.advisorapp.models.Indicator;
import org.fundacionparaguaya.advisorapp.models.IndicatorOption;
import org.fundacionparaguaya.advisorapp.models.IndicatorQuestion;
import org.fundacionparaguaya.advisorapp.models.LifeMapPriority;
import org.fundacionparaguaya.advisorapp.models.Login;
import org.fundacionparaguaya.advisorapp.models.ResponseType;
import org.fundacionparaguaya.advisorapp.models.Snapshot;
import org.fundacionparaguaya.advisorapp.models.Survey;

import java.text.CharacterIterator;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static org.fundacionparaguaya.advisorapp.models.BackgroundQuestion.QuestionType.ECONOMIC;
import static org.fundacionparaguaya.advisorapp.models.BackgroundQuestion.QuestionType.PERSONAL;
import static org.fundacionparaguaya.advisorapp.models.IndicatorOption.Level.Green;
import static org.fundacionparaguaya.advisorapp.models.IndicatorOption.Level.None;
import static org.fundacionparaguaya.advisorapp.models.IndicatorOption.Level.Red;
import static org.fundacionparaguaya.advisorapp.models.IndicatorOption.Level.Yellow;

/**
 * A utility for mapping IR objects to their corresponding model objects, and vice versa.
 */

public class IrMapper {

    //region Login
    public static Login mapLogin(LoginIr ir) {
        return new Login(ir.accessToken, ir.tokenType, ir.expiresIn, ir.refreshToken);
    }
    //endregion Login

    //region Family
    public static List<Family> mapFamilies(List<FamilyIr> ir) {
        List<Family> families = new ArrayList<>(ir.size());
        for (FamilyIr familyIr : ir) {
            families.add(mapFamily(familyIr));
        }
        return families;
    }

    public static FamilyIr mapFamily(Family family) {
        FamilyIr ir = new FamilyIr();
        ir.id = family.getRemoteId() != null ? family.getRemoteId() : -1;
        ir.name = family.getName();
        ir.code = family.getCode();
        ir.active = family.isActive();
        return ir;
    }

    public static Family mapFamily(FamilyIr ir) {
        return Family.builder()
                .remoteId(ir.id)
                .name(ir.name)
                .code(ir.code)
                .member(mapFamilyMember(ir.member))
                .build();
    }

    private static FamilyMember mapFamilyMember(FamilyMemberIr ir) {
        if (ir == null) return null;

        return FamilyMember.builder()
                .firstName(ir.firstName)
                .lastName(ir.lastName)
                .birthdate(ir.birthdate)
                .phoneNumber(ir.phoneNumber)
                .identificationType(ir.identificationType)
                .identificationNumber(ir.identificationNumber)
                .gender(ir.gender)
                .profileUrl(ir.profileUrl)
                .build();
    }
    //endregion

    //region Survey
    public static List<Survey> mapSurveys(List<SurveyIr> ir) {
        List<Survey> surveys = new ArrayList<>(ir.size());
        for (SurveyIr surveyIr : ir) {
            surveys.add(mapSurvey(surveyIr));
        }
        return surveys;
    }

    private static Survey mapSurvey(SurveyIr ir) {
        return Survey.builder()
                .remoteId(ir.id)
                .title(ir.title)
                .description(ir.description)
                .personalQuestions(mapPersonal(ir))
                .economicQuestions(mapEconomic(ir))
                .indicatorQuestions(mapIndicator(ir))
                .build();
    }

    private static List<BackgroundQuestion> mapPersonal(SurveyIr ir) {
        return mapBackground(PERSONAL, ir);
    }

    private static List<BackgroundQuestion> mapEconomic(SurveyIr ir) {
        return mapBackground(ECONOMIC, ir);
    }

    private static List<BackgroundQuestion> mapBackground(
            BackgroundQuestion.QuestionType type, SurveyIr ir) {

        List<String> names = type == PERSONAL ?
                ir.uiSchema.personalQuestions : ir.uiSchema.economicQuestions;
        List<BackgroundQuestion> questions = new ArrayList<>();
        for (String name : names) {
            SurveyQuestionIr questionIr = ir.schema.questions.get(name);
            questions.add(new BackgroundQuestion(
                    name,
                    questionIr.title.get("es"),
                    ir.schema.requiredQuestions.contains(name),
                    mapResponseType(questionIr, ir.uiSchema.customFields.get(name)),
                    type,
                    mapBackgroundOptions(questionIr.optionNames, questionIr.options)));
        }
        return questions;
    }

    private static List<IndicatorQuestion> mapIndicator(SurveyIr ir) {
        List<IndicatorQuestion> questions = new ArrayList<>();
        for (String name : ir.uiSchema.indicatorQuestions) {
            SurveyQuestionIr questionIr = ir.schema.questions.get(name);

            Indicator indicator = new Indicator(name, questionIr.title.get("es"));
            List<IndicatorOption> options = new ArrayList<>();
            for (IndicatorOptionIr optionIr : questionIr.indicatorOptions.values) {
                options.add(new IndicatorOption(
                        optionIr.description,
                        optionIr.url,
                        mapOptionLevel(optionIr.value)
                ));
            }
            indicator.setOptions(options);

            boolean required = ir.schema.requiredQuestions.contains(name);
            questions.add(new IndicatorQuestion(indicator, required));
        }
        return questions;
    }

    private static Map<String, String> mapBackgroundOptions(List<String> names, List<String> values) {
        if (names == null || values == null) {
            return null;
        }
        HashMap<String, String> options = new HashMap<>(values.size());
        for (int i = 0; i < values.size(); i++) {
            options.put(names.get(i), values.get(i));
        }
        return options;
    }

    private static ResponseType mapResponseType(SurveyQuestionIr ir,
                                                SurveyCustomFieldIr fieldIr) {
        switch (ir.type) {
            case "string":
                if (ir.format != null && ir.format.equals("date")) {
                    return ResponseType.DATE;
                } else if (fieldIr != null
                        && "gmap".equals(fieldIr.field)) {
                    return ResponseType.LOCATION;
                }
                return ResponseType.STRING;
            case "number":
                return ResponseType.INTEGER;
            case "integer":
                return ResponseType.INTEGER;
            default:
                throw new IllegalArgumentException("Response type not known!");
        }
    }

    private static IndicatorOption.Level mapOptionLevel(String ir) {
        switch (ir) {
            case "GREEN":
                return Green;
            case "YELLOW":
                return Yellow;
            case "RED":
                return Red;
            default:
                return None;
        }
    }
    //endregion

    //region Snapshot
    public static List<Snapshot> mapSnapshots(List<SnapshotIr> ir,
                                              List<SnapshotOverviewIr> overviewIrs,
                                              Family family,
                                              Survey survey) {
        List<Snapshot> snapshots = new ArrayList<>(ir.size());
        for (SnapshotIr snapshotIr : ir) {
            SnapshotOverviewIr overviewIr = findOverview(snapshotIr, overviewIrs);
            snapshots.add(mapSnapshot(snapshotIr, overviewIr.priorities, family, survey));
        }
        return snapshots;
    }

    private static SnapshotOverviewIr findOverview(SnapshotIr snapshotIr,
                                            List<SnapshotOverviewIr> overviewIrs) {
        for (SnapshotOverviewIr overviewIr : overviewIrs) {
            if (overviewIr.snapshotId == snapshotIr.id) {
                return overviewIr;
            }
        }
        throw new IllegalStateException(
                "Couldn't find the overview for snapshot " + snapshotIr.id + "!");
    }

    public static Snapshot mapSnapshot(SnapshotIr ir, List<PriorityIr> priorityIrs,
                                       Family family, Survey survey) {
        return new Snapshot(
                0,
                ir.id,
                family.getId(),
                survey.getId(),
                mapPersonalResponses(ir, survey),
                mapEconomicResponses(ir, survey),
                mapIndicatorResponses(ir, survey),
                mapPriorities(priorityIrs, survey),
                mapDateTime(ir.createdAt));
    }

    public static SnapshotIr mapSnapshot(Snapshot snapshot, Survey survey) {
        SnapshotIr ir = new SnapshotIr();
        ir.id = snapshot.getRemoteId() != null ? snapshot.getRemoteId() : -1;
        ir.surveyId = survey.getRemoteId();
        ir.personalResponses = mapBackgroundResponses(snapshot.getPersonalResponses());
        ir.economicResponses = mapBackgroundResponses(snapshot.getEconomicResponses());
        ir.indicatorResponses = mapIndicatorResponses(snapshot.getIndicatorResponses());
        return ir;
    }

    public static List<PriorityIr> mapPriorities(Snapshot snapshot) {
        List<PriorityIr> ir = new ArrayList<>(snapshot.getPriorities().size());
        for (LifeMapPriority priority : snapshot.getPriorities()) {
            ir.add(mapPriority(priority, snapshot));
        }
        return ir;
    }

    private static PriorityIr mapPriority(LifeMapPriority priority, Snapshot snapshot) {
        PriorityIr ir = new PriorityIr();
        ir.indicatorTitle = mapIndicatorName(priority.getIndicator());
        ir.snapshotId = snapshot.getRemoteId();
        ir.reason = priority.getReason();
        ir.action = priority.getAction();
        ir.estimatedDate = mapDate(priority.getEstimatedDate());
        return ir;
    }

    private static Map<String, Object> mapBackgroundResponses(Map<BackgroundQuestion, String> responses) {
        Map<String, Object> ir = new HashMap<>();
        for (BackgroundQuestion question : responses.keySet()) {
            Object response = responses.get(question);
            if (question.getResponseType() == ResponseType.INTEGER) {
                response = Integer.parseInt((String) response);
            }
            ir.put(question.getName(), response);
        }
        return ir;
    }

    private static Map<String, String> mapIndicatorResponses(Map<IndicatorQuestion, IndicatorOption> responses) {
        Map<String, String> ir = new HashMap<>();
        for (IndicatorQuestion question : responses.keySet()) {
            ir.put(question.getName(), mapIndicatorOptionLevel(responses.get(question).getLevel()));
        }
        return ir;
    }

    private static Map<BackgroundQuestion, String> mapPersonalResponses(SnapshotIr ir, Survey survey) {
        Map<BackgroundQuestion, String> responses = new HashMap<>();
        if (ir.personalResponses == null) return responses;
        for (String question : ir.personalResponses.keySet()) {
            responses.put(getBackgroundQuestion(survey.getPersonalQuestions(), question), mapBackgroundResponseValue(ir.personalResponses.get(question)));
        }
        return responses;
    }

    private static Map<BackgroundQuestion, String> mapEconomicResponses(SnapshotIr ir, Survey survey) {
        Map<BackgroundQuestion, String> responses = new HashMap<>();
        for (String question : ir.economicResponses.keySet()) {
            responses.put(getBackgroundQuestion(survey.getEconomicQuestions(), question), mapBackgroundResponseValue(ir.economicResponses.get(question)));
        }
        return responses;

    }

    private static String mapBackgroundResponseValue(Object ir) {
        if (ir == null) return null;
        return ir.toString();
    }

    private static Map<IndicatorQuestion, IndicatorOption> mapIndicatorResponses(SnapshotIr ir, Survey survey) {
        Map<IndicatorQuestion, IndicatorOption> responses = new HashMap<>();
        for (String question : ir.indicatorResponses.keySet()) {
            IndicatorQuestion indicatorQuestion =
                    getIndicatorQuestion(survey.getIndicatorQuestions(), question);
            responses.put(indicatorQuestion,
                    getIndicatorOption(indicatorQuestion.getOptions(), mapIndicatorOptionLevel(ir.indicatorResponses.get(question))));
        }
        return responses;
    }

    private static List<LifeMapPriority> mapPriorities(List<PriorityIr> ir, Survey survey) {
        List<LifeMapPriority> priorities = new ArrayList<>(ir.size());
        for (PriorityIr priorityIr : ir) {
            priorities.add(mapPriority(priorityIr, survey));
        }
        return priorities;
    }

    private static LifeMapPriority mapPriority(PriorityIr ir, Survey survey) {
        IndicatorQuestion question = getIndicatorQuestion(survey.getIndicatorQuestions(),
                mapIndicatorName(ir.indicatorTitle));
        return LifeMapPriority.builder()
                .indicator(question.getIndicator())
                .reason(ir.reason)
                .action(ir.action)
                .estimatedDate(mapDate(ir.estimatedDate))
                .build();
    }

    private static BackgroundQuestion getBackgroundQuestion(List<BackgroundQuestion> questions, String name) {
        for (BackgroundQuestion question : questions) {
            if (question.getName().equals(name))
                return question;
        }
        return null;
    }

    private static IndicatorQuestion getIndicatorQuestion(List<IndicatorQuestion> questions, String name) {
        for (IndicatorQuestion question : questions) {
            if (question.getName().equals(name))
                return question;
        }
        throw new UnsupportedOperationException("Could not find a matching indicator question for " + name + "!");
    }

    private static IndicatorOption getIndicatorOption(List<IndicatorOption> indicatorOptions, IndicatorOption.Level level) {
        for (IndicatorOption option : indicatorOptions) {
            if (option.getLevel() == level)
                return option;
        }
        return null;
    }

    private static Date mapDateTime(String ir) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
        df.setTimeZone(tz);
        try {
            return df.parse(ir);
        } catch (ParseException e) {
            return null;
        }
    }

    private static Date mapDate(String ir) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        df.setTimeZone(tz);
        try {
            return df.parse(ir);
        } catch (ParseException e) {
            return null;
        }
    }

    private static String mapDate(Date date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
        df.setTimeZone(tz);
        return df.format(date);
    }

    private static IndicatorOption.Level mapIndicatorOptionLevel(String level) {
        switch (level.toLowerCase()) {
            case "red":
                return Red;
            case "yellow":
                return Yellow;
            case "green":
                return Green;
            default:
                return None;
        }
    }

    private static String mapIndicatorOptionLevel(IndicatorOption.Level level) {
        switch (level) {
            case Red:
                return "RED";
            case Yellow:
                return "YELLOW";
            case Green:
                return "GREEN";
            default:
                return "";
        }
    }

    /**
     * Maps a "pretty" indicator name to it's referable value.
     */
    private static String mapIndicatorName(String title) {
        String[] words = title.split(" ");
        if (words.length == 0) {
            throw new UnsupportedOperationException(
                    "The given indicator title couldn't be converted! " + title);
        }
        // convert from words to camel case
        StringBuilder result = new StringBuilder();
        result.append(words[0].toLowerCase());
        for (int i = 1; i < words.length; i++) {
            String word = words[i];
            result.append(word.substring(0, 1).toUpperCase());
            result.append(word.substring(1).toLowerCase());
        }
        return result.toString();
    }

    /**
     * Maps a indicator to a "pretty" name.
     */
    private static String mapIndicatorName(Indicator indicator) {
        StringBuilder result = new StringBuilder();
        CharacterIterator iter = new StringCharacterIterator(indicator.getName());
        for (char c = iter.first(); c != CharacterIterator.DONE; c = iter.next()) {
            if (result.length() > 0) {
                result.append(" ");
            }
            result.append(Character.toUpperCase(c));
            c = iter.next();
            while (Character.isLowerCase(c)) {
                result.append(c);
                c = iter.next();
            }
            iter.previous();
        }
        return result.toString();
    }
    //endregion Snapshot
}
