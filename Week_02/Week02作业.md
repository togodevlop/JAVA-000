## Week02-1

GC总结：

- 串行 GC：是一个单线程的垃圾收集器，在执行GC时，会触发STW(Stop The World)。在大内存多处理器的环境下该GC充满劣势，但因 **SerialGC** 本身简单高效的特性，它适合于应用内存使用不高、内存使用资源受限制以及单核处理器下。不适合服务端模式。

  -  年轻代使用`拷贝-复制`，老年代使用`标记-清除-整理`
  - 发生GC时，会暂停
  - 内存越小，GC次数越多

- 并行 GC：支持多线程并发收集，基于标记-整理算法实现。由于采用多线程并发收集，它的垃圾收集效率很高，对于高吞吐的应用支持更好.

  - 年轻代使用`拷贝-复制`，老年代使用`标记-清除-整理`
  - GC处理时，暂停业务处理，所有线程处理GC垃圾回收。平常运行时，所以线程都去处理业务。因此，吞吐量比较高。
  - 内存越小，GC次数越多

- CMS GC

  - 年轻代使用`拷贝-复制`，老年代使用`标记-清除`
  - CMS默认GC线程数是1/4，并且老年代只清除，无整理。所以当GC发生时，吞吐量不如`并行 GC`
  - CMS GC 6个阶段
    - 初始化标记 - 暂停GC
    - 并行标记
    - 并行预清理
    - 最终标记 - 暂停GC
    - 并行清理
    - 并行重置
  - 因为无`整理`，并且`CMS GC` `6阶段` 暂停时间短，所以延迟比较低
  - 内存越小，GC次数越多

- G1 GC

  G1（Garbage-First）是jdk9中默认垃圾收集器。通过“-XX:+UseG1GC”启动参数即可指定使用G1 GC。从整体来说，G1也是利用多CPU来缩短stop the world时间，并且是高效的**并发**垃圾收集器。但是G1不再像上文所述的垃圾收集器，需要分代配合不同的垃圾收集器，因为G1中的垃圾收集区域是“分区”（Region）的。G1的分代收集和以上垃圾收集器不同的就是除了有年轻代的ygc，全堆扫描的fullgc外，还有包含所有年轻代以及部分老年代Region的MixedGC。G1的优势还有可以通过调整参数，指定垃圾收集的最大允许pause time。下面会详细阐述下G1分区以及分代的概念，以及G1 GC的几种收集过程的分类

## Week02-2

```java
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import java.io.IOException;

public class HttpClientDemo {
    public static void main(String[] args) {
        doGetReq();
    }
    public static void doGetReq() {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpGet httpGet = new HttpGet("http://localhost:8801/test");
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            System.out.println("响应状态码：" + response.getStatusLine());
            String result = IOUtils.toString(entity.getContent());
            System.out.println("返回内容：" + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```

