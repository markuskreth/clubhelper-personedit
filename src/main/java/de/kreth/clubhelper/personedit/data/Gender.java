package de.kreth.clubhelper.personedit.data;

public enum Gender {

	MALE(1), FEMALE(2);
	
	private int id;

	private Gender(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public static Gender valueOf(Integer id) {
		if (id == null) {
			throw new NullPointerException("Parameter must not be null!");
		}
		Gender[] values = values();
		for (Gender gender : values) {
			if (id.intValue() == gender.id) {
				return gender;
			}
		}
		throw new IllegalArgumentException("Gender for id '" + id + "' not found!" );
	}
	
	
}
