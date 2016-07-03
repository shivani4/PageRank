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

public class MatrixMul1Reduce extends Reducer<Text, Text, Text, Text> {

	public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
		
			Double ip=new Double(0);
			Double tp=new Double(0);
			String value1="";
			int s=0;
			String value2="";
			String total="";
			for (Text val : values)
			{
				value1=val.toString();
				//context.write(key, new Text(value1));
				if(value1.indexOf("###")!=-1)
				{
					s=value1.indexOf("###");
					if (s>0)
					{
						value2=value1.substring(0, s);
						if(value1.length()>s+3)
						{
							total=value1.substring(s+3);
							ip+=Double.valueOf(value2.toString());
							
						}
				}
				}
			}
			
			tp=(0.85d*ip)+((0.15d)*(1d/Double.valueOf(total)));
			Double diff=new Double(0);
			diff=ip-tp;
			String send = "!" + String.format("%.12f", ip) + "%" + String.format("%.12f", tp) + "$"+ String.format("%.12f", diff);
			context.write(key, new Text(send));	
					
				//context.write(key, new Text(val));
	}
			
			
}

	
