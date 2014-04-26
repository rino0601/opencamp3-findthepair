package opencamp.findthepair;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

/**
 * A simple demonstration object we are creating and persisting to the database.
 */
public class SimpleData {
	@DatabaseField(generatedId = true)
	int id;
	@DatabaseField(index = true)
	String name;
	@DatabaseField
	int score;
	@DatabaseField
	Date date;

	SimpleData() {
	}
	
	public SimpleData(String Name, int Score, Date Date) {
		this.name = Name;
		this.score = Score;
		this.date = Date;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(", ").append(""+score);

		return sb.toString();
	}
}