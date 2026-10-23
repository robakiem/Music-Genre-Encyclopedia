package com.encyclopedia.complaint;

import com.encyclopedia.auth.User;
import com.encyclopedia.complaint.dto.ComplaintDto;

import java.util.List;
import java.util.UUID;

public interface ComplaintService {
    ComplaintDto file(UUID articleId, String lang, String reason, User author);
    List<ComplaintDto> getAll();
    List<ComplaintDto> getOpen();
    void resolve(UUID complaintId, User admin, String decision);
}
