import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MovieRecordReader extends RecordReader<LongWritable, Text> {

    private LineRecordReader lineReader;
    private LongWritable currentKey = new LongWritable();
    private Text currentValue = new Text();

    private StringBuilder recordBuffer = new StringBuilder();
    private boolean insideRecord = false;

    private String lastLine = null; // buffer to hold the start of next record

    @Override
    public void initialize(InputSplit split, TaskAttemptContext context) throws IOException {
        lineReader = new LineRecordReader();
        lineReader.initialize(split, context);
    }

    @Override
    public boolean nextKeyValue() throws IOException {
        // First, check if we have a leftover line from previous call
        if (lastLine != null) {
            String line = lastLine;
            lastLine = null;
            currentKey.set(getMovieId(line));
            recordBuffer.setLength(0);
            recordBuffer.append(line).append(System.lineSeparator());
            insideRecord = true;
        }

        while (lineReader.nextKeyValue()) {
            String line = lineReader.getCurrentValue().toString();

            if (isMovieIdLine(line)) {
                if (insideRecord) {
                    // Buffer this line to be processed in the next call
                    lastLine = line;

                    currentValue.set(recordBuffer.toString());
                    recordBuffer.setLength(0);
                    insideRecord = false;

                    return true;
                } else {
                    // Start a new record
                    currentKey.set(getMovieId(line));
                    recordBuffer.setLength(0);
                    recordBuffer.append(line).append(System.lineSeparator());
                    insideRecord = true;
                }
            } else if (insideRecord) {
                recordBuffer.append(line).append(System.lineSeparator());
            }
        }

        // Emit final record at EOF
        if (insideRecord && recordBuffer.length() > 0) {
            currentValue.set(recordBuffer.toString());
            recordBuffer.setLength(0);
            insideRecord = false;
            return true;
        }

        return false;
    }

    @Override
    public LongWritable getCurrentKey() {
        return currentKey;
    }

    @Override
    public Text getCurrentValue() {
        return currentValue;
    }

    @Override
    public float getProgress() throws IOException {
        return lineReader.getProgress();
    }

    @Override
    public void close() throws IOException {
        lineReader.close();
    }

    private boolean isMovieIdLine(String line) {
        return line.matches("\\d+:");
    }

    private long getMovieId(String line) {
        String regex = "(\\d+):";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            String number = matcher.group(1);
            return Long.parseLong(number);
        }
        return -1;
    }
}
