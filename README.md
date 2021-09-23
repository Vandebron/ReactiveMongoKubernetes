# ReactiveMongo on Kubernetes connection failure

## Background
This a minimal example to reproduce a connection failure from Reactive Mongo when run on Kubernetes

## Instructions

```shell
sbt docker
```
```shell
docker run vdb-services/mongotest:latest
```
should produce loging as follows:
```shell
...
2021-09-23 17:23:33,189 [reactivemongo-akka.actor.default-dispatcher-2] [MongoDBSystem] DEBUG - [Supervisor-1/Connection-1] The node set is now authenticated
2021-09-23 17:23:33,189 [reactivemongo-akka.actor.default-dispatcher-2] [MongoDBSystem] DEBUG - [Supervisor-1/Connection-1] The primary is now authenticated
2021-09-23 17:23:33,189 [reactivemongo-akka.actor.default-dispatcher-6] [MongoConnection] DEBUG - [Supervisor-1/Connection-1] A node is available: ProtocolMetadata(3.0, 4.2)
2021-09-23 17:23:33,191 [reactivemongo-akka.actor.default-dispatcher-6] [MongoConnection] DEBUG - [Supervisor-1/Connection-1] A primary is available: ProtocolMetadata(3.0, 4.2)
2021-09-23 17:23:33,235 [reactivemongo-akka.actor.default-dispatcher-4] [MongoDBSystem] DEBUG - [Supervisor-1/Connection-1] Received a request expecting a response (3000): reactivemongo.core.actors.ExpectingResponse@4efc4d23
Found 2 items in testcollection collection
```

When the same container is deployed to kubernetes:
```shell
kubectl apply -f deployment.yaml
```
authentication fails with 
`[MongoDBSystem]  WARN - [Supervisor-1/Connection-1] Fails to process SCRAM-SHA-1 nonce for 1000
reactivemongo.api.commands.FailedAuthentication$$anon$1: CommandException['null']`

The full logs:
```
2021-09-23 17:42:26,231 [main] [Driver]  INFO - No mongo-async-driver configuration found
Opening connection
2021-09-23 17:42:26,647 [scala-execution-context-global-13] [Driver]  INFO - [Supervisor-1] Creating connection: Connection-1
2021-09-23 17:42:26,648 [reactivemongo-akka.actor.default-dispatcher-3] [Driver] DEBUG - [Supervisor-1] Add connection to the supervisor: Connection-1
2021-09-23 17:42:26,652 [scala-execution-context-global-13] [MongoConnection] DEBUG - [Supervisor-1/Connection-1] Waiting is available...
2021-09-23 17:42:26,669 [reactivemongo-akka.actor.default-dispatcher-5] [MongoDBSystem]  INFO - [Supervisor-1/Connection-1] Starting the MongoDBSystem
2021-09-23 17:42:26,671 [reactivemongo-akka.actor.default-dispatcher-5] [InternalLoggerFactory] DEBUG - Using SLF4J as the default logging framework
2021-09-23 17:42:26,672 [reactivemongo-akka.actor.default-dispatcher-5] [Pack] DEBUG - Cannot use Netty KQueue (shaded: true)
java.lang.ClassNotFoundException: reactivemongo.io.netty.channel.kqueue.KQueueSocketChannel
	at java.net.URLClassLoader.findClass(URLClassLoader.java:382)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:418)
	at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:352)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:351)
	at java.lang.Class.forName0(Native Method)
	at java.lang.Class.forName(Class.java:264)
	at reactivemongo.core.netty.Pack$.kqueue(Pack.scala:60)
	at reactivemongo.core.netty.Pack$.apply(Pack.scala:45)
	at reactivemongo.core.netty.ChannelFactory.<init>(ChannelFactory.scala:50)
	at reactivemongo.core.actors.StandardDBSystem.newChannelFactory(MongoDBSystem.scala:1840)
	at reactivemongo.core.actors.MongoDBSystem.preStart(MongoDBSystem.scala:345)
	at reactivemongo.core.actors.MongoDBSystem.preStart$(MongoDBSystem.scala:342)
	at reactivemongo.core.actors.StandardDBSystem.preStart(MongoDBSystem.scala:1832)
	at akka.actor.Actor.aroundPreStart(Actor.scala:550)
	at akka.actor.Actor.aroundPreStart$(Actor.scala:550)
	at reactivemongo.core.actors.StandardDBSystem.aroundPreStart(MongoDBSystem.scala:1832)
	at akka.actor.ActorCell.create(ActorCell.scala:676)
	at akka.actor.ActorCell.invokeAll$1(ActorCell.scala:547)
	at akka.actor.ActorCell.systemInvoke(ActorCell.scala:569)
	at akka.dispatch.Mailbox.processAllSystemMessages(Mailbox.scala:293)
	at akka.dispatch.Mailbox.run(Mailbox.scala:228)
	at akka.dispatch.Mailbox.exec(Mailbox.scala:241)
	at akka.dispatch.forkjoin.ForkJoinTask.doExec(ForkJoinTask.java:260)
	at akka.dispatch.forkjoin.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1339)
	at akka.dispatch.forkjoin.ForkJoinPool.runWorker(ForkJoinPool.java:1979)
	at akka.dispatch.forkjoin.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:107)
2021-09-23 17:42:26,673 [reactivemongo-akka.actor.default-dispatcher-5] [Pack] DEBUG - Cannot use Netty EPoll (shaded: true)
java.lang.ClassNotFoundException: reactivemongo.io.netty.channel.epoll.EpollSocketChannel
	at java.net.URLClassLoader.findClass(URLClassLoader.java:382)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:418)
	at sun.misc.Launcher$AppClassLoader.loadClass(Launcher.java:352)
	at java.lang.ClassLoader.loadClass(ClassLoader.java:351)
	at java.lang.Class.forName0(Native Method)
	at java.lang.Class.forName(Class.java:264)
	at reactivemongo.core.netty.Pack$.epoll(Pack.scala:86)
	at reactivemongo.core.netty.Pack$.$anonfun$apply$1(Pack.scala:45)
	at scala.Option.orElse(Option.scala:447)
	at reactivemongo.core.netty.Pack$.apply(Pack.scala:45)
	at reactivemongo.core.netty.ChannelFactory.<init>(ChannelFactory.scala:50)
	at reactivemongo.core.actors.StandardDBSystem.newChannelFactory(MongoDBSystem.scala:1840)
	at reactivemongo.core.actors.MongoDBSystem.preStart(MongoDBSystem.scala:345)
	at reactivemongo.core.actors.MongoDBSystem.preStart$(MongoDBSystem.scala:342)
	at reactivemongo.core.actors.StandardDBSystem.preStart(MongoDBSystem.scala:1832)
	at akka.actor.Actor.aroundPreStart(Actor.scala:550)
	at akka.actor.Actor.aroundPreStart$(Actor.scala:550)
	at reactivemongo.core.actors.StandardDBSystem.aroundPreStart(MongoDBSystem.scala:1832)
	at akka.actor.ActorCell.create(ActorCell.scala:676)
	at akka.actor.ActorCell.invokeAll$1(ActorCell.scala:547)
	at akka.actor.ActorCell.systemInvoke(ActorCell.scala:569)
	at akka.dispatch.Mailbox.processAllSystemMessages(Mailbox.scala:293)
	at akka.dispatch.Mailbox.run(Mailbox.scala:228)
	at akka.dispatch.Mailbox.exec(Mailbox.scala:241)
	at akka.dispatch.forkjoin.ForkJoinTask.doExec(ForkJoinTask.java:260)
	at akka.dispatch.forkjoin.ForkJoinPool$WorkQueue.runTask(ForkJoinPool.java:1339)
	at akka.dispatch.forkjoin.ForkJoinPool.runWorker(ForkJoinPool.java:1979)
	at akka.dispatch.forkjoin.ForkJoinWorkerThread.run(ForkJoinWorkerThread.java:107)
2021-09-23 17:42:26,677 [reactivemongo-akka.actor.default-dispatcher-5] [Pack]  INFO - Instantiated reactivemongo.core.netty.Pack
2021-09-23 17:42:26,679 [reactivemongo-akka.actor.default-dispatcher-5] [MultithreadEventLoopGroup] DEBUG - -Dio.netty.eventLoopThreads: 2
2021-09-23 17:42:26,685 [reactivemongo-akka.actor.default-dispatcher-5] [InternalThreadLocalMap] DEBUG - -Dio.netty.threadLocalMap.stringBuilder.initialSize: 1024
2021-09-23 17:42:26,685 [reactivemongo-akka.actor.default-dispatcher-5] [InternalThreadLocalMap] DEBUG - -Dio.netty.threadLocalMap.stringBuilder.maxSize: 4096
2021-09-23 17:42:26,688 [reactivemongo-akka.actor.default-dispatcher-5] [NioEventLoop] DEBUG - -Dio.netty.noKeySetOptimization: false
2021-09-23 17:42:26,688 [reactivemongo-akka.actor.default-dispatcher-5] [NioEventLoop] DEBUG - -Dio.netty.selectorAutoRebuildThreshold: 512
2021-09-23 17:42:26,695 [reactivemongo-akka.actor.default-dispatcher-5] [PlatformDependent0] DEBUG - -Dio.netty.noUnsafe: false
2021-09-23 17:42:26,695 [reactivemongo-akka.actor.default-dispatcher-5] [PlatformDependent0] DEBUG - Java version: 8
2021-09-23 17:42:26,695 [reactivemongo-akka.actor.default-dispatcher-5] [PlatformDependent0] DEBUG - sun.misc.Unsafe.theUnsafe: available
2021-09-23 17:42:26,696 [reactivemongo-akka.actor.default-dispatcher-5] [PlatformDependent0] DEBUG - sun.misc.Unsafe.copyMemory: available
2021-09-23 17:42:26,696 [reactivemongo-akka.actor.default-dispatcher-5] [PlatformDependent0] DEBUG - java.nio.Buffer.address: available
2021-09-23 17:42:26,696 [reactivemongo-akka.actor.default-dispatcher-5] [PlatformDependent0] DEBUG - direct buffer constructor: available
2021-09-23 17:42:26,696 [reactivemongo-akka.actor.default-dispatcher-5] [PlatformDependent0] DEBUG - java.nio.Bits.unaligned: available, true
2021-09-23 17:42:26,696 [reactivemongo-akka.actor.default-dispatcher-5] [PlatformDependent0] DEBUG - jdk.internal.misc.Unsafe.allocateUninitializedArray(int): unavailable prior to Java9
2021-09-23 17:42:26,696 [reactivemongo-akka.actor.default-dispatcher-5] [PlatformDependent0] DEBUG - java.nio.DirectByteBuffer.<init>(long, int): available
2021-09-23 17:42:26,696 [reactivemongo-akka.actor.default-dispatcher-5] [PlatformDependent] DEBUG - sun.misc.Unsafe: available
2021-09-23 17:42:26,696 [reactivemongo-akka.actor.default-dispatcher-5] [PlatformDependent] DEBUG - -Dio.netty.tmpdir: /tmp (java.io.tmpdir)
2021-09-23 17:42:26,696 [reactivemongo-akka.actor.default-dispatcher-5] [PlatformDependent] DEBUG - -Dio.netty.bitMode: 64 (sun.arch.data.model)
2021-09-23 17:42:26,697 [reactivemongo-akka.actor.default-dispatcher-5] [PlatformDependent] DEBUG - -Dio.netty.maxDirectMemory: 1822556160 bytes
2021-09-23 17:42:26,697 [reactivemongo-akka.actor.default-dispatcher-5] [PlatformDependent] DEBUG - -Dio.netty.uninitializedArrayAllocationThreshold: -1
2021-09-23 17:42:26,697 [reactivemongo-akka.actor.default-dispatcher-5] [CleanerJava6] DEBUG - java.nio.ByteBuffer.cleaner(): available
2021-09-23 17:42:26,697 [reactivemongo-akka.actor.default-dispatcher-5] [PlatformDependent] DEBUG - -Dio.netty.noPreferDirect: false
2021-09-23 17:42:26,700 [reactivemongo-akka.actor.default-dispatcher-5] [PlatformDependent] DEBUG - org.jctools-core.MpscChunkedArrayQueue: available
2021-09-23 17:42:26,708 [reactivemongo-akka.actor.default-dispatcher-5] [MongoDBSystem] DEBUG - [Supervisor-1/Connection-1] Initial node set: {{NodeSet None Node[testcluster-shard-00-02.bkyvc.mongodb.net:27017: Unknown<25142445730386ns> (0/0/0 available connections), latency=9223372036s, authenticated={}] | Node[testcluster-shard-00-00.bkyvc.mongodb.net:27017: Unknown<25142445730386ns> (0/0/0 available connections), latency=9223372036s, authenticated={}] | Node[testcluster-shard-00-01.bkyvc.mongodb.net:27017: Unknown<25142445730386ns> (0/0/0 available connections), latency=9223372036s, authenticated={}] }}
2021-09-23 17:42:26,717 [reactivemongo-akka.actor.default-dispatcher-5] [DefaultChannelId] DEBUG - -Dio.netty.processId: 1 (auto-detected)
2021-09-23 17:42:26,718 [reactivemongo-akka.actor.default-dispatcher-5] [NetUtil] DEBUG - -Djava.net.preferIPv4Stack: false
2021-09-23 17:42:26,718 [reactivemongo-akka.actor.default-dispatcher-5] [NetUtil] DEBUG - -Djava.net.preferIPv6Addresses: false
2021-09-23 17:42:26,718 [reactivemongo-akka.actor.default-dispatcher-5] [NetUtilInitializations] DEBUG - Loopback interface: lo (lo, 127.0.0.1)
2021-09-23 17:42:26,719 [reactivemongo-akka.actor.default-dispatcher-5] [NetUtil] DEBUG - /proc/sys/net/core/somaxconn: 4096
2021-09-23 17:42:26,719 [reactivemongo-akka.actor.default-dispatcher-5] [DefaultChannelId] DEBUG - -Dio.netty.machineId: 02:42:ac:ff:fe:11:00:03 (auto-detected)
2021-09-23 17:42:26,723 [reactivemongo-akka.actor.default-dispatcher-5] [ResourceLeakDetector] DEBUG - -Dreactivemongo.io.netty.leakDetection.level: simple
2021-09-23 17:42:26,723 [reactivemongo-akka.actor.default-dispatcher-5] [ResourceLeakDetector] DEBUG - -Dreactivemongo.io.netty.leakDetection.targetRecords: 4
2021-09-23 17:42:26,734 [reactivemongo-akka.actor.default-dispatcher-5] [PooledByteBufAllocator] DEBUG - -Dio.netty.allocator.numHeapArenas: 2
2021-09-23 17:42:26,734 [reactivemongo-akka.actor.default-dispatcher-5] [PooledByteBufAllocator] DEBUG - -Dio.netty.allocator.numDirectArenas: 2
2021-09-23 17:42:26,734 [reactivemongo-akka.actor.default-dispatcher-5] [PooledByteBufAllocator] DEBUG - -Dio.netty.allocator.pageSize: 8192
2021-09-23 17:42:26,734 [reactivemongo-akka.actor.default-dispatcher-5] [PooledByteBufAllocator] DEBUG - -Dio.netty.allocator.maxOrder: 11
2021-09-23 17:42:26,734 [reactivemongo-akka.actor.default-dispatcher-5] [PooledByteBufAllocator] DEBUG - -Dio.netty.allocator.chunkSize: 16777216
2021-09-23 17:42:26,734 [reactivemongo-akka.actor.default-dispatcher-5] [PooledByteBufAllocator] DEBUG - -Dio.netty.allocator.smallCacheSize: 256
2021-09-23 17:42:26,734 [reactivemongo-akka.actor.default-dispatcher-5] [PooledByteBufAllocator] DEBUG - -Dio.netty.allocator.normalCacheSize: 64
2021-09-23 17:42:26,734 [reactivemongo-akka.actor.default-dispatcher-5] [PooledByteBufAllocator] DEBUG - -Dio.netty.allocator.maxCachedBufferCapacity: 32768
2021-09-23 17:42:26,734 [reactivemongo-akka.actor.default-dispatcher-5] [PooledByteBufAllocator] DEBUG - -Dio.netty.allocator.cacheTrimInterval: 8192
2021-09-23 17:42:26,734 [reactivemongo-akka.actor.default-dispatcher-5] [PooledByteBufAllocator] DEBUG - -Dio.netty.allocator.cacheTrimIntervalMillis: 0
2021-09-23 17:42:26,734 [reactivemongo-akka.actor.default-dispatcher-5] [PooledByteBufAllocator] DEBUG - -Dio.netty.allocator.useCacheForAllThreads: true
2021-09-23 17:42:26,734 [reactivemongo-akka.actor.default-dispatcher-5] [PooledByteBufAllocator] DEBUG - -Dio.netty.allocator.maxCachedByteBuffersPerChunk: 1023
2021-09-23 17:42:26,737 [reactivemongo-akka.actor.default-dispatcher-5] [ByteBufUtil] DEBUG - -Dio.netty.allocator.type: pooled
2021-09-23 17:42:26,737 [reactivemongo-akka.actor.default-dispatcher-5] [ByteBufUtil] DEBUG - -Dio.netty.threadLocalDirectBufferSize: 0
2021-09-23 17:42:26,737 [reactivemongo-akka.actor.default-dispatcher-5] [ByteBufUtil] DEBUG - -Dio.netty.maxThreadLocalCharBufferSize: 16384
2021-09-23 17:42:26,750 [reactivemongo-akka.actor.default-dispatcher-5] [ChannelFactory] DEBUG - [Supervisor-1/Connection-1] Created new channel #83413f91 to testcluster-shard-00-02.bkyvc.mongodb.net:27017 (registered = true)
2021-09-23 17:42:26,750 [nioEventLoopGroup-2-1] [ChannelFactory] DEBUG - [Supervisor-1/Connection-1] Initializing channel 83413f91 to testcluster-shard-00-02.bkyvc.mongodb.net:27017 (Actor[akka://reactivemongo/user/Connection-1#735628298])
2021-09-23 17:42:26,751 [reactivemongo-akka.actor.default-dispatcher-5] [ChannelFactory] DEBUG - [Supervisor-1/Connection-1] Created new channel #93e3f69b to testcluster-shard-00-00.bkyvc.mongodb.net:27017 (registered = false)
2021-09-23 17:42:26,751 [nioEventLoopGroup-2-2] [ChannelFactory] DEBUG - [Supervisor-1/Connection-1] Initializing channel 93e3f69b to testcluster-shard-00-00.bkyvc.mongodb.net:27017 (Actor[akka://reactivemongo/user/Connection-1#735628298])
2021-09-23 17:42:26,751 [reactivemongo-akka.actor.default-dispatcher-5] [ChannelFactory] DEBUG - [Supervisor-1/Connection-1] Created new channel #7f3fcbf0 to testcluster-shard-00-01.bkyvc.mongodb.net:27017 (registered = false)
2021-09-23 17:42:26,752 [reactivemongo-akka.actor.default-dispatcher-5] [ChannelFactory] DEBUG - [Supervisor-1/Connection-1] Created new channel #65c2443c to testcluster-shard-00-02.bkyvc.mongodb.net:27017 (registered = false)
2021-09-23 17:42:26,753 [reactivemongo-akka.actor.default-dispatcher-5] [ChannelFactory] DEBUG - [Supervisor-1/Connection-1] Created new channel #914cb7c9 to testcluster-shard-00-00.bkyvc.mongodb.net:27017 (registered = false)
2021-09-23 17:42:26,754 [reactivemongo-akka.actor.default-dispatcher-5] [ChannelFactory] DEBUG - [Supervisor-1/Connection-1] Created new channel #22ac8e75 to testcluster-shard-00-01.bkyvc.mongodb.net:27017 (registered = false)
2021-09-23 17:42:26,760 [reactivemongo-akka.actor.default-dispatcher-5] [MongoDBSystem] DEBUG - [Supervisor-1/Connection-1] Register monitor Actor[akka://reactivemongo/user/Monitor-Connection-1#-825806880]
2021-09-23 17:42:26,872 [nioEventLoopGroup-2-2] [ChannelFactory] DEBUG - [Supervisor-1/Connection-1] Initializing channel 65c2443c to testcluster-shard-00-02.bkyvc.mongodb.net:27017 (Actor[akka://reactivemongo/user/Connection-1#735628298])
2021-09-23 17:42:26,872 [nioEventLoopGroup-2-1] [ChannelFactory] DEBUG - [Supervisor-1/Connection-1] Initializing channel 7f3fcbf0 to testcluster-shard-00-01.bkyvc.mongodb.net:27017 (Actor[akka://reactivemongo/user/Connection-1#735628298])
2021-09-23 17:42:26,873 [nioEventLoopGroup-2-2] [ChannelFactory] DEBUG - [Supervisor-1/Connection-1] Initializing channel 22ac8e75 to testcluster-shard-00-01.bkyvc.mongodb.net:27017 (Actor[akka://reactivemongo/user/Connection-1#735628298])
2021-09-23 17:42:26,892 [nioEventLoopGroup-2-1] [ChannelFactory] DEBUG - [Supervisor-1/Connection-1] Initializing channel 914cb7c9 to testcluster-shard-00-00.bkyvc.mongodb.net:27017 (Actor[akka://reactivemongo/user/Connection-1#735628298])
2021-09-23 17:42:26,938 [nioEventLoopGroup-2-2] [Recycler] DEBUG - -Dio.netty.recycler.maxCapacityPerThread: 4096
2021-09-23 17:42:26,938 [nioEventLoopGroup-2-2] [Recycler] DEBUG - -Dio.netty.recycler.maxSharedCapacityFactor: 2
2021-09-23 17:42:26,938 [nioEventLoopGroup-2-2] [Recycler] DEBUG - -Dio.netty.recycler.linkCapacity: 16
2021-09-23 17:42:26,938 [nioEventLoopGroup-2-2] [Recycler] DEBUG - -Dio.netty.recycler.ratio: 8
2021-09-23 17:42:26,938 [nioEventLoopGroup-2-2] [Recycler] DEBUG - -Dio.netty.recycler.delayedQueue.ratio: 8
2021-09-23 17:42:26,942 [nioEventLoopGroup-2-2] [AbstractByteBuf] DEBUG - -Dreactivemongo.io.netty.buffer.checkAccessible: true
2021-09-23 17:42:26,942 [nioEventLoopGroup-2-2] [AbstractByteBuf] DEBUG - -Dreactivemongo.io.netty.buffer.checkBounds: true
2021-09-23 17:42:26,943 [nioEventLoopGroup-2-2] [ResourceLeakDetectorFactory] DEBUG - Loaded default ResourceLeakDetector: reactivemongo.io.netty.util.ResourceLeakDetector@66c71bfe
2021-09-23 17:42:26,973 [reactivemongo-akka.actor.default-dispatcher-5] [MongoDBSystem] DEBUG - [Supervisor-1/Connection-1] Prepares a fresh IsMaster request to Node[testcluster-shard-00-00.bkyvc.mongodb.net:27017: Unknown<25142445730386ns> (0/0/1 available connections), latency=9223372036s, authenticated={}] (channel #93e3f69b@/172.17.0.3:35872)
2021-09-23 17:42:27,011 [reactivemongo-akka.actor.default-dispatcher-5] [MongoDBSystem] DEBUG - [Supervisor-1/Connection-1] Prepares a fresh IsMaster request to Node[testcluster-shard-00-02.bkyvc.mongodb.net:27017: Unknown<25142445730386ns> (0/0/1 available connections), latency=9223372036s, authenticated={}] (channel #83413f91@/172.17.0.3:49390)
2021-09-23 17:42:27,013 [reactivemongo-akka.actor.default-dispatcher-5] [MongoDBSystem] DEBUG - [Supervisor-1/Connection-1] Prepares a fresh IsMaster request to Node[testcluster-shard-00-01.bkyvc.mongodb.net:27017: Unknown<25142445730386ns> (0/0/1 available connections), latency=9223372036s, authenticated={}] (channel #7f3fcbf0@/172.17.0.3:55850)
2021-09-23 17:42:27,014 [reactivemongo-akka.actor.default-dispatcher-5] [MongoDBSystem] DEBUG - [Supervisor-1/Connection-1] Do not prepare a isMaster request to already probed testcluster-shard-00-01.bkyvc.mongodb.net:27017
2021-09-23 17:42:27,015 [reactivemongo-akka.actor.default-dispatcher-5] [MongoDBSystem] DEBUG - [Supervisor-1/Connection-1] Do not prepare a isMaster request to already probed testcluster-shard-00-00.bkyvc.mongodb.net:27017
2021-09-23 17:42:27,015 [reactivemongo-akka.actor.default-dispatcher-4] [MongoDBSystem] DEBUG - [Supervisor-1/Connection-1] Do not prepare a isMaster request to already probed testcluster-shard-00-02.bkyvc.mongodb.net:27017
2021-09-23 17:42:27,124 [nioEventLoopGroup-2-2] [SslHandler] DEBUG - [id: 0x93e3f69b, L:/172.17.0.3:35872 - R:testcluster-shard-00-00.bkyvc.mongodb.net/20.50.141.202:27017] HANDSHAKEN: protocol:TLSv1.2 cipher suite:TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384
2021-09-23 17:42:27,124 [nioEventLoopGroup-2-1] [SslHandler] DEBUG - [id: 0x83413f91, L:/172.17.0.3:49390 - R:testcluster-shard-00-02.bkyvc.mongodb.net/51.124.111.218:27017] HANDSHAKEN: protocol:TLSv1.2 cipher suite:TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384
2021-09-23 17:42:27,143 [nioEventLoopGroup-2-1] [SslHandler] DEBUG - [id: 0x7f3fcbf0, L:/172.17.0.3:55850 - R:testcluster-shard-00-01.bkyvc.mongodb.net/20.56.14.233:27017] HANDSHAKEN: protocol:TLSv1.2 cipher suite:TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384
2021-09-23 17:42:27,143 [nioEventLoopGroup-2-2] [SslHandler] DEBUG - [id: 0x22ac8e75, L:/172.17.0.3:55856 - R:testcluster-shard-00-01.bkyvc.mongodb.net/20.56.14.233:27017] HANDSHAKEN: protocol:TLSv1.2 cipher suite:TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384
2021-09-23 17:42:27,151 [nioEventLoopGroup-2-1] [SslHandler] DEBUG - [id: 0x914cb7c9, L:/172.17.0.3:35878 - R:testcluster-shard-00-00.bkyvc.mongodb.net/20.50.141.202:27017] HANDSHAKEN: protocol:TLSv1.2 cipher suite:TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384
2021-09-23 17:42:27,152 [nioEventLoopGroup-2-2] [SslHandler] DEBUG - [id: 0x65c2443c, L:/172.17.0.3:49396 - R:testcluster-shard-00-02.bkyvc.mongodb.net/51.124.111.218:27017] HANDSHAKEN: protocol:TLSv1.2 cipher suite:TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384
2021-09-23 17:42:27,213 [reactivemongo-akka.actor.default-dispatcher-5] [MongoDBSystem] DEBUG - [Supervisor-1/Connection-1] The node set is available (Set(testcluster-shard-00-02.bkyvc.mongodb.net:27017)); Waiting authentication: Set()
2021-09-23 17:42:27,218 [reactivemongo-akka.actor.default-dispatcher-5] [MongoDBSystem] DEBUG - [Supervisor-1/Connection-1] The node set is available (Set(testcluster-shard-00-00.bkyvc.mongodb.net:27017)); Waiting authentication: Set()
2021-09-23 17:42:27,224 [reactivemongo-akka.actor.default-dispatcher-5] [MongoDBSystem] DEBUG - [Supervisor-1/Connection-1] The node set is available (Set(testcluster-shard-00-01.bkyvc.mongodb.net:27017)); Waiting authentication: Set()
2021-09-23 17:42:27,230 [reactivemongo-akka.actor.default-dispatcher-5] [MongoDBSystem]  WARN - [Supervisor-1/Connection-1] Fails to process SCRAM-SHA-1 nonce for 1000
reactivemongo.api.commands.FailedAuthentication$$anon$1: CommandException['null']
2021-09-23 17:42:27,232 [reactivemongo-akka.actor.default-dispatcher-5] [MongoDBSystem] DEBUG - [Supervisor-1/Connection-1] No pending authentication is matching response: Authenticate(reactive-mongo-k8s, reactive-mongo-k8s-user)
2021-09-23 17:42:27,246 [reactivemongo-akka.actor.default-dispatcher-5] [MongoDBSystem]  WARN - [Supervisor-1/Connection-1] Fails to process SCRAM-SHA-1 nonce for 1001
reactivemongo.api.commands.FailedAuthentication$$anon$1: CommandException['null']
2021-09-23 17:42:27,247 [reactivemongo-akka.actor.default-dispatcher-5] [MongoDBSystem] DEBUG - [Supervisor-1/Connection-1] No pending authentication is matching response: Authenticate(reactive-mongo-k8s, reactive-mongo-k8s-user)
2021-09-23 17:42:27,252 [reactivemongo-akka.actor.default-dispatcher-5] [MongoDBSystem]  WARN - [Supervisor-1/Connection-1] Fails to process SCRAM-SHA-1 nonce for 1002
reactivemongo.api.commands.FailedAuthentication$$anon$1: CommandException['null']
2021-09-23 17:42:27,252 [reactivemongo-akka.actor.default-dispatcher-5] [MongoDBSystem] DEBUG - [Supervisor-1/Connection-1] No pending authentication is matching response: Authenticate(reactive-mongo-k8s, reactive-mongo-k8s-user)
```
