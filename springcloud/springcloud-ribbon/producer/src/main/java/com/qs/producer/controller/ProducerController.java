package com.heibaiying.producer.controller;


import com.heibaiying.common.api.IProductService;
import com.heibaiying.common.bean.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 
 * @author : qshomewy
 * @description :
 */
@RestController
public class ProducerController {

    @Autowired
    private IProductService productService;

    @GetMapping("products")
    public List<Product> productList() {
        return productService.queryAllProducts();
    }

    @GetMapping("product/{id}")
    public Product productDetail(@PathVariable int id) {
        return productService.queryProductById(id);
    }

    @PostMapping("product")
    public void save(@RequestBody Product product) {
        productService.saveProduct(product);
    }
}