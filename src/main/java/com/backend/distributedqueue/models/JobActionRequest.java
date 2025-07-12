package com.backend.distributedqueue.models;

import com.shared.protos.JobAction;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

public class JobActionRequest {

    @Setter
    @Getter
    @Schema(type = "string", format = "binary")
    private MultipartFile file;

    @Setter
    @Getter
    @Schema(enumAsRef = true)
    private JobAction jobAction;
}
