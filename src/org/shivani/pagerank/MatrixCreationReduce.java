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

public class MatrixCreationReduce extends Reducer<Text, Text, Text, Text> {

	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

		if (key.toString().equals("Shivani@@@@")) {
			int counter = 0;
			for (Text value : values) {

				counter++;
			}

			context.write(new Text("Total"), new Text(String.valueOf(counter)));
		}

		else {
			for (Text value : values) {
				context.write(key, new Text(value.toString()));
			}
		}

	}
}