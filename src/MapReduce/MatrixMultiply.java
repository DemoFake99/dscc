package MapReduce;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MatrixMultiply {

    public static class MatrixMapper extends Mapper<Object, Text, Text, Text> {

        private static final int N = 2; // Matrix size (example: 2x2)

        @Override
        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            String line = value.toString().trim();
            if (line.isEmpty()) return;

            String[] parts = line.split(",");
            if (parts.length != 4) return;

            String matrixName = parts[0];
            int i = Integer.parseInt(parts[1]);
            int j = Integer.parseInt(parts[2]);
            int val = Integer.parseInt(parts[3]);

            if (matrixName.equals("A")) {
                // A[i][j] contributes to all C[i][col]
                for (int col = 0; col < N; col++) {
                    context.write(new Text(i + "," + col), new Text("A," + j + "," + val));
                }
            } else if (matrixName.equals("B")) {
                // B[i][j] contributes to all C[row][j]
                for (int row = 0; row < N; row++) {
                    context.write(new Text(row + "," + j), new Text("B," + i + "," + val));
                }
            }
        }
    }

    public static class MatrixReducer extends Reducer<Text, Text, Text, IntWritable> {

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context)
                throws IOException, InterruptedException {

            Map<Integer, Integer> mapA = new HashMap<>();
            Map<Integer, Integer> mapB = new HashMap<>();

            for (Text val : values) {
                String[] parts = val.toString().split(",");
                String matrixName = parts[0];
                int k = Integer.parseInt(parts[1]);
                int v = Integer.parseInt(parts[2]);

                if (matrixName.equals("A")) {
                    mapA.put(k, v);
                } else {
                    mapB.put(k, v);
                }
            }

            // Compute dot product for this (i,j)
            int result = 0;
            for (int k : mapA.keySet()) {
                if (mapB.containsKey(k)) {
                    result += mapA.get(k) * mapB.get(k);
                }
            }

            context.write(key, new IntWritable(result));
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "Matrix Multiply");

        job.setJarByClass(MatrixMultiply.class);
        job.setMapperClass(MatrixMapper.class);
        job.setReducerClass(MatrixReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));   // input dir
        FileOutputFormat.setOutputPath(job, new Path(args[1])); // output dir

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}