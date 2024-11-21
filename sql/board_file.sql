USE prj20241114;

CREATE TABLE board_file
(
    board_id INT          NOT NULL,
    name     VARCHAR(300) NOT NULL,
    PRIMARY KEY (board_id, name)
);

SELECT *
FROM board_file;

