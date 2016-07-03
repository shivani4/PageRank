package org.shivani.pagerank;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

public class PageRankPartitioner extends Partitioner<Text, Text> {

	@Override
	public int getPartition(Text text, Text value, int numberOfReducers) {
		// TODO Auto-generated method stub

		if (text.toString().equalsIgnoreCase("#####Shivani"))
			return 9;
		else
			return (text.hashCode() & Integer.MAX_VALUE) % 9;
	}

}