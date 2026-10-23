package com.encyclopedia.complaint;

import com.encyclopedia.article.ArticleRepository;
import com.encyclopedia.auth.User;
import com.encyclopedia.auth.UserRepository;
import com.encyclopedia.common.ApiException;
import com.encyclopedia.complaint.dto.ComplaintDto;
import com.encyclopedia.genre.Genre;
import com.encyclopedia.genre.GenreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ArticleRepository articleRepository;
    private final GenreRepository genreRepository;
    private final UserRepository userRepository;

    public ComplaintServiceImpl(ComplaintRepository complaintRepository,
                                ArticleRepository articleRepository,
                                GenreRepository genreRepository,
                                UserRepository userRepository) {
        this.complaintRepository = complaintRepository;
        this.articleRepository = articleRepository;
        this.genreRepository = genreRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public ComplaintDto file(UUID articleId, String lang, String reason, User author) {
        if (articleRepository.findById(articleId).isEmpty()) {
            throw ApiException.notFound("Article not found: " + articleId);
        }
        Complaint complaint = new Complaint(articleId, lang, author.getId(), reason);
        complaint = complaintRepository.save(complaint);
        return toDto(complaint);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintDto> getAll() {
        return complaintRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ComplaintDto> getOpen() {
        return complaintRepository.findByStatusOrderByCreatedAtDesc(ComplaintStatus.OPEN).stream()
                .map(this::toDto).toList();
    }

    @Override
    @Transactional
    public void resolve(UUID complaintId, User admin, String decision) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> ApiException.notFound("Complaint not found: " + complaintId));
        if (complaint.getStatus() == ComplaintStatus.RESOLVED) {
            throw ApiException.badRequest("Complaint is already resolved");
        }
        complaint.resolve(admin.getId(), decision);
        complaintRepository.save(complaint);
    }

    private ComplaintDto toDto(Complaint c) {
        String authorName = userRepository.findById(c.getAuthorId())
                .map(User::getUsername).orElse("—");
        Genre genre = articleRepository.findById(c.getArticleId())
                .flatMap(a -> genreRepository.findById(a.getGenreId()))
                .orElse(null);
        UUID genreId = genre == null ? null : genre.getId();
        String genreName = genre == null ? null : genre.getNameUk();
        return new ComplaintDto(
                c.getId(), c.getArticleId(), genreId, genreName, c.getLang(),
                c.getReason(), c.getStatus().name(), authorName,
                c.getCreatedAt(), c.getResolvedAt(), c.getDecision());
    }
}
