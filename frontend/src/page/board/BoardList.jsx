import { Box, HStack, Input, Table } from "@chakra-ui/react";
import { useEffect, useState } from "react";
import axios from "axios";
import { useNavigate, useSearchParams } from "react-router-dom";
import {
  PaginationItems,
  PaginationNextTrigger,
  PaginationPrevTrigger,
  PaginationRoot,
} from "../../components/ui/pagination.jsx";
import {
  NativeSelectField,
  NativeSelectRoot,
} from "../../components/ui/native-select.jsx";
import { Button } from "../../components/ui/button.jsx";

export function BoardList() {
  const [boardList, setBoardList] = useState([]);
  const [count, setCount] = useState(0);
  const [search, setSearch] = useState({ type: "all", keyword: "" });
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  useEffect(() => {
    const controller = new AbortController();
    axios
      .get("/api/board/list", {
        params: searchParams,
        signal: controller.signal,
      })
      .then((res) => {
        setBoardList(res.data.list);
        setCount(res.data.count);
      });

    return () => {
      controller.abort();
    };
  }, [searchParams]);

  // searchParams
  console.log(searchParams.toString());

  // 검색 조건
  console.log("검색조건", search);

  // page 번호
  const pageParam = searchParams.get("page") ? searchParams.get("page") : "1";
  const page = Number(pageParam);

  function handleRowClick(id) {
    navigate(`/view/${id}`);
  }

  function handlePageChange(e) {
    console.log(e.page);
    const nextSearchParams = new URLSearchParams(searchParams);
    nextSearchParams.set("page", e.page);
    setSearchParams(nextSearchParams);
  }

  return (
    <Box>
      <h3>게시물 목록</h3>
      <Table.Root interactive>
        <Table.Header>
          <Table.Row>
            <Table.ColumnHeader>번호</Table.ColumnHeader>
            <Table.ColumnHeader>제목</Table.ColumnHeader>
            <Table.ColumnHeader>작성자</Table.ColumnHeader>
            <Table.ColumnHeader>작성일시</Table.ColumnHeader>
          </Table.Row>
        </Table.Header>
        <Table.Body>
          {boardList.map((board) => (
            <Table.Row onClick={() => handleRowClick(board.id)} key={board.id}>
              <Table.Cell>{board.id}</Table.Cell>
              <Table.Cell>{board.title}</Table.Cell>
              <Table.Cell>{board.writer}</Table.Cell>
              <Table.Cell>{board.inserted}</Table.Cell>
            </Table.Row>
          ))}
        </Table.Body>
      </Table.Root>

      <Box>
        <HStack>
          <NativeSelectRoot
            onChange={(e) => setSearch({ ...search, type: e.target.value })}
          >
            <NativeSelectField
              items={[
                { label: "전체", value: "all" },
                { label: "제목", value: "title" },
                { label: "본문", value: "content" },
              ]}
            />
          </NativeSelectRoot>
          <Input
            onChange={(e) => setSearch({ ...search, keyword: e.target.value })}
          />
          <Button>검색</Button>
        </HStack>
      </Box>

      <PaginationRoot
        onPageChange={handlePageChange}
        count={count}
        pageSize={10}
        page={page}
      >
        <HStack>
          <PaginationPrevTrigger />
          <PaginationItems />
          <PaginationNextTrigger />
        </HStack>
      </PaginationRoot>
    </Box>
  );
}
