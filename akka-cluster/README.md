## A Simple Cluster Example

See [application.conf](src/main/resources/application.conf)

See also [SimpleClusterApp.scala](src/main/scala/com/malsolo/akka/cluster/simple/cluster/simple/SimpleClusterApp.scala).

Open three terminal windows for starting the seed nodes as defined in the application.conf.

In the first terminal window, start the first seed node with the following command:

    sbt "runMain com.malsolo.akka.cluster.simple.SimpleClusterApp 2551"
    
In the second terminal window, start the second seed node with the following command:

    sbt "runMain com.malsolo.akka.cluster.simple.SimpleClusterApp 2552"

In the third terminal window, start the third seed node with the following command:

    sbt "runMain com.malsolo.akka.cluster.simple.SimpleClusterApp 2553"
    
Then open a fourth one to run an actor system that will join the cluster.

In the fourth terminal window, start one node with the following command:

    sbt "runMain com.malsolo.akka.cluster.simple.SimpleClusterApp 0"

Now you don't need to specify the port number, 0 means that it will use a random available port.

For each terminal, look at the log output in the different terminal windows to see how the nodes join the cluster.


