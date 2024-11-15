USE prj20241114;

# 프라이머리키는 변경해서는 안됨.
CREATE TABLE member
(
    id          VARCHAR(20) PRIMARY KEY,
    password    VARCHAR(32) NOT NULL,
    description VARCHAR(1000),
    inserted    DATETIME DEFAULT NOW()
);

SELECT *
FROM member;

DROP TABLE member;