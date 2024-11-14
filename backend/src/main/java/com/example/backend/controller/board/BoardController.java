package com.example.backend.controller.board;

import com.example.backend.dto.board.Board;
import com.example.backend.service.board.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/board")
public class BoardController {

    final BoardService service;

    @GetMapping("view/{id}")
    public Board view(@PathVariable int id) {
        return service.get(id);
    }

    @GetMapping("list")
    public List<Board> list() {
        return service.list();
    }

    @PostMapping("add")
    public Map<String, Object> add(@RequestBody Board board) {


        return service.add(board);
    }

}
