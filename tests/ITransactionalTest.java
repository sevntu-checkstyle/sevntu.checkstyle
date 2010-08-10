package com.revere.test;

import org.springframework.transaction.PlatformTransactionManager;

public interface ITransactionalTest {
	public PlatformTransactionManager getTransactionManager();
}
