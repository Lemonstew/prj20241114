USE prj20241114;

CREATE TABLE comment
(
    id        INT PRIMARY KEY AUTO_INCREMENT,
    board_id  INT          NOT NULL REFERENCES board (id),
    member_id VARCHAR(20) REFERENCES member (id),
    comment   VARCHAR(500) NOT NULL,
    inserted  DATETIME     NOT NULL DEFAULT NOW()
);

SELECT *
FROM comment;

# sub query
SELECT id,
       title,
       writer,
       inserted,
       (SELECT COUNT(c.id) FROM comment c WHERE board_id = b.id) c
FROM board b
ORDER BY id DESC;

# join
SELECT b.id, b.title, b.writer, b.inserted
FROM board b
         JOIN comment c ON b.id = c.board_id
GROUP BY b.id
ORDER BY b.id DESC;
