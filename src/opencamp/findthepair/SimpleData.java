package opencamp.findthepair;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

/**
 * A simple demonstration object we are creating and persisting to the database.
 */
public class SimpleData {
	@DatabaseField(generatedId = true)
	int id;
	@DatabaseField
	String name;
	@DatabaseField(index = true)
	int score;
	@DatabaseField
	double time;
	@DatabaseField
	double acc;
	@DatabaseField
	Date date;

	SimpleData() {
	}
	
	public SimpleData(String Name, int Score, double Time, double Acc, Date Date) {
		this.name = Name;
		this.score = Score;
		this.time = Time;
		this.acc = Acc;
		this.date = Date;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		sb.append(", ").append(String.format("%d점", score));
		sb.append(", ").append(time);
		sb.append(", ").append(String.format("%2.2f%%", acc));

		return sb.toString();
	}
}