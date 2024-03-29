package com.qs.producer.service.impl;

import com.qs.common.bean.Product;
import com.qs.producer.service.IProductService;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 
 * @author : qshomewy
 * @description : 产品提供接口实现类
 */
@Service
public class ProductService implements IProductService, ApplicationListener<WebServerInitializedEvent> {

    private static List<Product> productList = new ArrayList<>();

    public Product queryProductById(int id) {
        return productList.stream().filter(p->p.getId()==id).collect(Collectors.toList()).get(0);
    }


    public List<Product> queryAllProducts() {
        /*用于测试 hystrix 超时熔断
        try {
            int i = new Random().nextInt(2500);
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        return productList;
    }

    @Override
    public void saveProduct(Product product) {
        productList.add(product);
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        int port = event.getWebServer().getPort();
        for (long i = 0; i < 20; i++) {
            productList.add(new Product(i, port + "产品" + i, i / 2 == 0, new Date(), 66.66f * i));
        }
    }
}