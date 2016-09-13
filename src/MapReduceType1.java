import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
//import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class MapReduceType1 {

	public static class StdMap extends Mapper<Object, Text, IntWritable, Text>
	{
		Text value1 = new Text();
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException
		{
			String[] line = value.toString().split("\t");
			IntWritable id = new IntWritable(); 
			value1.set(line[1]+" "+line[2]+" "+line[3]+" ");
			id.set(Integer.parseInt(line[0]));
			char name = line[1].charAt(0);
			int marks = Integer.parseInt(line[2]);
			if(marks > 80)
			{
				//if(name == 'N')
				{
					context.write(id, value1);
				}
			}
		}
	}
	
	public static class StdCombiner extends Reducer<IntWritable, Text, IntWritable, Text>
	{
		Text value2 = new Text();
		public void reduce(IntWritable key, Text values, Context context) throws IOException,InterruptedException
		{
			String[] line = values.toString().split(" ");
			
			char name = line[0].charAt(0);
			int marks = Integer.parseInt(line[1]);
			if(marks > 90)
			{
				if(name == 'N')
				{
					value2.set(line[0]+" "+line[1]+" "+line[2]+" ");
					context.write(key, value2);
				}
			}
		}
	}
	
	public static class StdReducer extends Reducer<IntWritable, Text, IntWritable, Text>
	{
		Text value3 = new Text();
		public void reduce(IntWritable key, Text values, Context context) throws IOException,InterruptedException
		{
			String[] line = values.toString().split(" ");
			
			if(line[2].equals("Sec"))
			{
				value3.set(line[0]+" "+line[1]+" "+line[2]+" ");
				context.write(key, value3);
			}
		}
	}
	
	/*public static class StdPartition extends Partitioner<IntWritable, Text>
	{

		@Override
		public int getPartition(IntWritable key, Text values, int numReducerTasks)
		{
			String[] line = values.toString().split(" ");
			
			if(line[2].equals("Hyd"))
			{
				return 0;
			}
			else
				return 1;
		}
		
	}*/
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException 
	{
		Configuration conf = new Configuration();
		Job job = new Job(conf, "StudentInfo");
		
		job.setJarByClass(MapReduceType1.class);
		job.setMapperClass(StdMap.class);
		job.setCombinerClass(StdCombiner.class);
		job.setReducerClass(StdReducer.class);
		
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(Text.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true)? 0:1);

	}

}
