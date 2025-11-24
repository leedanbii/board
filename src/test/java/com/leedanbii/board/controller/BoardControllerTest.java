package com.leedanbii.board.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.leedanbii.board.config.SecurityConfig;
import com.leedanbii.board.dto.BoardDetailResponse;
import com.leedanbii.board.dto.BoardForm;
import com.leedanbii.board.dto.BoardResponse;
import com.leedanbii.board.dto.BoardUpdateForm;
import com.leedanbii.board.dto.CommentResponse;
import com.leedanbii.board.exception.CustomAuthFailureHandler;
import com.leedanbii.board.service.BoardService;
import com.leedanbii.board.service.CustomUserDetailsService;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(BoardController.class)
@Import(SecurityConfig.class)
public class BoardControllerTest {

    private static final String TEST_USER = "user1";
    private static final Long TEST_BOARD_ID = 1L;

    private static final String BASE_URL = "/boards";
    private static final String NEW_URL = BASE_URL + "/new";
    private static final String LIST_URL = BASE_URL + "/list";
    private static final String DETAIL_URL = BASE_URL + "/{id}";
    private static final String UPDATE_URL = BASE_URL + "/{id}";
    private static final String DELETE_URL = BASE_URL + "/{id}/delete";

    private static final OffsetDateTime FIXED_TIME = OffsetDateTime.of(2024, 1, 1, 12, 0, 0, 0, ZoneOffset.ofHours(9));

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BoardService boardService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private CustomAuthFailureHandler customAuthFailureHandler;


    @Test
    @DisplayName("홈 화면 접근 - 인증된 사용자")
    @WithMockUser(username = TEST_USER)
    void home_authenticatedUser() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(view().name("boards/home"))
                .andExpect(model().attribute("username", TEST_USER));
    }

    @Test
    @DisplayName("홈 화면 접근 - 익명 사용자 리다이렉트")
    void home_anonymousUser_redirectsHome() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("게시글 작성 화면 - 인증 사용자")
    @WithMockUser(username = TEST_USER)
    void showCreateForm_authenticatedUser() throws Exception {
        mockMvc.perform(get(NEW_URL))
                .andExpect(status().isOk())
                .andExpect(view().name("boards/form"))
                .andExpect(model().attributeExists("boardForm"));
    }

    @Test
    @DisplayName("게시글 작성 화면 - 익명 사용자 리다이렉트")
    void showCreateForm_anonymousUser_redirectsHome() throws Exception {
        mockMvc.perform(get(NEW_URL))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("게시글 목록 조회")
    void getAllBoards_success() throws Exception {
        given(boardService.getAllBoards()).willReturn(List.of(
                new BoardResponse(1L, "Title1", "user1", FIXED_TIME),
                new BoardResponse(2L, "Title2", "user2", FIXED_TIME)
        ));

        mockMvc.perform(get(LIST_URL))
                .andExpect(status().isOk())
                .andExpect(view().name("boards/list"))
                .andExpect(model().attributeExists("boards"));
    }

    @Test
    @DisplayName("게시글 상세 조회")
    void detail_success() throws Exception {
        List<CommentResponse> comments = List.of(
                new CommentResponse(1L, "First comment", "user1", "userName1", OffsetDateTime.now(ZoneOffset.ofHours(9))),
                new CommentResponse(2L, "Second comment", "user2", "userName2", OffsetDateTime.now(ZoneOffset.ofHours(9)))
        );

        BoardDetailResponse boardDetail = new BoardDetailResponse(
                TEST_BOARD_ID,
                "Test Title",
                "Test content",
                "Writer Name",
                "writerUserId",
                FIXED_TIME,
                comments
        );

        given(boardService.getBoardDetail(TEST_BOARD_ID)).willReturn(boardDetail);

        mockMvc.perform(get(DETAIL_URL, TEST_BOARD_ID))
                .andExpect(status().isOk())
                .andExpect(view().name("boards/detail"))
                .andExpect(model().attribute("board", boardDetail));
    }

    @Test
    @DisplayName("게시글 작성 - 성공")
    @WithMockUser(username = TEST_USER)
    void createBoard_success() throws Exception {
        Long boardId = 1L;
        given(boardService.createBoard(any(BoardForm.class), eq(TEST_USER)))
                .willReturn(TEST_BOARD_ID);

        mockMvc.perform(post(NEW_URL)
                        .with(csrf())
                        .param("boardTitle", "New Board")
                        .param("boardContent", "Content of the board"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL + "/" + boardId));
    }

    @Test
    @DisplayName("게시글 작성 - 검증 실패")
    @WithMockUser(username = TEST_USER)
    void createBoard_validationErrors() throws Exception {
        mockMvc.perform(post(NEW_URL)
                        .with(csrf())
                        .param("boardTitle", "")
                        .param("boardContent", "Content"))
                .andExpect(status().isOk())
                .andExpect(view().name("boards/form"))
                .andExpect(model().attributeExists("errors"));
    }

    @Test
    @DisplayName("게시글 수정 - 성공")
    @WithMockUser(username = TEST_USER)
    void updateBoard_success() throws Exception {
        given(boardService.updateBoard(eq(TEST_BOARD_ID), any(BoardUpdateForm.class), eq(TEST_USER)))
                .willReturn(TEST_BOARD_ID);

        mockMvc.perform(put(UPDATE_URL, TEST_BOARD_ID)
                        .with(csrf())
                        .param("boardTitle", "Updated Title")
                        .param("boardContent", "Updated Content"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL + "/" + TEST_BOARD_ID));
    }

    @Test
    @DisplayName("게시글 수정 - 검증 실패")
    @WithMockUser(username = TEST_USER)
    void updateBoard_validationErrors() throws Exception {

        BoardDetailResponse response = new BoardDetailResponse(
                TEST_BOARD_ID,
                "Old Title",
                "Old Content",
                "User One",
                TEST_USER,
                FIXED_TIME,
                List.of(new CommentResponse(1L, "First comment", TEST_USER, "userName1", FIXED_TIME))
        );

        given(boardService.getBoardForUpdate(TEST_BOARD_ID, TEST_USER)).willReturn(response);

        mockMvc.perform(put(UPDATE_URL, TEST_BOARD_ID)
                        .with(csrf())
                        .param("boardTitle", "")
                        .param("boardContent", "Updated Content"))
                .andExpect(status().isOk())
                .andExpect(view().name("boards/update"))
                .andExpect(model().attributeExists("errors"))
                .andExpect(model().attributeExists("board"));
    }

    @Test
    @DisplayName("게시글 삭제")
    @WithMockUser(username = TEST_USER)
    void deleteBoard_success() throws Exception {

        mockMvc.perform(delete(DELETE_URL, TEST_BOARD_ID)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(LIST_URL));

        verify(boardService).deleteBoard(TEST_BOARD_ID, TEST_USER);
    }
}
