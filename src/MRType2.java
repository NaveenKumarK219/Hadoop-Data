import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class MRType2 {

	public static class MarksMap extends Mapper<Object, Text, Text, Text>
	{
		private Text name = new Text();
		private Text marks = new Text();
		public void map(Object key, Text values, Context context) throws IOException,InterruptedException
		{
			//String[] line = values.toString().split(",");
			StringTokenizer str = new StringTokenizer(values.toString(), ",");
			while(str.hasMoreTokens())
			{
				String n1 = str.nextToken();
				String m1 = str.nextToken();
				if(n1.equals("MARY")|n1.equals("SUSAN"))
				{
					name.set(n1);
					marks.set(m1);
					context.write(name, marks);
				}
			}
		}
	}
	
	public static class StdCombiner extends Reducer<Text, Text, Text, Text>
	{
		private Text subject = new Text();
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException, NumberFormatException
		{
			ArrayList<String> al = new ArrayList<String>();
			
			for(Text iw : values)
			{
				al.add(iw.toString());
			}
			Iterator<String> itr = al.iterator();
			String s1 = null,s2="0";
			int marks =0, max = 0;
			while(itr.hasNext())
			{
				s1 = itr.next();
				marks = s1.compareTo(s2);
				s2 = s1;
				if(marks > 0)
				{
					max = al.indexOf(s1);
				}
			}
			int ch =max ;
			
			
			switch(ch)
			{
			case 0:	subject.set("English");
					break;
			case 1:	subject.set("Hindi");
					break;
			case 2:	subject.set("Telugu");
			break;
			case 3:	subject.set("Maths");
			break;
			case 4:	subject.set("Science");
			break;
			case 5:	subject.set("Social");
			break;
			case 6:	subject.set("Computers");
			break;
			default : subject.set(String.valueOf(ch));
					break;
			}
			
			//subject.set(max);
			context.write(key, subject);
			
		}
	}
	
	public static class MaxMarksReducer extends Reducer<Text, Text, Text, Text>
	{
		
		public void reduce(Text key, Text value, Context context) throws IOException, InterruptedException, NumberFormatException
		{
					context.write(key, value);
			
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args)throws Exception
	{
		Configuration conf = new Configuration();
		Job job = new Job(conf, "SubjectScore");
		
		job.setJarByClass(MRType2.class);
		job.setMapperClass(MarksMap.class);
		job.setReducerClass(MaxMarksReducer.class);
		job.setCombinerClass(StdCombiner.class);
		//job.setNumReduceTasks(0);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true)? 0:1);

	}

}
