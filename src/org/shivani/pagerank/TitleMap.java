package org.shivani.pagerank;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class TitleMap extends Mapper<LongWritable, Text, Text, Text> {

	@Override
	protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
		String line = value.toString();
		if(line.indexOf("$$$$$")!=-1)
		{
			int s=line.indexOf("$$$$$");
			if(s>0){
				String Title=line.substring(0,s).trim();
				Title=Title+"#####";
				context.write(new Text(""), new Text(Title));
		}
		}
	}
}
