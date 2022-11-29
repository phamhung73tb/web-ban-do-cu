package uet.ktmt.myproject.presentation.response;

import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageListResponse {
    private List<MessageResponse> messageResponseList;
    private long currentMessage;
    private long oldTotalMessage;
}
