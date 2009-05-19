package com.trg.dao;

/**
 * A runtime exception to be thrown by DAODispatchers when things go
 * wrong in the dispatching process.
 * 
 * @author dwolverton
 */
public class DAODispatcherException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DAODispatcherException(String message) {
		super(message);
	}
	
	public DAODispatcherException(Throwable cause) {
		super(cause);
	}
}
