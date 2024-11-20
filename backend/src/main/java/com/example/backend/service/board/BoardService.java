package com.example.backend.service.board;

import com.example.backend.dto.board.Board;
import com.example.backend.mapper.board.BoardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    final BoardMapper mapper;

    public boolean add(Board board, MultipartFile[] files, Authentication authentication) {
        board.setWriter(authentication.getName());
        int cnt = mapper.insert(board);

        if (files != null && files.length > 0) {
            // 폴더 만들기
            String directory = STR."C:/Temp/prj1114/\{board.getId()}";
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            // 파일 업로드
            // TODO : local -> aws
            for (MultipartFile file : files) {
                String filePath = STR."C:/Temp/prj1114/\{board.getId()}/\{file.getOriginalFilename()}";
                try {
                    file.transferTo(new File(filePath));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }


        return cnt == 1;


    }

    public Map<String, Object> list(Integer page, String searchType, String keyword) {
        // SQL 의 LIMIT 키워드에서 사용되는 offset
        Integer offset = (page - 1) * 10;
        List<Board> list = mapper.selectPage(offset, searchType, keyword);
        Integer count = mapper.countAll();

        return Map.of("list", list,
                "count", mapper.countAll());
    }

    public Board get(int id) {
        return mapper.selectById(id);
    }

    public boolean validate(Board board) {
        boolean title = board.getTitle().trim().length() > 0;
        boolean content = board.getContent().trim().length() > 0;

        return title && content;
    }

    public boolean remove(int id) {
        int cnt = mapper.deleteById(id);
        return cnt == 1;
    }

    public boolean update(Board board) {
        int cnt = mapper.update(board);
        return cnt == 1;
    }

    public boolean hasAccess(int id, Authentication authentication) {
        Board board = mapper.selectById(id);

        return board.getWriter().equals(authentication.getName());
    }
}
