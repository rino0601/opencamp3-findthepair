package opencamp.findthepair;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

/**
 * A simple demonstration object we are creating and persisting to the database.
 */
public class SimpleData implements Comparable {
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
		// needed by ormlite
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
//		sb.append("id=").append(id);
//		sb.append(", ").append("name=").append(name);
//		sb.append(", ").append("score=").append(score);
//		sb.append(", ").append("time=").append(time);
//		sb.append(", ").append("acc=").append(acc);
//		SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.S");
//		sb.append(", ").append("date=").append(dateFormatter.format(date));
		sb.append(name);
		sb.append(", ").append(String.format("%d점", score));
		sb.append(", ").append(time);
		sb.append(", ").append(String.format("%2.2f%%", acc));
//		String imageName = "_%3d" + "_%s";
//
//for( int i = 0; i < 1000; i++ ){
//    System.out.println( String.format( imageName, i, "foo" ) );
//}
		return sb.toString();
	}
	
	@Override
    public int compareTo(Object o) {

        SimpleData f = (SimpleData)o;

        if (score < f.score) {
            return 1;
        }
        else if (score > f.score) {
            return -1;
        }
        else {
            return 0;
        }

    }
}