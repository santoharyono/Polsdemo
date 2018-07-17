package com.mitrais.polsdemo.controller;

import com.mitrais.polsdemo.Exception.ResourceNotFoundException;
import com.mitrais.polsdemo.model.User;
import com.mitrais.polsdemo.payload.response.*;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Api(value = "api", description = "User API")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private PollService pollService;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    @ApiOperation(value = "get current user")
    public UserSummaryResponse getCurrentUser(@CurrentUser UserPrincipal currentUser) {
        UserSummaryResponse userSummary = new UserSummaryResponse(currentUser.getId(), currentUser.getUsername(), currentUser.getName());
        return userSummary;
    }

    @GetMapping("/user/checkUsernameAvailability")
    @ApiOperation(value = "check user availability")
    public UserIdentityAvailabilityResponse checkEmailAvailability(@RequestParam(value = "email") String email) {
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return new UserIdentityAvailabilityResponse(isAvailable);
    }

    @GetMapping("/users/{username}")
    @ApiOperation(value = "get user profile")
    public UserProfileResponse getUserProfile(@PathVariable(value = "username") String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        long pollCount = pollRepository.countByCreatedBy(user.getId());
        long voteCount = voteRepository.countByUserId(user.getId());

        UserProfileResponse userProfile = new UserProfileResponse(user.getId(), user.getUsername(), user.getName(), user.getCreatedAt(), pollCount, voteCount);

        return userProfile;
    }

    @GetMapping("/users/{username}/polls")
    @ApiOperation(value = "get all polls based on given user")
    public PagedResponse<PollResponse> getPollsCreatedBy(@PathVariable(value = "username") String username, @CurrentUser UserPrincipal currentUser,
                                                         @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                         @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return pollService.getPollsCreatedBy(username, currentUser, page, size);
    }

    @GetMapping("/users/{username}/votes")
    @ApiOperation(value = "get all votes based on given user")
    public PagedResponse<PollResponse> getPollsVotedBy(@PathVariable(value = "username") String username, @CurrentUser UserPrincipal currentUser,
                                                         @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
                                                         @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
        return pollService.getPollsVotedBy(username, currentUser, page, size);
    }
}
