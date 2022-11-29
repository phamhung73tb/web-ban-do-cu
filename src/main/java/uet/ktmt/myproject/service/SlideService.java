package uet.ktmt.myproject.service;

import uet.ktmt.myproject.persistance.entity.Slide;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public interface SlideService {
    List<Slide> getAllActive();

    void create(Slide slide, MultipartFile multipartFile) throws IOException;

    void edit(long id, Slide slide, MultipartFile multipartFile) throws Throwable;

    void delete(long id) throws Throwable;

    void updateStatusHiddenFlag(long id) throws Throwable;

    Page<Slide> getAll(int page);

    Slide getDetail(long slideId) throws Throwable;
}
