package cn.hao.common.cloud.lb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Order(-1)
@Component
public class CustomLoadBalancer implements LoadBalancer {

    @Autowired
    private DiscoveryClient dc;

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    private final int getAndIncrement() {
        int current;
        int next;
        for (; ; ) {
            current = this.atomicInteger.get();
            //是否大于int最大值，大于重新开始计数
            next = current >= Integer.MAX_VALUE ? 0 : current + 1;
            if (this.atomicInteger.compareAndSet(current, next)) {
                return next;
            }
        }
    }

    @Override
    public ServiceInstance instance(List<ServiceInstance> serviceInstances) {
        int index = getAndIncrement() % serviceInstances.size();
        System.out.println("index====" + index);

        return serviceInstances.get(index);
    }

    @Override
    public ServiceInstance instance(String serviceId, DiscoveryClient discoveryClient) {
        List<ServiceInstance> serviceInstance = getServiceInstance(serviceId, discoveryClient);
        return instance(serviceInstance);
    }

    @Override
    public ServiceInstance instance(String serviceId) {
        List<ServiceInstance> instanceList = dc.getInstances(serviceId);
        if (CollectionUtils.isEmpty(instanceList)) {
            throw new RuntimeException("没有找到" + serviceId + "相关服务");
        }
        return instance(instanceList);
    }

    private List<ServiceInstance> getServiceInstance(String serviceId, DiscoveryClient discoveryClient) {
        List<ServiceInstance> instances = discoveryClient.getInstances(serviceId);
        if (CollectionUtils.isEmpty(instances)) {
            throw new RuntimeException("没有找到" + serviceId + "相关服务");
        }
        return instances;
    }
}
