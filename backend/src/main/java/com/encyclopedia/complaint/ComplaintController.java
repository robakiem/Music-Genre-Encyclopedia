package com.encyclopedia.complaint;

import com.encyclopedia.auth.User;
import com.encyclopedia.common.ApiException;
import com.encyclopedia.complaint.dto.ComplaintDto;
import com.encyclopedia.complaint.dto.ComplaintRequestDto;
import com.encyclopedia.complaint.dto.ResolutionDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    // Any logged-in user can file a complaint.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ComplaintDto file(@Valid @RequestBody ComplaintRequestDto dto,
                             @AuthenticationPrincipal User author) {
        if (author == null) {
            throw ApiException.unauthorized("You must log in to file a complaint");
        }
        return complaintService.file(dto.articleId(), dto.lang(), dto.reason(), author);
    }

    // Admin-only review (secured in SecurityConfig).
    @GetMapping
    public List<ComplaintDto> list(@RequestParam(required = false) String status) {
        if ("OPEN".equalsIgnoreCase(status)) {
            return complaintService.getOpen();
        }
        return complaintService.getAll();
    }

    @PostMapping("/{complaintId}/resolve")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void resolve(@PathVariable UUID complaintId,
                        @RequestBody(required = false) ResolutionDto dto,
                        @AuthenticationPrincipal User admin) {
        complaintService.resolve(complaintId, admin, dto == null ? null : dto.decision());
    }
}
