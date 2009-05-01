package com.trg.dao;

public class DAODispatcherException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DAODispatcherException(String message) {
		super(message);
	}
	
	public DAODispatcherException(Throwable cause) {
		super(cause);
	}
}
