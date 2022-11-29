package uet.ktmt.myproject.service;

import uet.ktmt.myproject.persistance.entity.Property;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PropertyService {
    public List<Property> getAll();

    public void create(Property convertToProperty);

    public void delete(long propertyId);

    public Page<Property> getAllPage(int page);

    public void edit(long propertyId, Property convertToProperty);
}
