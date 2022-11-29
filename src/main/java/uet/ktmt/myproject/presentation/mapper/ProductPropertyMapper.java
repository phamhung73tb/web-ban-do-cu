package uet.ktmt.myproject.presentation.mapper;

import uet.ktmt.myproject.persistance.entity.ProductProperty;
import uet.ktmt.myproject.persistance.entity.Property;
import uet.ktmt.myproject.presentation.request.ProductPropertyRequest;
import uet.ktmt.myproject.presentation.response.ProductPropertyResponse;

public class ProductPropertyMapper {
    private ProductPropertyMapper() {
        super();
    }

    public static ProductPropertyResponse convertToProductPropertyResponse(ProductProperty productProperty) {
        return ProductPropertyResponse.builder()
                .propertyId(productProperty.getProperty().getId())
                .propertyName(productProperty.getProperty().getName())
                .value(productProperty.getValue())
                .note(productProperty.getProperty().getNote())
                .unit(productProperty.getProperty().getUnit())
                .build();
    }

    public static ProductProperty convertToProductProperty(ProductPropertyRequest productPropertyRequest) {
        return ProductProperty.builder()
                .property(Property.builder().id(productPropertyRequest.getPropertyId()).build())
                .value(productPropertyRequest.getValue())
                .build();
    }
}
