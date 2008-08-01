package com.trg.test;

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

/**
 * All jUnit test classes that need to access Spring bean wiring <br />
 * from src/main/resources/[..].xml should extend this class. <br/> This class does auto-clean-up of database after test
 * is executed.
 * 
 * @author Uki D. Lucas
 */
public abstract class TestCaseSpringAutoWire extends AbstractTransactionalDataSourceSpringContextTests
{
	protected String[] getConfigLocations()
	{
		setAutowireMode(AUTOWIRE_BY_NAME);
		return new String[]
		{ "classpath:jUnit-applicationContext.xml" };
	}
}
