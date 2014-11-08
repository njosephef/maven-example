import org.apache.spark.api.java.*;
import org.apache.spark.api.java.function.Function;

public class SimpleApp2 {
  public static void main(String[] args) {
    String logFile = "/home/scorpiovn/apps/README.md"; // Should be some file on your system
    JavaSparkContext sc = new JavaSparkContext("local", "Simple App",
      "/home/scorpiovn/apps/spark-1.1.0-bin-hadoop2.4", new String[]{"target/core-1.0.0.jar"});
    JavaRDD<String> logData = sc.textFile(logFile).cache();

    long numAs = logData.filter(new Function<String, Boolean>() {
    	/**
    	 * 
    	 */
		private static final long serialVersionUID = 1L;

		public Boolean call(String s) { return s.contains("a"); }
    	}).count();

    long numBs = logData.filter(new Function<String, Boolean>() {
    	/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public Boolean call(String s) { return s.contains("b"); }
    	}).count();

    System.out.println("Lines with a: " + numAs + ", lines with b: " + numBs);
  }
}