package io.bootique.job.runnable;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface JobFuture extends ScheduledFuture<JobResult> {

    /**
     * @since 0.13
     */
    static JobFutureBuilder forJob(String job) {
        return new JobFutureBuilder(job);
    }

    /**
     * @return Job name
     * @since 0.24
     */
    String getJobName();

    /**
     * Waits till the job is done and then returns the result.
     *
     * @since 0.13
     */
    @Override
    JobResult get();

    /**
     * Waits till the job is done and then returns the result.
     * Throws an exception, if time elapses before the job has finished.
     *
     * @since 0.13
     */
    @Override
    JobResult get(long timeout, TimeUnit unit);

    /**
     * Convenient shortcut for {@link java.util.concurrent.Future#cancel(boolean)}.
     *
     * @return {@code false} if the task could not be cancelled,
     * typically because it has already completed normally;
     * {@code true} otherwise
     */
    default boolean cancelInterruptibly() {
        return cancel(true);
    }
}
