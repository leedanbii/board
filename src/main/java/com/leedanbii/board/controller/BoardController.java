package com.leedanbii.board.controller;

import com.leedanbii.board.dto.BoardDetailResponse;
import com.leedanbii.board.dto.BoardForm;
import com.leedanbii.board.dto.BoardResponse;
import com.leedanbii.board.dto.BoardUpdateForm;
import com.leedanbii.board.service.BoardService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public String home(@AuthenticationPrincipal UserDetails loginUser, Model model) {
        if (loginUser == null) {
            return "redirect:/";
        }
        model.addAttribute("username", loginUser.getUsername());
        return "boards/home";
    }

    @GetMapping("/new")
    public String showCreateForm(@AuthenticationPrincipal UserDetails loginUser, Model model) {
        if (loginUser == null) {
            return "redirect:/";
        }
        model.addAttribute("boardForm", new BoardForm());
        return "boards/form";
    }

    @GetMapping("/list")
    public String getAllBoards(Model model) {
        List<BoardResponse> boards = boardService.getAllBoards();
        model.addAttribute("boards", boards);
        return "boards/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") Long id, Model model) {
        model.addAttribute("board", boardService.getBoardDetail(id));
        return "boards/detail";
    }

    @GetMapping("/{id}/update")
    public String update(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal UserDetails loginUser) {
        if (loginUser == null) {
            return "redirect:/";
        }
        model.addAttribute("board", boardService.getBoardForUpdate(id, loginUser.getUsername()));
        return "boards/update";
    }

    @PostMapping("/new")
    public String createBoard(@Valid BoardForm form,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal UserDetails loginUser,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "/boards/form";
        }
        Long boardId = boardService.createBoard(form, loginUser.getUsername());
        return "redirect:/boards/" + boardId;
    }

    @PutMapping("/{id}")
    public String updateBoard(@PathVariable("id") Long id,
                              @Valid BoardUpdateForm form,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal UserDetails loginUser,
                              Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("board", boardService.getBoardForUpdate(id, loginUser.getUsername()));
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "/boards/update";
        }
        Long boardId = boardService.updateBoard(id, form, loginUser.getUsername());
        return "redirect:/boards/" + boardId;
    }

    @DeleteMapping("/{id}/delete")
    public String deleteBoard(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails loginUser) {
        boardService.deleteBoard(id, loginUser.getUsername());
        return "redirect:/boards/list";
    }
}
