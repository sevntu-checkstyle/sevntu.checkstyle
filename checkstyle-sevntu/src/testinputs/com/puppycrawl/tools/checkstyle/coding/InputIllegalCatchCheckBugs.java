package com.revere.livevol.job;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.revere.livevol.WorkNow;
import com.revere.livevol.conf.DefaultConfiguration;
import com.revere.shell.support.AbstractShellProcessor;

public class Loader extends AbstractShellProcessor {

	private final Log log = LogFactory.getLog(getClass());
	private Locker locker;

	private DefaultConfiguration configuration;

	private WorkNow cli;

	@Override
	protected void init() throws Throwable {
		super.init();

		File lockFile = new File(getHomeDir(), getClass().getSimpleName() + ".lck");
		this.locker = new Locker(lockFile);

		this.configuration = new DefaultConfiguration();
		this.cli = configuration.get(WorkNow.class);
	}

	@Override
	public void release() throws Throwable {
		super.release();

		this.locker.close();

		this.configuration.destroy();
	}

	@Override
	public Object parserArguments(String[] procArgs) throws Throwable {
		parseArgs(procArgs, this.cli);
		return this.cli;
	}

	@Override
	public void execute(Object argsObj) throws Throwable {
		try {
			log.info("Livevol job started");
			cli.work();
			log.info("Livevol job finished");
		} catch (Exception e) { // no warnings here!
			//log.error("processing failed: " + e, e);
			throw e;
		}
	}

}