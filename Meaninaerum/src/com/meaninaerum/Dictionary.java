package com.meaninaerum;

public class Dictionary {

	private long id;
	private String name;
	
	public Dictionary(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}
