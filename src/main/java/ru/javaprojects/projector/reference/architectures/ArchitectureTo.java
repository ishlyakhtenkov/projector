package ru.javaprojects.projector.reference.architectures;

import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.javaprojects.projector.common.HasIdAndName;
import ru.javaprojects.projector.common.to.BaseTo;
import ru.javaprojects.projector.common.to.FileTo;
import ru.javaprojects.projector.common.validation.ImageFile;
import ru.javaprojects.projector.common.validation.NoHtml;

@Getter
@Setter
@NoArgsConstructor
public class ArchitectureTo extends BaseTo implements HasIdAndName {
    @NotBlank
    @NoHtml
    @Size(max = 32)
    private String name;

    @NotBlank
    @NoHtml
    @Size(max = 400)
    private String description;

    @Nullable
    @Valid
    @ImageFile
    private FileTo logo;

    public ArchitectureTo(Long id, String name, String description, FileTo logo) {
        super(id);
        this.name = name;
        this.description = description;
        this.logo = logo;
    }

    @Override
    public String toString() {
        return String.format("ArchitectureTo[id=%d, name=%s]", id, name);
    }
}
