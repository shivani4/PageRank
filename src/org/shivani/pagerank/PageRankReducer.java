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

public class PageRankReducer extends Reducer<Text, Text, Text, Text> {

	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

		/*if (key.toString().equals("#####Shivani")) {
			int counter = 0;

			HashSet<String> set = new HashSet<String>();

			for (Text value : values) {

				set.add(value.toString());
			}
			String app = "";
			for (String value : set) {
				counter++;
				app += '\n' + value + "#####"+counter;
			}
			context.write(null, new Text(app));
		}

		else {*/
			//int counter = 0;
			HashSet<String> set = new HashSet<String>();

			for (Text value : values) {

				set.add(value.toString());
			}
			String app = "";
			for (String value : set) {
				
				app +="$$$$$"+value;
			}
			context.write(key, new Text(app));
		}

	}
