package org.shivani.pagerank;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class PageRankMapper extends Mapper<LongWritable, Text, Text, Text> {

	@Override
	protected void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException {
		String line = value.toString();
		String page = "";
		String temp = "";
		// String link1 = "";

		int s, k;

		s = line.indexOf("<title>");
		k = line.lastIndexOf("</title>");
		page = line.substring(s + 7, k);
		// System.out.println("Vale inside the title tag:" + page);
		if (line.indexOf("<redirect title") == -1) {
			if (line.indexOf("<text") != -1) {
				s = line.indexOf("<text");
				k = line.indexOf("</text>");
				if (k > s) {
					temp = line.substring(s + 5, k);
					// System.out.println("Vale inside the text tag:" + temp);
					String re2 = "(\\[\\[.*?\\]\\])";
					Pattern p = Pattern.compile(re2, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
					Matcher m = p.matcher(temp);

					while (m.find()) {
						String c1 = m.group(1);
						if (c1.toString().indexOf('|') != -1) {
							StringBuilder link = new StringBuilder();
							s = c1.toString().indexOf('|');
							link = link.append(c1.toString().substring(2, s));
							context.write(new Text(page), new Text(link.toString()));
							//context.write(new Text("#####Shivani"), new Text(page.toString()));
						} else {
							StringBuilder link = new StringBuilder();
							link = link.append(c1.toString().substring(2, c1.toString().length() - 2));
							context.write(new Text(page), new Text(link.toString()));
							//context.write(new Text("#####Shivani"), new Text(page));
						}
					}

				}

			}
		}
	}
}