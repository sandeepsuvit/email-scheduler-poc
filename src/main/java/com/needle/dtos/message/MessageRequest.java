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
public class MessageRequest {
	private String content;
	/* Format to set the data is YYYY-MM-DDTHH:MM:SS ex., 2021-06-19T21:21:00*/
    private LocalDateTime makeVisibleAt;
}
