import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MovieRatings {

	public static class RatingsMapper extends Mapper<LongWritable, Text, LongWritable, IntWritable> {
		private IntWritable rating = new IntWritable();

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
						int ratingValue = Integer.parseInt(row[1].trim());
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

	public static class RatingsReducer extends Reducer<LongWritable, IntWritable, LongWritable, StatsWritable> {		
		@Override
		protected void reduce(LongWritable key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {			
			try {
					List<Integer> ratings = new ArrayList<>();
					for (IntWritable val : values) {
			            ratings.add(val.get());			            
			        }					
					Stats stats = new Stats(ratings);					
					StatsWritable obj = new StatsWritable(stats);
//					System.err.println("key: " + key.toString() + "value: " + stats.toString());
					context.write(key, obj);
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
		job.setMapOutputKeyClass(LongWritable.class);
		job.setMapOutputValueClass(IntWritable.class);
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(StatsWritable.class);		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
