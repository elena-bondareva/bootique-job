package com.nhl.launcher.job.command;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.nhl.launcher.command.CommandOutcome;
import com.nhl.launcher.command.OptionTriggeredCommand;
import com.nhl.launcher.job.runnable.JobFuture;
import com.nhl.launcher.job.scheduler.Scheduler;
import com.nhl.launcher.jopt.Options;

import joptsimple.OptionParser;

public class ExecCommand extends OptionTriggeredCommand {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExecCommand.class);

	private static final String EXEC_OPTION = "exec";
	public static final String JOB_OPTION = "job";

	private Provider<Scheduler> schedulerProvider;

	// using Provider for lazy init
	@Inject
	public ExecCommand(Provider<Scheduler> schedulerProvider) {
		this.schedulerProvider = schedulerProvider;
	}

	@Override
	protected String getOption() {
		return EXEC_OPTION;
	}

	@Override
	public void configOptions(OptionParser parser) {
		super.configOptions(parser);
		parser.accepts(JOB_OPTION).withRequiredArg();
	}

	@Override
	protected CommandOutcome doRun(Options options) {

		Collection<String> jobArgs = options.stringsFor(JOB_OPTION);
		if (jobArgs == null || jobArgs.isEmpty()) {
			return CommandOutcome.failed(1,
					String.format("No jobs specified. Use '--%s' option to provide job names", JOB_OPTION));
		}

		Set<String> jobNames = new HashSet<>(jobArgs);

		LOGGER.info("ExecCommand executed");

		Scheduler scheduler = schedulerProvider.get();
		Collection<JobFuture> results = jobNames.stream().map(jn -> scheduler.runOnce(jn)).collect(toList());

		// start a separate stream for Future processing. If we continue the
		// Stream above, it will block and won't be able to do parallel
		// scheduling

		results.stream().map(f -> f.get()).forEach(r -> LOGGER
				.info(String.format("Finished job '%s', result: %s", r.getMetadata().getName(), r.getOutcome())));
		return CommandOutcome.succeeded();
	}
}