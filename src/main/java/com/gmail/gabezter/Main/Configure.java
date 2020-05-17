package com.gmail.gabezter.Main;

public enum Configure {
	ID(""), FORCELINK(false), TIMEOUT_LENGTH("5"), TIMEOUT_TYPE("m"), UPDATE_FROM(true), UPDATE_RATE("4"),
	UPDATE_TEMPORAL("h"), UPDATE_MESSAGE(true), TOKEN(""), INVITE("");

	private Object obj;

	private Configure(Object obj) {
		this.obj = obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	@Override
	public String toString() {
		return obj.toString();
	}
	
	public Object getValue() {
		return obj;
	}

}
