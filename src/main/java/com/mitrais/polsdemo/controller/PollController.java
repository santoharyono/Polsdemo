package com.mitrais.polsdemo.controller;

import com.mitrais.polsdemo.model.Poll;
import com.mitrais.polsdemo.payload.request.PollRequest;
import com.mitrais.polsdemo.payload.request.VoteRequest;
import com.mitrais.polsdemo.payload.response.ApiResponse;
import com.mitrais.polsdemo.payload.response.PagedResponse;
import com.mitrais.polsdemo.payload.response.PollResponse;
import com.mitrais.polsdemo.repository.PollRepository;
import com.mitrais.polsdemo.repository.UserRepository;
import com.mitrais.polsdemo.repository.VoteRepository;
import com.mitrais.polsdemo.security.CurrentUser;
import com.mitrais.polsdemo.security.UserPrincipal;
import com.mitrais.polsdemo.service.PollService;
import com.mitrais.polsdemo.util.AppConstants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/api/polls")
@Api(value = "polls", description = "Polls API")
public class PollController {

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PollService pollService;

    private static final Logger LOGGER = LoggerFactory.getLogger(PollController.class);

    @GetMapping
    @ApiOperation(value = "get all polls")
    public PagedResponse<PollResponse> getPolls(@CurrentUser UserPrincipal currentUser,
                                                @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return pollService.getAllPolls(currentUser, page, size);
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "create new poll")
    public ResponseEntity<?> createPoll(@Valid @RequestBody PollRequest pollRequest) {
        Poll poll = pollService.createPoll(pollRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest().path("/{pollId}")
                .buildAndExpand(poll.getId()).toUri();

        return ResponseEntity.created(location)
                .body(new ApiResponse(true, "Poll created successfully"));
    }

    @GetMapping("/{pollId}")
    @ApiOperation(value = "search poll by poll ID")
    public PollResponse getPollById(@CurrentUser UserPrincipal currentUser, @PathVariable Long pollId) {
        return pollService.getPollById(pollId, currentUser);
    }

    @PostMapping("/{polllId}/votes")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "cast vote")
    public PollResponse castVote(@CurrentUser UserPrincipal currentUser, @PathVariable Long pollId, @Valid @RequestBody VoteRequest voteRequest) {
        return pollService.castVoteAndGetUpdatedPoll(pollId, voteRequest, currentUser);
    }
}
