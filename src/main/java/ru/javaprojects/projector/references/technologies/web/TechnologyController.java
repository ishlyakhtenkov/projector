package ru.javaprojects.projector.references.technologies.web;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.javaprojects.projector.common.error.IllegalRequestDataException;
import ru.javaprojects.projector.common.util.FileUtil;
import ru.javaprojects.projector.projects.DescriptionElementTo;
import ru.javaprojects.projector.references.technologies.TechnologyTo;
import ru.javaprojects.projector.common.model.Priority;
import ru.javaprojects.projector.references.technologies.model.Technology;
import ru.javaprojects.projector.references.technologies.model.Usage;
import ru.javaprojects.projector.references.technologies.TechnologyService;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

import static ru.javaprojects.projector.references.technologies.TechnologyUtil.asTo;

@Controller
@RequestMapping(TechnologyController.TECHNOLOGIES_URL)
@AllArgsConstructor
@Slf4j
public class TechnologyController {
    static final String TECHNOLOGIES_URL = "/references/technologies";

    private final TechnologyService service;
    private final UniqueTechnologyNameValidator nameValidator;
    private final MessageSource messageSource;

    @InitBinder("technologyTo")
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(nameValidator);
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }

    @GetMapping
    public String getAll(@RequestParam(value = "keyword", required = false) String keyword,
                         @PageableDefault Pageable pageable, Model model, RedirectAttributes redirectAttributes) {
        Page<Technology> technologies;
        if (keyword != null) {
            if (keyword.isBlank()) {
                return "redirect:/references/technologies";
            }
            log.info("get technologies (pageNumber={}, pageSize={}, keyword={})", pageable.getPageNumber(),
                    pageable.getPageSize(), keyword);
            technologies = service.getAll(pageable, keyword.trim());
        } else  {
            log.info("get technologies (pageNumber={}, pageSize={})", pageable.getPageNumber(), pageable.getPageSize());
            technologies = service.getAll(pageable);
        }
        if (technologies.getContent().isEmpty() && technologies.getTotalElements() != 0) {
            if (keyword != null) {
                redirectAttributes.addAttribute("keyword", keyword);
            }
            return "redirect:/references/technologies";
        }
        model.addAttribute("technologies", technologies);
        return "references/technologies";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        log.info("show technology add form");
        model.addAttribute("technologyTo", new TechnologyTo());
        addAttributesToModel(model);
        return "references/technology-form";
    }

    private void addAttributesToModel(Model model) {
        model.addAttribute("usages", Usage.values());
        model.addAttribute("priorities", Priority.values());
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable long id, Model model) {
        log.info("show edit form for technology with id={}", id);
        Technology technology = service.get(id);
        model.addAttribute("technologyTo", asTo(technology));
        addAttributesToModel(model);
        return "references/technology-form";
    }

    @PostMapping
    public String createOrUpdate(@Valid TechnologyTo technologyTo, BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {
        boolean isNew = technologyTo.isNew();
        if (result.hasErrors()) {
            addAttributesToModel(model);
            if (!FileUtil.isMultipartFileEmpty(technologyTo.getLogoFile())) {
                keepImageFile(technologyTo);
            }
            if (!isNew) {
                model.addAttribute("technologyName", service.get(technologyTo.getId()).getName());
            }
            return "references/technology-form";
        }
        log.info((isNew ? "create" : "update") + " {}", technologyTo);
        if (isNew) {
            service.create(technologyTo);
        }  else {
            service.update(technologyTo);
        }
        redirectAttributes.addFlashAttribute("action",
                messageSource.getMessage((isNew ? "technology.created" : "technology.updated"),
                new Object[]{technologyTo.getName()}, LocaleContextHolder.getLocale()));
        return "redirect:/references/technologies";
    }

    private void keepImageFile(TechnologyTo technologyTo) {
        try {
            technologyTo.setLogoFileAsString(Base64.getEncoder()
                    .encodeToString(Objects.requireNonNull(technologyTo.getLogoFile()).getBytes()));
            technologyTo.setLogoFileName(technologyTo.getLogoFile().getOriginalFilename());
            technologyTo.setLogoFileLink(null);
        } catch (IOException e) {
            throw new IllegalRequestDataException(e.getMessage(), "file.failed-to-upload",
                    new Object[]{technologyTo.getLogoFile().getOriginalFilename()});
        }
    }
}
