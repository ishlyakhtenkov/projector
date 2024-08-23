package ru.javaprojects.projector.common.util.validation;

import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import ru.javaprojects.projector.common.HasIdAndName;
import ru.javaprojects.projector.common.repository.NamedRepository;

import java.util.Objects;

@AllArgsConstructor
public abstract class UniqueNameValidator<E extends HasIdAndName, R extends NamedRepository<E>>
        implements org.springframework.validation.Validator {
    public static final String DUPLICATE_ERROR_CODE = "Duplicate";

    protected final R repository;
    protected final MessageSource messageSource;
    protected final String messageCode;

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return HasIdAndName.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        HasIdAndName named = ((HasIdAndName) target);
        if (StringUtils.hasText(named.getName())) {
            repository.findByNameIgnoreCase(named.getName())
                    .ifPresent(dbNamed -> {
                        if (named.isNew() || !Objects.equals(named.getId(), dbNamed.getId())) {
                            errors.rejectValue("name", DUPLICATE_ERROR_CODE,
                                    messageSource.getMessage(messageCode, null, LocaleContextHolder.getLocale()));
                        }
                    });
        }
    }
}
