package ru.javaprojects.projector.home.web;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.javaprojects.projector.AbstractControllerTest;
import ru.javaprojects.projector.projects.to.ProjectPreviewTo;

import java.util.List;
import java.util.Objects;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.javaprojects.projector.projects.ProjectTestData.*;
import static ru.javaprojects.projector.users.UserTestData.*;

class HomeControllerTest extends AbstractControllerTest {

    @Test
    @WithUserDetails(USER_MAIL)
    @SuppressWarnings("unchecked")
    void showHomePage() throws Exception {
        perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(PROJECTS_ATTRIBUTE))
                .andExpect(view().name("home/index"))
                .andExpect(result -> PROJECT_PREVIEW_TO_MATCHER
                        .assertMatchIgnoreFields((List<ProjectPreviewTo>) Objects.requireNonNull(result.getModelAndView())
                                        .getModel().get(PROJECTS_ATTRIBUTE), List.of(project2PreviewTo, project1PreviewTo),
                                "author.roles", "author.password", "author.registered"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void showHomePageUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(PROJECTS_ATTRIBUTE))
                .andExpect(view().name("home/index"))
                .andExpect(result -> PROJECT_PREVIEW_TO_MATCHER
                        .assertMatchIgnoreFields((List<ProjectPreviewTo>) Objects.requireNonNull(result.getModelAndView())
                                        .getModel().get(PROJECTS_ATTRIBUTE), List.of(project2PreviewTo, project1PreviewTo),
                                "author.roles", "author.password", "author.registered"));
    }

    @Test
    @WithUserDetails(ADMIN_MAIL)
    @SuppressWarnings("unchecked")
    void showHomePageWithAuthorProjectsWhenNotBelongs() throws Exception {
        perform(MockMvcRequestBuilders.get("/")
                .param("by-author", String.valueOf(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(PROJECTS_ATTRIBUTE))
                .andExpect(view().name("home/index"))
                .andExpect(result -> PROJECT_PREVIEW_TO_MATCHER
                        .assertMatchIgnoreFields((List<ProjectPreviewTo>) Objects.requireNonNull(result.getModelAndView())
                                        .getModel().get(PROJECTS_ATTRIBUTE), List.of(project1PreviewTo),
                                "author.roles", "author.password", "author.registered"));
    }

    @Test
    @WithUserDetails(USER_MAIL)
    @SuppressWarnings("unchecked")
    void showHomePageWithAuthorProjectsWhenBelongs() throws Exception {
        perform(MockMvcRequestBuilders.get("/")
                .param("by-author", String.valueOf(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(PROJECTS_ATTRIBUTE))
                .andExpect(view().name("home/index"))
                .andExpect(result -> PROJECT_PREVIEW_TO_MATCHER
                        .assertMatchIgnoreFields((List<ProjectPreviewTo>) Objects.requireNonNull(result.getModelAndView())
                                        .getModel().get(PROJECTS_ATTRIBUTE), List.of(project1PreviewTo),
                                "author.roles", "author.password", "author.registered"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void showHomePageWithAuthorProjectsUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get("/")
                .param("by-author", String.valueOf(USER_ID)))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(PROJECTS_ATTRIBUTE))
                .andExpect(view().name("home/index"))
                .andExpect(result -> PROJECT_PREVIEW_TO_MATCHER
                        .assertMatchIgnoreFields((List<ProjectPreviewTo>) Objects.requireNonNull(result.getModelAndView())
                                        .getModel().get(PROJECTS_ATTRIBUTE), List.of(project1PreviewTo),
                                "author.roles", "author.password", "author.registered"));
    }

    @Test
    @WithUserDetails(USER_MAIL)
    @SuppressWarnings("unchecked")
    void showHomePageWithPopularProjects() throws Exception {
        perform(MockMvcRequestBuilders.get("/")
                .param("popular", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(PROJECTS_ATTRIBUTE))
                .andExpect(view().name("home/index"))
                .andExpect(result -> PROJECT_PREVIEW_TO_MATCHER
                        .assertMatchIgnoreFields((List<ProjectPreviewTo>) Objects.requireNonNull(result.getModelAndView())
                                        .getModel().get(PROJECTS_ATTRIBUTE), List.of(project1PreviewTo, project2PreviewTo),
                                "author.roles", "author.password", "author.registered"));
    }

    @Test
    @WithUserDetails(USER_MAIL)
    @SuppressWarnings("unchecked")
    void showHomePageWithPopularProjectsUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get("/")
                .param("popular", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(PROJECTS_ATTRIBUTE))
                .andExpect(view().name("home/index"))
                .andExpect(result -> PROJECT_PREVIEW_TO_MATCHER
                        .assertMatchIgnoreFields((List<ProjectPreviewTo>) Objects.requireNonNull(result.getModelAndView())
                                        .getModel().get(PROJECTS_ATTRIBUTE), List.of(project1PreviewTo, project2PreviewTo),
                                "author.roles", "author.password", "author.registered"));
    }

    @Test
    @WithUserDetails(USER_MAIL)
    @SuppressWarnings("unchecked")
    void showHomePageWithProjectsByTag() throws Exception {
        perform(MockMvcRequestBuilders.get("/tags/spring"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(PROJECTS_ATTRIBUTE))
                .andExpect(view().name("home/index"))
                .andExpect(result -> PROJECT_PREVIEW_TO_MATCHER
                        .assertMatchIgnoreFields((List<ProjectPreviewTo>) Objects.requireNonNull(result.getModelAndView())
                                        .getModel().get(PROJECTS_ATTRIBUTE), List.of(project2PreviewTo, project1PreviewTo),
                                "author.roles", "author.password", "author.registered"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void showHomePageWithProjectsByTagUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get("/tags/spring"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists(PROJECTS_ATTRIBUTE))
                .andExpect(view().name("home/index"))
                .andExpect(result -> PROJECT_PREVIEW_TO_MATCHER
                        .assertMatchIgnoreFields((List<ProjectPreviewTo>) Objects.requireNonNull(result.getModelAndView())
                                        .getModel().get(PROJECTS_ATTRIBUTE), List.of(project2PreviewTo, project1PreviewTo),
                                "author.roles", "author.password", "author.registered"));
    }

    @Test
    void showAboutPageUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("home/about"));
    }

    @Test
    @WithUserDetails(USER_MAIL)
    void showAboutPageAuthorized() throws Exception {
        perform(MockMvcRequestBuilders.get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("home/about"));
    }

    @Test
    void showContactPageUnauthorized() throws Exception {
        perform(MockMvcRequestBuilders.get("/contact"))
                .andExpect(status().isOk())
                .andExpect(view().name("home/contact"));
    }

    @Test
    @WithUserDetails(USER_MAIL)
    void showContactPageAuthorized() throws Exception {
        perform(MockMvcRequestBuilders.get("/contact"))
                .andExpect(status().isOk())
                .andExpect(view().name("home/contact"));
    }
}
