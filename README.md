# DS644-Section854-MapReduceProject
final project for course ds644 - Introduction to big data
Prerequisites:
sudo apt-get install ssh
sudo apt-get install pdsh
sudo apt-get install openjdk-8-jdk

Other helpful tools that you may want to install:
sudo apt-get net-tools
sudo apt-get smartmontools
sudo apt-get sysstat

Configure bash profile - add this to the end and execute bash (Note: easy way to execute bash is to logg off and log on or reboot)
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
export HADOOP_HOME=/home/ubuntu/hadoop-3.4.1
export PATH=$HADOOP_HOME/bin:$PATH
export PATH=$JAVA_HOME/sbin:$PATH
export PDSH_RCMD_TYPE=ssh

download  hadoop - https://archive.apache.org/dist/hadoop/common/hadoop-3.4.1/hadoop-3.4.1.tar.gz
Extract Hadoop - tar -xvzf hadoop-3.4.1.tar.gz

Configure the files - 
1. core-site.xml
2. hdfs-site.xml
3. mapred-site.xml
4. yarn-site.xml
5. hadoop-env.sh
6. 

## Core-site.xml
```
<configuration>
    <property>
        <name>io.file.buffer.size</name>
        <value>131072</value>
    </property>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://bigdata-server:9000</value> ### pay attention - this is the name of the leader machine. You may have a different name.
    </property>
    <property>
        <name>hadoop.tmp.dir</name>
        <value>/home/ubuntu/hadoop-3.4.1/tmp</value> ### pay attention - this is the path where we describe the location for tmp directory. You can have it anywhere on your local.
    </property>
    <property>
        <name>ipc.client.connect.timeout</name>
        <value>30000</value>
    </property>
    <property>
        <name>ipc.client.connect.max.retries</name>
        <value>5</value>
    </property>
    <property>
        <name>hadoop.security.authentication</name>
        <value>simple</value> ### pay attention - this is saying the we are not going to use kerbros authentication and instead its simple authentication. This may/is production grade setting as every organisation has different security standards.
    </property>
</configuration>
```
## hdfs-site.xml
```
<configuration>
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>/home/ubuntu/hadoop-3.4.1/dfs/name</value> ### pay attention - this is the path where we describe the location for name node index data to be stored. This is internal to hadoop functioning. this is relevant to leader node/master node.
    </property>
    <property>
        <name>dfs.namenode.data.dir</name>
        <value>[DISK]file:///home/ubuntu/hadoop-3.4.1/dfs/data/</value>  ### pay attention - this is the path we describe where the files will get stored on the worker nodes. Make sure that such path exist for them.
    </property>
    <property>
        <name>dfs.replication</name>
        <value>2</value> ### pay attention - this is the number of data copies that you want hadoop to maintian across data nodes for fault tolerance. Ideal is 3 but since I have just 2 data nodes, I have kept it to 2. You don't need to match this to data nodes but this number cannot exceed the number data nodes.
    </property>
</configuration>
```
```
