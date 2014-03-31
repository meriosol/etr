package com.meriosol.performance;

import java.util.ArrayList;
import java.util.List;

/**
 * Very basic (at least for now) performance tracker.<br>
 * Usage: add named track milestones in code. Each time code reaches this milestone, tracker saves time.
 * In the end tracker provides report about milestone timelines.
 * You can compare them for different frameworks/DBs combinations and conclude roughly what's acceptable for you to use.<br>
 * TODO: make it thread-safe if it's planned to be used in mthread env-s
 *
 * @author meriosol
 * @version 0.1
 * @since 21/01/14
 */
public class PerformanceTracker {
    private List<Milestone> milestones;
    private String sessionName;
    private long created;

    public interface LifecycleMilestoneName {
        String BEGIN = "BEGIN";
        String END = "END";
    }

    public PerformanceTracker(String sessionName) {
        this.milestones = new ArrayList<>();
        this.sessionName = sessionName;
        this.created = System.currentTimeMillis();
    }

    public List<Milestone> getMilestones() {
        return milestones;
    }

    public String getSessionName() {
        return sessionName;
    }

    /**
     * @return When tracker object was created, in msecs.
     */
    public long getCreated() {
        return created;
    }

    /**
     * Starts performance tracking.
     */
    public void startTracking() {
        this.milestones = new ArrayList<>();
        this.milestones.add(new Milestone(LifecycleMilestoneName.BEGIN));
    }

    /**
     * @param name Milestone name
     */
    public void addMilestone(String name) {
        this.milestones.add(new Milestone(name));
    }

    /**
     * Finishes performance tracking.
     */
    public void finishTracking() {
        this.milestones.add(new Milestone(LifecycleMilestoneName.END));
    }

    /**
     * @return Stats about performance in form of report(e.g. in JSON format).
     */
    public String emitReport() {
        PerformanceReportEmitter reportEmitter = new JsonPerformanceReportEmitter();
        // TODO: write other format emitters if needed(XML?)..
        return reportEmitter.emit(this);
    }

    /**
     * @return Duration in msecs
     */
    public long getTrackingDuration() {
        long duration = 0;
        int milestonesSize = this.milestones.size();
        if (milestonesSize > 1) {
            Milestone startMilestone = this.milestones.get(0);
            Milestone endMilestone = this.milestones.get(milestonesSize - 1);
            duration = endMilestone.getTime() - startMilestone.getTime();
        }
        return duration;
    }

}
