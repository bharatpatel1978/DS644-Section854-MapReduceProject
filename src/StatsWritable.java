import org.apache.hadoop.io.Writable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class StatsWritable implements Writable {
    private int min;
    private int max;
    private double average;
    private double median;
    private double bottomQuartile;
	private double topQuartile;

    // Default constructor for deserialization
    public StatsWritable() {
    	this.min = -1;
        this.max = -1;
        this.average = -1.0;
        this.median = -1.0;
        this.topQuartile = -1.0;
        this.bottomQuartile = -1.0;
    }

    public StatsWritable(Stats stats) {
        this.min = stats.getMin();
        this.max = stats.getMax();
        this.average = stats.getAverage();
        this.median = stats.getMedian();
        this.topQuartile = stats.getTopQuartile();
        this.bottomQuartile = stats.getBottomQuartile();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(min);
        out.writeInt(max);
        out.writeDouble(average);
        out.writeDouble(median);
        out.writeDouble(topQuartile);
        out.writeDouble(bottomQuartile);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        this.min = in.readInt();
        this.max = in.readInt();
        this.average = in.readDouble();
        this.median = in.readDouble();
        this.topQuartile = in.readDouble();
        this.bottomQuartile = in.readDouble();
    }

    @Override
    public String toString() {
        return String.format("Min: %d, Max: %d, Avg: %.2f, Median: %.2f, Top Quartile: %.2f, Bottom Quartile: %.2f", min, max, average, median,topQuartile,bottomQuartile);
    }

    // Getters (optional, for convenience)
    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public double getAverage() {
        return average;
    }

    public double getMedian() {
        return median;
    }
    
    public double getTopQuartile() {
		return topQuartile;
	}
	
	public double getBottomQuartile() {
		return bottomQuartile;
	}
}