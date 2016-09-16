import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Reducer;



public class MRCoDecDemo {

	public static class MarksMapper extends Mapper<Object, Text, Text, Text>
	{
		private Text name = new Text();
		private Text marks = new Text();
		public void map(Object key, Text values, Context context) throws IOException,InterruptedException
		{
			StringTokenizer str = new StringTokenizer(values.toString(), ",");
			while(str.hasMoreTokens())
			{
				String n1 = str.nextToken();
				String m1 = str.nextToken();
				if(Integer.parseInt(m1) > 80)
				{
					name.set(n1);
					marks.set(m1);
					context.write(name, marks);
				}
			}
		}
	}
	
	public static class NameReducer extends Reducer<Text, Text, Text, Text>
	{
		private Text allmarks = new Text();
		public void reduce(Text name, Iterator<Text> values, Context context) throws IOException, InterruptedException
		{
			StringTokenizer str = new StringTokenizer(values.toString());
			String marks = null;
			while(str.hasMoreTokens())
			{
				marks = str.nextToken() + " ";
			}
			allmarks.set(marks);
			context.write(name, allmarks);
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException 
	{	
		Configuration conf = new Configuration();
		Job job = new Job(conf, "Dist_Score");
		
		conf.setBoolean("mapred.output.compress", true);
		conf.setClass("mapred.output.compression.Codec", GzipCodec.class, CompressionCodec.class);
		
		job.setJarByClass(MRCoDecDemo.class);
		job.setMapperClass(MarksMapper.class);
		job.setReducerClass(NameReducer.class);
		job.setCombinerClass(NameReducer.class);
		//job.setNumReduceTasks(0);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true)? 0:1);

	}

}
