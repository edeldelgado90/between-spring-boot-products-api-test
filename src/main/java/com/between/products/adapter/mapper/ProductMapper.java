package com.between.products.adapter.mapper;

import com.between.products.application.dto.ProductDTO;
import com.between.products.domain.product.Product;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDTO toProductDTO(Product product);
}
