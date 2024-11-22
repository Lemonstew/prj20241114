package com.example.backend.mapper.board;

import com.example.backend.dto.board.Board;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BoardMapper {
    @Insert("""
            INSERT INTO board
            (title, content, writer)
            VALUES (#{title}, #{content}, #{writer})
            """)
    @Options(keyProperty = "id", useGeneratedKeys = true)
    int insert(Board board);

    @Select("""
            SELECT id, title, writer, inserted FROM board
            ORDER BY id DESC
            """)
    List<Board> selectAll();

    @Select("""
            SELECT *
            FROM board
            WHERE id=#{id}
            """)
    Board selectById(int id);

    @Delete("""
            DELETE FROM board
            WHERE id=#{id}
            """)
    int deleteById(int id);

    @Update("""
            UPDATE board
            SET title=#{title}, content=#{content}
            WHERE id=#{id}
            """)
    int update(Board board);

    @Select("""
            <script>
            SELECT b.id, b.title, b.writer, b.inserted,
            COUNT(DISTINCT c.id) countComment,
            COUNT(DISTINCT f.name) countFile
            FROM board b LEFT JOIN comment c ON b.id = c.board_id
            LEFT JOIN board_file f
            ON b.id = f.board_id
            WHERE 
                <trim prefixOverrides="OR">
                    <if test="searchType == 'all' or searchType == 'title'">
                        title LIKE CONCAT('%', #{keyword}, '%')
                    </if>
                    <if test="searchType == 'all' or searchType == 'content'">
                    OR  content LIKE CONCAT('%', #{keyword}, '%')
                    </if>
                </trim>
            GROUP BY b.id
            ORDER BY id DESC
            LIMIT #{offset}, 10
            </script>
            """)
    List<Board> selectPage(Integer offset, String searchType, String keyword);

    @Select("""
            SELECT COUNT(*) FROM board
            """)
    Integer countAll();

    @Insert("""
            INSERT INTO board_file
            VALUES (#{id}, #{fileName})
            """)
    int insertFile(Integer id, String fileName);

    @Select("""
            SELECT name
            FROM board_file
            WHERE board_id=#{id}
            """)
    List<String> selectFilesByBoardId(int id);

    @Delete("""
            DELETE FROM board_file
            WHERE board_id=#{id}
            """)
    int deleteFileByBoardId(int id);

    @Select("""  
            SELECT id
            FROM board
            WHERE writer = #{id}
            """)
    List<Integer> selectByWriter(String id);

    @Delete("""
            DELETE FROM board_file
            WHERE board_id = #{id}
              AND name = #{name}
            """)
    int deleteFileByBoardIdAndName(Integer id, String name);

    @Delete("""
            DELETE FROM board_like
            WHERE board_id = #{id}
              AND member_id = #{name}
            """)
    int deleteLikeByBoardIdAndMemberId(Integer id, String name);

    @Insert("""
            INSERT INTO board_like
            VALUES (#{id}, #{name})
            """)
    int insertLike(Integer id, String name);

    @Select("""
            SELECT COUNT(*)
            FROM board_like
            WHERE board_id = #{id}
            """)
    int countLike(Integer id);
}
