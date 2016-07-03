package org.shivani.pagerank;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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

public class Sorted_TP_Map extends Mapper<LongWritable, Text, Text, Text> {
	File file;
	//File[] cache_file;
	int number;
	   HashMap<String, String> map = new HashMap<String, String>();
		@Override
		protected void setup(
		        Mapper<LongWritable, Text, Text, Text>.Context context)
		        throws IOException, InterruptedException {
		    if (context.getCacheFiles() != null && context.getLocalCacheFiles().length>0) {
		    	Path[] paths = context.getLocalCacheFiles();
		    	file=new File("./Title_Count");
		    }
		    super.setup(context);
		}
		
	
	@Override
	protected void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException {
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
								//System.out.println(firstColVal);
									count++;
								if (firstColVal!= null){
									map.put(String.valueOf(count),firstColVal.trim());
									//System.out.println(map.get(String.valueOf(count)));
								
							}
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
		
		s = 0;
		k = 0;
		count = 0;
		String values = value.toString();
		String value1 = "";
		Double TP=new Double(0);
		Double one=new Double(1);
		if(values.indexOf("!")!=-1)
		{	
			s=values.indexOf("!");
			if(0<s)
			{
				String key1=values.substring(0,s).trim();
				System.out.println(key1);
				if(map.get(key1)!=null)
				{
				key1=map.get(key1);
				if(values.indexOf("%")!=-1)
				{
					int u=values.indexOf("%");
				if(values.indexOf("$")!=-1)
				{
				k=values.indexOf("$");
					if(u+1<k)
					{
						value1=values.substring(u+1, k);
						//System.out.println("String Value:"+value1);
						TP=Double.valueOf(value1.trim());
						//System.out.println(IP);
						TP=one-TP;
						value1=String.format("%.12f", TP);
						context.write(new Text(value1), new Text(key1));
					}
				}
			}
		}
		}
}
	}
}
