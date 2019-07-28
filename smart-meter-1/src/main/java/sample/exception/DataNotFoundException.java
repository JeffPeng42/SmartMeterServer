package sample.exception;

class DataNotFoundException extends RuntimeException {
	
	DataNotFoundException(String name) {
		super("Could not find employee " + name);
	}
}
