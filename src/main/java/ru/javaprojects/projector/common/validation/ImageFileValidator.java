package ru.javaprojects.projector.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.javaprojects.projector.common.to.FileTo;

import java.util.Objects;

public class ImageFileValidator implements ConstraintValidator<ImageFile, FileTo> {

    @Override
    public boolean isValid(FileTo fileTo, ConstraintValidatorContext ctx) {
        return fileTo == null || fileTo.getInputtedFile() == null || fileTo.getInputtedFile().isEmpty()
                || Objects.requireNonNull(fileTo.getInputtedFile().getContentType()).contains("image/");
    }
}
