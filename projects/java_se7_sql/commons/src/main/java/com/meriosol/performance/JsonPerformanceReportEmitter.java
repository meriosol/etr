package com.meriosol.performance;

import com.meriosol.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * @author meriosol
 * @version 0.1
 * @since 21/01/14
 */
class JsonPerformanceReportEmitter implements PerformanceReportEmitter {
    private static Logger LOG = LoggerFactory.getLogger(JsonPerformanceReportEmitter.class);

    interface Chr {
        // For JSON maps:
        String MAP_OP = "{";
        String MAP_CL = "}";
        // For JSON arrays:
        String ARR_OP = "[";
        String ARR_CL = "]";

        String PAIR_DELIM = ":";
        String ELEM_DELIM = ", ";
        String STRING_QUOTE = "\"";
    }

    interface FieldName {
        String NAME = "name";
        String CREATED = "created";
        String TOTAL_DURATION = "totalDuration";

        interface Milestone {
            String ELEMENT = "milestones";
            String DURATION_SINCE_START = "durationSinceStart";
            String DURATION_SINCE_PREV = "durationSincePrev";

            interface Memory {
                String ELEMENT = "memory";
                String MAX = "max";
                String TOTAL = "total";
                String USED = "used";
            }
        }
    }

    /**
     * @param ptr
     * @return Json string with all gathered stats for tracking session. Session results can be gathered/compared via other util.
     */
    @Override
    public String emit(PerformanceTracker ptr) {
        assert ptr != null;
        StringBuilder sb = new StringBuilder(Chr.MAP_OP);
        // 1. Name and totals:
        String sessionNamePair = buildStringPair(FieldName.NAME, ptr.getSessionName());

        Date createdDate = new Date(ptr.getCreated());
        String formattedCreatedDate = "";
        try {
            formattedCreatedDate = DateUtil.formatDateWithDefaultFormat(createdDate);
        } catch (ParseException e) {
            LOG.error("ParseException while parse created date " + createdDate, e);
            formattedCreatedDate = createdDate.toString();
        }

        String createdPair = buildStringPair(FieldName.CREATED, formattedCreatedDate);
        String totalDurationPair = buildNumPair(FieldName.TOTAL_DURATION, ptr.getTrackingDuration());
        sb.append(sessionNamePair + Chr.ELEM_DELIM + createdPair + Chr.ELEM_DELIM + totalDurationPair);

        // 2. Milestones:
        sb.append(Chr.ELEM_DELIM + " " + emitMilestones(ptr.getMilestones()));

        sb.append("\n" + Chr.MAP_CL); // ~ Session
        return sb.toString();
    }

    /**
     * @param milestones
     * @return Report for Milestone list
     */
    private static String emitMilestones(List<Milestone> milestones) {
        StringBuilder sb = new StringBuilder(FieldName.Milestone.ELEMENT + Chr.PAIR_DELIM + "\n    " + Chr.ARR_OP + "\n");
        if (milestones != null && milestones.size() > 0) {
            Milestone begin = milestones.get(0);
            Milestone previous = begin;
            int i = 0;
            for (Milestone milestone : milestones) {
                String name = milestone.getName();
                long durationSinceStart = milestone.getTime() - begin.getTime();
                long durationSincePrev = milestone.getTime() - previous.getTime();

                String namePair = buildStringPair(FieldName.NAME, name);
                String durationSinceStartPair = buildNumPair(FieldName.Milestone.DURATION_SINCE_START, durationSinceStart);
                String durationSincePrevPair = buildNumPair(FieldName.Milestone.DURATION_SINCE_PREV, durationSincePrev);

                String memoryPair = emitMemory(milestone.getMemorySnapshot());

                sb.append("        " + Chr.MAP_OP + namePair + Chr.ELEM_DELIM
                        + durationSinceStartPair + Chr.ELEM_DELIM + durationSincePrevPair + Chr.ELEM_DELIM + memoryPair + Chr.MAP_CL);
                if (i++ < milestones.size() - 1) {
                    sb.append(",\n");
                }

                previous = milestone;
            }
        }
        sb.append("\n    " + Chr.ARR_CL); // ~ Milestones

        return sb.toString();
    }

    /**
     * @param memorySnapshot
     * @return Report for Memory snapshot.
     */
    private static String emitMemory(MemorySnapshot memorySnapshot) {
        assert memorySnapshot != null;

        String maxPair = buildNumPair(FieldName.Milestone.Memory.MAX, memorySnapshot.getMaxMemoryInMegs());
        String totalPair = buildNumPair(FieldName.Milestone.Memory.TOTAL, memorySnapshot.getTotalMemoryInMegs());
        String usedPair = buildNumPair(FieldName.Milestone.Memory.USED, memorySnapshot.getUsedMemoryInMegs());

        return buildObjectPair(FieldName.Milestone.Memory.ELEMENT, Chr.MAP_OP + maxPair + Chr.ELEM_DELIM + totalPair + Chr.ELEM_DELIM + usedPair + Chr.MAP_CL, "");
        //return FieldName.Milestone.Memory.ELEMENT + Chr.PAIR_DELIM + Chr.MAP_OP + maxPair + Chr.ELEM_DELIM + totalPair + Chr.ELEM_DELIM + usedPair + Chr.MAP_CL;
    }

    /**
     * @param name
     * @param value
     * @return string in form ~ name: "value"
     */
    private static String buildStringPair(String name, String value) {
        return buildObjectPair(name, value, Chr.STRING_QUOTE);
    }

    /**
     * @param name
     * @param value
     * @return string in form ~ name: value
     */
    private static String buildNumPair(String name, Number value) {
        return buildObjectPair(name, value, "");
    }

    /**
     * @param name
     * @param value
     * @param valueEmbraceChar
     * @return string in form ~ name: <code>valueEmbraceChar</code>value<code>valueEmbraceChar</code>
     */
    private static String buildObjectPair(String name, Object value, String valueEmbraceChar) {
        return Chr.STRING_QUOTE + name + Chr.STRING_QUOTE + Chr.PAIR_DELIM + " " + valueEmbraceChar + value + valueEmbraceChar;
    }

}
