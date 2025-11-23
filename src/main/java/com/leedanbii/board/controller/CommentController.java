package com.leedanbii.board.controller;

import com.leedanbii.board.dto.CommentForm;
import com.leedanbii.board.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/boards/{boardId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public String createComment(@PathVariable("boardId") Long boardId,
                                @Valid CommentForm form,
                                @AuthenticationPrincipal UserDetails loginUser
    ) {
        commentService.createComment(form, loginUser.getUsername(), boardId);
        return "redirect:/boards/" + boardId;
    }

    @DeleteMapping("/{commentId}/delete")
    public String deleteComment(
            @PathVariable("boardId") Long boardId,
            @PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal UserDetails loginUser
    ) {
        commentService.deleteComment(commentId, loginUser.getUsername());
        return "redirect:/boards/" + boardId;
    }
}
