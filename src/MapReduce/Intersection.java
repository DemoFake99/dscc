package MapReduce;

import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Intersection {

    public static class MapA extends Mapper<LongWritable, Text, Text, Text> {
        private Text keyEmit = new Text();
        private final Text valEmit = new Text("A");

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            for (String token : value.toString().split("\\s+")) {
                keyEmit.set(token);
                context.write(keyEmit, valEmit);
            }
        }
    }
    public static class MapB extends Mapper<LongWritable, Text, Text, Text> {
        private Text keyEmit = new Text();
        private final Text valEmit = new Text("B");

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {
            for (String token : value.toString().split("\\s+")) {
                keyEmit.set(token);
                context.write(keyEmit, valEmit);
            }
        }
    }
    public static class IntersectionReducer extends Reducer<Text, Text, Text, Text> {
        private final Text emptyVal = new Text("");
        @Override
        protected void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {
            boolean seenA = false, seenB = false;
            for (Text val : values) {
                String s = val.toString();
                if (s.equals("A")) seenA = true;
                else if (s.equals("B")) seenB = true;
                if (seenA && seenB) {
                    context.write(key, emptyVal);
                    return;
                }
            }
        }
    }
    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.err.println("Usage: Intersection <inputA> <inputB> <output>");
            System.exit(2);
        }
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        Path pA = new Path(otherArgs[0]);
        Path pB = new Path(otherArgs[1]);
        Path out = new Path(otherArgs[2]);
        FileSystem fs = FileSystem.get(conf);
        if (fs.exists(out)) fs.delete(out, true);

        Job job = Job.getInstance(conf, "Intersection");
        job.setJarByClass(Intersection.class);

        MultipleInputs.addInputPath(job, pA, TextInputFormat.class, MapA.class);
        MultipleInputs.addInputPath(job, pB, TextInputFormat.class, MapB.class);

        job.setReducerClass(IntersectionReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        FileOutputFormat.setOutputPath(job, out);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}

