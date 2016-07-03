package org.shivani.pagerank;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;

public class MatrixCreationMap extends Mapper<LongWritable, Text, Text, Text> {
   File file;
   HashMap<String, String> map = new HashMap<String, String>();
	@Override
	protected void setup(
	        Mapper<LongWritable, Text, Text, Text>.Context context)
	        throws IOException, InterruptedException {
	    if (context.getCacheFiles() != null && context.getLocalCacheFiles().length>0) {
	    	Path[] paths = context.getLocalCacheFiles();
	    	file=new File("./CacheFile");
	    }
	    super.setup(context);
	}
	@Override
    protected void map(LongWritable key, Text value, Mapper.Context context)
            throws IOException, InterruptedException
	{
		BufferedReader br = null;
		int s=0;
		int k=0;
		int count=0;
		String sCurrentline="";
		String firstColVal="";
		br = new BufferedReader(new FileReader(file));
		try
		{
			while ((sCurrentline = br.readLine()) != null) 
			{
					if (sCurrentline.indexOf("#####")!=-1)
					{
							s=sCurrentline.indexOf("#####");
							if (s > 0) 
							{
								firstColVal = sCurrentline.substring(0, s);
								count++;
								if (firstColVal != "")
									map.put(firstColVal.trim(), String.valueOf(count));
							}
					}
			}
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		finally 
		{
			try 
			{
				if (br != null)
					br.close();
			} catch (IOException ex) 
			{
				ex.printStackTrace();
			}
			
		}
			
		//Reading from the values field	
			
		StringTokenizer str=new StringTokenizer(value.toString(),"$$$$$");
		String key1="";
		if(str.hasMoreTokens())
		key1=str.nextToken();
		String links="";
		String value1="";
		count=0;
		String val="";
		if (map.get(key1.trim())!=null)
		{
			val=map.get(key1.trim());
			while(str.hasMoreTokens())
			{	value1=str.nextToken();
				{		if(map.get(value1)!=null)
						{
							links+="!!!"+(map.get(value1));
							count++;
						}
				}
			}
			if (links!=null)
			{	links+="$$$"+String.valueOf(count);
				context.write(new Text(val), new Text(links));
				context.write(new Text("Shivani@@@@"), new Text(String.valueOf(1)));
			}
		}
		}
	
}
