import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class CombineKeysDemo {

	public static class MapKey extends Mapper<LongWritable, Text, Text, IntWritable>
	{
		Text name = new Text();
		IntWritable mark = new IntWritable();
		public void map(LongWritable key, Text values, Context context) throws IOException, InterruptedException
		{
			StringTokenizer str = new StringTokenizer(values.toString(), ",");
			
			while(str.hasMoreTokens())
			{
				name.set(str.nextToken());
				mark.set(Integer.parseInt(str.nextToken()));
			}
			context.write(name, mark);
		}
	}
	
	public static class ReduceName extends Reducer<Text, IntWritable, Text, IntWritable>
	{
		IntWritable total = new IntWritable();
		public void reduce(Text name, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException
		{
			int sum =0;
			for(IntWritable val: values)
			{
				sum += val.get();
			}
			total.set(sum);
			context.write(name, total);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException
	{
		Configuration conf = new Configuration();
		Job job = new Job(conf, "Key Combo");
		
		job.setJarByClass(CombineKeysDemo.class);
		job.setMapperClass(MapKey.class);
		job.setCombinerClass(ReduceName.class);
		job.setReducerClass(ReduceName.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true)? 0:1);

	}

}
