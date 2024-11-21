package com.example.backend.service.board;

import com.example.backend.dto.board.Board;
import com.example.backend.dto.board.BoardFile;
import com.example.backend.mapper.board.BoardMapper;
import com.example.backend.mapper.comment.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;


@Service
@Transactional
@RequiredArgsConstructor
public class BoardService {

    final BoardMapper mapper;
    final CommentMapper commentMapper;
    final S3Client s3;

    @Value("${image.src.prefix}")
    String imageSrcPrefix;

    @Value("${bucket.name}")
    String bucketName;

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
                String objectKey = STR."prj1114/\{board.getId()}/\{file.getOriginalFilename()}";
                PutObjectRequest por = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .acl(ObjectCannedACL.PUBLIC_READ)
                        .build();
                try {
                    s3.putObject(por, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


                // board_file 테이블에 파일명 입력
                mapper.insertFile(board.getId(), file.getOriginalFilename());
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
        Board board = mapper.selectById(id);
        List<String> fileNameList = mapper.selectFilesByBoardId(id);
        List<BoardFile> fileSrcList = fileNameList.stream()
                .map(name -> new BoardFile(name, STR."\{imageSrcPrefix}/\{id}/\{name}"))
                .toList();
        //
        board.setFileList(fileSrcList);
        return board;
    }

    public boolean validate(Board board) {
        boolean title = board.getTitle().trim().length() > 0;
        boolean content = board.getContent().trim().length() > 0;

        return title && content;
    }

    public boolean remove(int id) {
        // 첨부파일 지우기
        // 실제 파일(s3) 지우기
        List<String> fileName = mapper.selectFilesByBoardId(id);

        for (String file : fileName) {
            String key = STR."prj1114/\{id}/\{file}";
            DeleteObjectRequest dor = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();
            s3.deleteObject(dor);
        }


        // db 지우기
        mapper.deleteFileByBoardId(id);

        // 댓글 지우기
        commentMapper.deleteByBoardId(id);


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
