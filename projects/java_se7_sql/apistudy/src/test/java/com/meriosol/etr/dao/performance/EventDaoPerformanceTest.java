package com.meriosol.etr.dao.performance;

import com.meriosol.etr.dao.EventDao;
import com.meriosol.etr.dao.EventDaoFactory;
import com.meriosol.etr.domain.Event;
import com.meriosol.mutasker.TaskPooledExecutor;
import com.meriosol.mutasker.domain.NamedMeasurableTaskResult;
import com.meriosol.mutasker.domain.Result;
import com.meriosol.mutasker.domain.Task;
import com.meriosol.mutasker.domain.TaskResultStats;
import com.meriosol.performance.PerformanceTracker;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * This test suite is about multi-threaded DAO loads.
 *
 * @author meriosol
 * @version 0.1
 * @since 11/03/14
 */
public class EventDaoPerformanceTest {
    private static Logger LOG = null;
    public PerformanceTracker performanceTracker;
    private EventDao eventDao;

    @BeforeClass
    public static void init() {
        LOG = LoggerFactory.getLogger(EventDaoPerformanceTest.class);
    }

    @Before
    public void setUp() {
        EventDaoFactory eventDaoFactory = EventDaoFactory.getInstance();
        if (eventDaoFactory != null) {
            this.eventDao = eventDaoFactory.loadEventDao();
            assert this.eventDao != null;
            LOG.info(">>>> EventDao loaded. Name='{}'. Details: {}", this.eventDao.getDaoName(), this.eventDao.getDaoDescription());
        }
        this.performanceTracker = new PerformanceTracker(this.getClass().getSimpleName() + "__" + this.eventDao.getDaoName());
        this.performanceTracker.startTracking();
    }

    @After
    public void tearDown() throws Exception {
        this.eventDao = null;
        if (this.performanceTracker != null) {
            this.performanceTracker.finishTracking();
            LOG.info("Performance report:\n{}\n", this.performanceTracker.emitReport());
        }
    }

    @Test
    public void testEventRetrieveById() {
        final String module = "testEventRetrieveById";
        this.performanceTracker.addMilestone(module + "." + PerformanceTracker.LifecycleMilestoneName.BEGIN);
        try {
            assertNotNull("EventDao should not be null!", this.eventDao);
            // CAUTION: DB should be initialized with events starting from 1000001.
            Long sampleEventId = 1000001L;
            String workUnitName = "RetrieveEventByIdUnit";

            int iterationAmount = 10;
            // CAUTION: be careful with this iteration amount (memory/CPU can be overloaded).

            int workersAmount = 20;
            // CAUTION: be careful with this thread amount (memory/CPU can be overloaded).

            List<Task<Event>> tasks = new ArrayList<>(workersAmount);
            for (int i = 0; i < workersAmount; i++) {
                Task<Event> eventRetrieveByIdTask = new EventRetrieveByIdTask(sampleEventId, iterationAmount);
                tasks.add(eventRetrieveByIdTask);
            }

            LOG.debug("About to start work with '{}' || workers..", workersAmount);
            TaskPooledExecutor<Event> taskPooledExecutor = new TaskPooledExecutor<>("EXEC_" + module);
            List<Result<Event>> results = taskPooledExecutor.perform(tasks);
            LOG.debug("Emulated work done.");

            EventResultsReport eventResultsReport = new EventResultsReport();
            eventResultsReport.printReportPerformanceStats(module, workUnitName, results);

        } finally {
            this.performanceTracker.addMilestone(module + "." + PerformanceTracker.LifecycleMilestoneName.END);
        }
    }

    //---------------------------
    // Task classes.
    // TODO: put them in dedicated package if too many task classes get written.

    private class EventRetrieveByIdTask implements Task<Event> {
        private long eventId;
        private int iterationAmount = 1;

        private EventRetrieveByIdTask() {
        }

        private EventRetrieveByIdTask(long eventId) {
            this.eventId = eventId;
        }

        private EventRetrieveByIdTask(long eventId, int iterationAmount) {
            this.eventId = eventId;
            this.iterationAmount = iterationAmount;
        }

        @Override
        public Result<Event> execute() {
            Result<Event> result;
            long startTime = System.currentTimeMillis();
            String name = "RetrieveEventByIDTask";
            int outputItemsCount = 1;
            Event event = null;
            for (int i = 0; i < this.iterationAmount; i++) {
                event = eventDao.retrieveEvent(eventId);
//                try {
//                    Thread.sleep(70);
//                } catch (InterruptedException e) {
//                   LOG.error("InterruptedException", e);
//                }
            }
            //Params: long startTime, String name, T details, long iterationAmount, long outputItemCount
            result = new NamedMeasurableTaskResult<>(startTime, name, event, this.iterationAmount, outputItemsCount);
            return result;

        }
    }

    //---------------------------
    // Reports
    private class EventResultsReport {
        void printReportPerformanceStats(String testName, String workUnitName, final List<Result<Event>> results) {
            if (results != null && results.size() > 0) {
                LOG.info("o>> ({}) ====================== Results report: V", testName);
                LOG.info("o>> ({}) '{}' results found..", testName, results.size());

                TaskResultStats<Event> stats = new TaskResultStats<>(Collections.unmodifiableList(results), workUnitName);
                String statsBasicInfo = stats.prepareBasicStatsReportForDurations();
                LOG.info("\n{}", statsBasicInfo);

                for (Result<Event> result : results) {
                    if (result != null) {
                        int rowCount = 0;
                        Event event = result.getDetails(); // TODO: show some data from event object.

                        long duration = stats.calculateDuration(result);
                        if (duration > 0) {
                            long durationPerUnit = stats.getDurationPerUnitOfWork(result);
                            if (durationPerUnit > 0) {
                                LOG.debug(" oo>> ({}) Result '{}'(EventID='{}'), duration='{}' msecs({}) (or '{}' msecs per result), '{}' items found."
                                        , testName, event.getId(), result.getName(), duration, DurationFormatUtils.formatDurationWords(duration, true, true),
                                        durationPerUnit, rowCount);
                            } else {
                                LOG.debug(" oo>> ({}) Result '{}'(EventID='{}'), WARN: duration/unit='{}' msecs (don't believe), '{}' items found."
                                        , testName, event.getId(), result.getName(), durationPerUnit, rowCount);
                            }
                        } else {
                            LOG.debug(" oo>> ({}) Result '{}'(EventID='{}'), WARN: duration='{}' msecs (don't believe), '{}' items found."
                                    , testName, event.getId(), result.getName(), duration, rowCount);
                        }
                    } else {
                        LOG.warn(" oo>> ({}) One of search results is null.", testName);
                    }
                }
                LOG.info("o>> ({}) ====================== Results report ^", testName);

            } else {
                LOG.warn("o>> ({}) No results provided.", testName);
            }
        }
    }
    //---------------------------
}
