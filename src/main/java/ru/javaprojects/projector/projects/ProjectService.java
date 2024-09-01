package ru.javaprojects.projector.projects;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import ru.javaprojects.projector.common.error.IllegalRequestDataException;
import ru.javaprojects.projector.common.error.NotFoundException;
import ru.javaprojects.projector.common.util.FileUtil;
import ru.javaprojects.projector.projects.model.Project;

import java.util.List;



@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository repository;
    private final ProjectUtil projectUtil;

    @Value("${content-path.projects}")
    private String contentPath;

    public Project get(long id) {
        return repository.getExisted(id);
    }

    public Project getWithTechnologies(long id) {
        return repository.findWithTechnologiesById(id).orElseThrow(() ->
                new NotFoundException("Not found project with id=" + id, "notfound.entity", new Object[]{id}));
    }

    public Project getWithTechnologiesAndDescription(long id) {
        return repository.findWithTechnologiesAndDescriptionById(id).orElseThrow(() ->
                new NotFoundException("Not found project with id=" + id, "notfound.entity", new Object[]{id}));
    }

    public Project getByName(String name) {
        return repository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new NotFoundException("Not found project with name =" + name, "notfound.project",
                        new Object[]{name}));
    }

    public List<Project> getAll() {
        return repository.findAllByOrderByName();
    }

    @Transactional
    public void delete(long id) {
        Project project = get(id);
        repository.delete(project);
        repository.flush();
        FileUtil.deleteFile(project.getLogoFile().getFileLink());
        FileUtil.deleteFile(project.getCardImageFile().getFileLink());
        if (project.getDockerComposeFile() != null) {
            FileUtil.deleteFile(project.getDockerComposeFile().getFileLink());
        }
    }

    @Transactional
    public void enable(long id, boolean enabled) {
        Project project = get(id);
        project.setEnabled(enabled);
    }

    @Transactional
    public Project create(ProjectTo projectTo) {
        Assert.notNull(projectTo, "projectTo must not be null");
        if (projectTo.getLogoFile() == null || projectTo.getLogoFile().isEmpty()) {
            throw new IllegalRequestDataException("Project logo file is not present",
                    "project.logo-not-present", null);
        }
        if (projectTo.getCardImageFile() == null || projectTo.getCardImageFile().isEmpty()) {
            throw new IllegalRequestDataException("Project card image file is not present",
                    "project.card-image-not-present", null);
        }
        Project project = repository.saveAndFlush(projectUtil.createNewFromTo(projectTo, contentPath));
        uploadFile(projectTo.getLogoFile(), project, "logo");
        uploadFile(projectTo.getCardImageFile(), project, "card_img");
        if (projectTo.getDockerComposeFile() != null && !projectTo.getDockerComposeFile().isEmpty()) {
            uploadFile(projectTo.getDockerComposeFile(), project, "docker");
        }
        return project;
    }

    @Transactional
    public Project update(ProjectTo projectTo) {
        Assert.notNull(projectTo, "projectTo must not be null");
        Project project = getWithTechnologies(projectTo.getId());
        String oldName = project.getName();
        String oldLogoFileLink = project.getLogoFile().getFileLink();
        String oldCardImageFileLink = project.getCardImageFile().getFileLink();
        String oldDockerComposeFileLink =
                project.getDockerComposeFile() != null ? project.getDockerComposeFile().getFileLink() : null;
        repository.saveAndFlush(projectUtil.updateFromTo(project, projectTo, contentPath));
        if (projectTo.getLogoFile() != null && !projectTo.getLogoFile().isEmpty()) {
            FileUtil.deleteFile(oldLogoFileLink);
            String newLogoFileName =  FileUtil.normalizeFileName(projectTo.getLogoFile().getOriginalFilename());
            FileUtil.upload(projectTo.getLogoFile(), contentPath + FileUtil.normalizeFileName(projectTo.getName()) +
                    "/logo/", newLogoFileName);
        } else if (!projectTo.getName().equalsIgnoreCase(oldName)) {
            FileUtil.moveFile(oldLogoFileLink, contentPath + FileUtil.normalizeFileName(projectTo.getName() + "/logo/"));
        }
        if (projectTo.getCardImageFile() != null && !projectTo.getCardImageFile().isEmpty()) {
            FileUtil.deleteFile(oldCardImageFileLink);
            String newCardImageFileName =  FileUtil.normalizeFileName(projectTo.getCardImageFile().getOriginalFilename());
            FileUtil.upload(projectTo.getCardImageFile(), contentPath + FileUtil.normalizeFileName(projectTo.getName()) +
                    "/card_img/", newCardImageFileName);
        } else if (!projectTo.getName().equalsIgnoreCase(oldName)) {
            FileUtil.moveFile(oldCardImageFileLink, contentPath + FileUtil.normalizeFileName(projectTo.getName() + "/card_img/"));
        }
        if (projectTo.getDockerComposeFile() != null && !projectTo.getDockerComposeFile().isEmpty()) {
            if (oldDockerComposeFileLink != null) {
                FileUtil.deleteFile(oldDockerComposeFileLink);
            }
            String newDockerComposeFileName =  FileUtil.normalizeFileName(projectTo.getDockerComposeFile().getOriginalFilename());
            FileUtil.upload(projectTo.getDockerComposeFile(), contentPath + FileUtil.normalizeFileName(projectTo.getName()) +
                    "/docker/", newDockerComposeFileName);
        } else if (!projectTo.getName().equalsIgnoreCase(oldName) && oldDockerComposeFileLink != null) {
            FileUtil.moveFile(oldDockerComposeFileLink, contentPath + FileUtil.normalizeFileName(projectTo.getName() + "/docker/"));
        }
        return project;
    }

    private void uploadFile(MultipartFile file, Project project, String dirName) {
        FileUtil.upload(file, contentPath +
                        FileUtil.normalizeFileName(project.getName()) + "/" + dirName + "/",
                FileUtil.normalizeFileName(file.getOriginalFilename()));
    }
}
