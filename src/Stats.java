import java.util.List;
import java.util.Collections;


public class Stats{
	
	private double average;
	private int min = 10; //we know that rating is between 1 to 5 so we can initialize it to higher side and then easily compare it for  
	private int max = 0;
	private double median;
	private double bottomQuartile;
	private double topQuartile;
	List<Integer> ratings;

	public Stats(List<Integer> ratings) {
		// TODO Auto-generated constructor stub
		this.ratings = ratings;
		calculate();
	}
	
	private void calculate() {
		Collections.sort(ratings);
		min = Collections.min(ratings);
        max = Collections.max(ratings);
        int sum = 0;
        for(int rating: ratings) {
        	sum += rating;
		}
        int size = ratings.size();
        average = (double) sum / size;		
		if(size % 2 == 0) {
			//even
			median = (ratings.get((size / 2) - 1) + ratings.get(size / 2)) / 2.0;
		}else {
			//odd
			median = ratings.get((size / 2) + 1); 
		}
		bottomQuartile = calculatePercentile(25);
		topQuartile = calculatePercentile(75);	
	}
	
	// Helper method to calculate a specific quartile / percentile
	private double calculatePercentile(int percentile) {
	    int size = ratings.size();

	    // Check for empty or very small lists
	    if (size == 0) {
	        throw new IllegalArgumentException("Ratings list cannot be empty.");
	    }
	    if (size == 1) {
	        return ratings.get(0); // Handle single-element list
	    }

	    // Convert percentile to fractional value (e.g., 25 -> 0.25)
	    double fraction = percentile / 100.0;
	    double position = fraction * (size - 1); // Ensure position is within bounds

	    // Calculate indices
	    int lowerIndex = (int) Math.floor(position);
	    int upperIndex = (int) Math.ceil(position);

	    // Safeguard: Ensure indices are within bounds
	    if (lowerIndex < 0 || upperIndex >= size) {
	        throw new IndexOutOfBoundsException("Percentile indices out of bounds.");
	    }

	    // Handle exact match or interpolate between indices
	    if (lowerIndex == upperIndex) {
	        return ratings.get(lowerIndex);
	    } else {
	        double fractionBetween = position - lowerIndex;
	        return ratings.get(lowerIndex) + fractionBetween * 
	               (ratings.get(upperIndex) - ratings.get(lowerIndex));
	    }
	}
	
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
	
	@Override
    public String toString() {
        return String.format("Min: %d, Max: %d, Avg: %.2f, Median: %.2f, Top Quartile: %.2f, Bottom Quartile: %.2f",
                min, max, average, median,topQuartile,bottomQuartile);
    }

	
}
