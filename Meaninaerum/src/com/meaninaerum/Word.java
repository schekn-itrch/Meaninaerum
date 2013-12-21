package com.meaninaerum;

public class Word {

	private long id;
	private String name;
	private String optionRight;
	private String optionWrong;
	
	public Word(long id, String name, String optionA, String optionB) {
		this.id = id;
		this.name = name;
		this.optionRight = optionA;
		this.optionWrong = optionB;
	}
	
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getOptionRight() {
		return optionRight;
	}

	public String getOptionWrong() {
		return optionWrong;
	}

	@Override
	public String toString() {
		return name;
	}
}
