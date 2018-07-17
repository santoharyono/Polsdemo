package com.mitrais.polsdemo.util;

import com.mitrais.polsdemo.model.Poll;
import com.mitrais.polsdemo.model.User;
import com.mitrais.polsdemo.payload.response.ChoiceResponse;
import com.mitrais.polsdemo.payload.response.PollResponse;
import com.mitrais.polsdemo.payload.response.UserSummaryResponse;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ModelMapper {
    public static PollResponse mapPollToPollResponse(Poll poll, Map<Long, Long> choiceVoteMap, User creator, Long userVote) {
        PollResponse pollResponse = new PollResponse();
        pollResponse.setId(poll.getId());
        pollResponse.setQuestion(poll.getQuestion());
        pollResponse.setCreationDatetime(poll.getCreatedAt());
        pollResponse.setExpirationDatetime(poll.getExpirationDatetime());
        Instant now = Instant.now();
        pollResponse.setExpired(poll.getExpirationDatetime().isBefore(now));

        List<ChoiceResponse> choiceResponses = poll.getChoices().stream().map(choice -> {
            ChoiceResponse choiceResponse = new ChoiceResponse();
            choiceResponse.setId(choice.getId());
            choiceResponse.setText(choice.getText());

            if (choiceVoteMap.containsKey(choice.getId())) {
                choiceResponse.setVoteCount(choiceVoteMap.get(choice.getId()));
            } else {
                choiceResponse.setVoteCount(0);
            }

            return choiceResponse;
        }).collect(Collectors.toList());

        pollResponse.setChoices(choiceResponses);
        UserSummaryResponse creatorSummary = new UserSummaryResponse(creator.getId(), creator.getUsername(), creator.getName());
        pollResponse.setCreatedBy(creatorSummary);

        if (userVote != null) {
            pollResponse.setSelectedChoice(userVote);
        }

        long totalVotes = pollResponse.getChoices().stream().mapToLong(ChoiceResponse::getVoteCount).sum();
        pollResponse.setTotalVotes(totalVotes);

        return pollResponse;
    }
}
