package ru.javaprojects.projector.references.web;

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
import ru.javaprojects.projector.references.TechnologyTo;
import ru.javaprojects.projector.references.UniqueTechnologyNameValidator;
import ru.javaprojects.projector.references.model.Priority;
import ru.javaprojects.projector.references.model.Technology;
import ru.javaprojects.projector.references.model.Usage;
import ru.javaprojects.projector.references.service.TechnologyService;

import static ru.javaprojects.projector.common.util.validation.ValidationUtil.checkNew;

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
        model.addAttribute("usages", Usage.values());
        model.addAttribute("priorities", Priority.values());
        return "references/technology-form";
    }

    @PostMapping
    public String create(@Valid TechnologyTo technologyTo, BindingResult result, Model model,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("usages", Usage.values());
            model.addAttribute("priorities", Priority.values());
            return "references/technology-form";
        }
        log.info("create {}", technologyTo);
        checkNew(technologyTo);
        service.create(technologyTo);
        redirectAttributes.addFlashAttribute("action", messageSource.getMessage("technology.created",
                new Object[]{technologyTo.getName()}, LocaleContextHolder.getLocale()));
        return "redirect:/references/technologies";
    }



}
