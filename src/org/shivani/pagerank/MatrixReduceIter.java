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

public class MatrixReduceIter extends Reducer<Text, Text, Text, Text> {

	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		{
			Double ip = new Double(0);
			Double tp = new Double(0);
			String value1 = "";
			int s = 0;
			int k = 0;
			String value2 = "";
			String value3 = "";
			String total = "";
			for (Text val : values) {
				value1 = val.toString();
				// context.write(key, new Text(value1));
				if (value1.indexOf("#") != -1) {
					s = value1.indexOf("#");
					if (s > 0) {
						value2 = value1.substring(0, s);
						if (value1.indexOf("##") != -1) {
							k = value1.indexOf("##");
							if (s + 1 < k) {
								value3 = value1.substring(s + 1, k);

								if (value1.length() > k + 2) {
									total = value1.substring(k + 2);
									ip += Double.valueOf(value2.toString());
									tp += Double.valueOf(value3.toString());
								}
							}
						}
					}
				}
			}
			tp = (0.85d * tp) + ((0.15d) * (1d / Double.valueOf(total)));
			Double diff = new Double(0);
			diff = ip - tp;
			String send = "!" + String.format("%.12f", ip) + "%" + String.format("%.12f", tp) + "$"+ String.format("%.12f", diff);
			context.write(key, new Text(send));
		}

	}
}
