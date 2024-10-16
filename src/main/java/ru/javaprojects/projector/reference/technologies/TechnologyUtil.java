package ru.javaprojects.projector.reference.technologies;

import lombok.experimental.UtilityClass;
import ru.javaprojects.projector.common.util.AppUtil;
import ru.javaprojects.projector.reference.technologies.model.Technology;

import static ru.javaprojects.projector.common.util.FileUtil.normalizePath;

@UtilityClass
public class TechnologyUtil {
    public static Technology createNewFromTo(TechnologyTo technologyTo, String technologyFilesPath) {
        return new Technology(null, technologyTo.getName(), technologyTo.getUrl(), technologyTo.getUsage(),
                technologyTo.getPriority(),
                AppUtil.createFile(technologyTo::getLogo, technologyFilesPath, technologyTo.getName() + "/"));
    }

    public static TechnologyTo asTo(Technology technology) {
        return new TechnologyTo(technology.getId(), technology.getName(), technology.getUrl(), technology.getUsage(),
                technology.getPriority(), technology.getLogo().getFileName(), technology.getLogo().getFileLink());
    }

    public static Technology updateFromTo(Technology technology, TechnologyTo technologyTo, String technologyFilesPath) {
        String technologyOldName = technology.getName();
        technology.setName(technologyTo.getName());
        technology.setUrl(technologyTo.getUrl());
        technology.setUsage(technologyTo.getUsage());
        technology.setPriority(technologyTo.getPriority());
        if (technologyTo.getLogo() != null && !technologyTo.getLogo().isEmpty()) {
            technology.setLogo(AppUtil.createFile(technologyTo::getLogo, technologyFilesPath,
                    technologyTo.getName() + "/"));
        } else if (!technology.getName().equalsIgnoreCase(technologyOldName)) {
            technology.getLogo().setFileLink(technologyFilesPath + normalizePath(technology.getName() + "/" +
                    technology.getLogo().getFileName()));
        }
        return technology;
    }
}
