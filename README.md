# DS644-Section854-MapReduceProject
final project for course ds644 - Introduction to big data

## Prerequisites:
sudo apt-get install ssh
sudo apt-get install pdsh
sudo apt-get install openjdk-8-jdk

Other helpful tools that you may want to install:
sudo apt-get install net-tools
sudo apt-get install smartmontools
sudo apt-get install sysstat

## Additional - 
hadoopuser is the user that I created and used for hadoop. This user is created for every node as part of my implementation. 
To unzip tar files use the command - ``` tar -xvzf <filename> ``` Example ```tar -xvzf hadoop-3.4.1.tar.gz```
You can scp to transfer the files across nodes.

### Setting up passwordless access:
1. Use this command to generate ssh key ```ssh-keygen -t rsa -N "" -f /home/hadoopuser/.ssh/id_rsa```
2. Now copy all the public keys from all the nodes using the command ```cat .ssh/id_rsa.pub```
3. Once all the public keys are copied, you need to add all these keys to all the machines/nodes authorized_keys files. You can use VIM or nano commandline editor or anyother way that you find suitable to add this information to the file.
4. Now you need to test by performing ssh connection using the command ```ssh nodename``` Example '''ssh bigdata-datanode1``` from let's say bigdata-server. Make sure that all the nodes are reachable.
5. You may need to modify the hosts file for dns name to ip translations if not available.

### Network translation basics:
You can edit the hosts file and add the IP and host names. Example Command to use ```sudo nano /etc/hosts``` or ```sudo vim /etc/hosts```. This is not recommended for production as mostly its expected to have better elaborte manner of handling dns translations.

## Configuration

### Setting up bash profile
Configure bash profile - add this to the end and execute bash (Note: easy way to execute bash is to logg off and log on or reboot) . Depending on the linux verion and flavour, this file can be .bashrc or .bash_profile or .profile file or anything else. You may need to find that out.
Hint: - if you hit an empty file, you are most likely at a wrong place as this file have additional script content.

export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH
export PATH=$JAVA_HOME/sbin:$PATH
export HADOOP_HOME=/home/ubuntu/hadoop-3.4.1
export PATH=$HADOOP_HOME/bin:$PATH
export HADOOP_MAPPED_HOME=$HADOOP_HOME
export HADOOP_COMMON_HOME=$HADOOP_HOME
export HADOOP_HDFS_HOME=$HADOOP_HOME
export YARN_HOME=$HADOOP_HOME
export PDSH_RCMD_TYPE=ssh

### Downloading Hadoop and some basic settings:
download  hadoop - https://archive.apache.org/dist/hadoop/common/hadoop-3.4.1/hadoop-3.4.1.tar.gz
Extract Hadoop - tar -xvzf hadoop-3.4.1.tar.gz

Configure the files - 
1. core-site.xml
2. hdfs-site.xml
3. mapred-site.xml
4. yarn-site.xml
5. hadoop-env.sh
6.  workers

## Core-site.xml
```
<configuration>
    <property>
        <name>io.file.buffer.size</name>
        <value>131072</value>
    </property>
    <property>
        <name>fs.defaultFS</name>
        <value>hdfs://bigdata-server:9000</value>
        <!--
            ### Pay Attention ### -
            This is the name of the leader machine. You may have a different name.
        -->
    </property>
    <property>
        <name>hadoop.tmp.dir</name>
        <value>/home/hadoopuser/hadoop-3.4.1/tmp</value>
        <!--
            ### Pay Attention ### -
            This is the path where we describe the location for tmp directory. You can have it anywhere on your local.
        -->
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
        <value>simple</value>
        <!--
            ### Pay Attention ### -
            This is saying the we are not going to use kerbros authentication and instead its simple authentication.
            This may/is production grade setting as every organisation has different security standards.
        -->
    </property>
</configuration>
```
## hdfs-site.xml
```
<configuration>
    <property>
        <name>dfs.namenode.name.dir</name>
        <value>/home/hadoopuser/hadoop-3.4.1/dfs/name</value>
        <!--
            ### Pay Attention ### -
            This is the path where we describe the location for name node index data to be stored. This is internal to hadoop functioning. This is relevant to leader node/master node.
        -->
    </property>
    <property>
        <name>dfs.namenode.data.dir</name>
        <value>[DISK]file:///home/hadoopuser/hadoop-3.4.1/dfs/data/</value>
        <!--
            ### Pay Attention ### -
            This is the path we describe where the files will get stored on the worker nodes. Make sure that such path exist for each worker node.
            Next - hadoop has introduce a new concept of storage policy. More Info - https://hadoop.apache.org/docs/r3.4.1/hadoop-project-dist/hadoop-hdfs/ArchivalStorage.html
            Possible values can be DISK, SSD, ARCHIVE, RAM_DISK, NVDIMM.            
        -->
    </property>
    <property>
        <name>dfs.replication</name>
        <value>2</value>
        <!--
            ### Pay Attention ### -
            This is the number of data copies that you want hadoop to maintian across data nodes for fault tolerance.
            Ideal is 3 for decent fault tolerance. Most businesses may find 2 copies to be sufficient for analytical purposes. Important - Replication value cannot exceed worker nodes.
    </property>
</configuration>
```
### mapred-site.xml
```
<configuration>
    <property>
        <name>mapreduce.framework.name</name>
        <value>yarn</value>
    </property>
    <property>
        <name>mapreduce.map.memory.mb</name>
        <value>4096</value>
    </property>
    <property>
        <name>mapreduce.map.java.opts</name>
        <value>-Xmx3072m</value>
    </property>
    <property>
        <name>mapreduce.reduce.memory.mb</name>
        <value>4096</value>
    </property>
    <property>
        <name>mapreduce.reduce.java.opts</name>
        <value>-Xmx3072m</value>
    </property>
    <property>
        <name>mapreduce.task.io.sort.mb</name>
        <value>512</value>
    </property>
    <property>
        <name>mapreduce.task.io.sort.factor</name>
        <value>100</value>
    </property>
    <property>
        <name>mapreduce.application.classpath</name>
        <value>$HADOOP_MAPRED_HOME/share/hadoop/mapreduce/*:$HADOOP_MAPRED_HOME/share/hadoop/mapreduce/lib/*</value>
        <!--
            ### Pay Attention ### -
            Hadoop directory has a share sub durectory and heirarchy. It should all resolve to valid path for all jar files as they get added to classpath.
            Convention is that the paths are all identical across all the machines to avoid failures.
        -->
    </property>
    <property>
        <name>yarn.app.mapreduce.am.env</name>
        <value>HADOOP_MAPRED_HOME=/home/hadoopuser/hadoop-3.4.1</value>
        <!--
            ### Pay Attention ### -
            This is the installation path of hadoop. You may have it at different location
        -->
    </property>
    <property>
        <name>mapreduce.map.env</name>
        <value>HADOOP_MAPRED_HOME=/home/hadoopuser/hadoop-3.4.1</value>
        <!--
            ### Pay Attention ### -
            This is the installation path of hadoop. You may have it at different location
        -->
    </property>
    <property>
        <name>mapreduce.reduce.env</name>
        <value>HADOOP_MAPRED_HOME=/home/hadoopuser/hadoop-3.4.1</value>
        <!--
            ### Pay Attention ### -
            This is the installation path of hadoop. You may have it at different location
        -->
    </property>
</configuration>
```
### yarn-site.xml
```
<configuration>
        <property>
                <name>yarn.acl.enable</name>
                <value>false</value>
                <!--
                    ### Pay Attention ### -
                    We are suggesting that we do not intend to use access control. This will be important for production grade setup as you may need this info to control access and also for auditing.
                -->
        </property>
        <property>
                <name>yarn.resourcemanage.address</name>
                <value>bigdata-server:8032</value>                
        </property>
        <property>
                <name>yarn.resourcemanager.scheduler.address</name>
                <value>bigdata-server:8030</value>                
        </property>
        <property>
                <name>yarn.resourcemanager.resource-tracker.address</name>
                <value>bigdata-server:8035</value>                
        </property>
        <property>
                <name>yarn.resourcemanager.admin.address</name>
                <value>bigdata-server:8033</value>                
        </property>
        <property>
                <name>yearn.resourcemanager.webapp.address</name>
                <value>bigdata-server:8088</value>
        </property>
        <property>
                <name>yarn.nodemanager.aux-services</name>
                <value>mapreduce_shuffle</value>
        </property>        
        <property>
                <name>yarn.nodemanager.aux-service.mapreduce.shuffle.class</name>
                <value>org.apache.hadoop.mapred.ShuffleHandler</value>
        </property>
</configuration>
```
### hadoop-env.sh

Ensure that JAVA_HOME is supplied. Optionally, you can also supply configure memory usage for name node.
```
export JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
export HDFS_NAMENODE_OPTS="-XX:+UseParallelGC -Xmx4g"
```

### workers 

This file will have the dns names of all the data nodes in the cluster. It needs paswordless SSH to be setup across the cluster. Alternate way to estabilsh connection could be to specify the IP Addresses.
