package ru.javaprojects.projector.reference.technologies;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.javaprojects.projector.common.error.IllegalRequestDataException;
import ru.javaprojects.projector.common.error.NotFoundException;
import ru.javaprojects.projector.common.to.FileTo;
import ru.javaprojects.projector.common.util.FileUtil;
import ru.javaprojects.projector.reference.technologies.model.Technology;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static ru.javaprojects.projector.common.util.FileUtil.normalizePath;
import static ru.javaprojects.projector.reference.technologies.TechnologyUtil.createNewFromTo;
import static ru.javaprojects.projector.reference.technologies.TechnologyUtil.updateFromTo;

@Service
@RequiredArgsConstructor
public class TechnologyService {
    private final TechnologyRepository repository;

    @Value("${content-path.technologies}")
    private String contentPath;

    public Technology get(long id) {
        return repository.getExisted(id);
    }

    public List<Technology> getAll() {
        return repository.findAll(Sort.by("name"));
    }

    public Page<Technology> getAll(Pageable pageable) {
        return repository.findAllByOrderByName(pageable);
    }

    public Page<Technology> getAll(Pageable pageable, String keyword) {
        return repository.findAllByKeyword(keyword, pageable);
    }

    public Technology getByName(String name) {
        return repository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new NotFoundException("Not found technology with name =" + name, "notfound.technology",
                        new Object[]{name}));
    }

    @Transactional
    public Technology create(TechnologyTo technologyTo) {
        Assert.notNull(technologyTo, "technologyTo must not be null");
        if (technologyTo.getLogo() == null || technologyTo.getLogo().isEmpty()) {
            throw new IllegalRequestDataException("Technology logo file is not present",
                    "technology.logo-not-present", null);
        }
        Technology technology = repository.saveAndFlush(createNewFromTo(technologyTo, contentPath));
        uploadImage(technologyTo, technology.getName());
        return technology;
    }

    @Transactional
    public void update(TechnologyTo technologyTo) {
        Assert.notNull(technologyTo, "technologyTo must not be null");
        Technology technology = get(technologyTo.getId());
        String oldName = technology.getName();
        String oldLogoFileLink = technology.getLogo().getFileLink();
        repository.saveAndFlush(updateFromTo(technology, technologyTo, contentPath));
        if (!technologyTo.getLogo().isEmpty()) {
            uploadImage(technologyTo, technology.getName());
            if (!oldLogoFileLink.equalsIgnoreCase(technology.getLogo().getFileLink())) {
                FileUtil.deleteFile(oldLogoFileLink);
            }
        } else if (!technology.getName().equalsIgnoreCase(oldName)) {
            FileUtil.moveFile(oldLogoFileLink, contentPath + FileUtil.normalizePath(technology.getName()));
        }
    }

    private void uploadImage(TechnologyTo technologyTo, String technologyName) {
        FileTo logo = technologyTo.getLogo();
        String fileName = normalizePath(logo.getInputtedFile() != null ? logo.getInputtedFile().getOriginalFilename() :
                logo.getFileName());
        FileUtil.upload(logo, contentPath + FileUtil.normalizePath(technologyName) + "/", fileName);
    }

    @Transactional
    public void delete(long id) {
        Technology technology = get(id);
        repository.delete(technology);
        repository.flush();
        FileUtil.deleteFile(technology.getLogo().getFileLink());
    }

    public Set<Technology> getAllByIds(Set<Long> ids) {
        return new HashSet<>(repository.findAllById(ids));
    }
}
