package com.leedanbii.board.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.leedanbii.board.config.SecurityConfig;
import com.leedanbii.board.dto.CommentForm;
import com.leedanbii.board.exception.CustomAuthFailureHandler;
import com.leedanbii.board.service.CommentService;
import com.leedanbii.board.service.CustomUserDetailsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(CommentController.class)
@Import(SecurityConfig.class)
class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private CustomAuthFailureHandler customAuthFailureHandler;

    private final Long boardId = 1L;

    @Test
    @DisplayName("익명 사용자는 댓글 작성 시 401 Unauthorized 발생")
    @WithAnonymousUser
    void createComment_anonymousUser_redirectsHome() throws Exception {
        CommentForm form = createCommentForm("Test comment");

        mockMvc.perform(post("/boards/{boardId}/comments", boardId)
                        .with(csrf())
                        .flashAttr("form", form))
                .andExpect(status().isUnauthorized());

        verify(commentService, never()).createComment(any(), anyString(), anyLong());
    }

    @Test
    @DisplayName("인증 사용자는 댓글 작성 성공 후 리다이렉트")
    @WithMockUser(username = "user1")
    void createComment_authenticatedUser_success() throws Exception {
        performPostComment("user1", "Authenticated comment")
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/" + boardId));

        verify(commentService).createComment(any(CommentForm.class), eq("user1"), eq(boardId));
    }

    @ParameterizedTest
    @DisplayName("댓글 작성 성공 - 유효한 댓글 내용")
    @ValueSource(strings = {"Hello!", "Good comment", "특수문자 OK!123"})
    void createComment_validContent(String validContent) throws Exception {
        performPostComment("testUser", validContent)
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/" + boardId));

        verify(commentService).createComment(any(CommentForm.class), eq("testUser"), eq(boardId));
    }

    @ParameterizedTest
    @DisplayName("빈 댓글 또는 공백 댓글은 BadRequest 발생")
    @ValueSource(strings = {"", " ", "\t", "\n"})
    void createComment_validationErrors(String invalidContent) throws Exception {
        CommentForm form = createCommentForm(invalidContent);

        mockMvc.perform(post("/boards/{boardId}/comments", boardId)
                        .with(csrf())
                        .with(user("testUser").roles("USER"))
                        .flashAttr("form", form))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("댓글 삭제 성공 후 리다이렉트")
    void deleteComment_success() throws Exception {
        Long commentId = 10L;

        mockMvc.perform(delete("/boards/{boardId}/comments/{commentId}/delete", boardId, commentId)
                        .with(csrf())
                        .with(user("testUser").roles("USER")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/boards/" + boardId));

        verify(commentService).deleteComment(commentId, "testUser");
    }

    private ResultActions performPostComment(String username, String content) throws Exception {
        return mockMvc.perform(post("/boards/{boardId}/comments", boardId)
                .with(csrf())
                .with(user(username).roles("USER"))
                .param("content", content));
    }

    private CommentForm createCommentForm(String content) {
        CommentForm form = new CommentForm();
        form.setContent(content);
        return form;
    }
}
