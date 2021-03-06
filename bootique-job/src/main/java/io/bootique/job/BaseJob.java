package io.bootique.job;

import java.util.Map;

import io.bootique.job.runnable.JobResult;

public abstract class BaseJob implements Job {

	private JobMetadata metadata;

	public BaseJob(JobMetadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public JobMetadata getMetadata() {
		return metadata;
	}

	@Override
	public abstract JobResult run(Map<String, Object> params);
}
