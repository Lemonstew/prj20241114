import { useState } from "react";
import axios from "axios";
import { Box, Input, Stack, Textarea } from "@chakra-ui/react";
import { Field } from "../../components/ui/field.jsx";
import { Button } from "../../components/ui/button.jsx";
import { useNavigate } from "react-router-dom";
import { toaster } from "../../components/ui/toaster.jsx";

export function BoardAdd() {
  const [title, setTitle] = useState("");
  const [content, setContent] = useState("");
  const [files, setFiles] = useState([]);
  const [progress, setProgress] = useState(false);

  const navigate = useNavigate();

  const handleSaveClick = () => {
    setProgress(true);

    axios
      .postForm("/api/board/add", { title, content })
      .then((res) => res.data)
      .then((data) => {
        const message = data.message;

        toaster.create({
          description: message.text,
          type: message.type,
        });
        navigate(`/view/${data.data.id}`);
      })
      .catch((e) => {
        const message = e.response.data.message;
        toaster
          .create({
            description: message.text,
            type: message.type,
          })
          .finally(() => {
            // 성공 / 실패 상관없이 실행
            setProgress(false);
          });
      });
  };
  // axios.post("/api/board/add", {title: title, content: content, writer: writer})}
  // 원래 이름과 사용하려는 변수명이 같으면 생략 가능

  const disabled = !(title.trim().length > 0 && content.trim().length > 0);

  // files 의 파일명을 component 리스트로 만들기
  const filesList = [];
  for (const file of files) {
    filesList.push(
      <li>
        {file.name}({Math.floor(file.size / 1024)} KB)
      </li>,
    );
  }

  return (
    <Box>
      <h2>게시물 작성</h2>
      <Stack gap={5}>
        <Field label={"제목"}>
          <Input value={title} onChange={(e) => setTitle(e.target.value)} />
        </Field>
        <Field label={"본문"}>
          <Textarea
            value={content}
            onChange={(e) => setContent(e.target.value)}
          />
        </Field>
        <Box>
          <Input
            type={"file"}
            onChange={(e) => setFiles(e.target.files)}
            accept={"image/*"}
            multiple
          />
          <Box>{filesList}</Box>
        </Box>
        <Box>
          <Button
            disabled={disabled}
            loading={progress}
            onClick={handleSaveClick}
          >
            저장
          </Button>
        </Box>
      </Stack>
    </Box>
  );
}
