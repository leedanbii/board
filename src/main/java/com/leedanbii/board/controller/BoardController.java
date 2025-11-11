package com.leedanbii.board.controller;

import com.leedanbii.board.dto.BoardForm;
import com.leedanbii.board.entity.User;
import com.leedanbii.board.service.BoardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping
    public String home(HttpSession session, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/";
        }
        model.addAttribute("username", loginUser.getName());
        return "boards/home";
    }

    @GetMapping("/new")
    public String showCreateForm() {
        return "boards/form";
    }

    @GetMapping("/list")
    public String getAllBoards(Model model) {
        model.addAttribute("boards", boardService.getAllBoards());
        return "boards/list";
    }

    @PostMapping("/new")
    public String createBoard(BoardForm form, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/";
        }

        boardService.createBoard(form, loginUser);
        return "redirect:/boards/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("board", boardService.getBoard(id));
        return "boards/detail";
    }
}
