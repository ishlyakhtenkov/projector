package ru.javaprojects.projector.projects.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.javaprojects.projector.common.repository.BaseRepository;
import ru.javaprojects.projector.projects.model.Comment;

import java.util.List;
import java.util.Optional;

@Transactional(readOnly = true)
public interface CommentRepository extends BaseRepository<Comment> {

    @EntityGraph(attributePaths = {"author", "likes"})
    List<Comment> findAllByProjectIdOrderByCreated(long projectId);

    @EntityGraph(attributePaths = {"author", "likes"})
    Optional<Comment> findById(long id);

    @Query("SELECT c.projectId AS projectId, COUNT(c.projectId) AS totalComment FROM Comment c GROUP BY c.projectId")
    List<CommentCount> countTotalCommentsByProject();
}
