package ru.javaprojects.projector.references.technologies;

import lombok.experimental.UtilityClass;
import ru.javaprojects.projector.references.technologies.TechnologyTo;
import ru.javaprojects.projector.references.technologies.model.LogoFile;
import ru.javaprojects.projector.references.technologies.model.Technology;

@UtilityClass
public class TechnologyUtil {
    public static Technology createNewFromTo(TechnologyTo technologyTo, String contentPath) {
        return new Technology(null, technologyTo.getName(), technologyTo.getUrl(), technologyTo.getUsage(),
                technologyTo.getPriority(), createLogoFile(technologyTo, contentPath));
    }

    public static TechnologyTo asTo(Technology technology) {
        return new TechnologyTo(technology.getId(), technology.getName(), technology.getUrl(), technology.getUsage(),
                technology.getPriority());
    }

    public static Technology updateFromTo(Technology technology, TechnologyTo technologyTo, String contentPath) {
        technology.setUrl(technologyTo.getUrl());
        technology.setUsage(technologyTo.getUsage());
        technology.setPriority(technologyTo.getPriority());
        if (technologyTo.getLogoFile() != null) {
            technology.setLogoFile(createLogoFile(technologyTo, contentPath));
        } else if (!technologyTo.getName().equalsIgnoreCase(technology.getName())) {
            technology.getLogoFile().setFileLink(contentPath + technologyTo.getName().toLowerCase() + "/" +
                    technology.getLogoFile().getFileName());
        }
        technology.setName(technologyTo.getName());
        return technology;
    }

    private LogoFile createLogoFile(TechnologyTo technologyTo, String contentPath) {
        String filename = technologyTo.getLogoFile().getOriginalFilename();
        return new LogoFile(filename, contentPath + technologyTo.getName().toLowerCase() + "/" + filename);
    }
}