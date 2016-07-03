package org.shivani.pagerank;
import java.io.IOException;
import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Reducer.Context;

public class Sorted_TP_Reduce extends Reducer<Text, Text, Text, Text> {

	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		{
			Double key1=new Double(0);
			//Double key_final=new Double(0);
			Double one=new Double(1);
			for (Text value : values) {

				key1=Double.valueOf(key.toString());
				key1=one-key1;
				context.write(new Text(value),new Text(String.format("%.12f", key1)));
			}
		}
	}
}