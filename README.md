An example project
- Scala 2.10.4
- JDK 1.8

run:
    //mvn scala:run -DmainClass=com.jlab.demo.ScalaJob

CentOS
- spark://localhost.localdomain:7077
- ./bin/spark-class org.apache.spark.deploy.worker.Worker spark://localhost.localdomain:7077
- ./bin/spark-submit --class com.jlab.demo.ScalaJob --master spark://localhost.localdomain:7077 /home/training/git/maven-example/core/target/core-1.0.0.jar

Ubuntu
- spark://scorpiovn:7077
- ./bin/spark-class org.apache.spark.deploy.worker.Worker spark://scorpiovn:7077
- ./bin/spark-submit --class com.jlab.demo.ScalaJob --master spark://scorpiovn:7077 /home/scorpiovn/git/maven-example/core/target/core-1.0.0.jar /data/prisonbreakfirst/*.srt /home/scorpiovn/sparkout/prisonbreak.text



