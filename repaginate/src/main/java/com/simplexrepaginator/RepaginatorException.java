package com.simplexrepaginator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class RepaginatorException extends Exception {

	protected List<Throwable> causes = new ArrayList<Throwable>();
	
	public RepaginatorException() {
		super();
	}

	public RepaginatorException(String message, Throwable cause) {
		super(message, cause);
	}

	public RepaginatorException(String message) {
		super(message);
	}

	public RepaginatorException(Throwable cause) {
		super(cause);
	}
	
	public RepaginatorException addCause(Throwable cause) {
		causes.add(cause);
		return this;
	}

	public List<Throwable> getCauses() {
		return Collections.unmodifiableList(causes);
	}
	
	@Override
	public String getMessage() {
		return super.getMessage() + ":\nCaused by:\t" + StringUtils.join(causes, "\n\t");
	}
	

}
