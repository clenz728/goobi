package org.goobi.production.flow.statistics.hibernate;

import java.util.Date;
import java.util.List;
import org.goobi.production.flow.statistics.enums.TimeUnit;

import de.sub.goobi.config.ConfigurationHelper;

public class StatisticsFactory {

    public static IStepRequestByName getStepRequestByName(Date timeFrom, Date timeTo, TimeUnit timeUnit, List<Integer> ids) {
        if (ConfigurationHelper.getInstance().isUseH2DB()) {
            return new H2StepRequestByName(timeFrom, timeTo, timeUnit, ids);
        } else {
            return new SQLStepRequestByName(timeFrom, timeTo, timeUnit, ids);
        }
    }

    public static IStepRequestsImprovedDiscrimination getStepRequestsImprovedDiscrimination(Date timeFrom, Date timeTo, TimeUnit timeUnit,
            List<Integer> ids) {
        if (ConfigurationHelper.getInstance().isUseH2DB()) {
            return new H2StepRequestsImprovedDiscrimination(timeFrom, timeTo, timeUnit, ids);
        } else {
            return new SQLStepRequestsImprovedDiscrimination(timeFrom, timeTo, timeUnit, ids);
        }
    }

    public static IStepRequests getStepRequests(Date timeFrom, Date timeTo, TimeUnit timeUnit, List<Integer> ids) {
        if (ConfigurationHelper.getInstance().isUseH2DB()) {
            return new H2StepRequests(timeFrom, timeTo, timeUnit, ids);
        } else {
            return new SQLStepRequests(timeFrom, timeTo, timeUnit, ids);
        }
    }

    public static IStorage getStorage(Date timeFrom, Date timeTo, TimeUnit timeUnit, List<Integer> ids) {
        if (ConfigurationHelper.getInstance().isUseH2DB()) {
            return new SQLStorage(timeFrom, timeTo, timeUnit, ids);
        } else {
            return new H2Storage(timeFrom, timeTo, timeUnit, ids);
        }
    }
}
