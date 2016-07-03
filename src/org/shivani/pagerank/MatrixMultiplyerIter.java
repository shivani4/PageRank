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

public class MatrixMultiplyerIter extends Mapper<LongWritable, Text, Text, Text> {
	File total1;
	File[] cache_file;
	int number;
	HashMap<String, String> map = new HashMap<String, String>();

	@Override
	protected void setup(Mapper<LongWritable, Text, Text, Text>.Context context)
			throws IOException, InterruptedException {

		cache_file = new File[10];
		String getconfig = context.getConfiguration().get("Count_loop");
		number = Integer.valueOf(getconfig);
		if (context.getCacheFiles() != null && context.getCacheFiles().length > 0) {
			for (int i = 0; i <= 9; i++) {

				cache_file[i] = new File("./Cache" + number + "_" + String.valueOf(i));
			}

			total1 = new File("./Total" + number);
		}

		super.setup(context);
	}

	public void addvalues() throws Exception {
	//System.out.println("Hashmap initiated");

		for (int i = 0; i < 10; i++) {
			String line = "";
			String title = "";
			String init_val = "";
			String final_val = "";
			int s = 0;
			File cachedFile = cache_file[i];
			BufferedReader br = new BufferedReader(new FileReader(cachedFile));
			while ((line = br.readLine()) != null) {
				StringTokenizer str = new StringTokenizer(line, "!");
				if (str.hasMoreTokens()) {
					title = str.nextToken();
					title = title.trim();
					init_val = str.nextToken();
					if (init_val.indexOf("$") != -1) {
						s = init_val.indexOf("$");
						if (s > 0) {
							final_val = init_val.substring(0, s);
							map.put(title, final_val);
						}
					}
				}

			}
			br.close();
		}

	//	System.out.println("Hashmap completed");
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
		br = new BufferedReader(new FileReader(total1));
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

		try {
			addvalues();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String values = value.toString();
		String value1 = "";
		int e = 0;
		int degree = 0;
		e = values.indexOf("$$$");
		if (e + 3 < values.length()) {
			//System.out.println("in E");
			degree = Integer.valueOf(values.substring(e + 3));
		}
		if (values.indexOf("!!!") != -1) {
			//System.out.println("HI!");
			s = values.indexOf("!!!");
			if (s > 0) {
				//System.out.println("I");
				value1 = values.substring(0, s);
				//System.out.println(values);
				value1 = value1.trim();
				if(map.get(value1)!=null){
				String title_value = map.get(value1);

				//System.out.println(title_value);

				if (title_value.indexOf("%") != -1) {
					//System.out.println("In Title Value");
					int some = title_value.indexOf("%");
					if (some > 0) {
						/*System.out.println("In IP");
						System.out.println(values);*/
						String IP = title_value.substring(0, some);
						if (some + 1 < title_value.length()) {
							//System.out.println("In TP");
							//System.out.println(values);
							String TP = title_value.substring(some + 1, title_value.length());
							//System.out.println(TP);
							Double next_ip = Double.valueOf(IP) * (1d / degree);
							//System.out.println(next_ip);
							Double next_tp = Double.valueOf(TP) * (1d / degree);
							//System.out.println(next_tp);
							String Final_IP = String.format("%.10f", next_ip);
							//System.out.println(Final_IP);
							String Final_TP = String.format("%.10f", next_tp);
							//System.out.println(Final_IP);
							
							if (e > s + 3) {
								//System.out.println(values);
								values = values.substring(s + 3, e);
								StringTokenizer str = new StringTokenizer(values, "!!!");

								while (str.hasMoreTokens()) {
									String link = str.nextToken();
									//System.out.println("In while loop now");
									String Fin_Mult = Final_IP + "#" + Final_IP + "##" + total;
									context.write(new Text(link), new Text(Fin_Mult));
								}
							}
						}
					}
				}
			}
		}
	}
}
}
