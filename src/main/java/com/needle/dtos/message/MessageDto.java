package com.needle.dtos.message;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class MessageDto {
	private String content;
    private Long makeVisibleAt;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String status;
}
