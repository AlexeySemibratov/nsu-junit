package org.nsu.junit.assertions;

public class AssertionException extends AssertionError {

	private static final long serialVersionUID = 5984516749810L;

	public AssertionException() {
		
	}
	
	public AssertionException(String message) {
		super(message);
	}
	
}
