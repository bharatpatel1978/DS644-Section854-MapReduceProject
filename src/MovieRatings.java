import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MovieRatings {

	public static class RatingsMapper extends Mapper<LongWritable, Text, LongWritable, DoubleWritable> {
		private DoubleWritable rating = new DoubleWritable();

		@Override
		protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			if (value == null || value.toString().trim().isEmpty()) {
				return; // Ignore empty or null lines
			}
			try {
				String[] lines = value.toString().trim().split("\\R");
				for(String line : lines) {
					if (line.contains(",")) {
						String[] row = line.split(",");
						double ratingValue = Double.parseDouble(row[1].trim());
						rating.set(ratingValue); // Set rating as value
						context.write(key, rating);
					}
				}
			}catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
				// Log and skip lines with invalid rating data
				System.err.println("Map Method - Invalid record ID: " + key.toString() + " Exception: " + e.getMessage());
			} catch (Exception e) {
				System.err.println("Map Method - Invalid record ID: " + key.toString() + " Exception: " + e.getMessage());
			}
			
			
		}
	}

	public static class RatingsReducer extends Reducer<LongWritable, DoubleWritable, LongWritable, DoubleWritable> {		
		@Override
		protected void reduce(LongWritable key, Iterable<DoubleWritable> values, Context context)
				throws IOException, InterruptedException {
			double sum = 0;
			int count = 0;
			try {				
				for (DoubleWritable value : values) {
					sum += value.get();
					count++;
				}
				if (count > 0) {
					double average = sum / count;
					context.write(key, new DoubleWritable(average));
				} else {
					System.err.println("No ratings for movie ID: " + key.toString());
				}
			} catch (Exception e) {
				System.err.println(
						"Reduce Method - Invalid record ID: " + key.toString() + " Exception: " + e.getMessage());
			}
		}
	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "Movie Ratings");
		job.setJarByClass(MovieRatings.class);
		// Set custom input format
		job.setInputFormatClass(MovieInputFormat.class);
		job.setMapperClass(RatingsMapper.class);
		job.setReducerClass(RatingsReducer.class);
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(DoubleWritable.class);		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
