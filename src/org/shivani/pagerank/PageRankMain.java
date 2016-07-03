package org.shivani.pagerank;

import java.net.URI;
import java.util.Collection;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
//import org.apache.hadoop.mapred.KeyValueTextInputFormat;
import org.apache.hadoop.mapred.TextInputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class PageRankMain {

	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.set("xmlinput.start", "<page>");
		conf.set("xmlinput.end", "</page>");
		Job job = new Job(conf);
		job.setJarByClass(PageRankMain.class);
		job.setJobName("MyParser");

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		job.setMapperClass(PageRankMapper.class);
		job.setReducerClass(PageRankReducer.class);

		// job.setPartitionerClass(PageRankPartitioner.class);
		job.setNumReduceTasks(10);

		job.setInputFormatClass(XmlInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.waitForCompletion(true);

		// Job for extracting titles
		Job job11 = new Job();
		job11.setJarByClass(PageRankMain.class);
		job11.setJobName("TitleExtractor");

		job11.setOutputKeyClass(Text.class);
		job11.setOutputValueClass(Text.class);

		job11.setMapperClass(TitleMap.class);
		// job11.setReducerClass(TitleReducer.class);

		for (int i = 0; i <= 9; i++) {
			FileInputFormat.addInputPath(job11, new Path(args[1] + "/" + "part-r-0000" + String.valueOf(i)));
		}
		FileOutputFormat.setOutputPath(job11, new Path(args[1] + "1"));
		job11.setOutputKeyClass(Text.class);
		job11.setOutputValueClass(Text.class);

		job11.waitForCompletion(true);
		
		

		// Another job for Matrix creation

		Job job1 = new Job();
		job1.setJarByClass(PageRankMain.class);
		job1.setJobName("MatrixCreation");

		job1.setMapperClass(MatrixCreationMap.class);
		job1.setReducerClass(MatrixCreationReduce.class);
		job1.setPartitionerClass(MatrixPartitioner.class);
		job1.setNumReduceTasks(10);
		for (int i = 0; i <= 9; i++) {
			FileInputFormat.addInputPath(job1, new Path(args[1] + "/" + "part-r-0000" + String.valueOf(i)));
		}
		FileOutputFormat.setOutputPath(job1, new Path(args[1] + "2"));
		job1.addCacheFile(new URI(args[1] + "1" + "/" + "part-r-00000" + "#CacheFile"));
		job1.setOutputKeyClass(Text.class);
		job1.setOutputValueClass(Text.class);
		job1.waitForCompletion(true);

		// Next job for Matrix Multiplication 1st iteration

		Job job2 = new Job();
		job2.setJarByClass(PageRankMain.class);
		job2.setJobName("MatrixMult1");
		// job1.addCacheFile(new URI("/output_Shivani_one/part-r-00009"));

		job2.setMapperClass(MatrixMul1Map.class);
		job2.setReducerClass(MatrixMul1Reduce.class);
		job2.setPartitionerClass(MatrixPartitioner.class);
		job2.setNumReduceTasks(10);
		for (int i = 0; i < 9; i++) {
			FileInputFormat.addInputPath(job2, new Path(args[1] + "2" + "/" + "part-r-0000" + String.valueOf(i)));
		}
		FileOutputFormat.setOutputPath(job2, new Path(args[1] + "3"));
		job2.addCacheFile(new URI(args[1] + "2" + "/" + "part-r-00009" + "#CacheFile2"));
		job2.setOutputKeyClass(Text.class);
		job2.setOutputValueClass(Text.class);
		job2.waitForCompletion(true);

		int out = 3;

		// Next job for Matrix Multiplication 24 iterations
		for (int j = 0; j < 25; j++) {
			System.out.println("In multi 1");

			Configuration config = new Configuration();
			config.set("Count_loop", String.valueOf(j));
			Job job3 = new Job(config);

			job3.setJarByClass(PageRankMain.class);
			job3.setJobName("MatrixMultiplicationComplete" + String.valueOf(j));
			job3.setMapperClass(MatrixMultiplyerIter.class);
			job3.setReducerClass(MatrixReduceIter.class);
			// Adding cache files from the previous output
			for (int i = 0; i <= 9; i++) {
				job3.addCacheFile(new URI(args[1] + String.valueOf(out) + "/" + "part-r-0000" + String.valueOf(i)
						+ "#Cache" + String.valueOf(j) + "_" + String.valueOf(i)));
			}

			// Sending the total count
			job3.addCacheFile(new URI(args[1] + "2" + "/" + "part-r-0000" + "9" + "#Total" + String.valueOf(j)));
			for (int i = 0; i < 9; i++) {
				FileInputFormat.addInputPath(job3, new Path(args[1] + "2" + "/" + "part-r-0000" + String.valueOf(i)));
			}

			out++;
			FileOutputFormat.setOutputPath(job3, new Path(args[1] + String.valueOf(out)));
			job3.setNumReduceTasks(10);

			job3.setOutputKeyClass(Text.class);
			job3.setOutputValueClass(Text.class);
			job3.waitForCompletion(true);
		}

		// Sorting on Idealized Page Rank
		Job job4 = new Job();
		job4.setJarByClass(PageRankMain.class);
		job4.setJobName("SortingIP");
		// job1.addCacheFile(new URI("/output_Shivani_one/part-r-00009"));

		job4.setMapperClass(SortingMapper.class);
		job4.setReducerClass(SortingReducer.class);
		// job4.setPartitionerClass(SortingPartitioner.class);
		// job4.setNumReduceTasks(10);
		for (int i = 0; i <= 9; i++) {
			FileInputFormat.addInputPath(job4,
					new Path(args[1] + String.valueOf(out) + "/" + "part-r-0000" + String.valueOf(i)));
		}
		FileOutputFormat.setOutputPath(job4, new Path(args[1] + "SortedIP"));
		// System.out.println("Cache File:" +
		// args[1]+"/"+"part-r-00009"+"#Title_Count");
		job4.addCacheFile(new URI(args[1] + "1" + "/" + "part-r-00000" + "#Title_Count"));
		job4.setOutputKeyClass(Text.class);
		job4.setOutputValueClass(Text.class);
		job4.waitForCompletion(true);

		// Sorting with taxation
		Job job5 = new Job();
		job5.setJarByClass(PageRankMain.class);
		job5.setJobName("SortingTP");
		// job1.addCacheFile(new URI("/output_Shivani_one/part-r-00009"));

		job5.setMapperClass(Sorted_TP_Map.class);
		job5.setReducerClass(Sorted_TP_Reduce.class);
		// job5.setPartitionerClass(SortingPartitioner.class);
		// job5.setNumReduceTasks(10);
		for (int i = 0; i <= 9; i++) {
			FileInputFormat.addInputPath(job5,
					new Path(args[1] + String.valueOf(out) + "/" + "part-r-0000" + String.valueOf(i)));
		}
		FileOutputFormat.setOutputPath(job5, new Path(args[1] + "SortedTP"));
		// System.out.println("Cache File:" +
		// args[1]+"/"+"part-r-00009"+"#Title_Count");
		job5.addCacheFile(new URI(args[1] + "1" + "/" + "part-r-00000" + "#Title_Count"));
		job5.setOutputKeyClass(Text.class);
		job5.setOutputValueClass(Text.class);
		job5.waitForCompletion(true);
		//Difference
		Job job6 = new Job();
		job6.setJarByClass(PageRankMain.class);
		job6.setJobName("Difference");
		// job1.addCacheFile(new URI("/output_Shivani_one/part-r-00009"));

		job6.setMapperClass(Average_CheckMap.class);
		job6.setReducerClass(Average_CheckReduce.class);
		// job5.setPartitionerClass(SortingPartitioner.class);
		// job6.setNumReduceTasks(10);
		for (int i = 0; i <= 9; i++) {
			FileInputFormat.addInputPath(job6,
					new Path(args[1] + String.valueOf(out) + "/" + "part-r-0000" + String.valueOf(i)));
		}
		FileOutputFormat.setOutputPath(job6, new Path(args[1] + "Difference"));
		// System.out.println("Cache File:" +
		// args[1]+"/"+"part-r-00009"+"#Title_Count");
		job6.addCacheFile(new URI(args[1] + "1" + "/" + "part-r-00000" + "#Title_Count"));
		job6.setOutputKeyClass(Text.class);
		job6.setOutputValueClass(Text.class);
		job6.waitForCompletion(true);
	}
}
	