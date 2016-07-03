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

public class MatrixMul1Map extends Mapper<LongWritable, Text, Text, Text> {
	File file;
	// HashMap<String, String> map = new HashMap<String, String>();

	@Override
	protected void setup(Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {
		if (context.getCacheFiles() != null && context.getLocalCacheFiles().length > 0) {
			Path[] paths = context.getLocalCacheFiles();
			file = new File("./CacheFile2");
		}
		super.setup(context);
	}

	@Override
	protected void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException {
		BufferedReader br = null;
		int s = 0;
		int k = 0;
		int count = 0;
		String sCurrentline = "";
		String firstColVal = "";
		String total = "";
		br = new BufferedReader(new FileReader(file));
		try {
			while ((sCurrentline = br.readLine()) != null) {
				StringTokenizer str = new StringTokenizer(sCurrentline);
				if (str.hasMoreTokens()) {
					total = str.nextToken();
					total = str.nextToken();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}

		}

		Double V0 = Double.valueOf(0);
		V0 = 1.0d / Integer.valueOf(total);
		// Reading from the values field

		String values = value.toString();
		String value1="";
		int e=0;
		int degree=0;
		if(values.indexOf("!!!")!=-1)
		{
			//System.out.println("HI!");
			s=values.indexOf("!!!");
			if(s>0){
				//System.out.println("I");
				value1=values.substring(0, s);
				value1=value1.trim();
			}	
			
			e= values.indexOf("$$$");
			 if (e+3 < values.length()) {
					//System.out.println("in E");
					degree = Integer.valueOf(values.substring(e + 3));
			 }
				 if(e>s+3){
					 values=values.substring(s+3, e);
					 StringTokenizer str=new StringTokenizer(values,"!!!");
				 
				 while(str.hasMoreTokens())
				 {
						String value2=str.nextToken();
							//System.out.println("In degree");
							Double Mult = V0 * (1d/ degree);
							String multString = String.format("%.10f", Mult);
							String Fin_Mult=multString+"###"+total;
							context.write(new Text(value2), new Text(Fin_Mult));
					}
				}
					
			}
		}

}	