package com.qs.service;

import com.qs.api.IProductService;
import com.qs.bean.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author : qshomewy
 * @description : 产品提供接口实现类
 */
@Service
public class ProductService implements IProductService {

    private static List<Product> productList = new ArrayList<Product>();

    static {
        for (int i = 0; i < 20; i++) {
            productList.add(new Product(i, "产品" + i, i / 2 == 0, new Date(), 66.66f * i));
        }
    }

    public Product queryProductById(int id) {
        for (Product product : productList) {
            if (product.getId() == id) {
                return product;
            }
        }
        return null;
    }


    public List<Product> queryAllProducts() {
        return productList;
    }
}

